(ns crb.test)



(defn s->char [s]
  (.charAt s 0))

(defn s->int [s]
  (int (.charAt s 0)))

(def words
  (into #{}
        (concat
         (range (s->int "a") (inc (s->int "z")))
         (range (s->int "A") (inc (s->int "Z")))
         (range (s->int "0") (inc (s->int "9")))
         [(s->int "\"") (s->int "\\") (s->int "_")
          (s->int " ")  (s->int "(") (s->int ")")
          (s->int ",") (s->int ".") (s->int ":") (s->int ";")
          (s->int "+") (s->int "-") (s->int "/")  (s->int "*")
          (s->int "&")

          (s->int "@")])))



(int (s->char "A"))
(int (s->char "\n"))

(->> (slurp "raw/Alabama/Alabama2.csv")
     (map char)
     (take 1000000)
     (map int)
     (remove #(contains? words %))
     ;(into #{})
   ;  (map char)
     (map (fn [c] [c (char c)]))
     (frequencies)
   ;
     )
