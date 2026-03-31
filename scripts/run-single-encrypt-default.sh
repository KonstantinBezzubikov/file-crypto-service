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

# Одноразовое шифрование одного файла.
# Исходный файл после успешной обработки будет возвращён на место.
INPUT_FILE="/opt/gpb/filecrypto/input/report.txt"
OUTPUT_FILE=""
CRL_FILE=""

CMD=(java -jar "${JAR_PATH}" -action encrypt -mode default -in "${INPUT_FILE}")
if [[ -n "${OUTPUT_FILE}" ]]; then
  CMD+=( -out "${OUTPUT_FILE}" )
fi
if [[ -n "${CRL_FILE}" ]]; then
  CMD+=( -crl-file "${CRL_FILE}" )
fi

"${CMD[@]}"
