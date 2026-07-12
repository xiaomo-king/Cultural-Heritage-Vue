// pages/checkin-detail/checkin-detail.js
const api = require('../../utils/api');
const history = require('../../utils/history');

Page({
  data: {
    loading: true,
    post: {},
    images: [],
    userInfo: {},
    isLiked: false,
    isFollowing: false,
    myUserId: null,
    postId: null,
    comments: [],
    commentText: '',
    postTime: '',
    commentPage: 0,
    commentTotal: 0,
    hasMoreComments: false,
    loadingComments: false
  },

  onLoad(options) {
    const postId = options.id;
    const myUserId = wx.getStorageSync('userId');
    this.setData({ myUserId: myUserId || null });
    if (postId) {
      this.setData({ postId });
      this.loadDetail(postId, myUserId);
      this.loadComments(postId, 0, true);
    }
  },

  loadDetail(postId, userId) {
    const that = this;
    api.getPostDetail(postId, userId).then(res => {
      const images = (res.images || []).filter(i => i);
      const firstImg = images.length > 0 ? images[0] : '';
      history.addPostHistory(postId, res.content || '打卡笔记', firstImg);
      const timeStr = that.formatTime(res.createdAt);
      // 去掉 comments，单独分页加载
      const { comments, ...postData } = res;
      that.setData({
        post: postData, images: images,
        userInfo: { userId: res.userId, nickName: res.nickName, avatarUrl: res.avatarUrl },
        isLiked: res.isLiked || false,
        postTime: timeStr, loading: false
      });
      wx.setNavigationBarTitle({ title: res.nickName + '的打卡' });
    }).catch(() => {
      that.setData({ loading: false });
      wx.showToast({ title: '加载失败', icon: 'none' });
    });
  },

  // 分页加载评论
  loadComments(postId, page, reset) {
    if (this.data.loadingComments) return;
    this.setData({ loadingComments: true });
    api.getPostComments(postId, page).then(res => {
      const list = (res && res.list) || res || [];
      const total = res.total !== undefined ? res.total : list.length;
      const oldComments = reset ? [] : this.data.comments;
      this.setData({
        comments: oldComments.concat(list),
        commentPage: page + 1,
        commentTotal: total,
        hasMoreComments: oldComments.length + list.length < total,
        loadingComments: false
      });
    }).catch(() => {
      this.setData({ loadingComments: false });
    });
  },

  // 触底加载更多评论
  onReachBottom() {
    if (this.data.hasMoreComments && !this.data.loadingComments) {
      this.loadComments(this.data.postId, this.data.commentPage, false);
    }
  },

  formatTime(dateStr) {
    if (!dateStr) return '';
    const d = new Date(dateStr);
    return (d.getMonth() + 1) + '月' + d.getDate() + '日 ' + d.getHours().toString().padStart(2,'0') + ':' + d.getMinutes().toString().padStart(2,'0');
  },

  onToggleLike() {
    const userId = this.data.myUserId;
    if (!userId) { wx.showToast({ title: '请先登录', icon: 'none' }); return; }
    const that = this;
    api.likePost(this.data.post.id).then(res => {
      const liked = (res && res.isLiked !== undefined) ? res.isLiked : !that.data.isLiked;
      const count = that.data.post.likeCount + (liked ? 1 : -1);
      that.setData({ isLiked: liked, 'post.likeCount': Math.max(0, count) });
      getApp().toggleLikedPostId(that.data.post.id, liked);
      getApp().globalData.postDataUpdated = true;
    }).catch(() => { wx.showToast({ title: '操作失败', icon: 'none' }); });
  },

  onToggleFollow() {
    const userId = this.data.myUserId;
    if (!userId) { wx.showToast({ title: '请先登录', icon: 'none' }); return; }
    const targetId = this.data.userInfo.userId;
    if (targetId === userId) return;
    api.toggleFollow(targetId).then(res => {
      this.setData({ isFollowing: (res && res.isFollowing !== undefined) ? res.isFollowing : !this.data.isFollowing });
    }).catch(() => {});
  },

  onCommentInput(e) { this.setData({ commentText: e.detail.value }); },

  onSubmitComment() {
    const content = this.data.commentText.trim();
    if (!content) { wx.showToast({ title: '请输入评论', icon: 'none' }); return; }
    if (!this.data.myUserId) { wx.showToast({ title: '请先登录', icon: 'none' }); return; }
    api.addComment(this.data.post.id, { content }).then(() => {
      wx.showToast({ title: '评论成功', icon: 'success' });
      this.setData({ commentText: '' });
      // 重新加载评论
      this.loadComments(this.data.post.id, 0, true);
      getApp().globalData.postDataUpdated = true;
    }).catch(() => { wx.showToast({ title: '评论失败', icon: 'none' }); });
  },

  onUserTap() {
    const uid = this.data.userInfo.userId;
    if (!uid) return;
    wx.navigateTo({ url: '/pages/user-feed/user-feed?id=' + uid });
  },

  onCommentUserTap(e) {
    const uid = e.currentTarget.dataset.userid;
    if (!uid) return;
    wx.navigateTo({ url: '/pages/user-feed/user-feed?id=' + uid });
  },

  onHeritageTap() {
    if (this.data.post.heritageId) wx.navigateTo({ url: '/pages/detail/detail?id=' + this.data.post.heritageId });
  },

  onTagTap(e) { wx.navigateTo({ url: '/pages/search/search?keyword=' + encodeURIComponent(e.currentTarget.dataset.tag) }); },

  onTopicTap(e) { wx.navigateTo({ url: '/pages/search/search?topic=' + encodeURIComponent(e.currentTarget.dataset.topic) }); },

  onLocationTap() {
    const loc = this.data.post;
    if (loc.latitude && loc.longitude) {
      wx.openLocation({ latitude: loc.latitude, longitude: loc.longitude, name: loc.locationName, scale: 15 });
    }
  },

  onShareAppMessage() {
    return { title: this.data.post.title || '江右拾遗', path: '/pages/checkin-detail/checkin-detail?id=' + this.data.post.id };
  }
});
