
(require '[babashka.curl :as curl])
(require '[cheshire.core :as json]) 
(require '[clojure.pprint :as pp])

; https://documentation.mailgun.com/en/latest/api-events.html#examples


(def api-key (System/getenv "MAILGUN_API_KEY"))

(println "api key: " api-key)

(defn max-l [s]
  (if (> (count s) 40)
    (subs s 0 40)
    s))

(defn request-params [domain endpoint query-params] 
  (-> (curl/get (str "https://api.mailgun.net/v3/" domain "/" endpoint)
                (merge query-params 
                       {:basic-auth ["user" api-key]
                        :headers {"Accept" "application/json"}}
                       ))
      :body
      (json/parse-string true)))

(defn request 
  ([domain endpoint]
   (request-params domain endpoint {}))
  ([domain endpoint query-params]
   (request-params domain endpoint {:query-params query-params})))



(def data (request "marketing.crbclean.com" "bounces"))

(defn convert [data]
    ;(:items data)
  (->> (:items data)
       (map #(assoc % :address (max-l (:address %))))
       (sort-by :created_at)
       ))

#_(pp/print-table [:created_at
                 :address 
                 :code 
                 ;:error
                 ;:MessageHash
                 ]
                (convert data))


; https://documentation.mailgun.com/en/latest/api-stats.html#examples

(->> (request "crbclean.com" "stats/total" {"event" "accepted"
                                            "duration" "24h"})
     :stats 
     (pp/print-table)
 )


#_(->> (request "crbclean.com" "messages" {"from" "<sebastiang@crbclean.com>"})
     ;:stats
     ;(pp/print-table)
     )

(defn from-unix-time
  "Return a Java Date object from a Unix time representation expressed
  in whole seconds."
  [unix-time]
  (java.util.Date. unix-time))

(defn iso-date
  []
  (-> (java.time.LocalDateTime/now)
      (.format (java.time.format.DateTimeFormatter/ISO_LOCAL_DATE))))

(defn to-epoch-sec [dt]
  (-> dt
  ;(.atZone  (java.time.ZoneId/systemDefault))
  ;(.toInstant)
  ;(.toEpochMilli)    
   (.toEpochSecond (java.time.ZoneOffset/UTC))

   ))
  
 


(->> (request "crbclean.com" "events" {"event" "accepted"
                                       "begin" (-> (java.time.LocalDateTime/now)
                                                   (to-epoch-sec) 
                                                   (- (* 60 60 2))
                                                   )
                                       "ascending" "yes"
                                      ;"from" "sebastiang@crbclean.com"
                                       ;"limit" "20"
                                       })
     :items
     (remove #(= "crb.clean@gmail.com" (:recipient %)))
     (remove #(= "sebastiangstrongindustriescorp@gmail.com" (:recipient %)))
     (map #(assoc % :timestamp  (from-unix-time (long (* 1000 (:timestamp %))))))
     (map #(assoc % :recipient (max-l (:recipient %))))
     
     (pp/print-table  [:timestamp
                       ;:method
                       ; :event
                       ;:originating-ip
                       :recipient
                       ; :id
                       ]
                      )
     )




 ;:envelope {:sender "", 
 ;           :transport "smtp", 
 ;           :targets "michelene.phillips@jondon.com"}, 
; :storage {:url "https://storage-us-west1.api.mailgun.net/v3/domains/crbclean.com/messages/AwABBd2l87Zafdcqwz1EkITGo_G_2RclYg==", 
;           :region "us-west1",
;           :env "production", 
;           :key "AwABBd2l87Zafdcqwz1EkITGo_G_2RclYg=="}, 
; :message {:headers {:to "michelene.phillips@jondon.com", 
;                     :message-id "20230202000629.DB14640AD3@box.hoertlehner.com", 
;                     :from "MAILER-DAEMON@box.hoertlehner.com (Mail Delivery System)",
;                     :subject "Delayed Mail (still being retried)"}
;           , :size 7087}}


(->  (java.time.LocalDateTime/now)
 to-epoch-sec
 )

