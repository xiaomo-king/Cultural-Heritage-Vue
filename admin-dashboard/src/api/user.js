import request from './index'

export function getUserList(params) {
  return request({ url: '/admin/user/list', method: 'get', params })
}
export function getUserDetail(id) {
  return request({ url: `/admin/user/${id}`, method: 'get' })
}
export function searchUser(params) {
  return request({ url: '/admin/user/search', method: 'get', params })
}
export function toggleUserStatus(id) {
  return request({ url: `/admin/user/toggle-status/${id}`, method: 'post' })
}
