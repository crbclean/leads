(ns crb.export
  (:require
   [clojure.string :as str]
   [tech.v3.dataset :as ds]
   [tablecloth.api :as tc]))

(defn in-cols [my-cols]
  (fn [m]
    (some #(= % m) my-cols)))


(defn drop-columns [ds-csv]
  (tc/drop-columns
   ds-csv
   (in-cols ["Countries"
             "Interests"
             "Company Size"
             "Company Founded"
             "Company Location Country"
             "Company Location Continent"
             "Location"
             "Location Country"
             "Location Continent"
             "Locality"
             "Street Address"
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
             "Company Location Name"
             "Twitter Username"
             "Github Url"
             "Github Username"
             "Company Location Locality"
             "Company Location Metro"
             "Last Updated"
             "Start Date"
             "Linkedin Connections"
             "Sub Role"
             "Industry"])
   :name))


(defn print-ds [ds-csv]
  (let [[rows cols] (tc/shape ds-csv)]
    (tc/print-dataset ds-csv {:print-index-range rows})))



(defn last-email [emails-cs]
  ;(println "last-email for: " emails-cs)
  (-> (str/split emails-cs #",")
      last))

(defn single-email [col]
  (map last-email col))


(defn mailgun-file-ds [ds-csv state]
  (let [ds-csv-with-emails
        (tc/select-rows
         ds-csv
         (fn [row]
           (not (str/blank? (get row "Emails")))))
        col-emails (get ds-csv-with-emails "Emails")]
    (-> ds-csv-with-emails
        (tc/select-columns ["Full name" "Emails"])
        (tc/add-column "Emails" (single-email col-emails))
        (ds/write! (str "mailgun/" state ".csv")
                   {:headers? false}))))


(defn export-ds-state [ds-csv state]
  (let [ds-cols-ok (drop-columns ds-csv)
        t (with-out-str (print-ds ds-cols-ok))]
    (spit (str "txt/" state ".txt") t)
    (ds/write! ds-cols-ok (str "csv/" state ".csv"))
    (mailgun-file-ds ds-csv state)
    ds-cols-ok))
