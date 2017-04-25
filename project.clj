(defproject re-frame-http-fxs "0.1.0-SNAPSHOT"

  :description "A extended re-frame `effects handler` for relaying Ajax tasks (via cljs-ajax) to multiple fx handlers Edit"
  :url "https://github.com/axrs/re-frame-http-fxs.git"
  :license {:name "MIT"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.229"]
                 [re-frame "0.8.0"]
                 [day8.re-frame/http-fx "0.1.3"]]

  :plugins [[lein-cljsbuild "1.1.4"]
            [lein-doo "0.1.7"]]

  :min-lein-version "2.5.3"

  :clean-targets ^{:protest false} [:target-path "target/compiled"]
  :resource-paths ["target/resources"]
  :jvm-opts ["-Xmx1g" "-XX:+UseConcMarkSweepGC"]

  :source-paths ["src"]
  :test-paths ["test"]

  :deploy-repositories [["releases" {:sign-releases false :url "https://clojars.org/repo"}]
                        ["snapshots" {:sign-releases false :url "https://clojars.org/repo"}]]

  :cljsbuild {:builds
              [{:id           "test"
                :source-paths ["test" "src"]
                :compiler     {:main                 axrs.re-frame.doo-runner
                               :output-to            "target/compiled/test/http-fxs.js"
                               :source-map           true
                               :output-dir           "target/compiled/test"
                               :optimizations        :none
                               :source-map-timestamp true
                               :pretty-print         true}}]}

  :test-refresh {:quiet        true
                 :with-repl    true
                 :changes-only true}

  :doo {:build "test"
        :alias {:default [:phantom]}}

  :aliases {"test"      ["do" "clean," "doo"]
            "test-once" ["do" "clean," "doo" "once"]})

