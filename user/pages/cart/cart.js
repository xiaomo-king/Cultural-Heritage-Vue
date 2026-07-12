const api = require('../../utils/api');

Page({
  data: {
    hasLogin: false,
    cartItems: [],
    allChecked: true,
    selectedCount: 0,
    totalPrice: 0,
    loading: false
  },

  onShow() {
    this.checkLogin();
  },

  checkLogin() {
    const userId = wx.getStorageSync('userId');
    if (userId) {
      this.setData({ hasLogin: true });
      this.loadCart();
    } else {
      this.setData({ hasLogin: false, cartItems: [] });
    }
  },

  loadCart() {
    this.setData({ loading: true });
    api.getCartList().then(items => {
      const cartItems = (items || []).map(item => ({
        ...item,
        productImage: api.getImageUrl(item.productImage || ''),
        checked: true
      }));
      this.setData({ cartItems }, () => this.calcTotal());
    }).catch(() => {
      wx.showToast({ title: '加载失败', icon: 'none' });
    }).finally(() => {
      this.setData({ loading: false });
    });
  },

  toggleCheck(e) {
    const index = e.currentTarget.dataset.index;
    const key = 'cartItems[' + index + '].checked';
    this.setData({ [key]: !this.data.cartItems[index].checked }, () => this.calcTotal());
  },

  toggleAll() {
    const newChecked = !this.data.allChecked;
    const items = this.data.cartItems.map(i => ({ ...i, checked: newChecked }));
    this.setData({ cartItems: items, allChecked: newChecked }, () => this.calcTotal());
  },

  calcTotal() {
    let selected = 0;
    let total = 0;
    this.data.cartItems.forEach(item => {
      if (item.checked) {
        selected += item.quantity;
        total += item.price * item.quantity;
      }
    });
    const allChecked = this.data.cartItems.every(i => i.checked);
    this.setData({
      selectedCount: selected,
      totalPrice: total.toFixed(2),
      allChecked: allChecked
    });
  },

  increaseQty(e) {
    const index = e.currentTarget.dataset.index;
    const item = this.data.cartItems[index];
    const newQty = item.quantity + 1;
    api.updateCart({ cartId: item.id, quantity: newQty }).then(() => {
      const key = 'cartItems[' + index + '].quantity';
      this.setData({ [key]: newQty }, () => this.calcTotal());
    }).catch(() => {});
  },

  decreaseQty(e) {
    const index = e.currentTarget.dataset.index;
    const item = this.data.cartItems[index];
    if (item.quantity <= 1) {
      this.removeItem(e);
      return;
    }
    const newQty = item.quantity - 1;
    api.updateCart({ cartId: item.id, quantity: newQty }).then(() => {
      const key = 'cartItems[' + index + '].quantity';
      this.setData({ [key]: newQty }, () => this.calcTotal());
    }).catch(() => {});
  },

  removeItem(e) {
    const index = e.currentTarget.dataset.index;
    const item = this.data.cartItems[index];
    wx.showModal({
      title: '提示',
      content: '确定删除该商品？',
      success: (res) => {
        if (res.confirm) {
          api.removeFromCart(item.id).then(() => {
            const items = this.data.cartItems.filter((_, i) => i !== index);
            this.setData({ cartItems: items }, () => this.calcTotal());
          }).catch(() => {});
        }
      }
    });
  },

  goToCheckout() {
    const selected = this.data.cartItems.filter(i => i.checked);
    if (selected.length === 0) {
      wx.showToast({ title: '请选择商品', icon: 'none' });
      return;
    }
    const items = selected.map(i => ({
      cartId: i.id,
      productId: i.productId,
      productName: i.productName,
      productImage: i.productImage,
      price: i.price,
      quantity: i.quantity,
      spec: i.spec
    }));
    wx.navigateTo({
      url: '/pages/checkout/checkout?items=' + encodeURIComponent(JSON.stringify(items))
    });
  },

  goToDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: '/pages/shop-detail/shop-detail?id=' + id });
  },

  goToLogin() {
    wx.navigateTo({ url: '/pages/login/login' });
  },

  goToShop() {
    wx.switchTab({ url: '/pages/shop/shop' });
  }
});
