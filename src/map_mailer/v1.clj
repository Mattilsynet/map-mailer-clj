(ns map-mailer.v1
  (:import (com.google.protobuf Timestamp Timestamp$Builder)
           (java.time Instant ZoneId)
           (no.mattilsynet.map.email.v1
            Attachment Attachment$Builder
            Body Body$Builder
            DateTime DateTime$Builder
            EmailAddress EmailAddress$Builder
            Flag Flag$Builder
            Flag Flag$Builder
            InternetMessageHeader InternetMessageHeader$Builder
            Message Message$Builder
            Recipient Recipient$Builder
            Response Response$Status Response$Builder))
  (:require [clojure.set :as set]))

(defn ->Attachment [{:keys [o_data_type name content_type content_bytes]}]
  (cond-> ^Attachment$Builder (Attachment/newBuilder)
    o_data_type (.setODataType o_data_type)
    name (.setName name)
    content_type (.setContentType content_type)
    content_bytes (.setContentBytes content_bytes)
    :then .build))

(defn Attachment->map [^Attachment attachment]
  {:o_data_type (.getODataType attachment)
   :name (.getName attachment)
   :content_type (.getContentType attachment)
   :content_bytes (.getContentBytes attachment)})

(def content-types
  {:content.type/text "Text"
   :content.type/html "HTML"})

(def s->content-type (set/map-invert content-types))

(defn ->Body [{:keys [content_type content]}]
  (cond-> ^Body$Builder (Body/newBuilder)
    content_type (.setContentType (get content-types content_type))
    content (.setContent content)
    :then .build))

(defn Body->map [^Body body]
  (let [content-type (s->content-type (.getContentType body))]
    (cond-> {:content (.getContent body)}
      content-type (assoc :content_type content-type))))

(defn ->EmailAddress [address]
  (-> ^EmailAddress$Builder (EmailAddress/newBuilder)
      (.setAddress address)
      .build))

(defn EmailAddress->s [^EmailAddress email-address]
  (.getAddress email-address))

(defn ->Recipient [email-address]
  (-> ^Recipient$Builder (Recipient/newBuilder)
      (.setEmailAddress (->EmailAddress email-address))
      .build))

(defn Recipient->s [^Recipient recipient]
  (EmailAddress->s (.getEmailAddress recipient)))

(defn ->DateTime [zdt]
  (-> ^DateTime$Builder (DateTime/newBuilder)
      (.setTimeZone (.getId (.getZone zdt)))
      (.setDateTime (str (.toInstant zdt)))
      .build))

(defn DateTime->zdt [^DateTime date-time]
  (-> (Instant/parse (.getDateTime date-time))
      (.atZone (ZoneId/of (.getTimeZone date-time)))))

(defn ->Flag [{:keys [flag_status start_date_time due_date_time]}]
  (cond-> ^Flag$Builder (Flag/newBuilder)
    flag_status (.setFlagStatus flag_status)
    start_date_time (.setStartDateTime (->DateTime start_date_time))
    due_date_time (.setDueDateTime (->DateTime due_date_time))
    :then .build))

(defn Flag->map [^Flag flag]
  {:flag_status (.getFlagStatus flag)
   :start_date_time (DateTime->zdt (.getStartDateTime flag))
   :due_date_time (DateTime->zdt (.getDueDateTime flag))})


(def statuses
  {:response.status/unspecified Response$Status/STATUS_UNSPECIFIED
   :response.status/success Response$Status/STATUS_SUCCESS
   :response.status/error Response$Status/STATUS_ERROR})

(def status->kw (set/map-invert statuses))

(defn ->Timestamp [^Instant inst]
  (-> ^Timestamp$Builder (Timestamp/newBuilder)
      (.setSeconds (.getEpochSecond inst))
      (.setNanos (.getNano inst))
      .build))

(defn Timestamp->inst [^Timestamp timestamp]
  (Instant/ofEpochSecond (.getSeconds timestamp) (.getNanos timestamp)))

(defn ->Response [{:keys [status time_stamp error_message stream_name sequence_number]}]
  (cond-> ^Response$Builder (Response/newBuilder)
    status (.setStatus (get statuses status))
    time_stamp (.setTimeStamp (->Timestamp time_stamp))
    error_message (.setErrorMessage error_message)
    stream_name (.setStreamName stream_name)
    sequence_number (.setSequenceNumber sequence_number)
    :then .build))

(defn Response->map [^Response response]
  {:status (status->kw (.getStatus response))
   :time_stamp (Timestamp->inst (.getTimeStamp response))
   :error_message (.getErrorMessage response)
   :stream_name (.getStreamName response)
   :sequence_number (.getSequenceNumber response)})

(defn ->InternetMessageHeader [{:keys [name value]}]
  (cond-> ^InternetMessageHeader$Builder (InternetMessageHeader/newBuilder)
    name (.setName name)
    value (.setValue value)
    :then .build))

(defn InternetMessageHeader->map [^InternetMessageHeader header]
  {:name (.getName header)
   :value (.getValue header)})

(defn ->Message [{:keys [subject body to_recipients cc_recipients
                         internet_message_headers attachments flag save_to_sent_items]}]
  (cond-> ^Message$Builder (Message/newBuilder)
    subject (.setSubject subject)
    body (.setBody (->Body body))
    (seq to_recipients) (.addAllToRecipients (mapv ->Recipient to_recipients))
    (seq cc_recipients) (.addAllCcRecipients (mapv ->Recipient cc_recipients))
    (seq internet_message_headers) (.addAllInternetMessageHeaders (mapv ->InternetMessageHeader internet_message_headers))
    (seq attachments) (.addAllAttachments (mapv ->Attachment attachments))
    flag (.setFlag (->Flag flag))
    (boolean? save_to_sent_items) (.setSaveToSentItems save_to_sent_items)
    :then .build))

(defn Message->map [^Message message]
  {:subject (.getSubject message)
   :body (Body->map (.getBody message))
   :to_recipients (mapv Recipient->s (.getToRecipientsList message))
   :cc_recipients (mapv Recipient->s (.getCcRecipientsList message))
   :internet_message_headers (mapv InternetMessageHeader->map (.getInternetMessageHeadersList message))
   :attachments (mapv Attachment->map (.getAttachmentsList message))
   :flag (Flag->map (.getFlag message))
   :save_to_sent_items (.getSaveToSentItems message)})

(defn ^:export message->protobuf-bytes [message]
  (.toByteArray (->Message message)))
