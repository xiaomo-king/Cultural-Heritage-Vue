import request from './index'

export function loginAPI(data) {
  return request({
    url: '/admin/login',
    method: 'post',
    data
  })
}
