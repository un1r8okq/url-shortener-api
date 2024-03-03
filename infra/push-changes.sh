#!/bin/bash
set -e

export $(cat .env | xargs)
export $(cat version.txt | xargs)

docker build -t url-shortener .
docker tag url-shortener ghcr.io/un1r8okq/url-shortener:$VERSION
echo $GITHUB_TOKEN | docker login ghcr.io -u un1r8okq --password-stdin
docker push ghcr.io/un1r8okq/url-shortener:$VERSION
