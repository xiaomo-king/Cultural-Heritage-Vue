// pages/history/history.js
Page({
  data: {
    currentTab: 'post',
    postHistory: [],
    productHistory: []
  },

  onShow() {
    this.loadHistory();
  },

  loadHistory() {
    const postHistory = wx.getStorageSync('history_posts') || [];
    const productHistory = wx.getStorageSync('history_products') || [];

    // 按时间倒序排列
    postHistory.sort((a, b) => b.timestamp - a.timestamp);
    productHistory.sort((a, b) => b.timestamp - a.timestamp);

    this.setData({ postHistory, productHistory });
  },

  switchTab(e) {
    this.setData({ currentTab: e.currentTarget.dataset.tab });
  },

  onPostTap(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: '/pages/checkin-detail/checkin-detail?id=' + id });
  },

  onProductTap(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: '/pages/shop-detail/shop-detail?id=' + id });
  }
});
