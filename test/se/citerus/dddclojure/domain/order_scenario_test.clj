(ns se.citerus.dddclojure.domain.order-scenario-test
  (:use
    [se.citerus.dddclojure.domain.order
     :only (create-order add-item total remove-item)]
    [midje.sweet])
  (:require
    [se.citerus.dddclojure.domain.order :as o])
  (:import
    [se.citerus.dddclojure.domain.order PurchaseOrder LineItem LineProduct]
    [org.joda.time DateTime]))


(def order-time (DateTime. 2011 4 16 21 31 0 0))

(facts "Order scenario"

  (fact "Create order"
    (create-order 1 order-time 2000)
    => (PurchaseOrder. 1 order-time ::o/open 2000 []))

  (let [order (create-order 1 order-time 2000)
        cheese (LineProduct. "Cheese" 10)]

    (fact "Add one item"
      (add-item order 2 cheese)
      => (PurchaseOrder. 1 order-time ::o/open 2000 [(LineItem. 2 cheese)]))

    (fact "Calculate order total"
      (let [order-with-items
            (-> order
              (add-item 2 (LineProduct. "Cheese" 10))
              (add-item 1 (LineProduct. "Ham" 20))
              (add-item 2 (LineProduct. "Juice" 15)))]

        (total order-with-items)
        => 70))

    (facts "Remove item from order"
      (let [cheese (LineProduct. "Cheese" 10)
            ham (LineProduct. "Ham" 20)
            order-with-items
            (-> order
              (add-item 2 cheese)
              (add-item 1 ham))]

        (remove-item order-with-items 1 cheese)
        =>
        (PurchaseOrder. 1 order-time ::o/open 2000
          [(LineItem. 1 cheese)
           (LineItem. 1 ham)])))))