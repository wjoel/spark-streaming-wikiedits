(ns com.wjoel.spark.streaming.wikiedits.edit-event
  (:require [clj-bean.core :refer :all]))

(defbean com.wjoel.spark.streaming.wikiedits.WikipediaEditEvent
  [[Long timestamp]
   [String channel]
   [String title]
   [String diffUrl]
   [String user]
   [Integer byteDiff]
   [String summary]
   [Boolean minor]
   [Boolean new]
   [Boolean unPatrolled]
   [Boolean botEdit]
   [Boolean special]
   [Boolean talk]])
