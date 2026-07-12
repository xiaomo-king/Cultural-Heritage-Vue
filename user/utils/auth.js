// 登录鉴权模块
const api = require('./api');

// 调用后端注册接口
function register(account, password, phone) {
  return api.register(account, password, phone).then(data => {
    saveUserInfo(data);
    return data;
  });
}

// 调用后端登录接口
function login(account, password) {
  return api.login(account, password).then(data => {
    saveUserInfo(data);
    return data;
  });
}

// 保存用户信息到 Storage 和全局
function saveUserInfo(data) {
  wx.setStorageSync('token', data.token);
  wx.setStorageSync('userId', data.userId);
  wx.setStorageSync('userInfo', {
    userId: data.userId,
    nickName: data.nickName,
    avatarUrl: data.avatarUrl || '',
    balance: data.balance || 0
  });

  const app = getApp();
  if (app) {
    app.globalData.userInfo = {
      userId: data.userId,
      nickName: data.nickName,
      avatarUrl: data.avatarUrl || '',
      balance: data.balance || 0
    };
    app.globalData.hasLogin = true;
    app.globalData.userId = data.userId;
  }
}

// 退出登录
function logout() {
  wx.removeStorageSync('token');
  wx.removeStorageSync('userId');
  wx.removeStorageSync('userInfo');

  const app = getApp();
  if (app) {
    app.globalData.userInfo = null;
    app.globalData.hasLogin = false;
    app.globalData.userId = null;
  }
}

module.exports = {
  register,
  login,
  logout
};
