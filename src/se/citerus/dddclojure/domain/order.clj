(ns se.citerus.dddclojure.domain.order
  (:use
    [clojure.contrib.core :only (dissoc-in)]))

;;Order aggregate

(defprotocol Order
  (add-item [this product qty] "Add an item to the order")
  (remove-item [this product qty] "Remove item from order")
  (order-total [this] "Calculate order total")
  (close-order [this] "Close order, it is not cool to add or remove items to a closd order"))

(defn line-total [{:keys [qty piece-price]}] ;TODO: Define in protocol?
  (* qty piece-price))

(defn find-line-item [lines product]
  (filter #(= (:product %) product) lines))

(defrecord LineProduct [product pice-price])

(defrecord LineItem [line-product qty])

(defrecord SimpleOrder [number date status limit lines] ;Create FastOrder where LineItems are in a map
  Order
  (add-item [this product qty]
    (update-in this [:lines] assoc product (LineItem. product qty)))
  
  (order-total [this]
    (reduce #(+ %1 (line-total %2)) 0 (vals (:lines this))))

  (remove-item [this product qty]
    (if-let [line (-> this :lines (get product))]
      (if (> (:qty line) qty)
        (update-in this [:lines product :qty] - qty)
        (dissoc-in this [:lines product]))
      this)))


;; Order factory methods

(defn create-order [number date limit]
  ;User :pre condition to handle limit
  (SimpleOrder. number date ::open limit {}))


