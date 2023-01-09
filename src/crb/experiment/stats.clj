(ns crb.experiment.stats
  (:require
   [clojure.string :as str]
   [tech.v3.dataset :as ds]
   [tablecloth.api :as tc]
   [crb.csv.charred :refer [load-csv-state]]
   [crb.rowfilter]
   [crb.export]))



(defn stats-for [ds-csv field]
  (->  ds-csv
       ;(valid-data)
       (tc/group-by (fn [row]
                      (get row field)))
       (tc/aggregate #(count (get % "Full name")))
       (tc/print-dataset {:print-index-range 1000})))


#_(->  ds-csv
       (crb.rowfilter/valid-data)
       (tc/group-by (fn [row]
                   ;(get row "Industry")
                      (get row "Industry 2")))
       (tc/aggregate #(count (get % "Full name")))
    ;(tc/order-by [:symbol :years)
    ;(tc/head 100)
    ;(tc/shape)
       (tc/print-dataset {:print-index-range 100}))

(comment

  (def ds-csv
    (load-csv-state "Alabama"))

  (stats-for ds-csv "Industry")
  (stats-for ds-csv "Industry 2")
  (stats-for ds-csv "Sub Role")
  (stats-for ds-csv "Skills")


  ;
  )

