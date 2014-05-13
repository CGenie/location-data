
(defproject server "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.8"]  ; webserver
                 [http-kit "2.1.16"]  ; webserver
                 [korma "0.3.0-RC5"]  ; DB
                 [lobos "1.0.0-beta1"]  ; DB migrations
                 [org.xerial/sqlite-jdbc "3.7.2"]  ; DB
                 [org.clojure/data.json "0.2.4"]  ; JSON
                 ]
  :main ^:skip-aot server.core
  :target-path "target/%s"
  ;:profiles {:uberjar {:aot :all}}
  :plugins [[lein-ring "0.8.10"]]
  :ring {:handler server.core/app}
  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                               [ring-mock "0.1.5"]]}})
