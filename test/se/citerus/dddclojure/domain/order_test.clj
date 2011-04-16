(ns se.citerus.dddclojure.domain.order-test
  (:use
    [se.citerus.dddclojure.domain.order :only (create-order)]
    [midje.sweet])
  (:require
    [se.citerus.dddclojure.domain.order :as o])
  (:import
    [se.citerus.dddclojure.domain.order SimpleOrder LineItem]
    [org.joda.time DateTime]))


;-- parse-int

(def order-time-point (DateTime. 2011 4 16 21 31 0 0))

(fact "Create order"
  (create-order 1 order-time-point 2000) =>
  (SimpleOrder. 1 order-time-point ::o/open 2000 []))
