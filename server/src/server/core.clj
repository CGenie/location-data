(ns server.core
  (:gen-class)
  (:use compojure.core
        org.httpkit.server)
  (:require [compojure.handler :as handler] ; form, query params decode; cookie; session, etc
            [compojure.core :only [defroutes GET POST DELETE ANY context]]))

(defn location-store [req]
  (let [timestamp (-> req :params :timestamp)
        latitude (-> req :params :latitude)
        longitude (-> req :params :longitude)]
    (println "timestamp" timestamp)
    (println "latitude" latitude)
    (println "longitude" longitude)))

(defroutes all-routes
  (POST "/location/store" [] location-store))

;; (def app
;;   (handler/site all-routes))

(defn -main [& args]
  (println "running server")
  (run-server (handler/site  #'all-routes) {:port 8080}))
