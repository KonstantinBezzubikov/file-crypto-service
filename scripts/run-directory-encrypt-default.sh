#!/usr/bin/env bash
set -euo pipefail

# GPB file crypto service launcher.
# Перед запуском при необходимости поправьте пути ниже.

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
JAR_PATH="${PROJECT_DIR}/build/libs/gpb-file-crypto-service-1.0.0.jar"

if [[ ! -f "${JAR_PATH}" ]]; then
  echo "Jar not found: ${JAR_PATH}"
  echo "Build the project first: gradle clean build"
  exit 1
fi

# Одноразовая обработка папки на шифрование.
# После успешной обработки исходные файлы будут удалены.
INPUT_DIR="/opt/gpb/filecrypto/inbox"
OUTPUT_DIR="/opt/gpb/filecrypto/outbox"
CONF_FILE="${PROJECT_DIR}/application-example.properties"

java -jar "${JAR_PATH}"   -action encrypt   -mode default   -in "${INPUT_DIR}"   -out "${OUTPUT_DIR}"   -conf "${CONF_FILE}"
