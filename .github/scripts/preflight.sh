#!/usr/bin/env bash
set -euo pipefail

required_values=(
  RELEASE_KEYSTORE_BASE64
  RELEASE_KEY_ALIAS
  RELEASE_STORE_PASSWORD
  RELEASE_KEY_PASSWORD
  APPLICATION_ID
  VERSION_CODE
  VERSION_NAME
)

for name in "${required_values[@]}"; do
  if [[ -z "${!name:-}" ]]; then
    echo "Missing required production value: ${name}" >&2
    exit 1
  fi
done

if ! printf '%s' "$RELEASE_KEYSTORE_BASE64" | base64 --decode >/dev/null 2>&1; then
  echo "RELEASE_KEYSTORE_BASE64 is not valid base64" >&2
  exit 1
fi

if [[ ! "$APPLICATION_ID" =~ ^[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+$ ]]; then
  echo "APPLICATION_ID is not a valid Android application ID" >&2
  exit 1
fi

if [[ ! "$VERSION_CODE" =~ ^[1-9][0-9]*$ ]]; then
  echo "VERSION_CODE must be a positive integer" >&2
  exit 1
fi

echo "Production preflight passed"
