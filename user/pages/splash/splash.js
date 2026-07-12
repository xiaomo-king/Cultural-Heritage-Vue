// pages/splash/splash.js
const api = require('../../utils/api');

Page({
  data: {
    imageUrl: api.getImageUrl('/uploads/images/start/user.jpg')
  },

  onLoad() {
    // 3秒后自动跳转首页
    this._timer = setTimeout(() => {
      this.goToHome();
    }, 3000);
  },

  onUnload() {
    if (this._timer) {
      clearTimeout(this._timer);
      this._timer = null;
    }
  },

  onSkip() {
    if (this._timer) {
      clearTimeout(this._timer);
      this._timer = null;
    }
    this.goToHome();
  },

  goToHome() {
    wx.switchTab({ url: '/pages/index/index' });
  }
});
