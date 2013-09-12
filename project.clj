(defproject ilmo-rest "0.1.0-SNAPSHOT"
  ; a local repo for oracle driver, checkout the README to see how this is set up
  ;:repositories {"local" ~(str (.toURI (java.io.File. "maven_repository")))}
  :description "Rest API for Ilmo"
  :url "http://www.github.com/ilmo-rest"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.5"]
                 [ring/ring-json "0.2.0"]
                 [c3p0/c3p0 "0.9.1.2"]
   ;              [oracle/ojdbc "1.4"]
                 [org.clojure/java.jdbc "0.2.3"]
                 [com.h2database/h2 "1.3.173"]
                 [cheshire "5.2.0"]]
  :plugins [[lein-ring "0.8.7"]
            [lein2-eclipse "2.0.0"]]
  :ring {:handler ilmo-rest.handler/app}
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.5"]
                        [ring-serve "0.1.2"]]}})
