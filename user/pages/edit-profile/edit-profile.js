// pages/edit-profile/edit-profile.js
const api = require('../../utils/api');

Page({
  data: {
    nickName: '',
    avatarUrl: '',
    gender: '',
    bio: '',
    location: ''
  },

  onShow() {
    this.loadProfile();
  },

  loadProfile() {
    const userId = wx.getStorageSync('userId');
    if (!userId) { wx.showToast({ title: '请先登录', icon: 'none' }); return; }

    api.getUser(userId).then(res => {
      if (res) {
        this.setData({
          nickName: res.nickName || '',
          avatarUrl: api.getImageUrl(res.avatarUrl || ''),
          gender: res.gender || '',
          bio: res.bio || '',
          location: res.location || ''
        });
        // 更新本地存储
        const stored = wx.getStorageSync('userInfo') || {};
        stored.nickName = res.nickName;
        wx.setStorageSync('userInfo', stored);
      }
    }).catch(() => {});
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({ [field]: e.detail.value });
  },

  onGenderTap(e) {
    this.setData({ gender: e.currentTarget.dataset.value });
  },

  onChangeAvatar() {
    const that = this;
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success(res) {
        const path = res.tempFilePaths[0];
        api.uploadImage(path, 'avatar').then(url => {
          that.setData({ avatarUrl: url });
          wx.showToast({ title: '头像已上传', icon: 'success' });
        }).catch(() => {
          wx.showToast({ title: '上传失败', icon: 'none' });
        });
      }
    });
  },

  onSave() {
    const userId = wx.getStorageSync('userId');
    if (!userId) { wx.showToast({ title: '请先登录', icon: 'none' }); return; }

    const { nickName, avatarUrl, gender, bio, location } = this.data;
    if (!nickName.trim()) {
      wx.showToast({ title: '昵称不能为空', icon: 'none' });
      return;
    }

    api.updateProfile(userId, {
      nickName: nickName.trim(),
      avatarUrl,
      gender,
      bio,
      location
    }).then(res => {
      wx.showToast({ title: '保存成功', icon: 'success' });
      // 更新本地缓存
      const stored = wx.getStorageSync('userInfo') || {};
      stored.nickName = nickName.trim();
      stored.avatarUrl = avatarUrl;
      wx.setStorageSync('userInfo', stored);
      // 标记头像已更新，通知其他页面
      getApp().globalData.profileUpdated = true;
      setTimeout(() => wx.navigateBack(), 1500);
    }).catch(() => {
      wx.showToast({ title: '保存失败', icon: 'none' });
    });
  }
});
