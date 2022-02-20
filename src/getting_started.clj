(ns getting-started
  "Sample code provided by https://docs.datomic.com/on-prem/getting-started/connect-to-a-database.html"
  (:require [datomic-play :as play]
            [datomic.client.api :as d]))

(def system (play/start))
(def conn (:conn system))
(def movie-schema [{:db/ident :movie/title
                    :db/valueType :db.type/string
                    :db/cardinality :db.cardinality/one
                    :db/doc "The title of the movie"}

                   {:db/ident :movie/genre
                    :db/valueType :db.type/string
                    :db/cardinality :db.cardinality/one
                    :db/doc "The genre of the movie"}

                   {:db/ident :movie/release-year
                    :db/valueType :db.type/long
                    :db/cardinality :db.cardinality/one
                    :db/doc "The year the movie was released in theaters"}])
(d/transact conn {:tx-data movie-schema})
(def first-movies [{:movie/title "The Goonies"
                    :movie/genre "action/adventure"
                    :movie/release-year 1985}
                   {:movie/title "Commando"
                    :movie/genre "action/adventure"
                    :movie/release-year 1985}
                   {:movie/title "Repo Man"
                    :movie/genre "punk dystopia"
                    :movie/release-year 1984}])

(defn write-movies! [movies]
  (d/transact conn {:tx-data movies}))

(defn write-first-movies! []
  (write-movies! first-movies))

(def all-movies-q '[:find ?e
                    :where [?e :movie/title]])
