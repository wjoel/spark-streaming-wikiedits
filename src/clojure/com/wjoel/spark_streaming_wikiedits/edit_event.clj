(ns com.wjoel.spark-streaming-wikiedits.edit-event
  (:import [scala Product]))

(definterface IWikipediaEditEvent
  (^Integer getTimestamp [])
  (^void setTimestamp [^Integer v])
  (^String getChannel [])
  (^void setChannel [^String v])
  (^String getTitle [])
  (^void setTitle [^String v])
  (^String getDiffUrl [])
  (^void setDiffUrl [^String v])
  (^String getUser [])
  (^void setUser [^String v])
  (^Integer getByteDiff [])
  (^void setByteDiff [^Integer v])
  (^String getSummary [])
  (^void setSummary [^String v])
  (^Boolean getIsMinor [])
  (^void setIsMinor [^Boolean v])
  (^Boolean getIsNew [])
  (^void setIsNew [^Boolean v])
  (^Boolean getIsUnpatrolled [])
  (^void setIsUnpatrolled [^Boolean v])
  (^Boolean getIsBotEdit [])
  (^void setIsBotEdit [^Boolean v])
  (^Boolean getIsSpecial [])
  (^void setIsSpecial [^Boolean v])
  (^Boolean getIsTalk [])
  (^void setIsTalk [^Boolean v]))

(deftype WikipediaEditEvent
    [^{:volatile-mutable true
       :tag java.lang.Integer} timestamp
     ^{:volatile-mutable true
       :tag java.lang.String} channel
     ^{:volatile-mutable true
       :tag java.lang.String} title
     ^{:volatile-mutable true
       :tag java.lang.String} diffUrl
     ^{:volatile-mutable true
       :tag java.lang.String} user
     ^{:volatile-mutable true
       :tag java.lang.Integer} byteDiff
     ^{:volatile-mutable true
       :tag java.lang.String} summary
     ^{:volatile-mutable true
       :tag java.lang.Boolean} isMinor
     ^{:volatile-mutable true
       :tag java.lang.Boolean} isNew
     ^{:volatile-mutable true
       :tag java.lang.Boolean} isUnpatrolled
     ^{:volatile-mutable true
       :tag java.lang.Boolean} isBotEdit
     ^{:volatile-mutable true
       :tag java.lang.Boolean} isSpecial
     ^{:volatile-mutable true
       :tag java.lang.Boolean} isTalk]
  IWikipediaEditEvent
  (getTimestamp [_] timestamp)
  (setTimestamp [_ v] (set! timestamp v))
  (getChannel [_] channel)
  (setChannel [_ v] (set! channel v))
  (getTitle [_] title)
  (setTitle [_ v] (set! title v))
  (getDiffUrl [_] diffUrl)
  (setDiffUrl [_ v] (set! diffUrl v))
  (getUser [_] user)
  (setUser [_ v] (set! user v))
  (getByteDiff [_] byteDiff)
  (setByteDiff [_ v] (set! byteDiff v))
  (getSummary [_] summary)
  (setSummary [_ v] (set! summary v))
  (getIsMinor [_] isMinor)
  (setIsMinor [_ v] (set! isMinor v))
  (getIsNew [_] isNew)
  (setIsNew [_ v] (set! isNew v))
  (getIsUnpatrolled [_] isUnpatrolled)
  (setIsUnpatrolled [_ v] (set! isUnpatrolled v))
  (getIsBotEdit [_] isBotEdit)
  (setIsBotEdit [_ v] (set! isBotEdit v))
  (getIsSpecial [_] isSpecial)
  (setIsSpecial [_ v] (set! isSpecial v))
  (getIsTalk [_] isTalk)
  (setIsTalk [_ v] (set! isTalk v))
  java.io.Serializable)
