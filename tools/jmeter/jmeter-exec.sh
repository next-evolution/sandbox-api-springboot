#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${SCRIPT_DIR}"

execDateTime=`date +'%Y%m%d_%H%M%S'`
jmxPrefix=$1
resultSuffix=$2
threads=${3:-1}
loops=${4:-1}
rampUp=${5:-1}
jmxFile=${jmxPrefix}.jmx
outputDir=result/${resultSuffix}

if [ -z "$1" ] || [ -z "$2" ]; then
  echo "Usage: $0 <jmxPrefix> <resultSuffix> [threads] [loops] [rampUp]"
  echo "  jmxPrefix    : JMX ファイル名のプレフィックス (例: sandbox_100_initialize)"
  echo "  resultSuffix : 結果フォルダの suffix (例: 20240614_001)"
  echo "  threads      : スレッド数 (デフォルト: 1)"
  echo "  loops        : ループ数 (デフォルト: 1)"
  echo "  rampUp       : Ramp-up 秒数 (デフォルト: 1)"
  exit 1
fi

USER_COUNT=0
if [ -f "data/user.csv" ]; then
  USER_COUNT=$(( $(wc -l < "data/user.csv") - 1 ))
fi

mkdir -pv ${outputDir}

echo "execDateTime=${execDateTime}"
echo "jmxPrefix=${jmxPrefix}"
echo "resultSuffix=${resultSuffix}"
echo "threads=${threads}"
echo "loops=${loops}"
echo "rampUp=${rampUp}"
echo "USER_COUNT=${USER_COUNT}"
echo "jmxFile=${jmxFile}"
echo "outputDir=${outputDir}"

jmeter -n \
  -t ${jmxFile} \
  -JresultSuffix=${resultSuffix} \
  -Jthreads=${threads} \
  -Jloops=${loops} \
  -JrampUp=${rampUp} \
  -JUSER_COUNT=${USER_COUNT} \
  |tee -a ${outputDir}/jmeter.log
