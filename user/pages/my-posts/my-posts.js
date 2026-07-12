// pages/my-posts/my-posts.js
const api = require('../../utils/api');

Page({
  data: {
    loading: true,
    posts: [],
    drafts: []
  },

  onShow() {
    this.loadMyPosts();
    this.loadDrafts();
  },

  loadDrafts() {
    const drafts = wx.getStorageSync('draft_posts') || [];
    this.setData({ drafts });
  },

  loadMyPosts() {
    const userId = wx.getStorageSync('userId');
    if (!userId) {
      this.setData({ loading: false });
      wx.showToast({ title: '请先登录', icon: 'none' });
      return;
    }

    const that = this;
    api.getPostList({ userId: userId, page: 0, size: 50 }).then(res => {
      const list = (res && res.list) || res || [];
      list.forEach(p => {
        if (typeof p.images === 'string') p.images = p.images ? p.images.split(',') : [];
        if (!p.images) p.images = [];
      });
      that.setData({ posts: list, loading: false });
    }).catch(() => {
      that.setData({ loading: false });
    });
  },

  onEditDraft(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: '/pages/checkin-post/checkin-post?draftId=' + id });
  },

  onDeleteDraft(e) {
    const id = e.currentTarget.dataset.id;
    const that = this;
    wx.showModal({
      title: '删除草稿',
      content: '确定要删除这条草稿吗？',
      success(res) {
        if (res.confirm) {
          let drafts = wx.getStorageSync('draft_posts') || [];
          drafts = drafts.filter(d => d.id !== id);
          wx.setStorageSync('draft_posts', drafts);
          that.loadDrafts();
          wx.showToast({ title: '已删除', icon: 'success' });
        }
      }
    });
  },

  onPostTap(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: '/pages/checkin-detail/checkin-detail?id=' + id });
  },

  onEditPost(e) {
    const id = e.currentTarget.dataset.id;
    const post = this.data.posts.find(p => p.id === id);
    wx.navigateTo({ url: '/pages/checkin-post/checkin-post?postId=' + id });
  },

  onDeletePost(e) {
    const id = e.currentTarget.dataset.id;
    const that = this;
    wx.showModal({
      title: '删除打卡',
      content: '确定要删除这条打卡记录吗？',
      success(res) {
        if (res.confirm) {
          api.updatePost(id, { content: '', visibility: 'private' }).then(() => {
            wx.showToast({ title: '已删除', icon: 'success' });
            that.loadMyPosts();
          }).catch(() => {
            wx.showToast({ title: '删除失败', icon: 'none' });
          });
        }
      }
    });
  }
});
