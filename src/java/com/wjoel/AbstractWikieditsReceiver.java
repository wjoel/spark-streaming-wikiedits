package com.wjoel.spark_streaming_wikiedits;

import org.apache.spark.streaming.receiver.Receiver;
import org.apache.spark.storage.StorageLevel;
import com.wjoel.spark_streaming_wikiedits.edit_event.WikipediaEditEvent;

public abstract class AbstractWikieditsReceiver extends Receiver<WikipediaEditEvent> {
    public AbstractWikieditsReceiver(StorageLevel storageLevel) {
        super(storageLevel);
    }
}