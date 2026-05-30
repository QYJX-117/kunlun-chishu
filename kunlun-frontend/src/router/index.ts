import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/pages/Login.vue'),
      meta: { title: '昆仑驰枢 - 登录' }
    },
    {
      path: '/dashboard',
      component: () => import('@/layout/DefaultLayout.vue'),
      redirect: '/dashboard/map',
      children: [
        { path: 'map', name: 'Map', component: () => import('@/pages/dashboard/Map.vue'), meta: { title: '库存健康地图' } },
        { path: 'quality', name: 'Quality', component: () => import('@/pages/dashboard/Quality.vue'), meta: { title: '数据质量监控' } },
        { path: 'sales-trend', name: 'SalesTrend', component: () => import('@/pages/dashboard/SalesTrend.vue'), meta: { title: '销量趋势分析' } }
      ]
    },
    {
      path: '/replenishment',
      component: () => import('@/layout/DefaultLayout.vue'),
      redirect: '/replenishment/mrp',
      children: [
        { path: 'mrp', name: 'MrpRun', component: () => import('@/pages/replenishment/MrpRun.vue'), meta: { title: '运行MRP' } },
        { path: 'suggestion', name: 'Suggestion', component: () => import('@/pages/replenishment/Suggestion.vue'), meta: { title: '补货建议列表' } },
        { path: 'prediction-log', name: 'PredictionLog', component: () => import('@/pages/replenishment/PredictionLog.vue'), meta: { title: '预测日志' } }
      ]
    },
    {
      path: '/order',
      component: () => import('@/layout/DefaultLayout.vue'),
      redirect: '/order/pending',
      children: [
        { path: 'pending', name: 'Pending', component: () => import('@/pages/order/Pending.vue'), meta: { title: '待处理工单' } },
        { path: 'history', name: 'History', component: () => import('@/pages/order/History.vue'), meta: { title: '历史订单' } }
      ]
    },
    {
      path: '/dispatch',
      component: () => import('@/layout/DefaultLayout.vue'),
      redirect: '/dispatch/vehicle',
      children: [
        { path: 'vehicle', name: 'VehicleDispatch', component: () => import('@/pages/dispatch/VehicleDispatch.vue'), meta: { title: '车辆调度' } },
        { path: 'equipment', name: 'EquipmentLedger', component: () => import('@/pages/dispatch/EquipmentLedger.vue'), meta: { title: '设备台账' } }
      ]
    },
    {
      path: '/basic',
      component: () => import('@/layout/DefaultLayout.vue'),
      redirect: '/basic/station',
      children: [
        { path: 'station', name: 'Station', component: () => import('@/pages/basic/Station.vue'), meta: { title: '站点管理' } },
        { path: 'material', name: 'Material', component: () => import('@/pages/basic/Material.vue'), meta: { title: '油品管理' } },
        { path: 'user', name: 'User', component: () => import('@/pages/basic/User.vue'), meta: { title: '用户管理' } }
      ]
    }
  ]
})

export default router
