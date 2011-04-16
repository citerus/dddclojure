(ns se.citerus.dddclojure.domain.order-test
  (:use
    [se.citerus.dddclojure.domain.order
     :only (create-order add-item order-total remove-item)]
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
  => (SimpleOrder. 1 order-time-point ::o/open 2000 {}))

(facts "Order scenario"

  (fact "Create order"
    (create-order 1 order-time-point 2000)
    => (SimpleOrder. 1 order-time-point ::o/open 2000 {}))

  (let [order (create-order 1 order-time-point 2000)]
    (fact "Add one item"

      (add-item order "Cheese" 2 10)
      => (SimpleOrder. 1 order-time-point ::o/open 2000 {"Cheese" (LineItem. "Cheese" 2 10)}))

    (fact "Calculate order total"
      (let [order-with-items
            (-> order
              (add-item "Cheese" 2 10)
              (add-item "Ham" 1 20)
              (add-item "Juice" 2 15))]

        (order-total order-with-items)
        => 70))

    (facts "Remove item from order"
      (let [order-with-items
            (-> order
              (add-item "Cheese" 2 10)
              (add-item "Ham" 1 20))]

        (remove-item order-with-items "Cheese" 1)
        =>
        (SimpleOrder. 1 order-time-point ::o/open 2000
          {"Cheese" (LineItem. "Cheese" 1 10)
           "Ham" (LineItem. "Ham" 1 20)})))))

;also check (but not in scenario) that 0 or negative qty lines are removed, and


(facts "Removing items leaving zero (or negative) qty drops entire line"
  (let [order-with-items
        (-> (create-order 1 order-time-point 2000)
          (add-item "Cheese" 2 10)
          (add-item "Ham" 1 20))]

    (remove-item order-with-items "Cheese" 2)
    =>
    (SimpleOrder. 1 order-time-point ::o/open 2000
      {"Ham" (LineItem. "Ham" 1 20)})

    (remove-item order-with-items "Cheese" 3)
    =>
    (SimpleOrder. 1 order-time-point ::o/open 2000
      {"Ham" (LineItem. "Ham" 1 20)})


    (remove-item order-with-items "Milk" 3)
    =>
    (SimpleOrder. 1 order-time-point ::o/open 2000
      {"Cheese" (LineItem. "Cheese" 2 10),
       "Ham" (LineItem. "Ham" 1 20)})))



;(facts "Adding items already in order increments qty"
;  (let [order-with-items
;        (-> (create-order 1 order-time-point 2000)
;          (add-item "Cheese" 2 10)
;          (add-item "Ham" 1 20))]
;
;    (add-item order-with-items "Cheese" 2 10)
;    =>
;    (SimpleOrder. 1 order-time-point ::o/open 2000
;      {"Cheese" (LineItem. "Cheese" 2 10),
;       "Ham" (LineItem. "Ham" 1 20)})))
