import request from './index'

export function getCommentList(params) {
  return request({ url: '/admin/comment/list', method: 'get', params })
}
export function deleteComment(id) {
  return request({ url: `/admin/comment/delete/${id}`, method: 'post' })
}
