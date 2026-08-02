const INTEGER_METRICS = new Set(['soil_moisture', 'air_humidity']);

export const formatSensorMetricValue = (metric, value) => {
  if (value === null || value === undefined || value === '') return '--';

  const number = Number(value);
  if (!Number.isFinite(number)) return String(value);

  if (INTEGER_METRICS.has(metric)) {
    return number.toLocaleString('zh-CN', {
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    });
  }

  if (metric === 'air_temperature') {
    return number.toLocaleString('zh-CN', {
      minimumFractionDigits: 1,
      maximumFractionDigits: 1
    });
  }

  return number.toLocaleString('zh-CN', { maximumFractionDigits: 2 });
};
