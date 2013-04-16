(ns kata.request-parser
  (:import (java.io ByteArrayInputStream InputStreamReader BufferedReader)))

(defn parse-request [input-stream]
  (let [request (.split (.readLine (BufferedReader. (InputStreamReader. input-stream))) " ")]
    (hash-map :uri (nth request 1) :method (nth request 0))))
