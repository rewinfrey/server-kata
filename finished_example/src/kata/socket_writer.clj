(ns kata.socket-writer
  (import java.io.ByteArrayOutputStream))

(defn write-socket [output-stream response]
  (.write output-stream (.getBytes (response :status)))
  (.write output-stream (.getBytes (response :connection)))
  (.write output-stream (.getBytes (response :server)))
  (.write output-stream (.getBytes (response :content-type)))
  (.write output-stream (.getBytes (response :body)))
  (.write output-stream (.getBytes "\r\n")))

