(ns datomic-play.entity-resolution
  (:require [datomic.client.api :as d]
            [datomic-play.entity-resolution.model :as model]
            [datomic-play.system :as sys]
            [taoensso.timbre :as timbre]))

(defn load-schema!
  [conn]
  (d/transact conn {:tx-data model/schema}))

(defn write-keyring-id!
  ([conn keyring-id]
   (write-keyring-id! conn keyring-id nil))
  ([conn keyring-id type-string]
   (d/transact conn {:tx-data [(cond-> {:entity-resolution.entity/id keyring-id}
                                 type-string (assoc :entity-resolution.entity/type type-string))]})))

(defn write-key!
  [conn key-name key-value]
  (d/transact conn {:tx-data [{:entity-resolution.key/name key-name
                               :entity-resolution.key/value key-value
                               :db/ensure :entity-resolution.key/validate}]}))

(defn add-key-to-keyring!
  [conn keyring-id key-name key-value]
  (d/transact conn {:tx-data [
                              ;; TODO: This guard actually needs to be defined by peer...
                              #_[:db/add
                               [:entity-resolution.entity/id keyring-id]
                               :db/ensure
                               :entity-resolution.entity/guard]
                              [:db/add
                               [:entity-resolution.entity/id keyring-id]
                               :entity-resolution.entity/keys
                               [:entity-resolution.key/name+value [key-name key-value]]]]}))

(defn remove-key-from-keyring!
  [conn keyring-id key-name key-value]
  (d/transact conn {:tx-data [[:db/retract
                               [:entity-resolution.entity/id keyring-id]
                               :entity-resolution.entity/keys
                               [:entity-resolution.key/name+value [key-name key-value]]]]}))

(defn update-by-keyring-id!
  [conn keyring-id tx-datum]
  (d/transact conn {:tx-data [(merge {:db/id [:entity-resolution.entity/id keyring-id]}
                                     tx-datum)]}))

(defn write-trial!
  [conn tx-datum]
  (d/transact conn {:tx-data [tx-datum]}))

(defn write-trial-mgmt-site-id!
  [conn site-id]
  (d/transact conn {:tx-data [{:trial-mgmt.site/id site-id}]}))

(def all-keyrings-query
  '[:find (pull ?e pattern)
    :in $ pattern
    :where
    [?e :entity-resolution.entity/id _]])

(def all-trials-query
  '[:find (pull ?e pattern)
    :in $ pattern
    :where
    [?e :entity-resolution.entity/id _]
    [?e :entity-resolution.entity/type "trial"]])

(def all-sites-query
  '[:find (pull ?e pattern)
    :in $ pattern
    :where
    [?e :entity-resolution.entity/id _]
    [?e :entity-resolution.entity/type "site"]])

(def all-trials-pull-pattern
  [:entity-resolution.entity/id
   :gov.clinicaltrials/nct-id
   :trial-mgmt.trial/id
   :trial-mgmt.trial/name])

(def pull-everything-pattern
  '[*])

(defn read-keyrings
  [conn]
  (let [db (d/db conn)]
    (d/q {:query all-keyrings-query
          :args [db '[* {:entity-resolution.entity/keys [*]}]]})))

(defn read-trials
  [conn]
  (let [db (d/db conn)]
    (d/q {:query all-trials-query
          :args [db pull-everything-pattern]})))

(defn read-sites
  [conn]
  (let [db (d/db conn)]
    (d/q {:query all-sites-query
          :args [db pull-everything-pattern]})))

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
