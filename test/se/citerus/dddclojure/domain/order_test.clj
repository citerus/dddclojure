(ns se.citerus.dddclojure.domain.order-test
  (:use
    [se.citerus.dddclojure.domain.order :only (create-order add-item order-total)]
    [midje.sweet])
  (:require
    [se.citerus.dddclojure.domain.order :as o])
  (:import
    [se.citerus.dddclojure.domain.order SimpleOrder LineItem]
    [org.joda.time DateTime]))


;-- parse-int

(def order-time-point (DateTime. 2011 4 16 21 31 0 0))

(fact "Create order"
  (create-order 1 order-time-point 2000)
  => (SimpleOrder. 1 order-time-point ::o/open 2000 []))

(facts "Order scenario"

  (fact "Create order"
    (create-order 1 order-time-point 2000) =>
    (SimpleOrder. 1 order-time-point ::o/open 2000 []))

  (fact "Add one item"
    (let [order (create-order 1 order-time-point 2000)
          cheese-order (add-item order "Cheese" 2 10)]
      cheese-order => (SimpleOrder. 1 order-time-point ::o/open 2000 [(LineItem. "Cheese" 2 10)])))

  (fact "Calculate order total"
    (let [order (create-order 1 order-time-point 2000)
          order-with-items (-> order (add-item "Cheese" 2 10) (add-item "Ham" 1 20) (add-item "Juice" 2 15))]
      (order-total order-with-items) => 70)))