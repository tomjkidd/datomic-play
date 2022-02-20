(ns datomic-play.system
  (:require [datomic.client.api :as d]
            [datomic-play.config :as conf]
            [taoensso.timbre :as timbre]))

(def default-config conf/default-config)

(defn start
  "Return a datomic-play system map, with a datomic client and conn ready to go."
  ([] (start default-config))
  ([config]
   (timbre/info "Starting system")
   (let [client (d/client cfg)]
     {:client client
      :conn (d/connect client {:db-name db-name})})))

(defn stop
  [system]
  (timbre/info "Stopping system" system)
  (-> system
      (assoc :client nil)
      (assoc :conn nil)))
