(ns kata.core-spec
  (:use speclj.core)
  (:use clojure.java.shell)
  (:require [kata.core :as server])
  (:require [clj-http.client :as client]))

(context "routes"
  (it "returns a hash-map containing a time route"
      (should= true (contains? (server/routes) "/time")))

  (it "/time route key returns a hash-map containing http response keys"
      (should= true (contains? ((server/routes) "/time") :status))
      (should= true (contains? ((server/routes) "/time") :connection))
      (should= true (contains? ((server/routes) "/time") :server))
      (should= true (contains? ((server/routes) "/time") :content-type))
      (should= true (contains? ((server/routes) "/time") :body))
      (should= true (contains? ((server/routes) "/time") :end)))

  (it "/time route key returns a hash-map containing properly formatted http headers"
      (should= "HTTP/1.1 200 OK\r\n"   (((server/routes) "/time") :status))
      (should= "Connection: close\r\n" (((server/routes) "/time") :connection))
      (should= "Server: BoomTown\r\n"  (((server/routes) "/time") :server))
      (should= "Content-Type: text/plain\r\n\r\n" (((server/routes) "/time") :content-type))))

(describe "bind-socket"
  (it "binds a server socket to a port"
    (let [server-socket (server/bind-socket 3010)]
      (should (.isBound server-socket))
      (.close server-socket))))

(describe "socket-closed?"
  (it "returns true if a socket is closed, false if is not"
    (let [server-socket (server/bind-socket 3010)]
      (should= false (server/socket-closed? server-socket))
      (.close server-socket)
      (should (server/socket-closed? server-socket)))))

    ; uses apache benchmark to issue 12 requests total, four concurrent requests at a time
    ; and returns the time in seconds to complete the requests
    (defn timed-requests [request]
      (->
        ((sh "ab" "-n 12" "-c 4" "-r" request) :out)
        (clojure.string/split #"Time taken for tests:")
        (get 1)
        (clojure.string/split-lines)
        (get 0)
        (clojure.string/triml)
        (clojure.string/replace #" seconds" "")
        Float.))

; Assumptions:
; A parser is pre-written (parses incoming requests into a map)
; A writer response for writing the response to a client socket is pre-written
; A /time route already exists

