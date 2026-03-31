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

# Демон расшифровки папки.
# Скрипт пишет PID в файл рядом со скриптами, чтобы потом можно было аккуратно остановить процесс.
INPUT_DIR="/opt/gpb/filecrypto/inbox"
OUTPUT_DIR="/opt/gpb/filecrypto/outbox"
CONF_FILE="${PROJECT_DIR}/application-example.properties"
PID_FILE="${SCRIPT_DIR}/gpb-file-crypto-service.pid"
LOG_FILE="${SCRIPT_DIR}/gpb-file-crypto-service-decrypt.log"

nohup java -jar "${JAR_PATH}"   -action decrypt   -mode daemon   -in "${INPUT_DIR}"   -out "${OUTPUT_DIR}"   -conf "${CONF_FILE}"   > "${LOG_FILE}" 2>&1 &

echo $! > "${PID_FILE}"
echo "Started daemon with PID $(cat "${PID_FILE}")"
echo "Log file: ${LOG_FILE}"
