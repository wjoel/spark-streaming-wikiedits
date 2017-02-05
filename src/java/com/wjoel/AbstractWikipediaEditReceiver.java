package com.wjoel.spark_streaming_wikiedits;

import org.apache.spark.streaming.receiver.Receiver;
import org.apache.spark.storage.StorageLevel;
import com.wjoel.spark_streaming_wikiedits.edit_event.WikipediaEdit;

public abstract class AbstractWikipediaEditReceiver extends Receiver<WikipediaEdit> {
    public AbstractWikipediaEditReceiver(StorageLevel storageLevel) {
        super(storageLevel);
    }
}
