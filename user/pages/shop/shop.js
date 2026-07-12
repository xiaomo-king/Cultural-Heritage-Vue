const api = require('../../utils/api');

Page({
  data: {
    banners: [
      { title: '景德镇陶瓷', subtitle: '千年窑火·匠心传承', bg: 'linear-gradient(135deg, #8B1A1A, #C41E3A)' },
      { title: '婺源绿茶', subtitle: '高山云雾·明前嫩芽', bg: 'linear-gradient(135deg, #2E7D32, #4CAF50)' },
      { title: '夏布绣', subtitle: '麻布上的中国画', bg: 'linear-gradient(135deg, #5D4037, #8D6E63)' }
    ],
    categories: [
      { name: '全部', label: '全部', icon: '🎨', bg: '#f5f0eb' },
      { name: '陶瓷', label: '陶瓷', icon: '🏺', bg: '#fce4ec' },
      { name: '茶叶', label: '茶叶', icon: '🍵', bg: '#e8f5e9' },
      { name: '刺绣', label: '刺绣', icon: '🧵', bg: '#f3e5f5' },
      { name: '美术', label: '美术', icon: '🖼️', bg: '#e3f2fd' },
      { name: '文房', label: '文房', icon: '✒️', bg: '#fff3e0' },
      { name: '文创', label: '文创', icon: '🎁', bg: '#fce4ec' }
    ],
    products: [],
    currentCategory: '',
    page: 0,
    size: 10,
    total: 0,
    hasMore: true,
    loading: false,
    error: false
  },

  onLoad() {
    this.loadProducts();
  },

  onShow() {
    // 从分类返回时刷新
    if (this.data.products.length > 0 && !this.data.loading) {
      this.loadProducts(true);
    }
  },

  onPullDownRefresh() {
    this.loadProducts(true, () => {
      wx.stopPullDownRefresh();
    });
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadProducts();
    }
  },

  loadProducts(refresh = false, callback) {
    if (this.data.loading) return;
    const page = refresh ? 0 : this.data.page;

    this.setData({ loading: true, error: false });

    api.getProductList({
      category: this.data.currentCategory || undefined,
      page: page,
      size: this.data.size
    }).then(res => {
      const products = (res.list || res.content || res || []).map(p => {
        p.firstImage = p.images ? api.getImageUrl(p.images.split(",")[0]) : "/images/tab_shop.png";
        p.displayName = p.name && p.name.length > 16 ? p.name.slice(0, 16) + "..." : (p.name || "");
        return p;
      });
      const total = res.total !== undefined ? res.total : products.length;

      this.setData({
        products: refresh ? products : this.data.products.concat(products),
        page: page + 1,
        total: total,
        hasMore: products.length === this.data.size,
        loading: false
      });
      if (callback) callback();
    }).catch(() => {
      this.setData({
        loading: false,
        error: true,
        products: refresh ? [] : this.data.products
      });
      if (callback) callback();
    });
  },

  filterByCategory(e) {
    const category = e.currentTarget.dataset.category;
    if (category === this.data.currentCategory) return;
    this.setData({
      currentCategory: category === '全部' ? '' : category,
      products: [],
      page: 0,
      hasMore: true
    });
    this.loadProducts();
  },

  goToDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: '/pages/shop-detail/shop-detail?id=' + id });
  },

  goToSearch() {
    wx.navigateTo({ url: '/pages/search/search?from=shop' });
  },

  goToCart() {
    wx.navigateTo({ url: '/pages/cart/cart' });
  }
});
