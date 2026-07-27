#!/usr/bin/env bash

set -euo pipefail

project_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
simulator_dir="${project_dir}/simulator"
config_file="${simulator_dir}/devices.json"

if [[ ! -f "${config_file}" ]]; then
    echo "缺少 ${config_file}，请先根据 devices.example.json 创建配置。" >&2
    exit 1
fi

cd "${simulator_dir}"
exec mvn compile exec:java \
    -Dexec.mainClass=com.farmwise.simulator.VirtualDeviceCluster \
    "-Dexec.args=${config_file}"
