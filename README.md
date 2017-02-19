# spark-streaming-wikiedits

A Spark Streaming custom receiver for Wikipedia edits.

Maven coordinates: `com.wjoel:spark-streaming-wikiedits:0.0.1`

## Usage

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

Copyright © 2016 Joel Wilsson

Distributed under the MIT License.
