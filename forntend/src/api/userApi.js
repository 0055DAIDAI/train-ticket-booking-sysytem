import request from "./request";


export default {
    // 获取用户信息
    getUserInfo(userId) {
        return request({
            url: '/user/info',
            method: 'get',
            params: {
                userId
            }
        })
    },
    // 修改用户信息
    updateUserInfo(data) {
        return request({
            url: '/user/update',
            method: 'post',
            data
        })
    },

    registerUser(data) {
        return request({
            url: '/user/register',
            method: 'post',
            data
        })
    },
    // 新增：上传头像（返回 { url: '...'}）
    uploadAvatar(formData) {
        return request({
            url: '/user/upload',
            method: 'post',
            data: formData,
        })
    },
    //  登录
    login(data) {
        return request({
            url: '/login',
            method: 'post',
            data
        })
    },
        // 充值接口，payload 示例：{ userId: 1, amount: 100 }
    recharge(payload) {
        return request({
            url: '/user/recharge',
            method: 'post',
            data: payload
        })
    }
}