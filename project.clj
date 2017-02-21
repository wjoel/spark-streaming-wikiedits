(defproject com.wjoel/spark-streaming-wikiedits "0.1.3"
  :description "Spark Streaming receiver for Wikipedia edits"
  :url "https://github.com/wjoel/spark-streaming-wikiedits"
  :license {:name "MIT License"
            :url "http://www.opensource.org/licenses/mit-license.php"}
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  :prep-tasks [["compile" "com.wjoel.spark.streaming.wikiedits.edit-event"] "javac" "compile"]
  :aot :all
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.schwering/irclib "1.10"]
                 [com.wjoel/clj-bean "0.2.0"]]
  :profiles {:provided {:dependencies [[org.scala-lang/scala-library "2.11.8"]
                                       [org.apache.spark/spark-core_2.11 "2.0.2"]
                                       [org.apache.spark/spark-streaming_2.11 "2.0.2"]]}}
  :deploy-repositories {"releases" {:url "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
                                    :creds :gpg}
                        "snapshots" {:url "https://oss.sonatype.org/content/repositories/snapshots/"
                                     :creds :gpg}}
  :scm {:url "git@github.com:wjoel/spark-streaming-wikiedits.git"}
  :classifiers {:javadoc {:source-paths ^:replace []
                          :java-source-paths ^:replace []
                          :prep-tasks ^:replace []}
                :sources {:prep-tasks ^:replace []}}
  :pom-addition [:developers [:developer
                              [:name "Joel Wilsson"]
                              [:url "https://wjoel.com"]
                              [:email "joel.wilsson@gmail.com"]]])
