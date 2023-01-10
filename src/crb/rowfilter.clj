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

(defn valid-contact [row]
  ; at least one way of contact 
  (or (valid-string? row "Emails")
      (valid-string?  row "Mobile")
      (valid-string?  row "Phone numbers")))


#_(def my-cols
    ["Full name"
     "Industry"
     "Job title"
     "Industry 2"])


#_(defn cs [m]
    (some #(= % m) my-cols))


(defn valid-data [row]
         (and  ; name is mandatory
          (not (str/blank? (get row "Full name")))

              ; at least one way of contact 
          (or (not (str/blank? (get row "Emails")))
              (not (str/blank? (get row "Mobile")))
              (not (str/blank? (get row "Phone numbers"))))

          (not (str/blank? (get row "Industry")))))

(defn col-contains [column text row]
  (let [row-text (get row column)
        t (type row-text)]
    (when (and row-text (not (= t String)))
      ;(println "skill type: " (type row-skill))  
      )
    (and row-text
         (= t String)
         (not (str/blank? row-text))
         (.contains row-text text))))


(defn has-skill [skill row]
  (let [row-skill (get row "Skills")
        t (type row-skill)
        ]
    (when (and row-skill (not (= t String)))
      ;(println "skill type: " (type row-skill))  
      )
    (and row-skill
         (= t String)
         (not (str/blank? row-skill))
         (.contains row-skill skill))))


(defn carpet-cleaning [row]
  (and (valid-contact row)
       ;(has-skill "carpet cleaning" row)
       (col-contains "Skills" "carpet cleaning" row)
       ))

; house cleaning
; carpet cleaning
; professional cleaning
; clean rooms
; upholstery cleaning

; oxifresh  oxifresh.com

(defn oxifresh [row]
  (and (valid-contact row)
       ;(has-skill "carpet cleaning" row)
       (col-contains "Emails" "oxifresh.com" row)))

; servpro
; email contains:
; servpro     bbarr.servpro@gmail.com servpro9662 @aol.com
; company name:
; servpro of bath/brunswick
; servpro of metairie and north kenner

(defn servpro [row]
  (and (valid-contact row)
       (col-contains "Company Name" "servpro of" row)))


; servicemaster of baltimore
; servicemaster of baltimore

(defn servicemaster [row]
  (and (valid-contact row)
       (col-contains "Company Name" "servicemaster of" row)))