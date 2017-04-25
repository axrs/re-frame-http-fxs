(ns axrs.re-frame.http-fxs
  (:require
    [re-frame.core :refer [reg-event-fx debug subscribe dispatch]]
    [clojure.string :as string]))

(declare clj->jskw)

(defn- key->jskw [k]
  (if (satisfies? IEncodeJS k)
    (-clj->js k)
    (if (or (string? k)
            (number? k)
            (keyword? k)
            (symbol? k))
      (clj->jskw k)
      (pr-str k))))

(defn- clj->jskw
  "Note: Altered from cljs.core to encode keywords
  Recursively transforms ClojureScript values to JavaScript.
  sets/vectors/lists become Arrays, Keywords and Symbol become Strings,
  Maps become Objects. Arbitrary keys are encoded to by key->js."
  [x]
  (when-not (nil? x)
    (if (satisfies? IEncodeJS x)
      (-clj->js x)
      (cond
        (keyword? x) (string/replace (str (keyword x)) #"^:" "")
        (symbol? x) (str x)
        (map? x) (let [m (js-obj)]
                   (doseq [[k v] x]
                     (aset m (key->jskw k) (clj->jskw v)))
                   m)
        (coll? x) (let [arr (array)]
                    (doseq [x (map clj->jskw x)]
                      (.push arr x))
                    arr)
        :else x))))

(defn- append-result
  "Appends the HTTP result object to the end of all after requests"
  [result afters]
  (mapv #(conj % result) afters))

(defn http-result [_ [_ afters result]] {:dispatch-n (append-result result afters)})
(reg-event-fx ::http-result http-result)

(defn json-request
  "Issues a JSON request to a specified URL, sending a provided body and dispatching success or
  error handlers respectively"
  [_ [_ {:keys [type url body after-success after-errors]
         :or   {type :post body nil after-success [] after-errors []}}]]

  {:http-xhrio {:method          (keyword type)
                :uri             url
                :format          (ajax/json-request-format)
                :response-format (ajax/json-response-format {:keywords? true})
                :params          (clj->jskw body)
                :on-success      [::http-result after-success]
                :on-failure      [::http-result after-errors]}})

(reg-event-fx :json-request json-request)
