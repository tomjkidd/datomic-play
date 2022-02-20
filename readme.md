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
(require '[getting-started :as gs]
         '[datomic.client.api :as d])
(gs/write-first-movies!)
(d/q gs/all-movies-q (d/db gs/conn))
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
