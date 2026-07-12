<template>
  <router-view />
</template>

<script setup>
import { watch } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

// 路由变化时更新页面标题
watch(() => route.meta?.title, (title) => {
  if (title) {
    document.title = title + ' - 江右拾遗管理后台'
  } else {
    document.title = '江右拾遗管理后台'
  }
}, { immediate: true })
</script>

<style>
html, body, #app {
  margin: 0;
  padding: 0;
  height: 100%;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', sans-serif;
}
</style>
