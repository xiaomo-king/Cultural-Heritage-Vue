// pages/user-feed/user-feed.js
const api = require('../../utils/api');

Page({
  data: {
    loading: true,
    user: {},
    posts: [],
    userId: null,
    myUserId: null,
    isFollowing: false
  },

  onLoad(options) {
    const targetId = parseInt(options.id);
    const myUserId = wx.getStorageSync('userId');
    this.setData({ userId: targetId, myUserId: myUserId || null });

    if (targetId) {
      this.loadUser(targetId, myUserId);
    }
  },

  loadUser(targetId, myUserId) {
    const that = this;

    // 是否查看自己的主页
    const isSelf = targetId === myUserId;

    // 并行加载用户信息和打卡列表
    Promise.all([
      api.getUser(targetId).catch(() => null),
      api.getPostList({ userId: targetId, page: 0, size: 20 }).catch(() => ({ list: [] })),
      !isSelf && myUserId ? api.checkFollow(targetId).catch(() => null) : Promise.resolve(null)
    ]).then(([user, postRes, followRes]) => {
      const posts = (postRes && postRes.list) || postRes || [];
      posts.forEach(p => {
        if (typeof p.images === 'string') p.images = p.images ? p.images.split(',') : [];
        if (!p.images) p.images = [];
      });

      // 如果后端没有用户数据，用本地存储的信息
      if (!user && isSelf) {
        const localInfo = wx.getStorageSync('userInfo') || {};
        user = {
          id: targetId,
          nickName: localInfo.nickName || '我',
          avatarUrl: localInfo.avatarUrl || '',
          followCount: 0,
          followerCount: 0,
          checkinCount: 0
        };
      }

      // 转换头像为完整URL
      if (user) {
        user.avatarUrl = api.getImageUrl(user.avatarUrl || '');
      }

      that.setData({
        user: user || {},
        posts: posts,
        isFollowing: followRes ? followRes.isFollowing : false,
        loading: false
      });
      wx.setNavigationBarTitle({ title: (user ? user.nickName || '用户' : '用户') + '的主页' });
    }).catch(() => {
      // 最后的兜底
      const localInfo = wx.getStorageSync('userInfo') || {};
      that.setData({
        loading: false,
        user: { nickName: localInfo.nickName || '用户', avatarUrl: '' },
        posts: []
      });
    });
  },

  // 关注/取消
  onToggleFollow() {
    const that = this;
    if (!this.data.myUserId) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      return;
    }
    api.toggleFollow(this.data.userId).then(res => {
      const now = (res && res.isFollowing !== undefined) ? res.isFollowing : !that.data.isFollowing;
      const diff = now ? 1 : -1;
      that.setData({
        isFollowing: now,
        'user.followerCount': Math.max(0, (that.data.user.followerCount || 0) + diff)
      });
    }).catch(() => {
      wx.showToast({ title: '操作失败', icon: 'none' });
    });
  },

  // 查看关注列表
  onFollowings() {
    wx.showToast({ title: '关注列表开发中', icon: 'none' });
  },

  // 查看粉丝列表
  onFollowers() {
    wx.showToast({ title: '粉丝列表开发中', icon: 'none' });
  },

  // 点击笔记
  onPostTap(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: '/pages/checkin-detail/checkin-detail?id=' + id });
  },

  // 编辑资料
  onEditProfile() {
    wx.navigateTo({ url: '/pages/edit-profile/edit-profile' });
  }
});
