(ns com.wjoel.spark.streaming.wikiedits.core
  (:require [com.wjoel.spark.streaming.wikiedits.edit-event :as ev])
  (:import [org.apache.spark.storage StorageLevel]
           [org.schwering.irc.lib
            IRCConnection
            IRCEventAdapter
            IRCModeParser
            IRCUser]))

(set! *warn-on-reflection* true)

(gen-class
 :name com.wjoel.spark.streaming.wikiedits.WikipediaEditReceiver
 :extends com.wjoel.spark.streaming.wikiedits.AbstractWikipediaEditReceiver
 :init init
 ;; while we would normally use an atom with a hash-map as state,
 ;; atoms can not be serialized, so we use a java.util.HashMap instead.
 :state state
 :prefix "receiver-"
 :constructors {[] [org.apache.spark.storage.StorageLevel]
                [String] [org.apache.spark.storage.StorageLevel]
                [String org.apache.spark.storage.StorageLevel] [org.apache.spark.storage.StorageLevel]}
 :main false)

(def wikimedia-irc-host "irc.wikimedia.org")
(def wikimedia-irc-port 6667)

(defn receiver-init
  ([]
   (receiver-init (str "spark-bot-" (+ 10000 (rand-int 8999))) (StorageLevel/MEMORY_ONLY)))
  ([nick]
   (receiver-init nick (StorageLevel/MEMORY_ONLY)))
  ([nick storage-level]
   [[storage-level] (doto (java.util.HashMap.)
                      (.put :nick nick))]))

(defn get-from-state [^com.wjoel.spark.streaming.wikiedits.WikipediaEditReceiver this key]
  (.get ^java.util.HashMap (.state this) key))

(defn make-irc-events-listener [message-fn]
  (proxy [IRCEventAdapter] []
    (onPrivmsg [target user msg]
      (message-fn msg))))

(def edit-event-regexp #"\[\[(.*)\]\]\s(.*)\s(.*)\s\*\s(.*)\s\*\s\(\+?(.\d*)\)\s(.*)")

(defn parse-flags [^String flags]
  {:minor? (.contains flags "M")
   :new? (.contains flags "N")
   :unpatrolled? (.contains flags "!")
   :bot-edit? (.contains flags "B")
   :special? (.contains flags "Special:")
   :talk? (.contains flags "Talk:")})

(defn init-connection [^com.wjoel.spark.streaming.wikiedits.WikipediaEditReceiver this
                       ^IRCConnection conn]
  (doto conn
    (.setEncoding "UTF-8")
    (.setPong true)
    (.setColors false)
    (.setDaemon true)
    (.setName "WikieditsReceiverIrcThread"))
  (try
    (doto conn
      (.connect)
      (.send "JOIN #en.wikipedia")
      (.addIRCEventListener
       (make-irc-events-listener
        (fn [msg]
          (when-let [match (re-matches edit-event-regexp msg)]
            (let [[_ title flags diff-url user byte-diff-str summary] match
                  flags (parse-flags flags)
                  byte-diff (try (java.lang.Integer/parseInt byte-diff-str)
                                 (catch Exception e
                                   (int 0)))]
              (.store this
                      ^com.wjoel.spark.streaming.wikiedits.WikipediaEditEvent
                      (com.wjoel.spark.streaming.wikiedits.WikipediaEditEvent.
                       (System/currentTimeMillis)
                       "#en.wikipedia"
                       title
                       diff-url
                       user
                       byte-diff
                       summary
                       (:minor? flags)
                       (:new? flags)
                       (:unpatrolled? flags)
                       (:bot-edit? flags)
                       (:special? flags)
                       (:talk? flags)))))))))
    (catch java.io.IOException e
      (println "Failed to connect: " e))))

(defn connect-as [^com.wjoel.spark.streaming.wikiedits.WikipediaEditReceiver this nick]
  (if-let [conn (IRCConnection. wikimedia-irc-host (int-array [wikimedia-irc-port]) "" nick nick nick)]
    (.put ^java.util.HashMap (.state this)
          "connection" (init-connection this conn))
    (println "Failed to connect")))

(defn receiver-onStart [^com.wjoel.spark.streaming.wikiedits.WikipediaEditReceiver this]
  (.start (Thread. (fn []
                     (connect-as this ^String (get-from-state this :nick))))))

(defn receiver-onStop [^com.wjoel.spark.streaming.wikiedits.WikipediaEditReceiver this]
  (let [conn ^IRCConnection (get-from-state this :connection)]
    (when (and conn (.isConnected conn))
      (doto conn
        (.send "PART #en.wikipedia")
        (.interrupt)
        (.join 3000)))))
