(ns com.wjoel.spark-streaming-wikiedits.edit-event
  (:import [scala Product]))

(definterface IWikipediaEditEvent
  (^String getTitle [])
  (^void setTitle [^String v])
  (^Long getByteDiff [])
  (^void setByteDiff [^Long v]))

;; (gen-class
;;  :name WikipediaEditEvent
;;  :extends [Product]
;;  :implements [IWikipediaEditEvent]
;;  :init init
;;  :state state
;;  :prefix "wiki-"
;;  :constructors {[Long   ; timestamp
;;                  String ; channel
;;                  String ; title
;;                  String ; diffUrl
;;                  String ; user
;;                  Long   ; byteDiff
;;                  String ; Summary
;;                  Boolean ; isMinor
;;                  Boolean ; isNew
;;                  Boolean ; isUnpatrolled
;;                  Boolean ; isBotEdit
;;                  Boolean ; isSpecial
;;                  Boolean ; isTalk
;;                  ] []})

;; (defn wiki-init
;;   [^Long timestamp
;;    ^String channel
;;    ^String title
;;    ^String diffUrl
;;    ^String user
;;    ^Long byteDiff
;;    ^String Summary
;;    ^Boolean isMinor
;;    ^Boolean isNew
;;    ^Boolean isUnpatrolled
;;    ^Boolean isBotEdit
;;    ^Boolean isSpecial
;;    ^Boolean isTalk]
;;   [[] [^Long timestamp
;;        ^String channel
;;        ^String title
;;        ^String diffUrl
;;        ^String user
;;        ^Long byteDiff
;;        ^String Summary
;;        ^Boolean isMinor
;;        ^Boolean isNew
;;        ^Boolean isUnpatrolled
;;        ^Boolean isBotEdit
;;        ^Boolean isSpecial
;;        ^Boolean isTalk]])

;; (defn wiki-productArity [this]
;;   ^Integer (int (count (.state this))))

;; (defn wiki-productElement [this ^Integer n]
;;   (get (.state this) n))

(deftype WikipediaEditEvent
    [^{:volatile-mutable true
       :tag java.lang.Long} timestamp
     ^{:volatile-mutable true
       :tag java.lang.String} channel
     ^{:volatile-mutable true
       :tag java.lang.String} title
     ^{:volatile-mutable true
       :tag java.lang.String} diffUrl
     ^{:volatile-mutable true
       :tag java.lang.String} user
     ^{:volatile-mutable true
       :tag java.lang.Long} byteDiff
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
  (getTitle [_] title)
  (setTitle [_ v] (set! title v))
  (getByteDiff [_] byteDiff)
  (setByteDiff [_ v] (set! byteDiff v))
  java.io.Serializable
  scala.Product
  (productArity [_] 12)
  (productElement [_ n]
    (get [timestamp channel title diffUrl user byteDiff summary
          isMinor isNew isUnpatrolled isBotEdit isSpecial isTalk] n)))
