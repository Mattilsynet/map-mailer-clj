(ns map-mailer.v1-test
  (:require [clojure.test :refer [deftest is testing]]
            [map-mailer.v1 :as mailer])
  (:import (java.time Instant ZoneId)))

(def attachment
  {:o_data_type "?"
   :name "File.csv"
   :content_type "text/plain"
   :content_bytes "Hello"})

(deftest attachment-test
  (testing "Maps attachment"
    (is (= (mailer/Attachment->map (mailer/->Attachment attachment))
           attachment))))

(def body
  {:content_type :content.type/text
   :content "Hello world!"})

(deftest body-test
  (testing "Maps body"
    (is (= (mailer/Body->map (mailer/->Body body))
           body))))

(deftest email-address-test
  (testing "Maps email address"
    (is (= (mailer/EmailAddress->s (mailer/->EmailAddress "christian.johansen@mattilsynet.no"))
           "christian.johansen@mattilsynet.no"))))

(deftest recipient-test
  (testing "Maps recipient address"
    (is (= (mailer/Recipient->s (mailer/->Recipient "christian.johansen@mattilsynet.no"))
           "christian.johansen@mattilsynet.no"))))

(def zdt
  (-> (.toInstant #inst "2025-05-22T12:00:00Z")
      (.atZone (ZoneId/of "Europe/Oslo"))))

(deftest date-time-test
  (testing "Maps zdts to date times"
    (is (= (mailer/DateTime->zdt (mailer/->DateTime zdt))
           zdt))))

(def flag
  {:flag_status "???"
   :start_date_time zdt
   :due_date_time (-> (.toInstant #inst "2025-05-22T14:00:00Z")
                      (.atZone (ZoneId/of "Europe/Oslo")))})

(deftest flag-test
  (testing "Maps flags"
    (is (= (mailer/Flag->map (mailer/->Flag flag))
           flag))))

(def inst (Instant/now))

(deftest timestamp-test
  (testing "Maps timestamp"
    (is (= (mailer/Timestamp->inst (mailer/->Timestamp inst))
           inst))))

(def response
  {:status :response.status/success
   :time_stamp inst
   :error_message "Oh noes!"
   :stream_name "emails"
   :sequence_number 10})

(deftest response-test
  (testing "Maps response"
    (is (= (mailer/Response->map (mailer/->Response response))
           response))))

(def header
  {:name "Content-Type"
   :value "text/plain"})

(deftest header-test
  (testing "Maps header"
    (is (= (mailer/InternetMessageHeader->map (mailer/->InternetMessageHeader header))
           header))))

(def message
  {:subject "Howdy!"
   :body {:content_type :content.type/text
          :content "Hello world!"}
   :to_recipients ["christian.johansen@mattilsynet.no"]
   :cc_recipients ["magnar.sveen@mattilsynet.no"]
   :internet_message_headers [{:name "X-lol" :value "Haha!"}]
   :attachments [{:o_data_type "?"
                  :name "File.csv"
                  :content_type "text/plain"
                  :content_bytes "Hello"}]
   :flag {:flag_status "???"
          :start_date_time zdt
          :due_date_time (-> (.toInstant #inst "2025-05-22T14:00:00Z")
                             (.atZone (ZoneId/of "Europe/Oslo")))}
   :save_to_sent_items true})

(deftest message-test
  (testing "Maps message"
    (is (= (mailer/Message->map (mailer/->Message message))
           message))))
