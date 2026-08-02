<template>
  <article class="min-w-0 rounded-xl border border-gray-100 bg-white p-4">
    <div class="mb-3 flex flex-wrap items-end justify-between gap-3">
      <div>
        <h4 class="text-lg font-semibold text-gray-800">{{ label }}</h4>
        <p class="mt-1 text-sm text-gray-400">{{ sampledPoints.length }} 个绘图点 · 共 {{ points.length }} 条数据</p>
      </div>
      <div v-if="values.length" class="flex gap-4 text-right text-sm text-gray-500">
        <span>最低 <strong class="block text-base text-gray-700">{{ formatMeasurement(minValue) }}</strong></span>
        <span>最新 <strong class="block text-base" :style="{ color }">{{ formatMeasurement(latestValue) }}</strong></span>
        <span>最高 <strong class="block text-base text-gray-700">{{ formatMeasurement(maxValue) }}</strong></span>
      </div>
    </div>

    <div v-if="sampledPoints.length === 0" class="flex h-40 items-center justify-center text-sm text-gray-400">
      当前范围暂无数据
    </div>
    <svg v-else class="block h-48 w-full min-w-0" viewBox="0 0 800 220" role="img" :aria-label="`${label}趋势图`">
      <g v-for="tick in yTicks" :key="tick.value">
        <line x1="58" :y1="tick.y" x2="784" :y2="tick.y" stroke="#e5e7eb" stroke-dasharray="4 4" />
        <text x="50" :y="tick.y + 6" text-anchor="end" fill="#6b7280" font-size="18">{{ formatAxisValue(tick.value) }}</text>
      </g>
      <line x1="58" y1="12" x2="58" y2="184" stroke="#d1d5db" />
      <line x1="58" y1="184" x2="784" y2="184" stroke="#d1d5db" />
      <path :d="linePath" fill="none" :stroke="color" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" />
      <circle v-for="point in visibleDots" :key="point.key" :cx="point.x" :cy="point.y" r="3" :fill="color">
        <title>{{ formatTime(point.time) }}：{{ formatMeasurement(point.value) }}</title>
      </circle>
      <circle v-if="latestProjectedPoint" :cx="latestProjectedPoint.x" :cy="latestProjectedPoint.y"
        r="6" :fill="color" stroke="white" stroke-width="3">
        <title>最新：{{ formatTime(latestProjectedPoint.time) }}，{{ formatMeasurement(latestProjectedPoint.value) }}</title>
      </circle>
      <g v-for="tick in xTicks" :key="tick.key">
        <line :x1="tick.x" y1="184" :x2="tick.x" y2="189" stroke="#9ca3af" />
        <text :x="tick.x" y="210" text-anchor="middle" fill="#6b7280" font-size="18">{{ formatAxisTime(tick.time) }}</text>
      </g>
    </svg>
  </article>
</template>

<script setup>
import { computed } from 'vue'
import { parseUtcDateTime } from '../../utils/dateTime'
import { formatSensorMetricValue } from '../../utils/sensorMetric'

const props = defineProps({
  metric: { type: String, required: true },
  label: { type: String, required: true },
  unit: { type: String, default: '' },
  color: { type: String, default: '#16a34a' },
  points: { type: Array, required: true }
})

const chart = { left: 58, right: 752, top: 12, bottom: 184 }
const maxDrawingPoints = 240

const normalizedPoints = computed(() => props.points
  .map(point => ({ time: parseUtcDateTime(point.time).getTime(), value: Number(point.value) }))
  .filter(point => Number.isFinite(point.time) && Number.isFinite(point.value))
  .sort((left, right) => left.time - right.time))

const sampledPoints = computed(() => {
  const source = normalizedPoints.value
  if (source.length <= maxDrawingPoints) return source
  const interval = (source.length - 1) / (maxDrawingPoints - 1)
  return Array.from({ length: maxDrawingPoints }, (_, index) => source[Math.round(index * interval)])
})

const values = computed(() => normalizedPoints.value.map(point => point.value))
const minValue = computed(() => values.value.length ? Math.min(...values.value) : 0)
const maxValue = computed(() => values.value.length ? Math.max(...values.value) : 0)
const latestValue = computed(() => normalizedPoints.value.at(-1)?.value ?? 0)

const valueRange = computed(() => {
  const range = maxValue.value - minValue.value
  const padding = range === 0 ? Math.max(Math.abs(maxValue.value) * 0.05, 1) : range * 0.08
  return { min: minValue.value - padding, max: maxValue.value + padding }
})

const timeRange = computed(() => ({
  min: sampledPoints.value[0]?.time ?? 0,
  max: sampledPoints.value.at(-1)?.time ?? 1
}))

const projectPoint = (point, index) => {
  const timeSpan = timeRange.value.max - timeRange.value.min
  const valueSpan = valueRange.value.max - valueRange.value.min || 1
  return {
    ...point,
    key: `${point.time}-${index}`,
    x: timeSpan === 0
      ? chart.right
      : chart.left + ((point.time - timeRange.value.min) / timeSpan) * (chart.right - chart.left),
    y: chart.bottom - ((point.value - valueRange.value.min) / valueSpan) * (chart.bottom - chart.top)
  }
}

const projectedPoints = computed(() => sampledPoints.value.map(projectPoint))
const latestProjectedPoint = computed(() => projectedPoints.value.at(-1) ?? null)
const linePath = computed(() => projectedPoints.value.map(
  (point, index) => `${index === 0 ? 'M' : 'L'} ${point.x.toFixed(2)} ${point.y.toFixed(2)}`
).join(' '))
const visibleDots = computed(() => projectedPoints.value.length <= 50 ? projectedPoints.value : projectedPoints.value.filter((_, index) => index % 12 === 0))

const yTicks = computed(() => Array.from({ length: 5 }, (_, index) => {
  const ratio = index / 4
  return {
    value: valueRange.value.max - ratio * (valueRange.value.max - valueRange.value.min),
    y: chart.top + ratio * (chart.bottom - chart.top)
  }
}))

const xTicks = computed(() => {
  const count = Math.min(4, sampledPoints.value.length)
  if (count === 1) return [{ ...projectedPoints.value[0], key: 'x-0' }]
  return Array.from({ length: count }, (_, index) => {
    const pointIndex = Math.round(index * (projectedPoints.value.length - 1) / (count - 1))
    return { ...projectedPoints.value[pointIndex], key: `x-${index}` }
  })
})

const formatValue = value => formatSensorMetricValue(props.metric, value)
const formatMeasurement = value => props.unit === 'k'
  ? `${formatValue(value)}k`
  : `${formatValue(value)}${props.unit ? ` ${props.unit}` : ''}`
const formatAxisValue = value => props.unit === 'k' ? `${formatValue(value)}k` : formatValue(value)
const formatTime = value => new Intl.DateTimeFormat('zh-CN', {
  timeZone: 'Asia/Shanghai', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', hour12: false
}).format(new Date(value))
const formatAxisTime = value => new Intl.DateTimeFormat('zh-CN', {
  timeZone: 'Asia/Shanghai', month: '2-digit', day: '2-digit', hour: '2-digit', hour12: false
}).format(new Date(value))
</script>
