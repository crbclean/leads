(ns crb.job
  (:require
   [clojure.string :as str]
   [tech.v3.dataset :as ds]
   [tablecloth.api :as tc]
   [crb.convert :refer [load-csv-state]] 
   [crb.rowfilter]
   [crb.export]
   [crb.states]
   ))



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

  (crb.rowfilter/filter-valid-contact ds-csv)

;
  )

(defn process-csv-state [state]
  (println "processing: " state)
  (-> (load-csv-state state)
      (crb.rowfilter/carpet-cleaning)   
      (crb.export/export-ds-state state)))


(defn add-csv-state [r state]
  (let [ds (process-csv-state state)] 
    (if (nil? r) 
      ds
     (tc/concat ds r))))


(defn combine-states [states]
  (let [ds-states (reduce add-csv-state nil states)]
    (println "ds states: " (tc/shape ds-states))
     (crb.export/export-ds-state ds-states "all")
    ))



(comment

  (-> (load-csv-state "Alabama")
      (tc/shape))
  ; [851429 62]

  (process-csv-state "Alabama")
  (process-csv-state "Georgia")
  
  (combine-states ["Alabama" "Georgia"])
    (combine-states crb.states/states)

  
  (doall (map process-csv-state crb.states/states))

  (doall (map process-csv-state (reverse crb.states/states-big)))

  ; out of memory: 
;  (process-csv-state "Illinois")
;  "California"




  ;
  )


