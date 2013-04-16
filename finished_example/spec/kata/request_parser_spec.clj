(ns kata.request-parser-spec
  (:use speclj.core)
  (:require [kata.request-parser :as parser])
  (import java.io.ByteArrayInputStream))

(defn mock-input []
  (ByteArrayInputStream. (.getBytes "GET / HTTP/1.1\r\n")))

(describe "parse-request"
  (it "returns a hash map of the request line's key value pairs"
      (should= { :uri "/" :method "GET" } (parser/parse-request (mock-input)))))
