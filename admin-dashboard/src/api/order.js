import request from './index'

export function getOrderList(params) {
  return request({ url: '/admin/order/list', method: 'get', params })
}
export function getOrderDetail(id) {
  return request({ url: `/admin/order/${id}`, method: 'get' })
}
export function shipOrder(data) {
  return request({ url: '/admin/order/ship', method: 'post', data })
}
export function updateOrderStatus(data) {
  return request({ url: '/admin/order/status', method: 'post', data })
}
