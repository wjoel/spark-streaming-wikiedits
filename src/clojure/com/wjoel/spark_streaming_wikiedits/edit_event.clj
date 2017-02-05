(ns com.wjoel.spark-streaming-wikiedits.edit-event
  (:require [clj-bean.core :refer :all]))

(defbean com.wjoel.spark_streaming_wikiedits.edit_event.WikipediaEdit
  [[Long timestamp]
   [String channel]
   [String title]
   [String diffUrl]
   [String user]
   [Integer byteDiff]
   [String summary]
   [Boolean isMinor]
   [Boolean isNew]
   [Boolean isUnPatrolled]
   [Boolean isBotEdit]
   [Boolean isSpecial]
   [Boolean isTalk]])
