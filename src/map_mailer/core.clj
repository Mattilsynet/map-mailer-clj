(ns map-mailer.core
  (:import (no.mattilsynet.map.email.v2
            Attachment Attachment$Builder
            Content Content$Builder
            Email Email$Builder
            Headers Headers$Builder
            Recipient Recipient$Builder
            Recipients Recipients$Builder)))

(defn ->Attachment [{:keys [name content_type content_in_base64]}]
  (cond-> ^Attachment$Builder (Attachment/newBuilder)
    name (.setName name)
    content_type (.setContentType content_type)
    content_in_base64 (.setContentInBase64 content_in_base64)
    :then .build))

(defn Attachment->map [^Attachment attachment]
  {:name (.getName attachment)
   :content_type (.getContentType attachment)
   :content_in_base64 (.getContentInBase64 attachment)})

(defn ->Content [{:keys [subject plain_text html]}]
  (cond-> ^Content$Builder (Content/newBuilder)
    subject (.setSubject subject)
    plain_text (.setPlainText plain_text)
    html (.setHtml html)
    :then .build))

(defn Content->map [^Content content]
  {:subject (.getSubject content)
   :plain_text (.getPlainText content)
   :html (.getHtml content)})

(defn ->Headers [{:keys [client_correlation_id client_custom_header_name]}]
  (cond-> ^Headers$Builder (Headers/newBuilder)
    client_correlation_id (.setClientCorrelationId client_correlation_id)
    client_custom_header_name (.setClientCustomHeaderName client_custom_header_name)
    :then .build))

(defn Headers->map [^Headers headers]
  {:client_correlation_id (.getClientCorrelationId headers)
   :client_custom_header_name (.getClientCustomHeaderName headers)})

(defn ->Recipient [{:keys [address display_name]}]
  (cond-> ^Recipient$Builder (Recipient/newBuilder)
    address (.setAddress address)
    display_name (.setDisplayName display_name)
    :then .build))

(defn Recipient->map [^Recipient recipient]
  {:address (.getAddress recipient)
   :display_name (.getDisplayName recipient)})

(defn ->Recipients [{:keys [to cc bcc]}]
  (let [builder ^Recipients$Builder (Recipients/newBuilder)]
    (.addAllTo builder ^Recipient (mapv ->Recipient to))
    (.addAllCc builder ^Recipient (mapv ->Recipient cc))
    (.addAllBcc builder ^Recipient (mapv ->Recipient bcc))
    (.build builder)))

(defn Recipients->map [^Recipients recipients]
  (let [to (.getToList recipients)
        cc (.getCcList recipients)
        bcc (.getBccList recipients)]
    (cond-> {}
      (seq to) (assoc :to (mapv Recipient->map to))
      (seq cc) (assoc :cc (mapv Recipient->map cc))
      (seq bcc) (assoc :bcc (mapv Recipient->map bcc)))))

(defn ->Email [{:keys [headers sender_address content recipients
                       attachments reply_to
                       user_engagement_tracking_disabled]}]
  (cond-> ^Email$Builder (Email/newBuilder)
    attachments (.addAllAttachments (mapv ->Attachment attachments))
    content (.setContent (->Content content))
    headers (.setHeaders (->Headers headers))
    recipients (.setRecipients (->Recipients recipients))
    reply_to (.setReplyTo (->Recipient reply_to))
    sender_address (.setSenderAddress sender_address)
    (boolean? user_engagement_tracking_disabled)
    (.setUserEngagementTrackingDisabled user_engagement_tracking_disabled)
    :then .build))

(defn Email->map [^Email email]
  (let [attachments (mapv Attachment->map (.getAttachmentsList email))
        headers (.getHeaders email)
        reply_to (.getReplyTo email)]
    (cond-> {:sender_address (.getSenderAddress email)
             :recipients (Recipients->map (.getRecipients email))
             :content (Content->map (.getContent email))
             :user_engagement_tracking_disabled (.getUserEngagementTrackingDisabled email)}
      (seq attachments) (assoc :attachments attachments)
      headers (assoc :headers (Headers->map headers))
      reply_to (assoc :reply_to (Recipient->map reply_to)))))

(defn email->protobuf-bytes [email]
  (.toByteArray (->Email email)))
