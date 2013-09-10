(ns ilmo-rest.devdb
    (:import com.mchange.v2.c3p0.ComboPooledDataSource)
    (:require [clojure.java.jdbc :as sql]))


(def db-config
      {:classname "org.h2.Driver"
       :subprotocol "h2"
       :subname "mem:documents"
       :user ""
       :password ""})

(defn pool
      [config]
      (let [cpds (doto (ComboPooledDataSource.)
                   (.setDriverClass (:classname config))
                   (.setJdbcUrl (str "jdbc:" (:subprotocol config) ":" (:subname config)))
                   (.setUser (:user config))
                   (.setPassword (:password config))
                   (.setMaxPoolSize 6)
                   (.setMinPoolSize 1)
                   (.setInitialPoolSize 1))]
        {:datasource cpds}))

(def pooled-db (delay (pool db-config)))

(defn db-connection [] @pooled-db)

(defn create-schema []
  (sql/with-connection (db-connection)
     (do
       (sql/create-table :training [:id "bigint" "auto_increment" "primary key"]
                                   [:name "varchar(100)"]
                                   [:organizer "varchar(100)"]
                                   [:linktomaterial "varchar(100)"]
                                   [:description :varchar])
       (sql/create-table :trainingsession [:id "bigint" "auto_increment" "primary key"]
                                          [:training "bigint"]
                                          [:place :varchar(100)]
                                          [:maxparticipants "bigint"]
                                          [:date_c "timestamp"]
                                          [:enddate "timestamp"])
       (sql/create-table :participant [:id "bigint" "auto_increment" "primary key"]
                                      [:name "varchar(1024)"]
                                      [:trainingsession "bigint"]))))

