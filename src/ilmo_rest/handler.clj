(ns ilmo-rest.handler
    (:import com.mchange.v2.c3p0.ComboPooledDataSource)
    (:use compojure.core)
    ;(:use cheshire.core)
    (:use ring.util.response)
    (:require [compojure.handler :as handler]
              [ring.middleware.json :as middleware]
              [clojure.java.jdbc :as sql]
              [compojure.route :as route]
              [ilmo-rest.devdb :as db]))

(defn get-all-trainings []
      (response
        (sql/with-connection (db/db-connection)
          (sql/with-query-results results
            ["select * from training"]
            (into [] results)))))


(defn create-new-training [training]
    (sql/with-connection (db/db-connection)
        (let [generated-training-id (sql/insert-record :training training)]
            generated-training-id)))

(defroutes app-routes
      (context "/trainings" [] (defroutes trainings-routes
        (GET  "/" [] (get-all-trainings))
        (POST "/" {body :body} (create-new-training body))))
        ;(context "/:id" [id] (defroutes document-routes
        ;  (GET    "/" [] (get-document id))
        ;  (PUT    "/" {body :body} (update-document id body))
        ;  (DELETE "/" [] (delete-document id))))))
      (route/not-found "Not Found"))


(def app
      (-> (handler/api app-routes)
        (middleware/wrap-json-body)
        (middleware/wrap-json-response)))