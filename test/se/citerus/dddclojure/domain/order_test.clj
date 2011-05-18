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

(facts "Removing items leaving zero (or negative) qty drops entire line"
  (let [cheese (LineProduct. "Cheese" 10)
        ham (LineProduct. "Ham" 20)
        order-with-items
        (-> (create-order 1 order-time 2000)
          (add-item 2 cheese)
          (add-item 1 ham))]

    (remove-item order-with-items 2 cheese)
    =>
    (PurchaseOrder. 1 order-time ::o/open 2000
      [(LineItem. 1 ham)])

    (remove-item order-with-items 3 cheese)
    =>
    (PurchaseOrder. 1 order-time ::o/open 2000
      [(LineItem. 1 ham)])


    (remove-item order-with-items 3 "Milk")
    =>
    (PurchaseOrder. 1 order-time ::o/open 2000
      [(LineItem. 2 cheese),
       (LineItem. 1 ham)])))

(facts "Adding items already in order increments qty"
  (let [
    cheese (LineProduct. "Cheese" 10)
    ham (LineProduct. "Ham" 20)
    order-with-items
    (-> (create-order 1 order-time 2000)
      (add-item 2 cheese)
      (add-item 1 ham))]

    (add-item order-with-items 2 cheese)
    =>
    (PurchaseOrder. 1 order-time ::o/open 2000
      [(LineItem. 4 cheese),
       (LineItem. 1 ham)])))


(fact "Create order"
  (create-order 1 order-time 2000)
  => (PurchaseOrder. 1 order-time ::o/open 2000 []))


(facts "Order limit must be  100 <= limit <= 10000"
  (create-order 1 order-time 100) => truthy
  (create-order 1 order-time 99) => (throws AssertionError)

  (create-order 1 order-time 10000) => truthy
  (create-order 1 order-time 10001) => (throws AssertionError))

(fact "Order total must not be above order limit"
  (let [cheese (LineProduct. "Cheese" 100)
        ham (LineProduct. "Ham" 200)
        order (-> (create-order 1 order-time 150) (add-item 1 cheese))]

    (add-item order 1 ham) => (throws AssertionError)))

(facts "Total protocol implementations"
  (let [line1 (LineItem. 10 (LineProduct. "Egg" 20))
        line2 (LineItem. 4 (LineProduct. "Milk" 5))
        order (-> (create-order 1 order-time 2000) (assoc :lines [line1 line2]))]

        (total line1) => 200
        (total line2) => 20
        (total order) => 220))

      


