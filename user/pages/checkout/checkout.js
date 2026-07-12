const api = require('../../utils/api');

Page({
  data: {
    items: [],
    selectedAddress: null,
    remark: '',
    totalAmount: '0.00',
    submitting: false
  },

  onLoad(options) {
    if (options.items) {
      try {
        const items = JSON.parse(decodeURIComponent(options.items));
        this.setData({ items });
        this.calcTotal();
      } catch (e) {
        wx.showToast({ title: '参数错误', icon: 'none' });
      }
    }
    // 尝试加载默认地址
    this.loadDefaultAddress();
  },

  onShow() {
    // 从地址选择页返回后重新读取
    // 地址选择页通过 setData 直接更新
  },

  calcTotal() {
    let total = 0;
    this.data.items.forEach(item => {
      total += item.price * item.quantity;
    });
    this.setData({ totalAmount: total.toFixed(2) });
  },

  loadDefaultAddress() {
    api.getAddressList().then(list => {
      const addrList = list || [];
      const defaultAddr = addrList.find(a => a.isDefault == 1);
      if (defaultAddr) {
        this.setData({ selectedAddress: defaultAddr });
      }
    }).catch(() => {});
  },

  goToAddress() {
    wx.navigateTo({ url: '/pages/address/address?from=checkout' });
  },

  onRemarkInput(e) {
    this.setData({ remark: e.detail.value });
  },

  submitOrder() {
    const userId = wx.getStorageSync('userId');
    if (!userId) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      return;
    }
    if (!this.data.selectedAddress) {
      wx.showToast({ title: '请选择收货地址', icon: 'none' });
      return;
    }
    if (this.data.items.length === 0) {
      wx.showToast({ title: '请选择商品', icon: 'none' });
      return;
    }

    this.setData({ submitting: true });

    const orderItems = this.data.items.map(item => ({
      productId: item.productId,
      quantity: item.quantity,
      spec: item.spec || ''
    }));

    api.createOrder({
      addressId: this.data.selectedAddress.id,
      remark: this.data.remark,
      items: orderItems
    }).then(res => {
      this.setData({ submitting: false });
      // 清空购物车中已结算的商品
      const cartIds = this.data.items.filter(i => i.cartId).map(i => i.cartId);
      if (cartIds.length > 0) {
        cartIds.forEach(id => {
          api.removeFromCart(id).catch(() => {});
        });
      }
      wx.redirectTo({
        url: '/pages/payment/payment?orderId=' + res.orderId + '&orderNo=' + res.orderNo
      });
    }).catch(err => {
      this.setData({ submitting: false });
      wx.showToast({ title: err || '提交失败', icon: 'none' });
    });
  }
});
