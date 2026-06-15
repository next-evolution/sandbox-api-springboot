#!/bin/bash
# ローカル初期構築用スクリプト。DB 再構築後に1回だけ実行する。
# 追加の初期化 JMX ができたらここに直列実行を追加する。

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TEST_DIR="${SCRIPT_DIR}/.."

suffix=$1
if [ -z "$1" ]; then
  echo "Usage: $0 <resultSuffix>"
  echo "  resultSuffix : 結果フォルダの suffix（例: 20240614_001）"
  exit 1
fi

"${TEST_DIR}/jmeter-exec.sh" setup/sandbox_100_initialize "${suffix}"
