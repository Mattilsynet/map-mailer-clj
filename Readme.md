# map-mailer-clj

Clojure-wrapper for Protobuf-kontrakten til Mattilsynets map-mailer-API.
Inneholder tooling for å generere bøtter og spann med Java-kode samt å kompilere
den, og tilbyr noen Clojure-funksjoner for å jobbe med resultatet.

## API

Se de respektive proto-filene for oversikt over felter osv. Alle navn er helt
like som i definisjonen.

[`email.proto`
V1](https://github.com/Mattilsynet/map-mailer/blob/master/protos/no/mattilsynet/map/email/v1/email.proto):

```clj
(require '[map-mailer.v1 :as mailer])

(mailer/message->protobuf-bytes
 {:sender_address "christian.johansen@mattilsynet.no"
  :recipients {:to [{:address "magnar.sveen@mattilsynet.no"
                     :display_name "Magnar Sveen"}]}
  :content {:subject "Hello there!"
            :plain_text "This is my email"
            :html "<h1>This is my email</h1>"}}{:subject "Howdy!"
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
```

[`email.proto` V2](https://github.com/Mattilsynet/map-mailer/blob/master/protos/no/mattilsynet/map/email/v2/email.proto):

```clj
(require '[map-mailer.v2 :as mailer])

(mailer/email->protobuf-bytes
 {:sender_address "christian.johansen@mattilsynet.no"
  :recipients {:to [{:address "magnar.sveen@mattilsynet.no"
                     :display_name "Magnar Sveen"}]}
  :content {:subject "Hello there!"
            :plain_text "This is my email"
            :html "<h1>This is my email</h1>"}})
```

V2 skal etter sigende ikke brukes nå.

## Generer kode

```sh
bin/codegen.sh
```

## Jobbe med koden

Du må generere Java-koden og få kompilert klassene for å kunne starte et REPL,
se over.

## Publisere

bin/publish.sh
