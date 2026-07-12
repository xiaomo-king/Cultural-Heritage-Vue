// pages/detail/detail.js
const api = require('../../utils/api');

Page({
  data: {
    id: null,
    heritage: null,
    loading: true,
    loadError: false,
    isFavorited: false,
    currentTab: 'intro',
    relatedProducts: [],
    imageList: [],
    tabs: [
      { key: 'intro', name: '简介' },
      { key: 'history', name: '历史' },
      { key: 'features', name: '特色' },
      { key: 'travel', name: '探访' }
    ]
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ id: options.id });
      this.loadDetail(options.id);
      this.checkFavorite(options.id);
      this.loadRelatedProducts(options.id);
    }
  },

  loadRelatedProducts(heritageId) {
    api.getProductList({ heritageId: heritageId, page: 0, size: 20 }).then(res => {
      const list = (res.list || res.content || res || []).map(p => {
        p.firstImage = p.images ? p.images.split(",")[0] : "/images/tab_shop.png";
        return p;
      });
      this.setData({ relatedProducts: list });
    }).catch(() => {});
  },

  loadDetail(id) {
    const that = this;
    api.getHeritageDetail(id).then(res => {
      // 解析图片列表：images 是逗号分隔的URL字符串，转换为完整URL供小程序显示
      let imageList = [];
      if (res.images) {
        imageList = res.images.split(',').filter(i => i).map(url => api.getImageUrl(url));
      } else if (res.coverImage) {
        imageList = [api.getImageUrl(res.coverImage)];
      }
      that.setData({ heritage: res, imageList, loading: false });
      wx.setNavigationBarTitle({ title: res.name || '非遗详情' });
    }).catch(err => {
      console.error('加载详情失败:', err);
      that.setData({ loading: false, loadError: true });
      wx.showToast({ title: '加载失败', icon: 'none' });
    });
  },

  checkFavorite(id) {
    const userId = wx.getStorageSync('userId');
    if (!userId) return;
    api.checkFavorite(id, 'heritage').then(res => {
      this.setData({ isFavorited: res && res.isFavorited });
    }).catch(() => {});
  },

  // Tab 切换
  onTabTap(e) {
    const key = e.currentTarget.dataset.key;
    this.setData({ currentTab: key });
  },

  // 收藏
  onToggleFavorite() {
    const userId = wx.getStorageSync('userId');
    if (!userId) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      return;
    }
    const that = this;
    const id = this.data.id;
    api.toggleFavorite(id, 'heritage').then(res => {
      const now = res && res.isFavorited;
      that.setData({ isFavorited: now });
      wx.showToast({ title: now ? '已收藏' : '已取消', icon: 'success' });
    }).catch(() => {
      wx.showToast({ title: '操作失败', icon: 'none' });
    });
  },

  // 查看关联商品
  goToProduct(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: '/pages/shop-detail/shop-detail?id=' + id });
  },

  onViewProducts() {
    wx.switchTab({ url: '/pages/shop/shop' });
  },

  // 去打卡
  onGoCheckin() {
    wx.navigateTo({ url: '../../pages/checkin-post/checkin-post?heritageId=' + this.data.id + '&heritageName=' + this.data.heritage.name });
  },

  // 查看地图
  onViewMap() {
    wx.switchTab({ url: '/pages/map/map' });
  },

  onRetry() {
    this.setData({ loadError: false, loading: true });
    this.loadDetail(this.data.id);
  },

  // 分享
  onShareAppMessage() {
    const h = this.data.heritage;
    return {
      title: h ? h.name : '江右拾遗 - 江西非遗',
      path: '/pages/detail/detail?id=' + this.data.id
    };
  }
});
