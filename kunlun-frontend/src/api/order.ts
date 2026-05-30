import request from '@/utils/request'

export function getOrders(params: { status?: string; page: number; size: number }) {
  return request.get('/orders', { params })
}

export function confirmOrder(id: number, userId = 0) {
  return request.put(`/orders/${id}/confirm`, null, { params: { userId } })
}

export function completeOrder(id: number) {
  return request.put(`/orders/${id}/complete`)
}

export function cancelOrder(id: number) {
  return request.put(`/orders/${id}/cancel`)
}
