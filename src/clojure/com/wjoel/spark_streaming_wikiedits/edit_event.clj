(ns com.wjoel.spark-streaming-wikiedits.edit-event)

(defn uppercase-first [s]
  (str (.toUpperCase (subs s 0 1))
       (subs s 1)))

(defn sym->upcase-1-str
  "Converts the first character of a symbol's name to uppercase, returns a string"
  [sym]
  (let [s (name sym)]
    (str (.toUpperCase (subs s 0 1)) (subs s 1))))

(defn tagged-sym
  "Returns a symbol with the given sym-name and :tag in metadata set to tag-value"
  [sym-name tag-value]
  (vary-meta (symbol sym-name) assoc :tag tag-value))

(defn type-and-field->interface-methods
  [[type field-name]]
  (let [sym-name (sym->upcase-1-str field-name)]
    (list
     `(~(tagged-sym (str "get" sym-name) type) [])
     `(~(tagged-sym (str "set" sym-name) 'void) [~(tagged-sym "v" type)]))))

(defn type-and-field->methods
  [[type field-name]]
  (let [sym-name (sym->upcase-1-str field-name)
        _-sym (symbol "_")
        v-sym (symbol "v")]
    (list
     `(~(symbol (str "get" sym-name)) [~_-sym] ~field-name)
     `(~(symbol (str "set" sym-name)) [~_-sym ~v-sym] (set! ~field-name ~v-sym)))))

(defmacro defbean
  "Generates a Java bean. The fields are a set of field type and name."
  [bean-name typed-fields]
  {:pre [(vector? typed-fields)
         (even? (count typed-fields))]}
  (let [types-and-fields (partition 2 typed-fields)
        interface-name (symbol (str "I" (sym->upcase-1-str bean-name)))
        interface-decls (mapcat type-and-field->interface-methods types-and-fields)
        method-decls (mapcat type-and-field->methods types-and-fields)
        typed-fields (for [[type fname] types-and-fields]
                       (vary-meta (symbol fname) merge
                                  {:tag type
                                   :volatile-mutable true}))]
    `(do
       (definterface ~interface-name
         ~@interface-decls)
       (deftype ~bean-name [~@typed-fields]
         ~interface-name
         ~@method-decls
         java.io.Serializable))))

(defbean WikipediaEditEvent
  [Long timestamp
   String channel
   String title
   String diffUrl
   String user
   Integer byteDiff
   String summary
   Boolean isMinor
   Boolean isNew
   Boolean isUnPatrolled
   Boolean isBotEdit
   Boolean isSpecial
   Boolean isTalk])

;; (definterface IWikipediaEditEvent
;;   (^Long getTimestamp [])
;;   (^void setTimestamp [^Long v])
;;   (^String getChannel [])
;;   (^void setChannel [^String v])
;;   (^String getTitle [])
;;   (^void setTitle [^String v])
;;   (^String getDiffUrl [])
;;   (^void setDiffUrl [^String v])
;;   (^String getUser [])
;;   (^void setUser [^String v])
;;   (^Integer getByteDiff [])
;;   (^void setByteDiff [^Integer v])
;;   (^String getSummary [])
;;   (^void setSummary [^String v])
;;   (^Boolean getIsMinor [])
;;   (^void setIsMinor [^Boolean v])
;;   (^Boolean getIsNew [])
;;   (^void setIsNew [^Boolean v])
;;   (^Boolean getIsUnpatrolled [])
;;   (^void setIsUnpatrolled [^Boolean v])
;;   (^Boolean getIsBotEdit [])
;;   (^void setIsBotEdit [^Boolean v])
;;   (^Boolean getIsSpecial [])
;;   (^void setIsSpecial [^Boolean v])
;;   (^Boolean getIsTalk [])
;;   (^void setIsTalk [^Boolean v]))

;; (deftype WikipediaEditEvent
;;     [^{:volatile-mutable true
;;        :tag java.lang.Long} timestamp
;;      ^{:volatile-mutable true
;;        :tag java.lang.String} channel
;;      ^{:volatile-mutable true
;;        :tag java.lang.String} title
;;      ^{:volatile-mutable true
;;        :tag java.lang.String} diffUrl
;;      ^{:volatile-mutable true
;;        :tag java.lang.String} user
;;      ^{:volatile-mutable true
;;        :tag java.lang.Integer} byteDiff
;;      ^{:volatile-mutable true
;;        :tag java.lang.String} summary
;;      ^{:volatile-mutable true
;;        :tag java.lang.Boolean} isMinor
;;      ^{:volatile-mutable true
;;        :tag java.lang.Boolean} isNew
;;      ^{:volatile-mutable true
;;        :tag java.lang.Boolean} isUnpatrolled
;;      ^{:volatile-mutable true
;;        :tag java.lang.Boolean} isBotEdit
;;      ^{:volatile-mutable true
;;        :tag java.lang.Boolean} isSpecial
;;      ^{:volatile-mutable true
;;        :tag java.lang.Boolean} isTalk]
;;   IWikipediaEditEvent
;;   (getTimestamp [_] timestamp)
;;   (setTimestamp [_ v] (set! timestamp v))
;;   (getChannel [_] channel)
;;   (setChannel [_ v] (set! channel v))
;;   (getTitle [_] title)
;;   (setTitle [_ v] (set! title v))
;;   (getDiffUrl [_] diffUrl)
;;   (setDiffUrl [_ v] (set! diffUrl v))
;;   (getUser [_] user)
;;   (setUser [_ v] (set! user v))
;;   (getByteDiff [_] byteDiff)
;;   (setByteDiff [_ v] (set! byteDiff v))
;;   (getSummary [_] summary)
;;   (setSummary [_ v] (set! summary v))
;;   (getIsMinor [_] isMinor)
;;   (setIsMinor [_ v] (set! isMinor v))
;;   (getIsNew [_] isNew)
;;   (setIsNew [_ v] (set! isNew v))
;;   (getIsUnpatrolled [_] isUnpatrolled)
;;   (setIsUnpatrolled [_ v] (set! isUnpatrolled v))
;;   (getIsBotEdit [_] isBotEdit)
;;   (setIsBotEdit [_ v] (set! isBotEdit v))
;;   (getIsSpecial [_] isSpecial)
;;   (setIsSpecial [_ v] (set! isSpecial v))
;;   (getIsTalk [_] isTalk)
;;   (setIsTalk [_ v] (set! isTalk v))
;;   java.io.Serializable)

(gen-class
 :name com.wjoel.spark_streaming_wikiedits.edit_event.EditGenClass
 :implements [java.io.Serializable]
 :init init
 :state state
 :prefix "edit-"
 :constructors {[] []
                [Long
                 String
                 String
                 String
                 String
                 Integer
                 String
                 Boolean
                 Boolean
                 Boolean
                 Boolean
                 Boolean
                 Boolean] []}
 :methods [[getTimestamp [] Long]
           [setTimestamp [Long] void]
           [getChannel [] String]
           [setChannel [String] void]
           [getTitle [] String]
           [setTitle [String] void]
           [getDiffUrl [] String]
           [setDiffUrl [String] void]
           [getUser [] String]
           [setUser [String] void]
           [getByteDiff [] Integer]
           [setByteDiff [Integer] void]
           [getSummary [] String]
           [setSummary [String] void]
           [getIsMinor [] Boolean]
           [setIsMinor [Boolean] void]
           [getIsNew [] Boolean]
           [setIsNew [Boolean] void]
           [getIsUnpatrolled [] Boolean]
           [setIsUnpatrolled [Boolean] void]
           [getIsBotEdit [] Boolean]
           [setIsBotEdit [Boolean] void]
           [getIsSpecial [] Boolean]
           [setIsSpecial [Boolean] void]
           [getIsTalk [] Boolean]
           [setIsTalk [Boolean] void]]
 :main false)

(defn edit-init
  ([] (edit-init 0 nil nil nil nil 0 nil false false false false false false))
  ([timestamp
    channel
    title
    diffUrl
    user
    byteDiff
    summary
    isMinor
    isNew
    isUnpatrolled
    isBotEdit
    isSpecial
    isTalk]
   [[] (into-array Object [timestamp
                           channel
                           title
                           diffUrl
                           user
                           byteDiff
                           summary
                           isMinor
                           isNew
                           isUnpatrolled
                           isBotEdit
                           isSpecial
                           isTalk])]))

(defn edit-getTimestamp [this] (aget (.state this) 0))
(defn edit-setTimestamp [this v] (aset (.state this) 0 v))
(defn edit-getChannel [this] (aget (.state this) 1))
(defn edit-setChannel [this v] (aset (.state this) 1 v))
(defn edit-getTitle [this] (aget (.state this) 2))
(defn edit-setTitle [this v] (aset (.state this) 2 v))
(defn edit-getDiffUrl [this] (aget (.state this) 3))
(defn edit-setDiffUrl [this v] (aset (.state this) 3 v))
(defn edit-getUser [this] (aget (.state this) 4))
(defn edit-setUser [this v] (aset (.state this) 4 v))
(defn edit-getByteDiff [this] (aget (.state this) 5))
(defn edit-setByteDiff [this v] (aset (.state this) 5 v))
(defn edit-getSummary [this] (aget (.state this) 6))
(defn edit-setSummary [this v] (aset (.state this) 6 v))
(defn edit-getIsMinor [this] (aget (.state this) 7))
(defn edit-setIsMinor [this v] (aset (.state this) 7 v))
(defn edit-getIsNew [this] (aget (.state this) 8))
(defn edit-setIsNew [this v] (aset (.state this) 8 v))
(defn edit-getIsUnpatrolled [this] (aget (.state this) 9))
(defn edit-setIsUnpatrolled [this v] (aset (.state this) 9 v))
(defn edit-getIsBotEdit [this] (aget (.state this) 10))
(defn edit-setIsBotEdit [this v] (aset (.state this) 10 v))
(defn edit-getIsSpecial [this] (aget (.state this) 11))
(defn edit-setIsSpecial [this v] (aset (.state this) 11 v))
(defn edit-getIsTalk [this] (aget (.state this) 12))
(defn edit-setIsTalk [this v] (aset (.state this) 12 v))
