(ns datomic-play-test
  (:require [datomic-play :as sut]
            [clojure.test :refer [deftest is]]))

(deftest x-test
  (is (= sut/x 1)
      "The x var is under test"))
