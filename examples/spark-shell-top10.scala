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
