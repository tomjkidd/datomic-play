(ns datomic-play.entity-resolution.model
  "The datomic schema model for entity resolution entities")

(def keyring-schema
  "A keyring represents a logical keyring, like in the physical world.
  A keyring can hold keys."
  [{:db/ident :entity-resolution.entity/id
    :db/valueType :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/doc "The entity uuid that provides uniqueness across the whole business-level domain of entities (\"keyring-id\")"
    :db/unique :db.unique/value}
   {:db/ident :entity-resolution.entity/type
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "The entity type of the entity, to aid in partitioning keyrings by entity"}
   {:db/ident :entity-resolution.entity/keys
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many
    :db/doc "The \"keys\" associated with a particular entity, that link the entity across different information systems"
    :db/unique :db.unique/value}
   {:db/ident :entity-resolution.entity/guard
    ;; NOTE: This needs to be installed by peer, not this library...
    ;:db.entity/preds ['datomic-play.entity-resolution.model.entity-preds/only-one-key-for-a-given-name?]
    }])

(def key-schema
  "A key represents a logical key that can be used to index into some internal or
  external information system.
  A key must be unique by name/value, and there is no notion of copies.
  A key can only be associated with at most one keyring."
  [{:db/ident :entity-resolution.key/name
    :db/valueType :db.type/keyword
    :db/cardinality :db.cardinality/one
    :db/doc "A namespaced keyword that provides the attribute name of the key"}
   {:db/ident :entity-resolution.key/value
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "A string representation of the actual key value.
Actual domain type of the key value can vary based on the :entity-resolution.key/name."}
   {:db/ident :entity-resolution.key/name+value
    :db/valueType :db.type/tuple
    :db/tupleAttrs [:entity-resolution.key/name :entity-resolution.key/value]
    :db/cardinality :db.cardinality/one
    :db/doc "The composite tuple that uniquely identifies an entity-resolution.key"
    :db/unique :db.unique/value}
   {:db/ident :entity-resolution.key/validate
    :db.entity/attrs [:entity-resolution.key/name
                      :entity-resolution.key/value]
    :db/doc "A validation spec to ensure that a key has a name and a value.
See https://docs.datomic.com/on-prem/schema/schema.html#required-attributes for more information"}])

(def site-schema
  "A location used in the context of a particular trial"
  [;; Salesforce attributes
   {:db/ident :salesforce.site/id
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "The entity id that salesforce uses to uniquely identify a site"
    :db/unique :db.unique/identity}
   {:db/ident :salesforce.site/site-number
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "The site-number that is recorded in salesforce."}

   ;; Trial MGMT attributes
   {:db/ident :trial-mgmt.site/id
    :db/valueType :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/doc "The entity uuid that trial-mgmt uses to uniquely identify a site"
    :db/unique :db.unique/identity}
   {:db/ident :trial-mgmt.site/name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "The trial-mgmt site name"}
   {:db/ident :trial-mgmt.site/site-number
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "The site-number that is recorded in trial-mgmt."}
   {:db/ident :trial-mgmt.site/trial
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc "The trial-mgmt trial associated with this site, can be found via [:trial-mgmt.trial/id uuid]"}

   ;; IxRS attributes
   {:db/ident :ixrs.site/site-number
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "The entity site-number that ixrs uses to uniquely identify a site. Not unique across trials!"}
   {:db/ident :ixrs.site/trial-id
    :db/valueType :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/doc "The trial-mgmt trial-id recorded in IxRS"}])

(def trial-schema
  "A sponsor asset used to perform a clinical trial"
  [;; Trial MGMT attributes
   {:db/ident :trial-mgmt.trial/id
    :db/valueType :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/doc "The entity uuid that trial-mgmt uses to uniquely identify a trial"}
   {:db/ident :trial-mgmt.trial/name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "The name of the trial recorded in trail-mgmt"}

   ;; ClinicalTrials.gov attributes
   {:db/ident :gov.clinicaltrials/nct-id
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "The NCT Id used by clinicaltrials.gov, https://clinicaltrials.gov/"}])

(def schema
  (reduce (fn [acc cur]
            (into acc cur))
          []
          [keyring-schema
           key-schema
           site-schema
           trial-schema]))
