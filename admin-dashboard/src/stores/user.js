import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { loginAPI } from '../api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('admin_token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('admin_user') || '{}'))

  const isLoggedIn = computed(() => !!token.value)
  const username = computed(() => userInfo.value.username || '')
  const nickname = computed(() => userInfo.value.nickname || '')

  async function login(username, password) {
    const data = await loginAPI({ username, password })
    token.value = data.token
    userInfo.value = {
      adminId: data.adminId,
      username: data.username,
      nickname: data.nickname,
      avatarUrl: data.avatarUrl
    }
    localStorage.setItem('admin_token', data.token)
    localStorage.setItem('admin_user', JSON.stringify(userInfo.value))
    return data
  }

  function logout() {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_user')
  }

  return { token, userInfo, isLoggedIn, username, nickname, login, logout }
})
