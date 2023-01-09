(ns crb.csv.univocity
  (:require
   [clojure.string :as str]
   [tech.v3.dataset :as ds]
   [tech.v3.dataset.io.string-row-parser]
   [tech.v3.dataset.io.csv :as tech-csv]
   ;[tablecloth.api :as tc]
   )
  (:import  [tech.v3.datatype ObjectReader]
            ;[com.univocity.parsers.common AbstractParser AbstractWriter CommonSettings]
            ;[com.univocity.parsers.csv CsvParserSettings CsvParser CsvWriterSettings CsvWriter]
            ;[com.univocity.parsers.tsv TsvWriterSettings TsvWriter]
            [java.io Reader Closeable Writer InputStreamReader File InputStream FileInputStream]
            [java.util List Arrays]
            [java.lang AutoCloseable]))

; stolen from techml
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

(comment
  (->character "r")
  ;
  )




(defn make-parser []
  (let [settings (CsvParserSettings.)
        settings (doto settings
                   (.setMaxCharsPerColumn (long 50000))

                   (.setParseUnescapedQuotes false)
                   (.setParseUnescapedQuotesUntilDelimiter false)
                   ;(.setNormalizeLineEndingsWithinQuotes true)
                   #_(.detectFormatAutomatically
                      (into-array Character/TYPE
                                  [(->character ",")]))
                ; (.setRecordEndsOnNewline false)
                   )
        format (doto (.getFormat settings)
                 (.setLineSeparator "\n")
                 (.setDelimiter ",")
                 (.setQuote \")
                 (.setQuoteEscape \\)
                 (.setNormalizedNewline \*))
        parser (CsvParser. settings)]
    parser))

(defn load-csv [filename]
  (let [file (File. filename)
        ;fis (FileInputStream. file)
        ;isr (InputStreamReader. fis)
        parser (make-parser)
        ;r (.parseAll parser isr "UTF-8")
        r (.parseAll parser file "UTF-8")
        ds-csv (tech.v3.dataset.io.string-row-parser/rows->dataset {:header-row? true
                                                                    :skip-bad-rows? false}
                                                                   r)]
     ;(doall (map (fn [row] (println "ROW: " (Arrays/toString row))) r))
     ;(count r)
    ds-csv))

(comment

  (load-csv "Alabama2.csv")

  (def csv-data
    (ds/->dataset
     "Alabama2.csv"
   ; "raw/Alabama/Alabama.csv"
   ;"https://gist.githubusercontent.com/awb99/c52fb78036d03ebd9cd2dcae3883d200/raw/3530d77761a4feaffc087f5be1fa1938db09e5e0/data.csv"
  ; "data.csv"
     {:header-row? true
      :file-type :csv
  ;  :disable-comment-skipping? true
    ;:max-chars-per-column 60000
   ; :separator ","
      :csv-parser (make-parser)}))


  csv-data
  (meta csv-data)

  (ds/shape csv-data)

  (ds/head csv-data)
; comment
  )
