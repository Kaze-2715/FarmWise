import { createRouter, createWebHistory } from 'vue-router'
import UserProfile from '../components/user-profile.vue'
import Login from '../components/login.vue'
import Register from '../components/register.vue'
import MainLayout from '../components/layout/main-layout.vue'
import Roles from '../components/roles.vue'
import FarmOwner from '../components/farm-owner.vue'
import TechAdvisor from '../components/tech-advisor.vue'
import Planting from '../components/planting.vue'
import MarketAnalysis from '../components/market-analysis.vue'
import Report from "../components/report.vue";
import Homepage from "../pages/homepage.vue";

const routes = [
    {
        path: '/',
        name: 'Homepage',
        component: Homepage
    },
    {
        path: '/dashboard',
        component: MainLayout,
        redirect: '/dashboard/land',
        children: [
            {
                path: 'planting',
                name: 'Planting',
                component: Planting
            },
            {
                path: 'market',
                name: 'MarketAnalysis',
                component: MarketAnalysis
            },
            {
                path: 'reports',
                name: 'Report',
                component: Report
            },
            {
                path: 'land',
                name: 'Land',
                component: FarmOwner
            },
            {
                path: 'advisor',
                name: 'Advisor',
                component: TechAdvisor
            },
            {
                path: 'roles',
                name: 'Roles',
                component: Roles
            },
            {
                path: 'user-profile',
                name: 'UserProfile',
                component: UserProfile
            }
        ]
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
