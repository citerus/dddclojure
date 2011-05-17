(ns se.citerus.dddclojure.mongodb.order-repository
  (:use [somnium.congomongo]))

(mongo! :db "dddclojure")

(defn store! [order]
  (insert! :orders order))

(defn find-order [order-id] 
  (fetch-one
    :orders
    :where {:number order-id}))