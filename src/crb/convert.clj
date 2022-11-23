(ns crb.convert
  (:require
   [clojure.string :as str]
   [tech.v3.dataset :as ds]
   [tech.v3.dataset.io.string-row-parser]
   [tablecloth.api :as tc]
   )
  (:import  [tech.v3.datatype ObjectReader]
            [com.univocity.parsers.common AbstractParser AbstractWriter CommonSettings]
            [com.univocity.parsers.csv CsvParserSettings CsvParser CsvWriterSettings CsvWriter]
            [com.univocity.parsers.tsv
             TsvWriterSettings TsvWriter]
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
                 (.setNormalizedNewline \*)
                
                 )
        parser (CsvParser. settings)]
    parser))


(def col-names ["Full name" 
                "Industry" 
                "Job title" 
                "Sub Role" 
                "Industry 2" 
                "Emails" 
                "Mobile" 
                "Phone numbers"
                "Company Name"
                "Company Industry" 
                "Company Website"
                "Company Size" 
                "Company Founded"
                "Location" 
                "Locality" 
                "Metro" 
                "Region" 
                "Skills" 
                "First Name" 
                "Middle Initial" 
                "Middle Name" 
                "Last Name" 
                "Birth Year" 
                "Birth Date" 
                "Gender" 
                "LinkedIn Url" 
                "LinkedIn Username" 
                "Facebook Url" 
                "Facebook Username" 
                "Twitter Url" 
                "Twitter Username" 
                "Github Url" 
                "Github Username"
                "Company Linkedin Url" 
                "Company Facebook Url"
                "Company Twitter Url" 
                "Company Location Name" 
                "Company Location Locality" 
                "Company Location Metro" 
                "Company Location Region"
                "Company Location Geo" 
                "Company Location Street Address"
                "Company Location Address Line 2"
                "Company Location Postal Code"
                "Company Location Country" 
                "Company Location Continent" 
                "Last Updated" 
                "Start Date" 
                "Job Summary" 
                "Location Country" 
                "Location Continent"
                "Street Address"
                "Address Line 2"
                "Postal Code" 
                "Location Geo" 
                "Last Updated" 
                "Linkedin Connections" 
                "Inferred Salary" 
                "Years Experience" 
                "Summary" 
                "Countries" 
                "Interests"
                ])


; |          column-0 |                     column-1 |                             column-2 |          column-3 |    column-4 |                                                column-5 | column-6 |                   column-7 |                                     column-8 |               column-9 |              column-10 |    column-11 |       column-12 |                           column-13 |   column-14 |           column-15 | column-16 |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         column-17 |  column-18 |      column-19 |   column-20 |  column-21 |  column-22 |  column-23 | column-24 |                                     column-25 |                     column-26 |                      column-27 |         column-28 |   column-29 |        column-30 |  column-31 |       column-32 |                                                         column-33 |              column-34 |                   column-35 |                                       column-36 |                 column-37 |              column-38 |               column-39 |            column-40 |                                 column-41 |                       column-42 |                    column-43 |                column-44 |                  column-45 |    column-46 |  column-47 |                                                                                                                                                                                                                                                                                                                                                                column-48 |        column-49 |          column-50 |                  column-51 |      column-52 |   column-53 |    column-54 |    column-55 |            column-56 |       column-57 |        column-58 |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      column-59 |                     column-60 |                                                                                                                                              column-61 |
(map-indexed (fn [i n] 
               [(str "column-" i) n])
             col-names)


(defn load-csv [filename]
  (let [file (File. filename)
        ;fis (FileInputStream. file)
        ;isr (InputStreamReader. fis)
        parser (make-parser)
        ;r (.parseAll parser isr "UTF-8")
        r (.parseAll parser file "UTF-8")
        ds-csv (tech.v3.dataset.io.string-row-parser/rows->dataset {:header-row? false
                                                                    :skip-bad-rows? false}
                                                                   r)
        ]
     ;(doall (map (fn [row] (println "ROW: " (Arrays/toString row))) r))
     ;(count r)
     
    (tc/rename-columns ds-csv (into {}
                         (map-indexed (fn [i n]
                                        [(str "column-" i) n])
                                         col-names)))
  ))


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
    :csv-parser (make-parser)
    }))

 
csv-data
(meta csv-data)

(ds/shape csv-data)

(ds/head csv-data)
; comment
)

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



(-> ds-csv
    ;(valid-data)
     (tc/head  49)   
    ;(tc/shape)
    )


(def ds-csv 
    ;(load-csv "Alabama2.csv")
    (load-csv "raw/Alabama/Alabama.csv"))


(->  ds-csv
    (valid-data)
    (tc/group-by (fn [row]
                   ;(get row "Industry")
                   (get row "Industry 2")
                   ))
    (tc/aggregate #(count (get % "Full name")))
    ;(tc/order-by [:symbol :years)
    ;(tc/head 100)
    ;(tc/shape)
    (tc/print-dataset {:print-index-range 100}))

Summary
Inferred Salary |
Years Experience

Industry

Sub Role