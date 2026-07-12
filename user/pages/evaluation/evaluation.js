const api = require('../../utils/api');

Page({
  data: {
    orderId: null,
    items: [],
    rating: 5,
    content: '',
    ratingTexts: ['非常差', '较差', '一般', '较好', '非常好'],
    loading: true,
    submitting: false
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ orderId: parseInt(options.id) });
      this.loadOrderItems(parseInt(options.id));
    } else {
      this.setData({ loading: false });
    }
  },

  loadOrderItems(orderId) {
    this.setData({ loading: true });
    api.getOrderDetail(orderId).then(res => {
      const items = res.items || [];
      this.setData({ items, loading: false });
    }).catch(() => {
      this.setData({ loading: false });
    });
  },

  setRating(e) {
    this.setData({ rating: e.currentTarget.dataset.rating });
  },

  onContentInput(e) {
    this.setData({ content: e.detail.value });
  },

  submitEvaluation() {
    if (this.data.submitting) return;
    const userId = wx.getStorageSync('userId');
    if (!userId) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      return;
    }

    this.setData({ submitting: true });
    wx.showLoading({ title: '提交中...' });

    // 对订单中的每个商品进行评价
    const promises = this.data.items.length > 0 
      ? this.data.items.map(item => 
          api.evaluateOrder(this.data.orderId, {
            productId: item.productId,
            rating: this.data.rating,
            content: this.data.content
          })
        )
      : [api.evaluateOrder(this.data.orderId, {
          rating: this.data.rating,
          content: this.data.content
        })];

    Promise.all(promises).then(() => {
      wx.hideLoading();
      wx.showToast({ title: '评价成功', icon: 'success' });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    }).catch(err => {
      wx.hideLoading();
      this.setData({ submitting: false });
      wx.showToast({ title: err || '提交失败', icon: 'none' });
    });
  },

  skipEvaluation() {
    wx.navigateBack();
  }
});
