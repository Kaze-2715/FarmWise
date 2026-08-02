const UTC_OFFSET_PATTERN = /(?:[zZ]|[+-]\d{2}:?\d{2})$/;

export const parseUtcDateTime = value => {
  if (value instanceof Date || typeof value !== 'string') {
    return new Date(value);
  }

  const normalizedValue = value.trim();
  const utcValue = UTC_OFFSET_PATTERN.test(normalizedValue)
    ? normalizedValue
    : `${normalizedValue}Z`;

  return new Date(utcValue);
};
