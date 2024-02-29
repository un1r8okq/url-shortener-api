#!/bin/bash
set -e

openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -sha256 -days 3650 -nodes -subj "/C=XX/ST=StateName/L=CityName/O=CompanyName/OU=CompanySectionName/CN=CommonNameOrHostname"
openssl pkcs12 -export -inkey key.pem -in cert.pem -out cert.pfx
mkdir -p ./api/src/main/resources/keystore
cp cert.pfx ./api/src/main/resources/keystore/
