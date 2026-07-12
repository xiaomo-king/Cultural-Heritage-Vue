const api = require('../../utils/api');

Page({
  data: {
    orderId: null,
    orderNo: '',
    totalAmount: '0.00',
    balance: 0,
    loading: true,
    paid: false
  },

  onLoad(options) {
    if (options.orderId) {
      this.setData({ orderId: parseInt(options.orderId) });
    }
    if (options.orderNo) {
      this.setData({ orderNo: options.orderNo });
    }
    if (options.from === 'order') {
      this.setData({ paid: true });
    }
    this.loadData();
  },

  loadData() {
    this.setData({ loading: true });

    // 获取用户余额
    const userId = wx.getStorageSync('userId');
    const balanceProm = userId ? api.getBalance(userId) : Promise.resolve(0);

    // 获取订单详情
    const orderProm = this.data.orderId ? api.getOrderDetail(this.data.orderId) : Promise.resolve(null);

    Promise.all([balanceProm, orderProm]).then(([balance, order]) => {
      const data = { loading: false, balance: balance || 0 };
      if (order) {
        const orderData = order.order || order;
        data.totalAmount = (orderData.totalAmount || 0).toString();
        data.orderNo = orderData.orderNo || this.data.orderNo;
        data.orderId = orderData.id || this.data.orderId;
        if (orderData.status !== 'pending') {
          data.paid = true;
        }
      }
      this.setData(data);
    }).catch(() => {
      this.setData({ loading: false });
    });
  },

  confirmPay() {
    const userId = wx.getStorageSync('userId');
    if (!userId) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      return;
    }
    if (this.data.balance < parseFloat(this.data.totalAmount)) {
      wx.showToast({ title: '余额不足', icon: 'none' });
      return;
    }
    wx.showModal({
      title: '确认支付',
      content: '将支付 ¥' + this.data.totalAmount + '，是否确认？',
      success: (res) => {
        if (res.confirm) {
          this.doPay();
        }
      }
    });
  },

  doPay() {
    wx.showLoading({ title: '支付中...' });
    api.payOrder(this.data.orderId).then(() => {
      wx.hideLoading();
      this.setData({ paid: true });
      // 刷新余额
      const userId = wx.getStorageSync('userId');
      api.getBalance(userId).then(balance => {
        this.setData({ balance: balance || 0 });
      }).catch(() => {});
    }).catch(err => {
      wx.hideLoading();
      wx.showToast({ title: err || '支付失败', icon: 'none' });
    });
  },

  goToRecharge() {
    wx.switchTab({ url: '/pages/profile/profile' });
  },

  goToOrderDetail() {
    wx.redirectTo({ url: '/pages/order-detail/order-detail?id=' + this.data.orderId });
  },

  goToHome() {
    wx.switchTab({ url: '/pages/index/index' });
  }
});
