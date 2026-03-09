import request from './request'

export default {
  // 管理员登录（单独路径）
  loginAdmin(data) {
    return request({
      url: '/admin/login',
      method: 'post',
      data
    })
  },

  // 路线管理接口
  createRoute(data) {
    return request({
      url: '/admin/route',
      method: 'post',
      data
    })
  },
  getRoutes() {
    return request({
      url: '/admin/route',
      method: 'get'
    })
  },
  updateRoute(data) {
    return request({
      url: '/admin/route',
      method: 'put',
      data
    })
  },
  deleteRoute(id) {
    return request({
      url: `/admin/route/${id}`,
      method: 'delete'
    })
  },

  // 车次管理接口
  createTrain(data) {
    return request({
      url: '/admin/schedule',
      method: 'post',
      data
    })
  },
  getTrains() {
    return request({
      url: '/admin/schedule',
      method: 'get'
    })
  },
  updateTrain(data) {
    return request({
      url: '/admin/schedule',
      method: 'put',
      data
    })
  },
  deleteTrain(id) {
    return request({
      url: `/admin/schedule/${id}`,
      method: 'delete'
    })
  },
    // 订单管理接口
  getOrders() {
    return request({
      url: '/admin/order',
      method: 'get'
    })
  },
  
  updateOrder(data) {
    return request({
      url: '/admin/order',
      method: 'put',
      data
    })
  },
  
  deleteOrder(id) {
    return request({
      url: `/admin/order/${id}`,
      method: 'delete'
    })
  }
}