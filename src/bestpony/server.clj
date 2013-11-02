(ns bestpony.server
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.file :refer [wrap-file]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.util.response :refer [redirect-after-post]]
            [hiccup.core :refer [html]]
            [hiccup.page :refer [html5 include-js include-css]]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.handler :as handler]
            [monger.core :as mongo]
            [monger.collection :as m])
  (:import [org.bson.types ObjectId]))

(mongo/connect!)
(mongo/use-db! "bestpony")

(defn page-template [content]
  (html5
   [:head
    [:title "Best Pony Database"]
    (include-css "/screen.css")
    (include-js "/catnip.js")]
   [:body
    [:h1
     [:img {:src "/dash.gif"}]
     [:a {:href "/"} "Best Pony Database"]
     [:img {:src "/pinkie.gif"}]]
    [:div.content content]]))

(defn list-view [docs]
  [:table.ponies
   [:tbody
    [:tr
     [:th "Pony"]
     [:th "Type"]
     [:th "Element"]
     [:th [:a {:href "/new"} "Add"]]]
    (for [doc docs]
         [:tr
          [:td (:name doc)]
          [:td (:type doc)]
          [:td (:element doc)]
          [:td [:a {:href (str "/edit/" (:_id doc))} "Edit"]]])]])

(defn pony-view [doc]
   [:table.form
      [:tbody
       [:form {:method "POST"
           :action (if doc
                       (str "/update/" (:_id doc))
                       "/insert")}
        [:tr
         [:th "Pony"]
         [:td [:input {:type "text" :name "name" :value (:name doc)}]]]
        [:tr
         [:th "Type"]
         [:td [:input {:type "text" :name "type" :value (:type doc)}]]]
        [:tr
         [:th "Element"]
         [:td [:input {:type "text" :name "element" :value (:element doc)}]]]
        [:tr
         [:td.submit {:colspan 2}
          [:input {:type "submit" :value "Save"}]]]]
       (when doc
         [:form {:method "POST" :action (str "/remove/" (:_id doc))}
          [:tr
           [:td.submit {:colspan 2}
            [:input {:type "submit" :value "Delete"}]]]])]])

(defroutes app
  (GET "/" []
    (->> (m/find-maps "ponies" {})
        (sort-by :name)
        (list-view)
        (page-template)))
  (GET "/new" []
    (-> (pony-view nil)
        (page-template)))
  (GET "/edit/:id" [id]
    (-> (m/find-map-by-id "ponies" (ObjectId. id))
        (pony-view)
        (page-template)))
  (POST "/insert" [name type element]
    (let [doc {:name name :type type :element element}]
         (m/insert "ponies" doc))
    (redirect-after-post "/"))
  (POST "/update/:id" [id name type element]
    (let [doc {:name name :type type :element element}]
         (m/update-by-id "ponies" (ObjectId. id) doc))
    (redirect-after-post "/"))
  (POST "/remove/:id" [id]
    (m/remove-by-id "ponies" (ObjectId. id))
    (redirect-after-post "/")))

(def handler
     (-> app
         (handler/site)
         (wrap-file "static")
         (wrap-file-info)))

(defonce server (run-jetty #'handler {:port 1337 :join? false}))
