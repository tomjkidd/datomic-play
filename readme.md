# Datomic play

A repository to play and learn with datomic

## Assumed before starting

- Clojure is installed
  - Clojure version 1.10.3
  - Clojure CLI version 1.10.3.1020
  - https://clojure.org/guides/getting_started
  - https://clojure.org/guides/deps_and_cli
- Datomic is installed locally
  - datomic-pro-1.0.6362 downloaded locally
  - bin/maven-install has run to provide com.datomic/datomic-pro "1.0.6362"
  - https://docs.datomic.com/on-prem/getting-started/get-datomic.html
    - Official download instructions
  - https://docs.datomic.com/on-prem/getting-started/connect-to-a-database.html
    - Basis for how project connects to datomic

## Usage

A `Makefile` is provided to give some simple make targets to run the important parts of the project.

The `sample.env` file has the value for where datomic was installed for me,
if you are using a different value, then just update that value to where
it is actually installed on your system.

### Run the datomic peer server

``` bash
source sample.env
make run-peer
```

### Run repl 

``` bash
clj
```

``` clojure
;; Witness datomic is up and running
(require '[datomic-play.entity-resolution :as er]
         '[datomic-play.sample-data.entity-resolution :as sd]
         '[datomic-play.entity-resolution.model.entity-preds :as preds]
         '[datomic.client.api :as d])
(require 'datomic-play.entity-resolution.model.entity-preds)
(def sys (er/start {:load-schema? true}))
(def conn (:conn sys))
(sd/load-sample-data! conn)
(er/read-trials conn)
(er/add-key-to-keyring! conn #uuid "9942979d-e2c0-43a5-b752-2edbc7141661" :salesforce.site/id "SF1")

;; This should fail, attempting to add key to more than one keyring
(er/add-key-to-keyring! conn #uuid "9942979d-e2c0-43a5-b752-2edbc7141662" :salesforce.site/id "SF1")

;; TODO: Figure out how to prevent this, don't want 2 types of same name
;; Perhaps: https://docs.datomic.com/on-prem/schema/schema.html#entity-predicates
(er/add-key-to-keyring! conn #uuid "9942979d-e2c0-43a5-b752-2edbc7141661" :salesforce.site/id "SF2")

(def eid (ffirst (d/q '[:find ?e
                        :in $ ?v
                        :where [?e :entity-resolution.entity/id ?v]]
                       (d/db conn)
                       #uuid "9942979d-e2c0-43a5-b752-2edbc7141661")))

(preds/only-one-key-for-a-given-name? (d/db conn) eid)

(er/remove-key-from-keyring! conn #uuid "9942979d-e2c0-43a5-b752-2edbc7141661" :salesforce.site/id "SF1")
(er/remove-key-from-keyring! conn #uuid "9942979d-e2c0-43a5-b752-2edbc7141661" :salesforce.site/id "SF2")

(er/add-key-to-keyring! conn #uuid "9942979d-e2c0-43a5-b752-2edbc7141662" :salesforce.site/id "SF1")

(er/read-keyrings conn)

(d/q '[:find (pull ?e [*])
         :in $ ?v
         :where [?e :entity-resolution.key/name+value ?v]]
       (d/db conn)
       [:salesforce.site/id "SF3"])

(d/q '[:find (pull ?e [*])
         :in $ ?v
         :where [?e :entity-resolution.key/name+value ?v]]
       (d/db conn)
       [:ixrs.site/trial-id+site-number "a09d1e79-cafb-4940-9f13-f316c06dba3d+SN4"])
```

## Tests

To run the project's tests, just run `make test`

# Resources

- https://docs.datomic.com/on-prem/index.html
  - On-prem documentation, since this is the method I used
- :db/valueTypes
  - https://docs.datomic.com/on-prem/schema/schema.html#value-types
  - `:db.type/{bigdec|bigint|boolean|double|float|instant|keyword|long|ref|string|symbol|tuple|uuid|uri|bytes}`
    - instant -> java.util.Date -> epoch ms
- :db/cardinality
  - https://docs.datomic.com/on-prem/schema/schema.html#cardinality
  - `:db.cardinality/{one|many}`
- https://blog.davemartin.me/posts/datomic-how-to-update-cardinality-many-attribute/
  - How to manage cardinality many attribute
