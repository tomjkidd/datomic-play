(ns datomic-play
  (:require [datomic.client.api :as d]
            [taoensso.timbre :as timbre]))

(def access-key (or (System/getenv "DATOMIC_ACCESS_KEY")
                    (do (timbre/info "Using default datomic access-key")
                        "myaccesskey")))

(def secret (or (System/getenv "DATOMIC_SECRET")
                (do (timbre/info "Using default datomic secret")
                    "mysecret")))

(def host (or (System/getenv "DATOMIC_HOST")
              (do (timbre/info "Using default datomic host")
                  "localhost")))
(def port (or (System/getenv "DATOMIC_PORT")
              (do (timbre/info "Using default datomic port")
                  "8998")))

(def db-name (or (System/getenv "DATOMIC_DB_NAME")
                 (do (timbre/info "Using default datomic db-name")
                     "hello")))

(def cfg {:server-type :peer-server
          :access-key access-key
          :secret secret
          :endpoint (format "%s:%s" host port)
          :validate-hostnames false})

(defn start
  "Return a datomic-play system map, with a datomic client and conn ready to go."
  []
  (let [client (d/client cfg)]
    {:client client
     :conn (d/connect client {:db-name db-name})}))

(def x 1)
