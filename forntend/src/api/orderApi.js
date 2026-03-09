import request from "./request";

export default {
    // 获取订单列表
    getTrainSchedule(data) {
    return request({
      url: '/route/search',
      method: 'post',
      data
    })
  },// 创建订单
    createOrder(data) {
      return request({
        url: '/order/create',
        method: 'post',
        data
      })
    },

      // 获取用户订单列表 -> GET /order/list?userId=xxx
      getOrderList(userId) {
    return request({
      url: '/order/list',
      method: 'get',
      params: {
        userId
      }
    })
  },
    // 获取订单详情
    getOrderDetail(id) {
        return request({
            url: '/order/detail',
            method: 'get',
            data: {
                id
            }
        })
    }
}