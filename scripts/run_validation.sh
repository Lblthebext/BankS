#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")"/.. && pwd)"
BACKEND_DIR="$ROOT_DIR/deposit-purchase-system/backend"
FRONTEND_DIR="$ROOT_DIR/deposit-purchase-system/frontend"

parse_proxy() {
  local proxy_value="$1"
  proxy_value="${proxy_value#http://}"
  proxy_value="${proxy_value#https://}"
  PROXY_HOST="${proxy_value%%:*}"
  PROXY_PORT="${proxy_value##*:}"
}

if [[ -z "${MAVEN_OPTS:-}" ]]; then
  MAVEN_OPTS=""
fi

if [[ -n "${https_proxy:-${HTTPS_PROXY:-}}" ]]; then
  parse_proxy "${https_proxy:-${HTTPS_PROXY}}"
  if [[ -n "$PROXY_HOST" && -n "$PROXY_PORT" ]]; then
    MAVEN_OPTS+=" -Dhttps.proxyHost=$PROXY_HOST -Dhttps.proxyPort=$PROXY_PORT"
    MAVEN_OPTS+=" -Dhttp.proxyHost=$PROXY_HOST -Dhttp.proxyPort=$PROXY_PORT"
  fi
fi

export MAVEN_OPTS
export npm_config_proxy="${http_proxy:-${HTTP_PROXY:-}}"
export npm_config_https_proxy="${https_proxy:-${HTTPS_PROXY:-}}"

echo "[1/3] Building backend (skip unit tests)."
mvn -f "$BACKEND_DIR/pom.xml" clean package -DskipTests

echo "[2/3] Installing frontend dependencies."
npm --prefix "$FRONTEND_DIR" install

echo "[3/3] Building frontend bundle."
npm --prefix "$FRONTEND_DIR" run build
