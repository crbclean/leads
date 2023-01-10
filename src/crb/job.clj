(ns crb.job
  (:require
   [clojure.string :as str]
   [tech.v3.dataset :as ds]
   [tablecloth.api :as tc]
   [crb.csv.charred :refer [load-csv-state-filtered]]
   [crb.rowfilter]
   [crb.export]
   [crb.states]))

(defn process-csv-state [dir row-filter-fn state]
  (println "processing: " state)
  (try (-> (load-csv-state-filtered row-filter-fn state)
           (crb.export/export-ds-state dir state))
       (catch Exception ex-cause
         (println "EXCEPTION ON STATE: " state "ex: " ex-cause)
         nil)))

(defn create-add-csv-state [dir row-filter-fn]
  (fn [ds-r state]
    (let [ds (process-csv-state dir row-filter-fn state)
          ds-r (if (nil? ds-r)
                 ds
                 (tc/concat ds ds-r))]
      (println "ds-states-all shape: " (tc/shape ds-r))
      (when ds-r
        (crb.export/export-ds-state ds-r dir "all"))
      ds-r)))



(defn combine-states [dir row-filter-fn states]
  (let [ds-states (reduce (create-add-csv-state dir row-filter-fn) nil states)]
    (println "ds states: " (tc/shape ds-states))
    (crb.export/export-ds-state ds-states dir "all")))



(comment

  (process-csv-state "export/carpet-cleaning/"
                     crb.rowfilter/carpet-cleaning
                     "Alabama2")

  (process-csv-state "export/carpet-cleaning/"
                     crb.rowfilter/carpet-cleaning
                     "Alabama")

  (process-csv-state "export/carpet-cleaning/"
                     crb.rowfilter/carpet-cleaning
                     "Maryland") ; tech.v3.dataset.Text


  (process-csv-state "export/carpet-cleaning/"
                     crb.rowfilter/carpet-cleaning
                     "Massachusetts")
  ; Massachusetts
  ; Michigan
  ; Ohio
  ; California
  ; Oklahoma
  ; Execution error at tech.v3.dataset.io.column_parsers.PromotionalStringParser/addValue (column_parsers.clj:419).
  ; Parse failure detected in promotional parser - Please file issue.


  (doall (map (partial process-csv-state crb.rowfilter/carpet-cleaning) crb.states/states))
  (doall (map (partial process-csv-state crb.rowfilter/carpet-cleaning) (reverse crb.states/states-big)))

  ; out of memory: 
  ;  (process-csv-state "Illinois")
  ;  "California"

  (combine-states "export/carpet-cleaning/"
                  crb.rowfilter/carpet-cleaning
                  ["Alabama" "Georgia"])

  (combine-states "export/carpet-cleaning/"
                  crb.rowfilter/carpet-cleaning
                  crb.states/states-all)

  (combine-states "export/oxifresh/"
                  crb.rowfilter/oxifresh
                  crb.states/states-all)

  (combine-states "export/servpro/"
                  crb.rowfilter/servpro
                  crb.states/states-all)








  ;
  )


