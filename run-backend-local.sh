#!/usr/bin/env bash

set -euo pipefail

project_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
env_file="${project_dir}/.env"

if [[ ! -f "${env_file}" ]]; then
    echo "缺少 ${env_file}，请先复制 .env.example 并填写本地配置。" >&2
    exit 1
fi

set -a
# shellcheck disable=SC1090
source "${env_file}"
set +a

required_variables=(
    JWT_SECRET
    SPRING_MAIL_HOST
    SPRING_MAIL_USERNAME
    SPRING_MAIL_PASSWORD
)

if [[ "${MQTT_ENABLED:-false}" == "true" ]]; then
    required_variables+=(
        MQTT_USERNAME
        MQTT_PASSWORD
    )
fi

missing_variables=()

for variable_name in "${required_variables[@]}"; do
    if [[ -z "${!variable_name:-}" ]]; then
        missing_variables+=("${variable_name}")
    fi
done

if (( ${#missing_variables[@]} > 0 )); then
    echo "以下本地环境变量尚未配置：" >&2
    printf '  %s\n' "${missing_variables[@]}" >&2
    exit 1
fi

if ! decoded_jwt_bytes="$(
    printf '%s' "${JWT_SECRET}" |
        base64 --decode 2>/dev/null |
        wc -c
)"; then
    echo "JWT_SECRET 必须是有效的 Base64 字符串。" >&2
    exit 1
fi

if (( decoded_jwt_bytes < 32 )); then
    echo "JWT_SECRET 解码后不能少于 32 字节；可使用 openssl rand -base64 32 重新生成。" >&2
    exit 1
fi

cd "${project_dir}/backend"
exec mvn spring-boot:run
