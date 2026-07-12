const api = require('../../utils/api');

Page({
  data: {
    currentTab: 'heritage',
    list: [],
    loading: false
  },

  onLoad() {
    this.loadFavorites();
  },

  onShow() {
    this.loadFavorites();
  },

  switchTab(e) {
    const type = e.currentTarget.dataset.type;
    if (type === this.data.currentTab) return;
    this.setData({ currentTab: type, list: [] });
    this.loadFavorites();
  },

  loadFavorites() {
    const userId = wx.getStorageSync('userId');
    if (!userId) {
      this.setData({ list: [], loading: false });
      return;
    }

    // 点赞的打卡走独立逻辑
    if (this.data.currentTab === 'liked') {
      this.loadLikedPosts();
      return;
    }

    this.setData({ loading: true });

    api.getFavorites(this.data.currentTab).then(favorites => {
      const favList = favorites || [];
      if (favList.length === 0) {
        this.setData({ list: [], loading: false });
        return;
      }

      // 获取每个收藏项的详情
      const promises = favList.map(fav => {
        if (this.data.currentTab === 'heritage') {
          return api.getHeritageDetail(fav.targetId).then(h => {
            const data = h ? {
              id: h.id,
              name: h.name,
              summary: h.summary || '',
              category: h.category || '',
              coverImage: h.coverImage || '',
              images: h.images || ''
            } : null;
            return { id: fav.targetId, data };
          }).catch(() => null);
        } else {
          return api.getProductDetail(fav.targetId).then(p => {
            const data = p ? {
              id: p.id,
              name: p.name,
              price: p.price,
              category: p.category || '',
              images: p.images || ''
            } : null;
            return { id: fav.targetId, data };
          }).catch(() => null);
        }
      });

      return Promise.all(promises).then(results => {
        const list = results.filter(r => r && r.data).map(r => {
          const d = r.data;
          let img = d.coverImage || '';
          if (!img && d.images && typeof d.images === 'string') {
            img = d.images.split(',')[0];
          }
          d.displayImage = img;
          d.summaryDisplay = d.summary ? (d.summary.length > 60 ? d.summary.slice(0, 60) + "..." : d.summary) : "";
          return d;
        });
        this.setData({ list, loading: false });
      });
    }).catch(() => {
      this.setData({ loading: false });
    });
  },

  // 加载点赞过的打卡
  loadLikedPosts() {
    this.setData({ loading: true });
    api.getLikedPosts().then(res => {
      const posts = (res && res.list) || res || [];
      const list = (Array.isArray(posts) ? posts : []).map(p => {
        let img = '';
        if (p.images && p.images.length > 0) {
          img = p.images[0];
        }
        return {
          id: p.id,
          title: p.title || '',
          content: p.content || '',
          heritageName: p.heritageName || '',
          likeCount: p.likeCount || 0,
          commentCount: p.commentCount || 0,
          displayImage: img,
          displayTime: p.createdAt ? (p.createdAt.substring(0, 10)) : ''
        };
      });
      this.setData({ list, loading: false });
    }).catch(() => {
      this.setData({ loading: false });
    });
  },

  goToDetail(e) {
    const item = e.currentTarget.dataset.item;
    if (this.data.currentTab === 'heritage') {
      wx.navigateTo({ url: '/pages/detail/detail?id=' + item.id });
    } else if (this.data.currentTab === 'product') {
      wx.navigateTo({ url: '/pages/shop-detail/shop-detail?id=' + item.id });
    } else {
      wx.navigateTo({ url: '/pages/checkin-detail/checkin-detail?id=' + item.id });
    }
  },

  removeFavorite(e) {
    const id = e.currentTarget.dataset.id;
    const type = e.currentTarget.dataset.type;
    wx.showModal({
      title: '提示',
      content: '确定取消收藏？',
      success: (res) => {
        if (res.confirm) {
          api.toggleFavorite(id, type).then(() => {
            wx.showToast({ title: '已取消收藏', icon: 'success' });
            this.loadFavorites();
          }).catch(() => {});
        }
      }
    });
  }
});
