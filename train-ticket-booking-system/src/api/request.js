import axios from 'axios';
import { ElMessage } from 'element-plus' // 新增：导入 ElMessage

const service = axios.create({
  baseURL: '/api',
  timeout: 5000,
});

service.interceptors.request.use(
  (config) => {
    try {
      const url = (config.url || '').toString()
      // 不给登录接口添加旧 token
            if (/\/(register|login)$/.test(url) || /\/admin\/login$/.test(url)) {
        // 清除可能存在的认证头
        if (config.headers) {
          delete config.headers.Authorization
          delete config.headers.token
        }
        return config
      }

      // 先从 localStorage 读取（确保登录逻辑已写入）
      const userToken = localStorage.getItem('token') || ''
      const adminToken = localStorage.getItem('admin_token') || ''

      // admin 接口优先使用 admin_token；其他接口使用 user token
      const useAdmin = url.startsWith('/admin') || url.includes('/admin/')
      const tokenToUse = useAdmin ? adminToken : userToken

      if (tokenToUse) {
        config.headers = config.headers || {}
        // 同时设置 Authorization 和 后端可能期望的 'token' 字段
        config.headers['Authorization'] = `Bearer ${tokenToUse}`
        config.headers['token'] = tokenToUse
      }
    } catch (e) {
      // 忽略拦截器异常
      console.error('[request interceptor error]', e)
    }
    return config
  },
  (error) => {
    // 对请求错误做些什么
    return Promise.reject(error);
  }
);

service.interceptors.response.use(
  (response) => {
    const { code, msg, data } = response.data || {}

    if (code === 1) {
        return {data,
            msg,
        };
    }
    else {
            const NETWORK_ERROR = '网络异常，请稍后再试！';
            ElMessage.error(msg || 'Error');
            return Promise.reject(new Error(msg || NETWORK_ERROR));
        }
  },
  (error) => {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    return Promise.reject(error);
  }
);

function request(options){
    options.method = options.method || 'get';
    // 关于get请求参数调整
    if (options.method.toLowerCase() === 'get' && options.data) {
        options.params = options.data;
    }

    return service(options);
}

export default request;
