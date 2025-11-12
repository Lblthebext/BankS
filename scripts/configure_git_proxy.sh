#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<USAGE
Usage: $0 [enable|disable|status] [proxy-url]

Commands:
  enable [proxy-url]  Set git http/https proxy. Defaults to http://127.0.0.1:7890 if proxy-url not provided.
  disable             Remove git http/https proxy configuration.
  status              Show the currently configured git proxy values.
USAGE
}

if [[ $# -lt 1 ]]; then
  usage
  exit 1
fi

command="$1"
proxy_url="${2:-http://127.0.0.1:7890}"

case "$command" in
  enable)
    git config --global http.proxy "$proxy_url"
    git config --global https.proxy "$proxy_url"
    echo "Configured git proxy to $proxy_url"
    ;;
  disable)
    git config --global --unset http.proxy || true
    git config --global --unset https.proxy || true
    echo "Cleared git proxy configuration"
    ;;
  status)
    current_http=$(git config --global --get http.proxy || echo "<unset>")
    current_https=$(git config --global --get https.proxy || echo "<unset>")
    echo "http.proxy: $current_http"
    echo "https.proxy: $current_https"
    ;;
  *)
    usage
    exit 1
    ;;
esac
