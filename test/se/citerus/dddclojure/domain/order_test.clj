(ns se.citerus.dddclojure.domain.order-test
  (:use
    [se.citerus.dddclojure.domain.order
     :only (create-order add-item total remove-item)]
    [midje.sweet])
  (:require
    [se.citerus.dddclojure.domain.order :as o])
  (:import
    [se.citerus.dddclojure.domain.order PurchaseOrder LineItem LineProduct]
    [org.joda.time DateTime]))


;-- parse-int

(def order-time-point (DateTime. 2011 4 16 21 31 0 0))

(facts "Order scenario"

  (fact "Create order"
    (create-order 1 order-time-point 2000)
    => (PurchaseOrder. 1 order-time-point ::o/open 2000 {}))

  (let [order (create-order 1 order-time-point 2000)
        cheese (LineProduct. "Cheese" 10)]
    (fact "Add one item"

      (add-item order cheese 2)
      => (PurchaseOrder. 1 order-time-point ::o/open 2000 {cheese (LineItem. cheese 2)}))

    (fact "Calculate order total"
      (let [order-with-items
            (-> order
              (add-item (LineProduct. "Cheese" 10) 2)
              (add-item (LineProduct. "Ham" 20) 1)
              (add-item (LineProduct. "Juice" 15) 2))]

        (total order-with-items)
        => 70))

    (facts "Remove item from order"
      (let [cheese (LineProduct. "Cheese" 10)
            ham (LineProduct. "Ham" 20)
            order-with-items
            (-> order
              (add-item cheese 2)
              (add-item ham 1))]

        (remove-item order-with-items cheese 1)
        =>
        (PurchaseOrder. 1 order-time-point ::o/open 2000
          {cheese (LineItem. cheese 1)
           ham (LineItem. ham 1)})))))


;also check (but not in scenario) that 0 or negative qty lines are removed, and

(facts "Removing items leaving zero (or negative) qty drops entire line"
  (let [cheese (LineProduct. "Cheese" 10)
        ham (LineProduct. "Ham" 20)
        order-with-items
        (-> (create-order 1 order-time-point 2000)
          (add-item cheese 2)
          (add-item ham 1))]

    (remove-item order-with-items cheese 2)
    =>
    (PurchaseOrder. 1 order-time-point ::o/open 2000
      {ham (LineItem. ham 1)})

    (remove-item order-with-items cheese 3)
    =>
    (PurchaseOrder. 1 order-time-point ::o/open 2000
      {ham (LineItem. ham 1)})


    (remove-item order-with-items "Milk" 3)
    =>
    (PurchaseOrder. 1 order-time-point ::o/open 2000
      {cheese (LineItem. cheese 2),
       ham (LineItem. ham 1)})))

(facts "Adding items already in order increments qty"
  (let [
    cheese (LineProduct. "Cheese" 10)
    ham (LineProduct. "Ham" 20)
    order-with-items
    (-> (create-order 1 order-time-point 2000)
      (add-item cheese 2)
      (add-item ham 1))]

    (add-item order-with-items cheese 2)
    =>
    (PurchaseOrder. 1 order-time-point ::o/open 2000
      {cheese (LineItem. cheese 4),
       ham (LineItem. ham 1)})))


(fact "Create order"
  (create-order 1 order-time-point 2000)
  => (PurchaseOrder. 1 order-time-point ::o/open 2000 {}))


(facts "Order limit must be  100 <= limit <= 10000"
  (create-order 1 order-time-point 100) => truthy
  (create-order 1 order-time-point 99) => (throws AssertionError)
  
  (create-order 1 order-time-point 10000) => truthy
  (create-order 1 order-time-point 10001) => (throws AssertionError))


