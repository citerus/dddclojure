(ns se.citerus.dddclojure.order-scenario-db-test
  (:use
    [se.citerus.dddclojure.domain.order
     :only (create-order add-item)]
    [se.citerus.dddclojure.mongodb.order-repository]
    [midje.sweet])
  (:require
    [se.citerus.dddclojure.domain.order :as o])
  (:import
    [se.citerus.dddclojure.domain.order PurchaseOrder LineItem LineProduct]
    [java.util Date]))

(def now (Date.))

(def order-number 1000)

(comment ;Requires a running mongo-db instance
  (facts "Order scenario db test"

    (let [order
          (-> (create-order order-number now 2000)
            (add-item (LineProduct. "Cheese" 10) 2)
            (add-item (LineProduct. "Ham" 20) 1)
            (add-item (LineProduct. "Juice" 15) 2))
          stored-order (store! order)
          fetched-order (find-order order-number)
          nil-order (do (delete-order order-number) (find-order order-number))]

      (facts
        (println order)
        (println stored-order)
        (println fetched-order)
        (get fetched-order :number) => order-number
        nil-order => nil)))
  )