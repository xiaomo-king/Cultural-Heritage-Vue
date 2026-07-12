// pages/profile/profile.js
const auth = require('../../utils/auth');
const api = require('../../utils/api');

Page({
  data: {
    userInfo: null,
    hasLogin: false,
    userId: null,
    balance: 0
  },

  onShow() {
    this.loadUserData();
    // 如果其他页面标记了资料更新，强制刷新
    const app = getApp();
    if (app.globalData.profileUpdated) {
      app.globalData.profileUpdated = false;
    }
    // 延迟再读一次，确保登录回来后能拿到 Storage 数据
    const that = this;
    setTimeout(() => {
      if (!that.data.userId) {
        that.loadUserData();
      }
    }, 300);
  },

  loadUserData() {
    const userId = wx.getStorageSync('userId');
    const userInfo = wx.getStorageSync('userInfo');

    if (userId && userInfo) {
      this.setData({
        userId: userId,
        hasLogin: true,
        userInfo: {
          nickName: userInfo.nickName || '游客',
          avatarUrl: api.getImageUrl(userInfo.avatarUrl || ''),
          userId: userId
        },
        balance: userInfo.balance || 0
      });

      api.getUser(userId).then(res => {
        if (res) {
          this.setData({
            'userInfo.nickName': res.nickName,
            'userInfo.avatarUrl': api.getImageUrl(res.avatarUrl || ''),
            balance: res.balance || 0
          });
          const stored = wx.getStorageSync('userInfo') || {};
          stored.nickName = res.nickName;
          stored.avatarUrl = res.avatarUrl;
          stored.balance = res.balance;
          wx.setStorageSync('userInfo', stored);
        }
      }).catch(() => {});
    } else {
      this.setData({ hasLogin: false, userInfo: null, userId: null });
    }
  },

  // 跳转登录页
  goToLogin() {
    wx.navigateTo({ url: '/pages/login/login' });
  },

  // 充值
  onRecharge() {
    let userId = this.data.userId;
    if (!userId) {
      userId = wx.getStorageSync('userId');
      if (!userId) { wx.showToast({ title: '请先登录', icon: 'none' }); return; }
    }
    const that = this;
    wx.showModal({
      title: '模拟充值',
      content: '',
      editable: true,
      placeholderText: '请输入充值金额',
      success(res) {
        if (res.confirm) {
          const input = (res.content || '').trim();
          if (!input || !/^\d+(\.\d{1,2})?$/.test(input) || parseFloat(input) <= 0) {
            wx.showToast({ title: '请输入有效金额', icon: 'none' });
            return;
          }
          const amount = parseFloat(input);
          api.recharge(userId, amount).then(data => {
            that.setData({ balance: data.balance || 0 });
            wx.showToast({ title: '充值成功', icon: 'success' });
          }).catch(() => {
            wx.showToast({ title: '充值失败', icon: 'none' });
          });
        }
      }
    });
  },

  // 退出
  onLogout() {
    const that = this;
    wx.showModal({
      title: '退出登录',
      content: '确定退出当前账号吗？',
      success(res) {
        if (res.confirm) {
          auth.logout();
          that.setData({ hasLogin: false, userInfo: null, userId: null, balance: 0 });
          wx.showToast({ title: '已退出', icon: 'success' });
        }
      }
    });
  },

  goToPage(e) {
    let userId = this.data.userId;
    if (!userId) {
      // 兜底：从 Storage 重新读取
      userId = wx.getStorageSync('userId');
      if (!userId) {
        wx.showToast({ title: '请先登录', icon: 'none' });
        return;
      }
      this.setData({ userId });
    }
    wx.navigateTo({ url: '/pages/' + e.currentTarget.dataset.page + '/' + e.currentTarget.dataset.page });
  },

  goToMyFootprint() {
    let userId = this.data.userId;
    if (!userId) {
      userId = wx.getStorageSync('userId');
      if (!userId) { wx.showToast({ title: '请先登录', icon: 'none' }); return; }
      this.setData({ userId });
    }
    wx.navigateTo({ url: '/pages/user-feed/user-feed?id=' + userId });
  },

  // 我的打卡
  goToMyPosts() {
    let userId = this.data.userId;
    if (!userId) {
      userId = wx.getStorageSync('userId');
      if (!userId) { wx.showToast({ title: '请先登录', icon: 'none' }); return; }
      this.setData({ userId });
    }
    wx.navigateTo({ url: '/pages/my-posts/my-posts' });
  },

  // 个人主页
  goToMyProfile() {
    let userId = this.data.userId;
    if (!userId) {
      userId = wx.getStorageSync('userId');
      if (!userId) { wx.showToast({ title: '请先登录', icon: 'none' }); return; }
      this.setData({ userId });
    }
    wx.navigateTo({ url: '/pages/user-feed/user-feed?id=' + userId });
  },

  // 历史记录
  goToFavorite() {
    wx.navigateTo({ url: '/pages/favorite/favorite' });
  },

  goToHistory() {
    wx.navigateTo({ url: '/pages/history/history' });
  },

  goToOrder(e) {
    let userId = this.data.userId;
    if (!userId) {
      userId = wx.getStorageSync('userId');
      if (!userId) { wx.showToast({ title: '请先登录', icon: 'none' }); return; }
      this.setData({ userId });
    }
    wx.navigateTo({ url: '/pages/order/order?status=' + (e.currentTarget.dataset.status || 'all') });
  },

  goToFollow() {
    let userId = this.data.userId;
    if (!userId) {
      userId = wx.getStorageSync('userId');
      if (!userId) { wx.showToast({ title: '请先登录', icon: 'none' }); return; }
      this.setData({ userId });
    }
    wx.navigateTo({ url: '/pages/user-feed/user-feed?id=' + userId });
  },

  goToAbout() {
    wx.showModal({
      title: '关于江右拾遗',
      content: '江右拾遗是一款江西非遗文化传承小程序，旨在让更多人发现、探访、带走江西非遗之美。',
      showCancel: false
    });
  }
});
