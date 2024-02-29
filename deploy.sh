#!/bin/bash
docker build -t url-shortener .
docker tag url-shortener ghcr.io/un1r8okq/url-shortener:LATEST
echo GITHUB_TOKEN | docker login ghcr.io -u un1r8okq --password-stdin
docker push ghcr.io/un1r8okq/url-shortener:LATEST