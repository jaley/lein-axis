(ns leiningen.test.core
  (:require leiningen.axis)
  (:require [leiningen.core] :reload)
  (:require clojure.pprint)
  (:require [clojure.contrib.str-utils2 :as s])
  (:use [clojure.test])
  (:use lancet.core)
  (:import java.io.File))

(def java-source-path   "src/java")
(def package-name       "org.lein.axis")
;;(def hello-service-file "src/java/org/lein/axis/Hello_Service.java")
(def hello-service-file (str java-source-path
                             (File/separator)
                             (s/replace package-name #"\." (File/separator))
                             (File/separator)
                             "Hello_Service.java"))
(def axis-test-data     [["test/resources/wsdls/clearbooks.wsdl" package-name]])

(defn test-fixture [f]
  ;; set-up
  (let [project (assoc (leiningen.core/read-project)
                  :axis axis-test-data
                  :java-source-path java-source-path)]
    ;; Call the task to generate the test bindings
    (leiningen.axis/axis project)
    ;; test
    (f)
    ;; tear-down - delete the test bindings
    (delete {:dir "src/java/org"})
    ))

;; Use the above fixture for all tests
(use-fixtures :once test-fixture)

;; Tests to see if the task generates the expected bindings
(deftest generates-bindings
  (is (. (new File hello-service-file) exists))
  )
