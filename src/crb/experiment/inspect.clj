(ns crb.experiment.inspect)


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