# map-mailer-clj

Clojure-wrapper for Protobuf-kontrakten til Mattilsynets map-mailer-API.
Inneholder tooling for å generere bøtter og spann med Java-kode samt å kompilere
den, og tilbyr noen Clojure-funksjoner for å jobbe med resultatet.

## API

```clj
(require '[map-mailer.core :as mailer])

(mailer/email->protobuf-bytes
 {:sender_address "christian.johansen@mattilsynet.no"
  :recipients {:to [{:address "magnar.sveen@mattilsynet.no"
                     :display_name "Magnar Sveen"}]}
  :content {:subject "Hello there!"
            :plain_text "This is my email"
            :html "<h1>This is my email</h1>"}})
```

For oversikt over felter, se `json_name` på de ymse feltene i
[email.proto](https://github.com/Mattilsynet/map-mailer/blob/master/protos/no/mattilsynet/map/email/v2/email.proto).

## Generer kode

```sh
bin/codegen.sh
```

## Jobbe med koden

Du må generere Java-koden og få kompilert klassene for å kunne starte et REPL,
se over.

## Publisere

bin/publish.sh
