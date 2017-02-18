package com.wjoel.spark.streaming.wikiedits;

import org.apache.spark.streaming.receiver.Receiver;
import org.apache.spark.storage.StorageLevel;
import com.wjoel.spark.streaming.wikiedits.WikipediaEditEvent;

public abstract class AbstractWikipediaEditReceiver extends Receiver<WikipediaEditEvent> {
    public AbstractWikipediaEditReceiver(StorageLevel storageLevel) {
        super(storageLevel);
    }
}
