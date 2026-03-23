#!/usr/bin/env bash
set -euo pipefail

java -jar build/libs/file-crypto-service-1.0.0.jar \
  -action encrypt \
  -mode default \
  -in /tmp/input.txt
