import request from './index'

export function getHeritageList(params) {
  return request({ url: '/admin/heritage/list', method: 'get', params })
}
export function getHeritageDetail(id) {
  return request({ url: `/admin/heritage/${id}`, method: 'get' })
}
export function createHeritage(data) {
  return request({ url: '/admin/heritage/create', method: 'post', data })
}
export function updateHeritage(data) {
  return request({ url: '/admin/heritage/update', method: 'post', data })
}
export function deleteHeritage(id) {
  return request({ url: `/admin/heritage/delete/${id}`, method: 'post' })
}
