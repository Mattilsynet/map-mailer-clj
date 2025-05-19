#!/bin/bash

set -xe

bin/codegen.sh

rm -rf classes/no/mattilsynet/map/email/v1
rm -f map-mailer-clj.jar

clojure -M:dev:jar

mvn deploy:deploy-file \
    -Dfile=map-mailer-clj.jar \
    -DrepositoryId=clojars \
    -Durl=https://clojars.org/repo \
    -DpomFile=pom.xml
