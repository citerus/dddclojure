(ns se.citerus.dddclojure.domain.order
  (:use
    [clojure.contrib.core :only (dissoc-in)]))

;;Order aggregate

(defprotocol Order
  (add-item [this product qty] "Add an item to the order")
  (remove-item [this product qty] "Remove item from order")
  (close-order [this] "Close order, it is not cool to add or remove items to a closed order"))

(defprotocol Total
  (total [this] "Calculate total"))


(defrecord LineProduct [product piece-price])


(defrecord LineItem [line-product qty]
  Total
  (total [{:keys [qty line-product]}]
    (* qty (:piece-price line-product))))


(defrecord PurchaseOrder [number date status limit lines]

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
    (let [line-totals (map #(total %) (vals (:lines this)))]
      (apply + line-totals))))

;; Order factory methods

(defn create-order [number date limit]
  {:pre [(>= limit 100) (<= limit 10000)]  }
  (PurchaseOrder. number date ::open limit {}))



