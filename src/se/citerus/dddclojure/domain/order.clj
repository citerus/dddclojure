(ns se.citerus.dddclojure.domain.order)

;;Order aggregate

(defprotocol Order
  (order-total [this] "calculate-order-total")
  (add-item [product qty piece-price])
  (remove-item [product qty])
  (close-order [this]))

(defrecord SimpleOrder [number date status limit lines])

(defrecord LineItem [product qty piece-price])

;; Order factory methods

(defn create-order [number date limit]
  ;User :pre condition to handle limit
  (SimpleOrder. number date ::open limit []))


