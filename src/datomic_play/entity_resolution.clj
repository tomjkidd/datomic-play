(ns datomic-play.entity-resolution
  (:require [datomic.client.api :as d]
            [datomic-play.entity-resolution.model :as model]
            [datomic-play.system :as sytem]
            [taoensso.timbre :as timbre]))

(defn load-schema!
  [conn]
  (d/transact conn {:tx-data model/schema}))

(defn start
  [{:keys [load-schema?]
    :or {load-schema? true}}]
  (let [system (system/start)]
    (when load-schema?
      (timbre/info "Loading entity-resolution schema")
      (load-schema! (:conn system)))
    system))

(defn stop
  [system]
  (system/stop system))
