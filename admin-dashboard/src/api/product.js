import request from './index'

export function getProductList(params) {
  return request({ url: '/admin/product/list', method: 'get', params })
}
export function getProductDetail(id) {
  return request({ url: `/admin/product/${id}`, method: 'get' })
}
export function searchProduct(params) {
  return request({ url: '/admin/product/search', method: 'get', params })
}
export function createProduct(data) {
  return request({ url: '/admin/product/create', method: 'post', data })
}
export function updateProduct(data) {
  return request({ url: '/admin/product/update', method: 'post', data })
}
export function deleteProduct(id) {
  return request({ url: `/admin/product/delete/${id}`, method: 'post' })
}
