(ns map-mailer.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [map-mailer.core :as mailer]))

(def attachment
  {:name "File.csv"
   :content_type "text/plain"
   :content_in_base64 "SGVsbG8="})

(deftest attachment-test
  (testing "Maps attachment"
    (is (= (mailer/Attachment->map (mailer/->Attachment attachment))
           attachment))))

(def headers
  {:client_correlation_id "1"
   :client_custom_header_name "X-Wat"})

(deftest headers-test
  (testing "Maps headers"
    (is (= (mailer/Headers->map (mailer/->Headers headers))
           headers))))

(def content
  {:subject "Hello there!"
   :plain_text "This is my email"
   :html "<h1>This is my email</h1>"})

(deftest content-test
  (testing "Maps content"
    (is (= (mailer/Content->map (mailer/->Content content))
           content))))

(def christian
  {:address "christian.johansen@mattilsynet.no"
   :display_name "Christian Johansen"})

(def recipients
  {:to
   [christian]

   :cc
   [{:address "magnar.sveen@mattilsynet.no"
     :display_name "Magnar Sveen"}]

   :bcc
   [{:address "teodor.lunaas.heggelund@mattilsynet.no"
     :display_name "Teodor Lunaas Heggelund"}]})

(deftest recipients-test
  (testing "Maps single recipient"
    (is (= (mailer/Recipient->map (mailer/->Recipient christian))
           christian)))

  (testing "Maps recipients"
    (is (= (mailer/Recipients->map (mailer/->Recipients recipients))
           recipients))))

(deftest email-test
  (testing "Maps Email"
    (is (= (-> {:headers headers
                :sender_address "christian.johansen@mattilsynet.no"
                :content content
                :recipients recipients
                :attachments [attachment]
                :reply_to christian
                :user_engagement_tracking_disabled true}
               mailer/->Email
               mailer/Email->map)
           {:sender_address "christian.johansen@mattilsynet.no"
            :recipients {:to [{:address "christian.johansen@mattilsynet.no"
                               :display_name "Christian Johansen"}]
                         :cc [{:address "magnar.sveen@mattilsynet.no"
                               :display_name "Magnar Sveen"}]
                         :bcc [{:address "teodor.lunaas.heggelund@mattilsynet.no"
                                :display_name "Teodor Lunaas Heggelund"}]}
            :content {:subject "Hello there!"
                      :plain_text "This is my email"
                      :html "<h1>This is my email</h1>"}
            :user_engagement_tracking_disabled true
            :attachments [{:name "File.csv"
                           :content_type "text/plain"
                           :content_in_base64 "SGVsbG8="}]
            :headers {:client_correlation_id "1"
                      :client_custom_header_name "X-Wat"}
            :reply_to {:address "christian.johansen@mattilsynet.no"
                       :display_name "Christian Johansen"}}))))
