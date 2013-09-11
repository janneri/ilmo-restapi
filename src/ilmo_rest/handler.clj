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

(defn user-report [username]
  (response
     (sql/with-connection (db/db-connection)
       (sql/with-query-results results
         [(str "select t.name, to_char(ts.date_c, 'yyyy-mm-dd') when " 
               "from training t join trainingsession ts on (t.id=ts.training) "
               "     join participant p on (ts.id = p.trainingsession)"
               "where p.name like ?") username]
           (into [] results)))))

(defn create-new-training [training]
    (sql/with-connection (db/db-connection)
        (sql/insert-record :training training)))

(defroutes app-routes
      (GET "/foo" [] (user-report "Janne Rintanen"))
      (context "/reports" [] (defroutes reports-routes
        (GET  "/user/:name" [name] (user-report name))))
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