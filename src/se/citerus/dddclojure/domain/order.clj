(ns se.citerus.dddclojure.domain.order
  (:use
    [clojure.contrib.core :only (dissoc-in)]
    [clojure.contrib.seq-utils :only (positions)]))

;;Order aggregate

(defprotocol Order
  (add-item [this product qty]
    "Add an item to the order")
  (remove-item [this product qty]
    "Remove item from order")
  (close-order [this]
    "Close order, it is not cool to change a closed order"))

(defprotocol Total
  (total [this] "Calculate total"))

;; Records

(defrecord LineProduct [product piece-price])


(defrecord LineItem [line-product qty] ;;TODO: Change argument order, reads better
  Total
  (total [{:keys [qty line-product]}]
    (* qty (:piece-price line-product))))


(defrecord PurchaseOrder [number date status limit lines])

(defn- find-line-ix [order product] (first (positions #(= (:line-product %) product) (:lines order))))

(extend-type PurchaseOrder
  Order
  (add-item [this product qty]
    {:post [(<= (total %) (:limit %))]}
    (if-let [ix (find-line-ix this product)]
      (update-in this [:lines ix :qty] + qty)
      (update-in this [:lines] conj (LineItem. product qty))))

  (remove-item [this product qty]
    (if-let [ix (find-line-ix this product)]
      (if (> (get-in this [:lines ix :qty]) qty)
        (update-in this [:lines ix :qty] - qty)
        (assoc this :lines (remove #(= (:line-product %) product) (:lines this))))
      this))

  Total
  (total [this]
    (let [line-totals (map #(total %) (:lines this))]
      (apply + line-totals))))

;; Order factory methods

(defn create-order [number date limit]
  {:pre [(>= limit 100) (<= limit 10000)]}
  (PurchaseOrder. number date ::open limit []))



