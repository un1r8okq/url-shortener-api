#!/bin/bash
set -e

$version=$(cat version.txt)

docker pull ghcr.io/un1r8okq/url-shortener:$version
docker compose up --detach --env VERSION=$version
