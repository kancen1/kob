import store from '@/store'
import NotFound from '@/views/error/NotFound.vue'
import PkIndexView from '@/views/pk/PkIndexView.vue'
import RanklistIndexView from '@/views/ranklist/RanklistIndexView.vue'
import RecordContentView from '@/views/record/RecordContentView.vue'
import RecordIndexView from '@/views/record/RecordIndexView.vue'
import UserAccountLoginView from '@/views/user/account/UserAccountLoginView.vue'
import UserAccountRegisterView from '@/views/user/account/UserAccountRegisterView.vue'
import UserBotIndexView from '@/views/user/bots/UserBotIndexView.vue'
import { createRouter, createWebHistory } from 'vue-router'


const routes = [
  {
    path: '/',
    name: 'home',
    redirect: '/pk/',
    // 定义是否需要授权 meta requestAuth都可以自己定义
    meta: {
      requestAuth: true,
    }
  },
  {
    path: '/pk/',
    name: 'pk_index',
    component: PkIndexView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: '/record/',
    name: 'record_index',
    component: RecordIndexView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: '/record/:recordId/',
    name: 'record_content',
    component: RecordContentView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: '/ranklist/',
    name: 'ranklist_index',
    component: RanklistIndexView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: '/user/bot/',
    name: 'user_bot_index',
    component: UserBotIndexView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: '/user/account/login/',
    name: 'user_account_login',
    component: UserAccountLoginView,
    meta: {
      // 表示不需要授权
      requestAuth: false,
    }
  },
  {
    path: '/user/account/register/',
    name: 'user_account_register',
    component: UserAccountRegisterView,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: '/404/',
    name: '404',
    component: NotFound,
    meta: {
      requestAuth: false,
    }
  },
  {
    // 没找到重定向
    path: '/:catchAll(.*)',
    redirect: '/404/'
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 实现前端页面授权 没次在router使用之前调用这个函数
// to表示跳转到哪 from表示从哪个页面跳转过去 next表示要不要执行下一个操作

// to: 是一个 Route 对象，表示即将进入的目标路由。
// from: 同样是一个 Route 对象，表示当前导航正离开的路由。
// next: 是一个函数，用于指示路由如何继续。它有几种不同的用法：
// 不带参数调用 next() 表示“继续”当前的导航。
// 调用 next('/some/path') 表示重定向到一个新的路径。
// 调用 next(false) 或者不调用 next 则取消当前的导航。
router.beforeEach((to, from, next) => {
  // 进入的路由需要授权且没有登入的时候
  if (to.meta.requestAuth && !store.state.user.is_login) {
    next({ name: "user_account_login" });
  } else {
    // 不需要授权的话直接跳转到默认页面
    next();
  }
});

export default router
