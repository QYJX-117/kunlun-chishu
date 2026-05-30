import request from '@/utils/request'

export function runMrp(data: { stationId: number; materialId: number; forecastDays?: number }) {
  return request.post('/mrp/run', data)
}
