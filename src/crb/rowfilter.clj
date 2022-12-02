(ns crb.rowfilter
  (:require
   [clojure.string :as str]
   [tech.v3.dataset :as ds]
   [tablecloth.api :as tc]))

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


#_(def my-cols
  ["Full name"
   "Industry"
   "Job title"
   "Industry 2"])


#_(defn cs [m]
  (some #(= % m) my-cols))


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
