// app.js
App({
  onLaunch() {
    // 检查已有登录状态
    const token = wx.getStorageSync('token');
    const userInfo = wx.getStorageSync('userInfo');
    if (token && userInfo) {
      this.globalData.userInfo = userInfo;
      this.globalData.hasLogin = true;
      this.globalData.userId = userInfo.userId;
      this.loadLikedPostIds();
    }
    this.checkUpdate();
  },

  // 加载用户点赞过的帖子ID
  loadLikedPostIds() {
    const api = require('./utils/api');
    api.getLikedPosts().then(res => {
      const posts = (res && res.list) || res || [];
      const ids = new Set((Array.isArray(posts) ? posts : []).map(p => p.id));
      this.globalData.likedPostIds = ids;
    }).catch(() => {
      this.globalData.likedPostIds = new Set();
    });
  },

  // 切换点赞状态时，更新全局点赞集合
  toggleLikedPostId(postId, liked) {
    if (!this.globalData.likedPostIds) {
      this.globalData.likedPostIds = new Set();
    }
    if (liked) {
      this.globalData.likedPostIds.add(postId);
    } else {
      this.globalData.likedPostIds.delete(postId);
    }
  },

  // 检查更新
  checkUpdate() {
    const updateManager = wx.getUpdateManager();
    updateManager.onUpdateReady(() => {
      wx.showModal({
        title: '更新提示',
        content: '新版本已准备好，是否重启应用？',
        success: (res) => {
          if (res.confirm) {
            updateManager.applyUpdate();
          }
        }
      });
    });
  },

  globalData: {
    userInfo: null,
    hasLogin: false,
    userId: null,
    profileUpdated: false,
    postDataUpdated: false,
    likedPostIds: null
  }
});
