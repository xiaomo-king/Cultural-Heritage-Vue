const api = require('../../utils/api');

Page({
  data: {
    order: {},
    items: [],
    loading: true,
    statusText: '',
    statusIcon: '',
    statusTip: '',
    statusMap: {
      'pending': { text: '待付款', icon: '⏳', tip: '请尽快完成支付' },
      'paid': { text: '待发货', icon: '📦', tip: '等待商家发货' },
      'shipped': { text: '待收货', icon: '🚚', tip: '商品正在路上' },
      'received': { text: '待评价', icon: '⭐', tip: '给商品一个评价吧' },
      'completed': { text: '已完成', icon: '✅', tip: '感谢您的购买' },
      'cancelled': { text: '已取消', icon: '❌', tip: '订单已取消' }
    }
  },

  onLoad(options) {
    if (options.id) {
      this.loadOrder(parseInt(options.id));
    }
  },

  loadOrder(id) {
    this.setData({ loading: true });
    api.getOrderDetail(id).then(res => {
      const order = res.order || res;
      const items = (res.items || []).map(sub => ({
        ...sub,
        productImage: api.getImageUrl(sub.productImage || '')
      }));
      const info = this.data.statusMap[order.status] || { text: order.status, icon: '📄', tip: '' };
      this.setData({
        order,
        items,
        statusText: info.text,
        statusIcon: info.icon,
        statusTip: info.tip,
        loading: false
      });
    }).catch(() => {
      this.setData({ loading: false });
      wx.showToast({ title: '订单不存在', icon: 'none' });
    });
  },

  goToPay() {
    wx.navigateTo({
      url: '/pages/payment/payment?orderId=' + this.data.order.id + '&orderNo=' + this.data.order.orderNo
    });
  },

  confirmReceive() {
    wx.showModal({
      title: '提示',
      content: '确定已收到商品？',
      success: (res) => {
        if (res.confirm) {
          api.updateOrderStatus(this.data.order.id, 'received').then(() => {
            wx.showToast({ title: '确认收货成功', icon: 'success' });
            this.loadOrder(this.data.order.id);
          }).catch(() => {
            wx.showToast({ title: '操作失败', icon: 'none' });
          });
        }
      }
    });
  },

  goToEvaluation() {
    wx.navigateTo({ url: '/pages/evaluation/evaluation?id=' + this.data.order.id });
  },

  goToOrderList() {
    wx.navigateBack();
  }
});
