(ns book-ll.app
  (:require [book-ll.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init! false)
