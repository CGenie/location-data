(ns server.core
  (:gen-class)
  (:refer-clojure :exclude [bigint boolean char double float time])
  (:use compojure.core
        org.httpkit.server
        (korma [core :only [insert select values]])
        (lobos core)
        [server.models])
  (:require [compojure.handler :as handler] ; form, query params decode; cookie; session, etc
            [compojure.core :only [defroutes GET POST DELETE ANY context]]
            [clojure.data.json :as json]))

(defn locations-read [req]
  (json/write-str (select location-data)))

(defn location-store [req]
  (let [device-id (-> req :params :device_id)
        timestamp (-> req :params :timestamp)
        latitude (-> req :params :latitude)
        longitude (-> req :params :longitude)]
    (println "device_id" device-id)
    (println "timestamp" timestamp)
    (println "latitude" latitude)
    (println "longitude" longitude)
    (insert location-data
            (values {:device_id device-id :timestamp timestamp :latitude latitude :longitude longitude}))))

(defroutes all-routes
  (GET "/location/" [] locations-read)
  (POST "/location/" [] location-store))

;; (def app
;;   (handler/site all-routes))

(defn -main [& args]
  (println "applying migrations")
  (migrate)
  (println "running server, port 8080")
  (run-server (handler/site  #'all-routes) {:port 8080}))
