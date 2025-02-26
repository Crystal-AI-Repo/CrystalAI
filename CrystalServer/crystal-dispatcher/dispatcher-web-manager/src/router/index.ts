import { createRouter, createWebHistory } from 'vue-router'
import WebManagerContainer from '../views/WebManagerContainer.vue'
import HomeView from "@/views/HomeView.vue";
import NodeManagerView from "@/views/NodeManagerView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'web',
      component: WebManagerContainer,
      children: [
        {
          path: 'home',
          name: 'home',
          component: HomeView,
        },
        {
          path: 'nodes',
          name: 'nodes',
          component: NodeManagerView,
        },
      ]
    },
  ],
})

export default router
