(ns datomic-play.system
  (:require [datomic.client.api :as d]
            [datomic-play.config :as conf]
            [taoensso.timbre :as timbre]))

(defn start
  "Return a datomic-play system map, with a datomic client and conn ready to go."
  ([] (start {}))
  ([config]
   (timbre/info "Starting system")
   (let [client-config (or (:datomic-client-config config)
                           conf/default-datomic-client-config)
         client (d/client client-config)
         db-name (or (:datomic-db-name config)
                     conf/db-name)]
     {:client client
      :db-name db-name
      :conn (d/connect client {:db-name db-name})})))

(defn stop
  [system]
  (timbre/info "Stopping system" system)
  (-> system
      (assoc :client nil)
      (assoc :db-name nil)
      (assoc :conn nil)))
