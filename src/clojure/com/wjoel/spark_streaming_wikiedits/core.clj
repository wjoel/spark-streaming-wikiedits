(ns com.wjoel.spark-streaming-wikiedits.core
  (:require [com.wjoel.spark-streaming-wikiedits.edit-event :as ev])
  (:import [org.apache.spark.storage StorageLevel]
           [org.schwering.irc.lib
            IRCConnection
            IRCEventAdapter
            IRCModeParser
            IRCUser]))

(set! *warn-on-reflection* true)

(definterface IIRCConnection
  (getConnection [])
  (setConnection [c]))

(deftype IRCReceiverState
    [^{:volatile-mutable true
       java.beans.Transient true} conn
     ^String nick]
  IIRCConnection
  (getConnection [_] conn)
  (setConnection [this c] (set! conn c))
  java.io.Serializable)

(gen-class
 :name com.wjoel.spark_streaming_wikiedits.core.WikipediaEditReceiver
 :extends com.wjoel.spark_streaming_wikiedits.AbstractWikieditsReceiver
 :init init
 :state ^IRCReceiverState state
 :prefix "receiver-"
 :constructors {[] [org.apache.spark.storage.StorageLevel]
                [org.apache.spark.storage.StorageLevel] [org.apache.spark.storage.StorageLevel]
                [org.apache.spark.storage.StorageLevel String] [org.apache.spark.storage.StorageLevel]}
 :main false)

(def wikimedia-irc-host "irc.wikimedia.org")
(def wikimedia-irc-port 6667)

(defn receiver-init
  ([]
   (receiver-init (str "spark-bot-" (+ 10000 (rand-int 8999))) (StorageLevel/MEMORY_AND_DISK)))
  ([nick]
   (receiver-init nick (StorageLevel/MEMORY_AND_DISK)))
  ([nick storage-level]
   [[storage-level] (->IRCReceiverState nick nil)]))

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

(defn init-connection [^com.wjoel.spark_streaming_wikiedits.core.WikipediaEditReceiver this
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
                      ^com.wjoel.spark_streaming_wikiedits.edit_event.EditGenClass
                      ;^com.wjoel.spark_streaming_wikiedits.edit_event.WikipediaEditEvent
                      (com.wjoel.spark_streaming_wikiedits.edit_event.EditGenClass.
                       ;ev/->WikipediaEditEvent
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

(defn connect-as [^com.wjoel.spark_streaming_wikiedits.core.WikipediaEditReceiver this nick]
  (if-let [conn (IRCConnection. wikimedia-irc-host (int-array [wikimedia-irc-port]) "" nick nick nick)]
    (.setConnection ^IRCReceiverState (.state this) (init-connection this conn))
    (println "Failed to connect")))

(defn receiver-onStart [^com.wjoel.spark_streaming_wikiedits.core.WikipediaEditReceiver this]
  (-> (Thread. (fn []
                 (connect-as this "foo-1239239292")))
      .start))

(defn receiver-onStop [^com.wjoel.spark_streaming_wikiedits.core.WikipediaEditReceiver this]
  (let [state ^IRCReceiverState (.state this)
        conn ^IRCConnection (.getConnection state)]
    (when (and conn (.isConnected conn))
      (doto conn
        (.send "PART #en.wikipedia")
        (.interrupt)
        (.join 3000)))))

(defn receiver-receive [^com.wjoel.spark_streaming_wikiedits.core.WikipediaEditReceiver this]
  (when-let [conn ^IRCConnection (.getConnection ^IRCReceiverState (.state this))]
    (when (.isConnected conn)
      (.join conn))))
