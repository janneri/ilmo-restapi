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

(defn query [sql]
  (sql/with-query-results results 
    sql (into [] results)))


(defn get-all-trainings []
  (response
     (sql/with-connection (db/db-connection)
       (query ["select * from training"]))))

(defn all-sessions-query []
  (str "select id, training, place, maxparticipants, "
       "       to_char(date_c, 'yyyy-mm-dd HH24:MI') starttime, to_char(enddate, 'yyyy-mm-dd HH24:MI') endtime "
       "from trainingsession "
       "order by date_c"))

(defn get-all []
  (response
     (sql/with-connection (db/db-connection)
         {:trainings (query ["select * from training"])
          :trainingsessions (query [(all-sessions-query)])
          :participants (query ["select name, trainingsession from participant"])})))
          

(defn user-report [username]
  (response
     (sql/with-connection (db/db-connection)
       (sql/with-query-results results
         [(str "select t.name, to_char(ts.date_c, 'yyyy-mm-dd') when " 
               "from training t join trainingsession ts on (t.id=ts.training) "
               "     join participant p on (ts.id = p.trainingsession)"
               "where p.name like ? "
               "order by ts.date_c") username]
           (into [] results)))))

(defn create-new-training [training]
    (sql/with-connection (db/db-connection)
        (sql/insert-record :training training)))

(defroutes app-routes
      (GET "/all" [] (get-all))
      (context "/reports" [] (defroutes reports-routes
        (GET  "/user/:name" [name] (user-report name))))
      (context "/trainings" [] (defroutes trainings-routes
        (GET  "/" [] (get-all-trainings))
        (POST "/" {body :body} (create-new-training body))))
      (route/not-found "Not Found"))


(def app
      (-> (handler/api app-routes)
        (middleware/wrap-json-body)
        (middleware/wrap-json-response)))