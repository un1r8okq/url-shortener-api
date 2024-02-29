#!/bin/bash
echo GITHUB_TOKEN | docker login ghcr.io -u un1r8okq --password-stdin
docker push ghcr.io/un1r8okq/url-shortener:LATEST
