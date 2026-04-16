<template>
  <router-view />
</template>

<script setup>
import UserProfile from './components/user-profile.vue'
import router from './router';
</script>

<style scoped>
h1 {
  color: blue;
}

button {
  padding: 10px;
}
</style>