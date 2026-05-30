import request from '@/utils/request'

const crud = (path: string) => ({
  page: (params: any) => request.get(path, { params }),
  getById: (id: number) => request.get(`${path}/${id}`),
  save: (data: any) => request.post(path, data),
  update: (id: number, data: any) => request.put(`${path}/${id}`, data),
  delete: (id: number) => request.delete(`${path}/${id}`)
})

export const stationApi = crud('/stations')
export const materialApi = crud('/materials')
export const userApi = crud('/users')

export function getPredictionLogs(params: any) {
  return request.get('/prediction/logs', { params })
}

export function getDispatchRoutes(params: any) {
  return request.get('/dispatch/routes', { params })
}

export function getEquipmentList(params: any) {
  return request.get('/equipment/list', { params })
}
