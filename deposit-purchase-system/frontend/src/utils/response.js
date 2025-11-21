export function unwrapResponseData(response, fallbackValue = null) {
  if (!response) {
    return fallbackValue;
  }
  const payload = response.data;
  if (payload === undefined || payload === null) {
    return fallbackValue;
  }
  if (
    typeof payload === 'object' &&
    !Array.isArray(payload) &&
    Object.prototype.hasOwnProperty.call(payload, 'data') &&
    (Object.prototype.hasOwnProperty.call(payload, 'code') ||
      Object.prototype.hasOwnProperty.call(payload, 'success'))
  ) {
    return payload.data ?? fallbackValue;
  }
  return payload;
}
