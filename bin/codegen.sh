#!/bin/bash

which buf > /dev/null 2>&1

if [[ $? -eq 1 ]]; then
    echo "Finner ikke buf, kjør brew install bufbuild/buf/buf"
    exit 1
fi

if [ -d "map-mailer/.git" ]; then
    git -C map-mailer pull
else
    git clone git@github.com:Mattilsynet/map-mailer.git map-mailer
fi

cd map-mailer/protos

cat <<EOF > buf.gen.yaml
version: v1
plugins:
  - plugin: java
    out: ../../gen/java
EOF

buf generate
cd ../..
mkdir -p classes
javac -cp "$(clojure -Spath)" -d classes $(find gen/java -name "*.java")
