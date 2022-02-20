(ns datomic-play.entity-resolution
  (:require [datomic.client.api :as d]
            [datomic-play.entity-resolution.model :as model]
            [datomic-play.system :as sys]
            [taoensso.timbre :as timbre]))

(defn load-schema!
  [conn]
  (d/transact conn {:tx-data model/schema}))

(defn load-sample-trial!
  [conn]
  (let [trial-keyring-id #uuid "37ED757E-22E2-4AAD-B10A-EB4EB8EE23A8"
        trial-id #uuid "A09D1E79-CAFB-4940-9F13-F316C06DBA3D"
        sample-trials [{:entity-resolution.entity/id trial-keyring-id
                        :gov.clinicaltrials/nct-id "NCT-Psilocybin"
                        :trial-mgmt.trial/id trial-id
                        :trial-mgmt.trial/name "Psilocybin"}]]
    (d/transact conn {:tx-data sample-trials})))

(def all-trials-query
  '[:find (pull ?e pattern)
    :in $ pattern
    :where [?e :entity-resolution.entity/id _]])

(def all-trials-pull-pattern
  [:entity-resolution.entity/id
   :gov.clinicaltrials/nct-id
   :trial-mgmt.trial/id
   :trial-mgmt.trial/name])

(defn read-trials
  [conn]
  (let [db (d/db conn)]
    (d/q {:query all-trials-query
          :args [db '[*]]})))

(defn start
  [{:keys [load-schema?]
    :as config
    :or {load-schema? true}}]
  (let [system (sys/start config)]
    (when load-schema?
      (timbre/info "Loading entity-resolution schema")
      (load-schema! (:conn system)))
    system))

(defn stop
  [system]
  (sys/stop system))
