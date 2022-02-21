(ns datomic-play.sample-data.entity-resolution
  (:require [clojure.spec.alpha :as s]
            [datomic.client.api :as d]
            [datomic-play.entity-resolution :as er]))

(defn load-sample-keyring-data!
  [conn]
  (let [trial-keyring-ids [#uuid "37ED757E-22E2-4AAD-B10A-EB4EB8EE23A8"]
        site-keyring-ids [#uuid "9942979d-e2c0-43a5-b752-2edbc7141661"
                          #uuid "9942979d-e2c0-43a5-b752-2edbc7141662"
                          #uuid "9942979d-e2c0-43a5-b752-2edbc7141663"
                          #uuid "9942979d-e2c0-43a5-b752-2edbc7141664"
                          #uuid "9942979d-e2c0-43a5-b752-2edbc7141665"]]
    (doseq [keyring-id trial-keyring-ids]
      (er/write-keyring-id! conn keyring-id "trial"))
    (doseq [keyring-id site-keyring-ids]
      (er/write-keyring-id! conn keyring-id "site"))
    {:trial-keyring-ids trial-keyring-ids
     :site-keyring-ids site-keyring-ids}))

(def trial-id #uuid "A09D1E79-CAFB-4940-9F13-F316C06DBA3D")

(def salesforce-site-keys
  ["SF1"
   "SF2"
   "SF3"
   "SF4"
   "SF5"])

(def trial-mgmt-site-keys
  [#uuid "68D438B0-8EA6-42F6-99F3-936E212ADE51"
   #uuid "68D438B0-8EA6-42F6-99F3-936E212ADE52"
   #uuid "68D438B0-8EA6-42F6-99F3-936E212ADE53"
   #uuid "68D438B0-8EA6-42F6-99F3-936E212ADE54"
   #uuid "68D438B0-8EA6-42F6-99F3-936E212ADE55"])

(def ixrs-site-keys
  [[trial-id "SN1"]
   [trial-id "SN2"]
   [trial-id "SN3"]
   [trial-id "SN4"]
   [trial-id "SN5"]])

(defn load-sample-key-data!
  [conn]
  (let [site-keys (mapv
                   (fn [site-key]
                     {:entity-resolution.key/name :salesforce.site/id
                      :entity-resolution.key/value site-key})
                   salesforce-site-keys)]
    (d/transact conn {:tx-data site-keys}))
  (let [site-keys (mapv
                   (fn [site-key]
                     {:entity-resolution.key/name :trial-mgmt.site/id
                      :entity-resolution.key/value (str site-key)})
                   trial-mgmt-site-keys)]
    (d/transact conn {:tx-data site-keys}))
  (let [site-keys (mapv
                   (fn [[trial-id site-number]]
                     {:entity-resolution.key/name :ixrs.site/trial-id+site-number
                      :entity-resolution.key/value (str trial-id
                                                        "+"
                                                        site-number)})
                   ixrs-site-keys)]
    (d/transact conn {:tx-data site-keys})))

(defn load-sample-site-trial-data!
  [conn])

(defn load-sample-trial-mgmt-data!
  [conn]
  (doseq [site-id trial-mgmt-site-keys]
    (er/write-trial-mgmt-site-id! conn site-id)))

(defn load-sample-salesforce-data!
  [conn])
(defn load-sample-ixrs-data!
  [conn])

(defn load-sample-data! [conn]
  (let [{:keys [trial-keyring-ids] :as keyring-data} (load-sample-keyring-data! conn)
        _ (load-sample-key-data! conn)
        trial-keyring-id (first trial-keyring-ids)

        trial {:db/id [:entity-resolution.entity/id trial-keyring-id]
               :gov.clinicaltrials/nct-id "NCT-Psilocybin"
               :trial-mgmt.trial/id trial-id
               :trial-mgmt.trial/name "Psilocybin"}
        _ (er/write-trial! conn trial)
        _ (load-sample-trial-mgmt-data! conn)]
    {:keyring-data
     {:keyrings (d/q '[:find (pull ?e [*])
                       :where [?e :entity-resolution.entity/id ?v]]
                     (d/db conn))
      :keys (d/q '[:find (pull ?e [*])
                   :where [?e :entity-resolution.key/name+value _]]
                 (d/db conn))}
     ;:trials (er/read-trials conn)
     ;:sites (er/read-sites conn)
     #_#_:site-data {:trial-mgmt (d/q '[:find (pull ?e [*])
                                    :where [?e :trial-mgmt.site/id ?v]]
                                  (d/db conn))}}))
