(ns se.citerus.dddclojure.domain.order
  (:use
    [clojure.contrib.core :only (dissoc-in)]))

;;Order aggregate

(defprotocol Order
  (add-item [this product qty] "Add an item to the order")
  (remove-item [this product qty] "Remove item from order")
  (close-order [this] "Close order, it is not cool to add or remove items to a closd order"))

(defprotocol Total
  (total [this] "Calculate total"))


(defrecord LineProduct [product piece-price])


(defrecord LineItem [line-product qty]
  Total
  (total [{:keys [qty line-product]}]
    (* qty (:piece-price line-product))))


(defrecord SimpleOrder [number date status limit lines]
  
  Order
  (add-item [this product qty]
    (if-let [line (-> this :lines (get product))]
      (update-in this [:lines product :qty] + qty)
      (update-in this [:lines] assoc product (LineItem. product qty))))

  (remove-item [this product qty]
    (if-let [line (-> this :lines (get product))]
      (if (> (:qty line) qty)
        (update-in this [:lines product :qty] - qty)
        (dissoc-in this [:lines product]))
      this))

  Total
  (total [this]
    (reduce #(+ %1 (total %2)) 0 (vals (:lines this)))))

;; Order factory methods

(defn create-order [number date limit]
  ;User :pre condition to handle limit
  (SimpleOrder. number date ::open limit {}))


