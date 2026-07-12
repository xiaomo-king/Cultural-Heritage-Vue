// pages/search/search.js
const api = require('../../utils/api');

Page({
  data: {
    keyword: '',
    searched: false,
    loading: false,
    fromShop: false,
    currentTab: 'heritage',
    heritageList: [],
    productList: [],
    postList: [],
    users: [],
    hotKeywords: ['景德镇陶瓷', '南丰傩舞', '赣南采茶戏', '婺源绿茶', '吉州窑', '夏布绣'],
    autoFocus: false
  },

  onLoad(options) {
    let keyword = '';
    if (options.from === 'shop') {
      this.setData({ fromShop: true, currentTab: 'product' });
    }
    if (options.topic) {
      keyword = decodeURIComponent(options.topic);
    } else if (options.keyword) {
      keyword = decodeURIComponent(options.keyword);
    }

    if (keyword) {
      this.setData({ keyword });
      this.doSearch(keyword);
    } else {
      this.setData({ autoFocus: true });
    }
  },

  onInput(e) {
    this.setData({ keyword: e.detail.value });
  },

  onSearch() {
    const keyword = this.data.keyword.trim();
    if (keyword) {
      this.doSearch(keyword);
    }
  },

  onTagSearch(e) {
    const keyword = e.currentTarget.dataset.keyword;
    this.setData({ keyword });
    this.doSearch(keyword);
  },

  switchTab(e) {
    const tab = e.currentTarget.dataset.tab;
    this.setData({ currentTab: tab });
    // 如果当前tab没数据就触发搜索
    const listKey = tab + 'List';
    if (this.data[listKey] && this.data[listKey].length === 0 && this.data.keyword) {
      this.doSearch(this.data.keyword);
    }
  },

  doSearch(keyword) {
    const that = this;
    that.setData({
      loading: true,
      searched: true,
      heritageList: [],
      productList: [],
      postList: [],
      users: []
    });

    if (this.data.fromShop) {
      // 好物页来的，只搜商品
      api.searchProducts(keyword, 0).then(res => {
        const list = (res.list || res.content || res || []).map(p => {
          p.firstImage = p.images ? api.getImageUrl(p.images.split(",")[0]) : "/images/tab_shop.png";
          return p;
        });
        that.setData({ productList: list, loading: false });
      }).catch(() => that.setData({ loading: false }));
      return;
    }

    // 首页来的，搜全部
    Promise.all([
      api.searchHeritage(keyword, 0).catch(() => ({ list: [] })),
      api.searchProducts(keyword, 0).catch(() => ({ list: [] })),
      api.searchPosts(keyword, 0).catch(() => ({ list: [] })),
      api.searchUsers(keyword).catch(() => [])
    ]).then(([heritageRes, productRes, postRes, users]) => {
      const heritageList = (heritageRes && heritageRes.list) || heritageRes || [];
      const productRaw = (productRes && productRes.list) || productRes || [];
      const productList = productRaw.map(p => {
        p.firstImage = p.images ? p.images.split(",")[0] : "/images/tab_shop.png";
        return p;
      });
      const postList = (postRes && postRes.list) || postRes || [];
      that.setData({
        heritageList: heritageList,
        productList: productList,
        postList: postList,
        users: users || [],
        loading: false
      });
    }).catch(() => {
      that.setData({ loading: false });
    });
  },

  // 点击非遗结果
  onHeritageTap(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: '/pages/detail/detail?id=' + id });
  },

  // 点击商品结果
  onProductTap(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: '/pages/shop-detail/shop-detail?id=' + id });
  },

  // 点击打卡结果
  onPostTap(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: '/pages/checkin-detail/checkin-detail?id=' + id });
  },

  // 点击用户结果
  onUserTap(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: '/pages/user-feed/user-feed?id=' + id });
  },

  onCancel() {
    wx.navigateBack();
  }
});
