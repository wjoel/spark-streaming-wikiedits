(defproject com.wjoel/spark-streaming-wikiedits "0.1.0-SNAPSHOT"
  :description "Spark Streaming receiver for Wikipedia edits"
  :url "https://github.com/wjoel/spark-streaming-wikiedits"
  :license {:name "MIT License"
            :url "http://www.opensource.org/licenses/mit-license.php"}
  :source-paths ["src" "src/clojure"]
  :java-source-paths ["src/java"]
  :prep-tasks [["compile" "com.wjoel.spark-streaming-wikiedits.edit-event"] "javac" "compile"]
  :aot :all
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.scala-lang/scala-library "2.11.8"]
                 [org.apache.spark/spark-core_2.11 "2.0.2"]
                 [org.apache.spark/spark-streaming_2.11 "2.0.2"]
                 [org.schwering/irclib "1.10"]])
