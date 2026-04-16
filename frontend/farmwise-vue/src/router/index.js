import { createRouter, createWebHistory } from 'vue-router'
import UserProfile from '../components/user-profile.vue'
import Login from '../components/login.vue'
import Register from '../components/register.vue'

const routes = [
    {
        path: '/',
        name: 'root',
        component: Login
    },
    {
        path: '/user-profile',
        name: 'UserProfile',
        component: UserProfile
    },
    {
        path: '/login',
        name: 'Login',
        component: Login
    },
    {
        path: '/register',
        name: 'Register',
        component: Register
    }
]

const router = createRouter({ history: createWebHistory(), routes })

export default router