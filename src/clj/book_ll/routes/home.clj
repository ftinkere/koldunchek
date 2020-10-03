(ns book-ll.routes.home
  (:require
    [book-ll.layout :as layout]
    [clojure.java.io :as io]
    [book-ll.middleware :as middleware]
    [ring.util.response]
    [ring.util.http-response :as response]))

(defn home-page [request]
  (layout/render request "home.html"))

(def books ["nul" "von" "div" "tur" "kaf" "pin" "sek" "kil" "hok" "nan" "dek" "len" "tel"])
(defn book-md [request]
  (let [id (-> request :path-params :id)]
    (-> (str "books/" (nth books (Integer/parseInt id)) ".md") io/resource slurp))
  )

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}

   ["/" {:get home-page}]
   ["/book"
    ["" {:get (fn [_] (response/temporary-redirect "/book/1"))}]
    ["/:id" {:get home-page}]]

   ["/api"
    ["/book/:id" {:get (fn [req]
                         (-> (response/ok (book-md req))
                             (response/header "Content-Type" "text/plain; charset=utf-8")
                             ))}]
    ["/docs" {:get (fn [_]
                     (-> (response/ok (-> "docs/docs.md" io/resource slurp))
                         (response/header "Content-Type" "text/plain; charset=utf-8")))}]]
   ])

