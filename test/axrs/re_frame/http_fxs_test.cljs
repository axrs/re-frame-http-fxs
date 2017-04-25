(ns axrs.re-frame.http-fxs-test
  (:require
    [cljs.test :refer-macros [is are deftest testing use-fixtures]]
    [axrs.re-frame.http-fxs :refer [clj->jskw]]))

(defn- js->str [s] (.stringify js/JSON s))

(deftest test-clj->jskw
  (testing "Replacement clj->js method to include namespaced keywords"
    (let [m {:black/knight "None shall pass."
             :arthur       "What?"
             :knight       "None shall pass."
             :king/arthur  ["I have no quarrel with you," "good Sir Knight," "but I must cross this bridge."]
             :pause        5
             :story        "Arthur cuts off the Black Knight's left arm."
             :scene        nil}]
      (is (= "{\"knight\":\"None shall pass.\",\"arthur\":[\"I have no quarrel with you,\",\"good Sir Knight,\",\"but I must cross this bridge.\"],\"pause\":5,\"story\":\"Arthur cuts off the Black Knight's left arm.\",\"scene\":null}"
             (js->str (clj->js m))))
      (is (= "{\"black/knight\":\"None shall pass.\",\"arthur\":\"What?\",\"knight\":\"None shall pass.\",\"king/arthur\":[\"I have no quarrel with you,\",\"good Sir Knight,\",\"but I must cross this bridge.\"],\"pause\":5,\"story\":\"Arthur cuts off the Black Knight's left arm.\",\"scene\":null}"
             (js->str (clj->jskw m))))))

  (testing "It should convert nested values aswell"
    (let [m {:black/knight {:says "None shall pass."}
             :king/arthur  {:questioning/reply "What?"}}]
      (is (= "{\"black/knight\":{\"says\":\"None shall pass.\"},\"king/arthur\":{\"questioning/reply\":\"What?\"}}"
             (js->str (clj->jskw m)))))))

(deftest append-result-test
  (testing "It should append the http results to to end of each provided vector"
    (let [d [[:black-knight {:says "None shall pass."}]
             [:king/arthur {:reply "What?"}]]
          expected {:king/arthur "I have no quarrel with you, good Sir Knight, but I must cross this bridge."}
          result (axrs.re-frame.http-fxs/append-result expected d)]
      (is (= (conj (first d) expected)) (first result))
      (is (= (conj (second d) expected)) (second result)))))

(deftest http-result-test
  (testing "Should return a map with dispatch-n key, and multiple fxs to dispatch"
    (let [result {:king/arthur "I have no quarrel with you, good Sir Knight, but I must cross this bridge."}
          afters [[:black-knight {:says "None shall pass."}] [:king/arthur {:reply "What?"}]]
          expected {:dispatch-n [[:black-knight {:says "None shall pass."} result] [:king/arthur {:reply "What?"} result]]}
          actual (axrs.re-frame.http-fxs/http-result nil [nil afters result])]
      (is (= expected actual)))))
