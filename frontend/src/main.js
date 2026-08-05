import { createApp } from 'vue'
import { setUnauthorizedHandler } from './api/client.js';
import { clearCurrentUser } from './composables/useAuthSession.js';
import { useFarmStore } from './composables/useFarmStore.js';
import './assets/tailwind.css'
import App from './App.vue'
import router from './router';

setUnauthorizedHandler(() => {
    clearCurrentUser();
    useFarmStore().clearFarmData();

    if (router.currentRoute.value.path.startsWith('/dashboard')) {
        router.replace('/login');
    }
})

const app = createApp(App);
app.use(router);
app.mount('#app');
