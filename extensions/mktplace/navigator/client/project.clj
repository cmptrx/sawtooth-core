; Copyright 2016 Intel Corporation
;
; Licensed under the Apache License, Version 2.0 (the "License");
; you may not use this file except in compliance with the License.
; You may obtain a copy of the License at
;
;     http://www.apache.org/licenses/LICENSE-2.0
;
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  ; See the License for the specific language governing permissions and
; limitations under the License.
; ------------------------------------------------------------------------------
(defn run-script [script]
  ["run" "-m" "clojure.main"  script])

(defn run-eval [clj]
  ["run" "-m" "clojure.main" "-e" clj])

(defn run-shell [& script-args]
  (let [script (format (slurp "scripts/shell-template.clj")
                       (clojure.string/join " " script-args)
                       (clojure.string/join "\", \"" script-args))]
    (run-eval script)))

(defproject exchange "0.1.0-SNAPSHOT"
  :description "Front-end client for the Ledger Explorer, using
               ClojureScript and Sass."
  :url "https://github.intel.com/maiden-lane/sawtooth-ledger-explorer"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.0.0"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.8.51"]
                 [org.clojure/core.async "0.2.374"
                  :exclusions [org.clojure/tools.reader]]
                 [org.omcljs/om "0.9.0"
                  :exclusions [cljsjs/react]]
                 [cljsjs/react-with-addons "0.14.7-0"]
                 [cljsjs/react-dom "0.14.7-0"
                  :exclusions [cljsjs/react]]
                 [sablono "0.6.3"]
                 [com.taoensso/timbre "4.3.1"]
                 [secretary "1.2.3"]]

  :aliases {"install:js" ~(run-shell "yarn" "install")

            "build:deps" ~(run-shell "npm" "run" "build:deps" )

            "build:css" ~(run-shell "sass"
                                    "src-style/main.scss"
                                    "resources/public/css/main.css")

            "cljs:build:min" ~(run-script "./scripts/build.clj")

            "build:prod" ["do"
                          "build:deps,"
                          "build:css,"
                          "cljs:build:min"]

            "cljs:build:test" ~(run-script "./scripts/build-test.clj")

            "run:test" ~(run-shell "phantomjs"
                                   "test/lib/phantom_runner.js"
                                   "resources/public/test.html")

            "cljs:test" ["do" "cljs:build:test,"
                         "run:test"]

            "cljs:clean-test" ["do" "clean,"
                               "build:deps,"
                               "cljs:test"]}

  :profiles {:dev {:dependencies [[figwheel-sidecar "0.5.7"]
                                  [binaryage/devtools "0.8.0"]]
                   :source-paths ["src/main" "src/dev"]
                   :test-paths ["test/main"]}
             :prod {:source-paths ^:replace ["src/main" "src/prod"]}}

  :source-paths ["src/main"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "resources/public/css/main.css"
                                    "resources/public/css/main.css.map"
                                    "resources/public/js/test"
                                    "lib/deps_library.js"
                                    "target"])
