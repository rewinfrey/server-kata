(defproject kata "0.1.0-SNAPSHOT"
  :main kata.core
  :description "Clojure Server Kata"
  :dependencies [[speclj "2.5.0"]
                 [org.clojure/clojure "1.4.0"]
                 [clj-http "0.6.5"]]
  :plugins [[speclj "2.5.0"]]
  :test-paths ["spec/"]
  :java-source-path "src/")
