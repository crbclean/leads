(ns crb.job
  (:require
   [clojure.string :as str]
   [tech.v3.dataset :as ds]
   [tablecloth.api :as tc]
   [crb.csv.charred :refer [load-csv-state-filtered]]
   [crb.rowfilter]
   [crb.export]
   [crb.states]))

(defn process-csv-state [row-filter-fn state]
  (println "processing: " state)
  (-> (load-csv-state-filtered row-filter-fn state)
      (crb.export/export-ds-state state)
      ))

(defn create-add-csv-state [row-filter-fn]
  (fn [r state]
    (let [ds (process-csv-state row-filter-fn state)
          r (if (nil? r)
              ds
              (tc/concat ds r))]
      (println "ds states: " (tc/shape r))
      (crb.export/export-ds-state r "all")
      r)))



(defn combine-states [row-filter-fn states]
  (let [ds-states (reduce (create-add-csv-state row-filter-fn) nil states)]
    (println "ds states: " (tc/shape ds-states))
    (crb.export/export-ds-state ds-states "all")))



(comment
 
  (process-csv-state crb.rowfilter/carpet-cleaning "Alabama")
  (process-csv-state crb.rowfilter/carpet-cleaning "Georgia")

  (doall (map (partial process-csv-state crb.rowfilter/carpet-cleaning) crb.states/states))
  (doall (map (partial process-csv-state crb.rowfilter/carpet-cleaning) (reverse crb.states/states-big)))

  ; out of memory: 
  ;  (process-csv-state "Illinois")
  ;  "California"
  
  (combine-states crb.rowfilter/carpet-cleaning ["Alabama" "Georgia"])
  (combine-states crb.rowfilter/carpet-cleaning crb.states/states)







  ;
  )


