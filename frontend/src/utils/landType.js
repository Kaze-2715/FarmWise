export const landTypeOptions = [
  { value: 'paddy', label: '水田' },
  { value: 'dryland', label: '旱地' },
  { value: 'greenhouse', label: '温室' }
];

const landTypeLabels = new Map(
  landTypeOptions.map(option => [option.value, option.label])
);

export const getLandTypeLabel = landType => landTypeLabels.get(landType) || landType || '--';
