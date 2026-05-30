import request from '@/utils/request'

export function getStationStatus() {
  return request.get('/bi/station-status')
}

export function getRecentSales(stationId: number, days = 7) {
  return request.get(`/bi/station/${stationId}/recent-sales`, { params: { days } })
}

export function getDataHealth() {
  return request.get('/bi/data-health')
}

export function getForecastAccuracy() {
  return request.get('/bi/forecast-accuracy')
}

export function getInventoryCompare() {
  return request.get('/bi/inventory-compare')
}
