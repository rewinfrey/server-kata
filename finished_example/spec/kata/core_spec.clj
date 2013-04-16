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

; Assumptions:
; A parser is pre-written (parses incoming requests into a map)
; A writer response for writing the response to a client socket is pre-written
; A /time route already exists

(context "GET requests"
  (before-all (server/-main))
  (describe "sanity test"
    (it "returns status 200 for GET http://www.google.com"
      (should= 200 ((client/get "http://www.google.com") :status))))

  (defn build-request [port]
    (clojure.string/replace "http://localhost:port/time" #"port" port))

  (def seq-port (fn [] "7005"))
  (def con-port (fn [] "7010"))

  (describe "start-server"
    (it "returns status 200 for GET localhost:7005/time via sequential handler"
      (should= 200 ((client/get (build-request (seq-port))) :status)))

    (it "returns status 200 two for two GET localhost:7005/time"
      (should= 200 ((client/get (build-request (seq-port))) :status))
      (should= 200 ((client/get (build-request (seq-port))) :status)))

    (it "returns status 200 for GET localhost:7005/time via concurrent handler"
      (should= 200 ((client/get (build-request (con-port))) :status)))

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

    (it "shows concurrent handler is faster than sequential handler"
      (let [con-time (timed-requests (build-request (con-port)))
            seq-time (timed-requests (build-request (seq-port)))]
        (println "concurrency time: " con-time)
        (println "sequential time: " seq-time)
        (should (< con-time seq-time))))))



