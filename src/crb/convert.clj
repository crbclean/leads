(ns crb.convert
  (:require
   [clojure.string :as str]
   [tech.v3.dataset :as ds]
   ;[tablecloth.api :as tc]
   )
  (:import  [tech.v3.datatype ObjectReader]
            [com.univocity.parsers.common AbstractParser AbstractWriter CommonSettings]
            [com.univocity.parsers.csv CsvParserSettings CsvParser CsvWriterSettings CsvWriter]
            [com.univocity.parsers.tsv
             TsvWriterSettings TsvWriter]
            [java.io Reader Closeable Writer InputStreamReader File InputStream FileInputStream]
            [java.util List]
            [java.lang AutoCloseable]))




(defn- ->character
  [item]
  (cond
    (instance? Character item)
    item
    (string? item)
    (if (== 1 (count item))
      (first item)
      (throw (Exception.
              (format "Multicharacter separators (%s) are not supported." item))))
    :else
    (throw (Exception. (format "'%s' is not a valid separator" item)))))


(defn make-parser []
  (let [settings (CsvParserSettings.)
        settings (doto settings
                   (.setMaxCharsPerColumn (long 50000))
                   (.setParseUnescapedQuotes false)
                   (.setParseUnescapedQuotesUntilDelimiter false)
                   (.setNormalizeLineEndingsWithinQuotes false)
                   (.detectFormatAutomatically 
                                    (into-array Character/TYPE
                                                [(->character ",")]))
                 ;(.setRecordEndsOnNewline false)
                   )
        format (doto (.getFormat settings)
                 (.setLineSeparator "\n")
                
                 )
        parser (CsvParser. settings)]
    parser))


(let [file (File. "raw/Alabama/Alabama2.csv")
      fis (FileInputStream. file)
      isr (InputStreamReader. fis)
      parser (make-parser)
      r (.parseAll parser isr)
      ]
     (count r)
  
  )


(def csv-data
  (ds/->dataset
   "Alabama2.csv"
    ;"raw/Alabama/Alabama2.csv"
   ;"https://gist.githubusercontent.com/awb99/c52fb78036d03ebd9cd2dcae3883d200/raw/3530d77761a4feaffc087f5be1fa1938db09e5e0/data.csv"
  ; "data.csv"
   {:header-row? true
    :file-type :csv
  ;  :disable-comment-skipping? true
    ;:max-chars-per-column 60000
   ; :separator ","
   ; :csv-parser (make-parser)
    }))


  
csv-data
(meta csv-data)

(ds/shape csv-data)


; 62 21




 

(ds/head csv-data)

(def my-cols
  ["Full name"
   "Industry"
   ;"Job title"
   ])

(defn cs [m]
  (some #(= % m) my-cols))

(cs "a")
(cs "Full name")

(defn valid-data [ds]
  (-> ds
      (tc/select-columns cs :name)
      (tc/select-rows
       (fn [row]
         (and (not (str/blank? (get row "Full name")))
              (not (str/blank? (get row "Industry"))))))))


(-> csv-data
    ;(valid-data)
     (tc/head  49)   
    ;(tc/shape)
    )



(-> csv-data
    (valid-data)
    (tc/group-by (fn [row]
                   (get row "Industry")))
    (tc/aggregate #(count (get % "Full name")))
    ;(tc/order-by [:symbol :year])
    ;(tc/head 100)
    ;(tc/shape)
    (tc/print-dataset {:print-index-range 100}))

Summary
Inferred Salary |
Years Experience

Industry

Sub Role