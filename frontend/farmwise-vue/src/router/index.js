import { createRouter, createWebHistory } from 'vue-router'
import UserProfile from '../components/user-profile.vue'
import Login from '../components/login.vue'
import Register from '../components/register.vue'
import MainLayout from '../components/layout/main-layout.vue'
import Roles from '../components/roles.vue'
import FarmOwner from '../components/farm-owner.vue'
import TechAdvisor from '../components/tech-advisor.vue'

const routes = [
    {
        path: '/dashboard',
        component: MainLayout,
        children: [
            {
                path: '/home',
                name: 'Home',
                component: {
                    template: '<div class="p-6"><h2>首页内容</h2></div>'
                }
            }
        ]
    },
    {
        path: '/roles',
        name: 'Roles',
        component: Roles
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
    },
    {
        path: '/land',
        name: 'Land',
        component: FarmOwner
    },
    {
        path: '/advisor',
        name: 'Advisor',
        component: TechAdvisor
    }
]

const router = createRouter({ history: createWebHistory(), routes })

export default router