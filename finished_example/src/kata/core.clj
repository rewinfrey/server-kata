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

(defn start-server [port handler]
  (let [server-socket (bind-socket port)]
    (while (not (socket-closed? server-socket))
    (try
      (let [socket (. server-socket accept)
            request (parser/parse-request (.getInputStream socket))]
        (handler request socket))
    (catch SocketException e)))
    (.close server-socket)))

(defn sequential []
  (fn [request socket]
    (let [response ((routes) (request :uri))]
        (writer/write-socket (.getOutputStream socket) response)
        (.close socket))))

(defn concurrent []
  (fn [request socket]
    (future
      ((sequential) request socket))))

(defn -main []
  (future (start-server 7005 (sequential)))
  (future (start-server 7010 (concurrent))))
