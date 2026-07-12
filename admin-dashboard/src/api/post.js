import request from './index'

export function getPostList(params) {
  return request({ url: '/admin/post/list', method: 'get', params })
}
export function getPostDetail(id) {
  return request({ url: `/admin/post/${id}`, method: 'get' })
}
export function deletePost(id) {
  return request({ url: `/admin/post/delete/${id}`, method: 'post' })
}
