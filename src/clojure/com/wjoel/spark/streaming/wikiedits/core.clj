(ns com.wjoel.spark.streaming.wikiedits.core
  (:require [clojure.tools.logging :as log]
            [com.wjoel.spark.streaming.wikiedits.edit-event :as ev])
  (:import [org.apache.spark.storage StorageLevel]
           [org.schwering.irc.lib
            IRCConnection
            IRCEventAdapter
            IRCModeParser
            IRCUser])
  (:gen-class
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
   :main false))

(set! *warn-on-reflection* true)

(def wikimedia-irc-host "irc.wikimedia.org")
(def wikimedia-irc-port 6667)
(def wikimedia-channel "#en.wikipedia")

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

(defn edit-event-message->edit-event [edit-event-message]
  (when-let [match (re-matches edit-event-regexp edit-event-message)]
    (let [[_ title ^String flags diff-url user byte-diff-str summary] match
          byte-diff (try (java.lang.Integer/parseInt byte-diff-str)
                         (catch Exception e
                           (int 0)))]
      (com.wjoel.spark.streaming.wikiedits.WikipediaEditEvent.
       (System/currentTimeMillis)
       "#en.wikipedia"
       title
       diff-url
       user
       byte-diff
       summary
       (.contains flags "M")
       (.contains flags "N")
       (.contains flags "!")
       (.contains flags "B")
       (.contains flags "Special:")
       (.contains flags "Talk:")))))

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
      (.send (str "JOIN " wikimedia-channel))
      (.addIRCEventListener
       (make-irc-events-listener
        (fn [msg]
          (when-let [edit-event (edit-event-message->edit-event msg)]
            (.store this edit-event))))))
    (catch java.io.IOException ex
      (log/warn ex (str "Failed to connect to " wikimedia-irc-host ":" wikimedia-irc-port))
      (throw ex))))

(defn connect-as [^com.wjoel.spark.streaming.wikiedits.WikipediaEditReceiver this nick]
  (when-let [conn (IRCConnection. wikimedia-irc-host (int-array [wikimedia-irc-port]) "" nick nick nick)]
    (.put ^java.util.HashMap (.state this)
          "connection" (init-connection this conn))))

(defn receiver-onStart [^com.wjoel.spark.streaming.wikiedits.WikipediaEditReceiver this]
  (.start (Thread. (fn []
                     (connect-as this ^String (get-from-state this :nick))))))

(defn receiver-onStop [^com.wjoel.spark.streaming.wikiedits.WikipediaEditReceiver this]
  (let [conn ^IRCConnection (get-from-state this :connection)]
    (when (and conn (.isConnected conn))
      (doto conn
        (.send (str "PART " wikimedia-channel))
        (.interrupt)
        (.join 3000)))))
