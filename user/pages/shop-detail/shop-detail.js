const api = require('../../utils/api');
const history = require('../../utils/history');

Page({
  data: {
    product: {},
    imageList: [],
    specList: [],
    specPriceMap: {},
    selectedSpec: '',
    currentPrice: 0,
    currentOriginalPrice: 0,
    heritageName: '',
    loading: true,
    productId: null,
    showToast: false,
    toastText: '',
    isFavorited: false,
    quantity: 1,
    totalPrice: '0.00'
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ productId: parseInt(options.id) });
      this.loadProduct(parseInt(options.id));
    }
  },

  checkFavorite(id) {
    const userId = wx.getStorageSync('userId');
    if (!userId) return;
    api.checkFavorite(id, 'product').then(res => {
      if (res && res.isFavorited !== undefined) {
        this.setData({ isFavorited: res.isFavorited });
      }
    }).catch(() => {});
  },

  loadProduct(id) {
    this.setData({ loading: true });
    api.getProductDetail(id).then(product => {
      const images = product.images ? product.images.split(',').filter(Boolean).map(url => api.getImageUrl(url)) : [];
      let specList = [];
      let specPriceMap = {};
      if (product.specs) {
        try {
          const specs = JSON.parse(product.specs);
          if (specs && specs.规格) specList = specs.规格;
          if (specs && specs.价格) specPriceMap = specs.价格;
        } catch (e) {}
      }
      // Compute star display string
      const rating = product.rating || 0;
      const rounded = Math.round(rating);
      product.starsDisplay = '★'.repeat(rounded) + '☆'.repeat(5 - rounded);

      const firstSpec = specList.length > 0 ? specList[0] : '';
      const specPrice = firstSpec && specPriceMap[firstSpec];
      const currentPrice = specPrice || product.price || 0;
      const currentOriginalPrice = product.originalPrice > currentPrice ? product.originalPrice : 0;

      this.setData({
        product,
        imageList: images,
        specList: specList,
        specPriceMap: specPriceMap,
        selectedSpec: firstSpec,
        currentPrice: currentPrice,
        currentOriginalPrice: currentOriginalPrice,
        quantity: 1,
        loading: false
      });
      this.calcTotalPrice();
      this.checkFavorite(product.id);
      // 记录浏览历史
      history.addProductHistory(product.id, product.name, images[0] || '');
      // 获取关联非遗名称
      if (product.heritageId) {
        api.getHeritageDetail(product.heritageId).then(h => {
          if (h) this.setData({ heritageName: h.name });
        }).catch(() => {});
      }
    }).catch(() => {
      this.setData({ loading: false });
      wx.showToast({ title: '商品不存在', icon: 'none' });
    });
  },

  selectSpec(e) {
    const spec = e.currentTarget.dataset.spec;
    const specPrice = this.data.specPriceMap[spec];
    if (specPrice) {
      this.setData({
        selectedSpec: spec,
        currentPrice: specPrice,
        currentOriginalPrice: this.data.product.originalPrice > specPrice ? this.data.product.originalPrice : 0
      });
    } else {
      this.setData({ selectedSpec: spec });
    }
    this.calcTotalPrice();
  },

  calcTotalPrice() {
    const price = this.data.currentPrice || 0;
    const total = (price * this.data.quantity).toFixed(2);
    this.setData({ totalPrice: total });
  },

  increaseQty() {
    const qty = this.data.quantity + 1;
    this.setData({ quantity: qty });
    this.calcTotalPrice();
  },

  decreaseQty() {
    if (this.data.quantity <= 1) return;
    const qty = this.data.quantity - 1;
    this.setData({ quantity: qty });
    this.calcTotalPrice();
  },

  onQtyInput(e) {
    let val = parseInt(e.detail.value) || 1;
    if (val < 1) val = 1;
    this.setData({ quantity: val });
    this.calcTotalPrice();
  },

  addToCart() {
    const userId = wx.getStorageSync('userId');
    if (!userId) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      return;
    }
    api.addToCart({
      productId: this.data.productId,
      quantity: this.data.quantity,
      spec: this.data.selectedSpec
    }).then(() => {
      this.showToast('已加入购物车');
    }).catch(() => {
      wx.showToast({ title: '添加失败', icon: 'none' });
    });
  },

  buyNow() {
    const userId = wx.getStorageSync('userId');
    if (!userId) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      return;
    }
    // 跳转结算页，传入商品信息
    const product = this.data.product;
    const item = {
      productId: product.id,
      productName: product.name,
      productImage: this.data.imageList[0] || '',
      price: product.price,
      quantity: this.data.quantity,
      spec: this.data.selectedSpec
    };
    wx.navigateTo({
      url: '/pages/checkout/checkout?items=' + encodeURIComponent(JSON.stringify([item]))
    });
  },

  goToCart() {
    wx.navigateTo({ url: '/pages/cart/cart' });
  },

  goToHome() {
    wx.switchTab({ url: '/pages/index/index' });
  },

  goToHeritage() {
    if (this.data.product.heritageId) {
      wx.navigateTo({ url: '/pages/detail/detail?id=' + this.data.product.heritageId });
    }
  },

  toggleFavorite() {
    const userId = wx.getStorageSync('userId');
    if (!userId) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      return;
    }
    api.toggleFavorite(this.data.productId, 'product').then(res => {
      const now = res && res.isFavorited;
      this.setData({ isFavorited: now });
      wx.showToast({ title: now ? '已收藏' : '已取消收藏', icon: 'success' });
    }).catch(() => {
      wx.showToast({ title: '操作失败', icon: 'none' });
    });
  },

  showToast(text) {
    this.setData({ showToast: true, toastText: text });
    setTimeout(() => {
      this.setData({ showToast: false });
    }, 2000);
  }
});
