(ns crb.csv.charred
  (:require
   [clojure.string :as str]
   [tech.v3.dataset :as ds]
   [tech.v3.dataset.io.string-row-parser]
   [tech.v3.dataset.io.csv :as tech-csv]
   [tablecloth.api :as tc]))

(defn load-csv-batched [filename]
  (let [s (tech-csv/csv->dataset-seq filename {;:separator ,.
                                               :quote \"
                                               :escape \\})]
    s))

(defn filter-batched [ds-seq filter-fn]
  (reduce (fn [ds-r ds-batch]
            (let [ds-batch-filtered (tc/select-rows ds-batch filter-fn)]
              (if ds-r
                (ds/concat ds-r ds-batch-filtered)
                ds-batch-filtered)))
          nil ds-seq))


(defn load-csv-state-filtered [row-filter-fn state]
  (let [filename-state (str "raw/" state "/" state ".csv")]
    (-> (load-csv-batched filename-state)
        (filter-batched row-filter-fn))))


(comment

  (defn filter-male [r]
    (let [gender (get r "Gender")]
      (println "gender: " gender)
      (and (not (nil? gender))
           (not (str/blank? gender))
           (= gender "male"))))

  (-> (load-csv-batched "Alabama2.csv")
    ;(filter-batched (fn [c] true))
      (filter-batched filter-male)
      (tc/select-columns ["Full name" "Gender"]))

  (load-csv-state-filtered (fn [c] true) "Alabama")

;  
  )






