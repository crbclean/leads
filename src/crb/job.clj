(ns crb.job
  (:require
   [clojure.string :as str]
   [tech.v3.dataset :as ds]
   [tablecloth.api :as tc]
   [crb.convert :refer [load-csv]]))

(def my-cols
  ["Full name"
   "Industry"
   "Job title"
   "Industry 2"])

(defn cs [m]
  (some #(= % m) my-cols))

(defn in-cols [my-cols]
  (fn [m]
    (some #(= % m) my-cols)))

(defn valid-data [ds]
  (-> ds
      ;(tc/select-columns cs :name)
      (tc/select-rows
       (fn [row]
         (and  ; name is mandatory
          (not (str/blank? (get row "Full name")))

              ; at least one way of contact 
          (or (not (str/blank? (get row "Emails")))
              (not (str/blank? (get row "Mobile")))
              (not (str/blank? (get row "Phone numbers"))))

          (not (str/blank? (get row "Industry"))))))))

(defn valid-string? [row field]
  (when-let [s (get row field)]
    ;(println "s: " s)
    (when (clojure.core/string? s)
      (not (str/blank? s)))))

(defn filter-valid-contact [ds]
  (tc/select-rows ds
                  (fn [row]
      ; at least one way of contact 
                    (or (valid-string? row "Emails")
                        (valid-string?  row "Mobile")
                        (valid-string?  row "Phone numbers")))))

(defn stats-for [ds-csv field]
  (->  ds-csv
       ;(valid-data)
       (tc/group-by (fn [row]
                      (get row field)))
       (tc/aggregate #(count (get % "Full name")))
       (tc/print-dataset {:print-index-range 1000})))

(comment

  (cs "a")
  (cs "Full name")

  (def ds-csv
    ;(load-csv "Alabama2.csv")
    (load-csv "raw/Alabama/Alabama.csv"))

  (-> ds-csv
    ;(valid-data)
      (tc/head  49)
    ;(tc/shape)
      )

  (filter-valid-contact ds-csv)

  (stats-for ds-csv "Industry")
  (stats-for ds-csv "Industry 2")
  (stats-for ds-csv "Sub Role")
  (stats-for ds-csv "Skills")

;
  )

(defn carpet-cleaning [ds-csv]
  (-> ds-csv
      (tc/select-rows
       (fn [row]
         (and (not (str/blank? (get row "Skills")))
              (.contains (get row "Skills") "carpet cleaning"))))
      (filter-valid-contact)))

; house cleaning
; carpet cleaning
; professional cleaning
; clean rooms
; upholstery cleaning


(defn print-ds [ds-csv]
  (let [[rows cols] (tc/shape ds-csv)]
    (-> ds-csv
        (tc/drop-columns  (in-cols ["Countries"
                                    "Interests"
                                    "Company Size"
                                    "Company Founded"
                                    "Company Location Country"
                                    "Company Location Continent"
                                    "Location"
                                    "Location Country"
                                    "Location Continent"
                                    "Locality"
                                    "Region"
                                    "LinkedIn Url"
                                    "Middle Initial"
                                    "Middle Name"
                                    "Last Name"
                                    "Birth Year"
                                    "Birth Date"
                                    "Gender"
                                    "Company Location Address Line 2"
                                    "Company Location Postal Code"
                                    "Address Line 2"
                                    "Postal Code"
                                    "Location Geo"
                                    "Inferred Salary"
                                    "Years Experience"
                                    "Summary"
                                    "Skills"
                                    "First Name"
                                    "Facebook Url"
                                    "Twitter Url"
                                    "Job Summary"
                                    "Company Linkedin Url"
                                    "Company Facebook Url"
                                    "Company Twitter Url"
                                    "Company Location Region"
                                    "Company Location Geo"
                                    "Company Location Street Address"
                                    "Twitter Username"
                                    "Github Url"
                                    "Github Username"
                                    "Company Location Locality"
                                    "Company Location Metro"
                                    "Last Updated"
                                    "Start Date"
                                    "Linkedin Connections"
                                    "Sub Role"
                                    "Industry"]) :name)
        (tc/print-dataset {:print-index-range rows}))))


(defn print-file-ds [ds-csv state]
  (let [t (with-out-str (print-ds ds-csv))]
    (spit (str "txt/" state ".txt") t)))


(defn load-csv-state [state]
  (load-csv (str "raw/" state "/" state ".csv")))


(defn process-csv-state [state]
  (println "processing: " state)
  (-> (load-csv-state state)
      (carpet-cleaning)
      (print-file-ds state)))

(def states-big 
  ["California"
   "NewYork"
   "Texas"
   "Florida"
   "Illinois"
   "Pennsylvania"
   "Georgia"
   "Ohio"
                 ])

(def states ["Alabama"
             "Alaska"
             "Arizona"
             "Arkansas"
  
             "Colorado"
             "Connecticut"
  
             "Indiana"
             "Iowa"
             "Kansas"
             "Kentucky"
             "Louisiana"
             "Maine"
             "Maryland"
             "Massachusetts"
             "Michigan"
             "Nebraska"
             "SouthCarolina"
             "Nevada"
             "SouthDakota"
             "NewHampshire"
             "NewJersey"
             "Tennessee"
             "NewMexico"

             "NorthCarolina"
             "Utah"
             "NorthDakota"
             "Virginia"
        
             "Minnesota"
             "Oklahoma"
            
             "Missouri"
             "Montana"
             "RhodeIsland"
             "Oregon"
             "Mississippi"
             "Washington"
            
             "WestVirginia"
             "Hawaii"
             "Idaho"
             "Wisconsin"
             "Wyoming"
             ])




(comment

  (-> (load-csv-state "Alabama")
      (tc/shape))
  ; [851429 62]

  (-> (load-csv-state "Alabama")
      (carpet-cleaning)
      (print-ds))

  (process-csv-state "Alabama")
  (process-csv-state "Georgia")

  (doall (map process-csv-state states))

  ; out of memory: 
;  (process-csv-state "Illinois")
;  "California"


  (->  ds-csv
       (valid-data)
       (tc/group-by (fn [row]
                   ;(get row "Industry")
                      (get row "Industry 2")))
       (tc/aggregate #(count (get % "Full name")))
    ;(tc/order-by [:symbol :years)
    ;(tc/head 100)
    ;(tc/shape)
       (tc/print-dataset {:print-index-range 100}))



  ;
  )


