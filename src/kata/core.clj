(ns kata.core
  (:require [kata.request-parser :as parser])
  (:require [kata.socket-writer :as writer])
  (:import (java.io BufferedReader InputStreamReader OutputStreamWriter))
  (:import (java.net ServerSocket Socket SocketException))
  (:import (java.util.Date)))

(defn now []
  (Thread/sleep 100)
  (java.util.Date. ))

(defn routes []
  (hash-map "/time" (hash-map :status "HTTP/1.1 200 OK\r\n"
                              :connection "Connection: close\r\n"
                              :server "Server: BoomTown\r\n"
                              :content-type "Content-Type: text/plain\r\n\r\n"
                              :body (str (str (now)) "\r\n")
                              :end "\r\n")))

(defn bind-socket [port]
  (ServerSocket. port))

(defn socket-closed? [socket]
  (.isClosed socket))
