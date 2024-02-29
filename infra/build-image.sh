#!/bin/bash
docker build -t url-shortener .
docker tag url-shortener ghcr.io/un1r8okq/url-shortener:LATEST
