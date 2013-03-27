(defproject hands-on-with-clojure "0.1.0-SNAPSHOT"
  :description "Make internets with Clojure"
  :url "https://github.com/bodil/hands-on-with-clojure"
  :license {:name "Apache License, version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.html"}
  :plugins [[lein-catnip "0.6.0-SNAPSHOT"]]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring "1.1.8"]
                 [compojure "1.1.5"]
                 [hiccup "1.0.3"]
                 [com.novemberain/monger "1.5.0-rc1"]])
