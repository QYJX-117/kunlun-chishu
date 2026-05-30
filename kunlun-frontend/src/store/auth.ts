import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<any>(null)
  const loggedIn = ref(true)

  function setUser(u: any) {
    user.value = u
    loggedIn.value = true
  }

  function logout() {
    user.value = null
    loggedIn.value = false
  }

  return { user, loggedIn, setUser, logout }
})
