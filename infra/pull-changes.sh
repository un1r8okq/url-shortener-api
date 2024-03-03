#!/bin/bash
set -e

docker compose --env-file version.txt --env-file .env up --detach
