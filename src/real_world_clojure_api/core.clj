(ns real-world-clojure-api.core
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [real-world-clojure-api.config :as config]
            [real-world-clojure-api.components.example-component :as example-component]))

(defn respond-hello [request]
  {:status 200 :body "Hello, world!"})


(def routes
  (route/expand-routes
    #{["/greet" :get respond-hello :route-name :greet]}))

(defn create-server
  [config]
  (http/create-server
    {::http/routes routes
     ::http/type   :jetty
     ::http/join?  false
     ::http/port   (-> config :server :port)}))

(defn start [config]
  (http/start (create-server config)))

(defn real-world-api-system
  [config]
  (component/system-map
    :example-component (example-component/new-example-component config))
  )

(defn -main
  []
  (let [system (-> (config/read-config)
        (real-world-api-system)
        (component/start-system))]
    (println "Starting RW Clojure API service with config")))