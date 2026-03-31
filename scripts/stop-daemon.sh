#!/usr/bin/env bash
set -euo pipefail

# Аккуратная остановка ранее запущенного daemon-процесса.
# Сначала используется обычный TERM, потом при необходимости можно добить процесс вручную.

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PID_FILE="${SCRIPT_DIR}/gpb-file-crypto-service.pid"

if [[ ! -f "${PID_FILE}" ]]; then
  echo "PID file not found: ${PID_FILE}"
  exit 1
fi

PID="$(cat "${PID_FILE}")"
if [[ -z "${PID}" ]]; then
  echo "PID file is empty"
  exit 1
fi

kill "${PID}"
echo "Stop signal sent to PID ${PID}"
rm -f "${PID_FILE}"
