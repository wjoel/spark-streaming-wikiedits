# spark-streaming-wikiedits

A Spark Streaming custom receiver for Wikipedia edits.

Available in [The Central Repository](https://search.maven.org/#artifactdetails%7Ccom.wjoel%7Cspark-streaming-wikiedits%7C0.1.3%7Cjar) as `com.wjoel:spark-streaming-wikiedits:0.1.3`

## Usage

Start a `spark-shell`

```shell
./bin/spark-shell --master local[4] \
  --packages "org.clojure:clojure:1.8.0,\
org.schwering:irclib:1.10,\
com.wjoel:clj-bean:0.2.0,\
com.wjoel:spark-streaming-wikiedits:0.1.3"
```

... and run the following (also available in the examples directory).

```scala
import org.apache.spark.streaming._
import org.apache.spark.sql.functions._
import com.wjoel.spark.streaming.wikiedits._

implicit val encoder = org.apache.spark.sql.Encoders.bean(classOf[WikipediaEditEvent])
val ssc = new org.apache.spark.streaming.StreamingContext(spark.sparkContext, Seconds(5))

ssc.receiverStream(new WikipediaEditReceiver()).
  window(Seconds(20)).
  filter { editEvent =>
    !editEvent.getTitle.contains(":")
  } foreachRDD { rdd =>
    spark.createDataset(rdd).
      groupBy($"title").
      agg(sum($"byteDiff") as "sumByteDiff").
      orderBy(abs($"sumByteDiff").desc).
      limit(10).
      show()
  }
ssc.start()
```

## License

Copyright © 2017 Joel Wilsson

Distributed under the MIT License.
