// pages/map/map.js
const api = require('../../utils/api');

Page({
  data: {
    loading: false,
    error: false,
    allHeritage: [],
    banners: [],
    cityGroups: [],
    currentCategory: '',
    totalCount: 0,
    categories: ['传统技艺', '传统戏剧', '传统舞蹈', '传统音乐', '曲艺', '传统美术', '民俗', '传统医药', '传统体育', '民间文学'],
    categoryIcons: {
      '传统技艺': '🏺', '传统戏剧': '🎭', '传统舞蹈': '💃', '传统音乐': '🎵',
      '曲艺': '🎤', '传统美术': '🎨', '民俗': '🏮', '传统医药': '💊',
      '传统体育': '🏋️', '民间文学': '📖'
    }
  },

  onLoad() {
    this.loadHeritage();
  },

  // Tab 切换到探访页时：重新加载数据
  onShow() {
    this.loadHeritage();
  },

  loadHeritage() {
    const that = this;
    that.setData({ loading: true, error: false });
    api.getHeritageList({ page: 0, size: 50 }).then(res => {
      const rawList = (res && res.list) || res || [];
      // 转换封面图为完整URL
      const list = rawList.map(h => ({
        ...h,
        coverImage: api.getImageUrl(h.coverImage || '')
      }));
      that.setData({ allHeritage: list, totalCount: list.length });
      const withCovers = list.filter(h => h.coverImage).slice(0, 5);
      that.setData({ banners: withCovers.length > 0 ? withCovers : list.slice(0, 5) });
      that.groupByCity(list);
    }).catch(() => {
      that.setData({ loading: false, error: true });
    });
  },

  groupByCity(list) {
    const groups = [];
    const cityMap = {};
    list.forEach(item => {
      const city = item.city || '其他';
      if (!cityMap[city]) {
        cityMap[city] = { city, list: [] };
        groups.push(cityMap[city]);
      }
      cityMap[city].list.push(item);
    });
    this.setData({ cityGroups: groups, loading: false });
  },

  onCategoryTap(e) {
    const category = e.currentTarget.dataset.category;
    wx.navigateTo({ url: '/pages/search/search?keyword=' + encodeURIComponent(category) });
  },

  onBannerTap(e) {
    const id = e.currentTarget.dataset.id;
    if (id) wx.navigateTo({ url: '/pages/detail/detail?id=' + id });
  },

  onItemTap(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: '/pages/detail/detail?id=' + id });
  },

  onLoadMore() {},

  onShareAppMessage() {
    return { title: '江右拾遗 - 江西非遗探访', path: '/pages/map/map' };
  }
});
