// pages/index/index.js
const api = require('../../utils/api');

Page({
  data: {
    loading: true,
    loadingMore: false,
    loadError: false,
    posts: [],
    leftPosts: [],
    rightPosts: [],
    leftHeight: 0,
    rightHeight: 0,
    page: 0,
    size: 10,
    hasMore: true
  },

  onLoad() {
    this.loadPosts();
  },

  onShow() {
    this.setData({ page: 0, posts: [], leftHeight: 0, rightHeight: 0 });
    this.loadPosts();
  },

  loadPosts() {
    const that = this;
    const currentPage = this.data.page;
    const size = this.data.size;
    that.setData({ loading: currentPage === 0, loadingMore: currentPage > 0, loadError: false });

    const userId = wx.getStorageSync('userId');

    api.getPostList({ page: currentPage, size, userId }).then(res => {
      const list = (res && res.list) || res || [];
      // 未登录时默认 isLiked = false
      list.forEach(p => { if (p.isLiked === undefined) p.isLiked = false; });

      const currentPosts = that.data.posts;
      const newPosts = currentPage === 0 ? list : currentPosts.concat(list);
      that.setData({
        posts: newPosts,
        page: currentPage + 1,
        hasMore: list.length >= size,
        loading: false,
        loadingMore: false
      });
      that.waterfallDistribute(list, currentPage === 0);
    }).catch(() => {
      that.setData({
        loading: false, loadingMore: false,
        loadError: that.data.posts.length === 0
      });
    });
  },

  waterfallDistribute(newPosts, isRefresh) {
    let leftPosts = isRefresh ? [] : [...this.data.leftPosts];
    let rightPosts = isRefresh ? [] : [...this.data.rightPosts];
    let leftH = isRefresh ? 0 : this.data.leftHeight;
    let rightH = isRefresh ? 0 : this.data.rightHeight;
    const variants = [0, 60, 100, -40, 80, -20];

    newPosts.forEach(post => {
      let imgH = 0;
      if (post.images && post.images.length > 0) {
        const seed = ((post.userId || 0) + (post.content ? post.content.length : 0));
        imgH = 320 + variants[Math.abs(seed) % variants.length];
      }
      let bodyH = 80;
      if (post.heritageName) bodyH += 44;
      bodyH += 56;
      const cardH = imgH + bodyH + 12;
      if (leftH <= rightH) { leftPosts.push(post); leftH += cardH; }
      else { rightPosts.push(post); rightH += cardH; }
    });
    this.setData({ leftPosts, rightPosts, leftHeight: leftH, rightHeight: rightH });
  },

  onPullDownRefresh() {
    this.data.page = 0; this.data.posts = [];
    this.setData({ leftHeight: 0, rightHeight: 0 });
    this.loadPosts();
    wx.stopPullDownRefresh();
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loadingMore && !this.data.loading) this.loadPosts();
  },

  onSearchTap() { wx.navigateTo({ url: '/pages/search/search' }); },
  onPostTap(e) { wx.navigateTo({ url: '/pages/checkin-detail/checkin-detail?id=' + e.currentTarget.dataset.id }); },
  onRetry() { this.data.page = 0; this.data.posts = []; this.loadPosts(); }
});
