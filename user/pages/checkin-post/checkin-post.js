// pages/checkin-post/checkin-post.js
const api = require('../../utils/api');

Page({
  data: {
    images: [],
    heritageId: null,
    heritageName: '',
    title: '',
    content: '',
    locationName: '',
    latitude: null,
    longitude: null,
    visibility: 'public',
    canSubmit: false,
    submitting: false,
    editingPostId: null,
    editingDraftId: null,
    heritageList: [],
    cities: ['南昌市', '景德镇市', '萍乡市', '九江市', '新余市', '鹰潭市', '赣州市', '吉安市', '宜春市', '抚州市', '上饶市']
  },

  onLoad(options) {
    if (options.draftId) this.loadDraft(parseInt(options.draftId));
    if (options.postId) this.loadPost(parseInt(options.postId));
    if (options.heritageId) {
      this.setData({
        heritageId: parseInt(options.heritageId),
        heritageName: options.heritageName || ''
      });
    }
    this.loadHeritageList();
    this.checkSubmit();
  },

  loadDraft(draftId) {
    const drafts = wx.getStorageSync('draft_posts') || [];
    const draft = drafts.find(d => d.id === draftId);
    if (draft) {
      this.setData({
        images: draft.images || [],
        heritageId: draft.heritageId || null,
        heritageName: draft.heritageName || '',
        title: draft.title || '',
        content: draft.content || '',
        locationName: draft.locationName || '',
        visibility: draft.visibility || 'public',
        editingDraftId: draft.id
      });
    }
  },

  loadPost(postId) {
    api.getPostDetail(postId).then(res => {
      const post = res.post || res;
      const images = post.images ? (typeof post.images === 'string' ? post.images.split(',') : post.images) : [];
      this.setData({
        images: images,
        heritageId: post.heritageId || null,
        heritageName: post.heritageName || '',
        title: post.title || '',
        content: post.content || '',
        locationName: post.locationName || '',
        visibility: post.visibility || 'public',
        editingPostId: post.id
      });
      this.checkSubmit();
    }).catch(() => {});
  },

  loadHeritageList() {
    const that = this;
    api.getHeritageList({ page: 0, size: 50 }).then(res => {
      const list = (res && res.list) || res || [];
      that.setData({ heritageList: list });
    }).catch(() => {});
  },

  checkSubmit() {
    const { title, images, content } = this.data;
    this.setData({ canSubmit: title.trim().length > 0 || images.length > 0 || content.trim().length > 0 });
  },

  onTitleInput(e) {
    this.setData({ title: e.detail.value });
    this.checkSubmit();
  },

  onChooseImage() {
    const that = this;
    const count = 9 - this.data.images.length;
    if (count <= 0) { wx.showToast({ title: '最多9张图片', icon: 'none' }); return; }
    wx.chooseImage({
      count: count,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success(res) {
        const tempFilePaths = res.tempFilePaths || [];
        if (tempFilePaths.length === 0) return;
        wx.showLoading({ title: '上传中...', mask: true });
        let uploaded = 0;
        const newImages = [...that.data.images];
        tempFilePaths.forEach(filePath => {
          wx.uploadFile({
            url: 'http://localhost:8080/api/upload/image?type=post',
            filePath: filePath,
            name: 'file',
            header: { userId: wx.getStorageSync('userId') || '' },
            success(uploadRes) {
              try {
                const body = JSON.parse(uploadRes.data);
                if (body.code === 200) {
                  const urlData = body.data;
                  let imgUrl = '';
                  if (typeof urlData === 'string') imgUrl = urlData;
                  else if (urlData && urlData.url) imgUrl = urlData.url;
                  if (imgUrl && !imgUrl.startsWith('http')) imgUrl = 'http://localhost:8080' + imgUrl;
                  if (imgUrl) newImages.push(imgUrl);
                } else {
                  newImages.push(filePath);
                }
              } catch (e) {
                newImages.push(filePath);
              }
            },
            fail() { newImages.push(filePath); },
            complete() {
              uploaded++;
              if (uploaded === tempFilePaths.length) {
                wx.hideLoading();
                that.setData({ images: newImages });
                that.checkSubmit();
              }
            }
          });
        });
      }
    });
  },

  onDeleteImage(e) {
    const index = e.currentTarget.dataset.index;
    const images = this.data.images;
    images.splice(index, 1);
    this.setData({ images: images });
    this.checkSubmit();
  },

  onHeritageChange(e) {
    const idx = e.detail.value;
    const selected = this.data.heritageList[idx];
    if (selected) this.setData({ heritageId: selected.id, heritageName: selected.name });
  },

  onContentInput(e) {
    this.setData({ content: e.detail.value });
    this.checkSubmit();
  },

  onCityChange(e) {
    const idx = e.detail.value;
    this.setData({ locationName: '江西省' + this.data.cities[idx] });
  },

  onLocationInput(e) {
    this.setData({ locationName: e.detail.value });
  },

  setVisibility(e) {
    this.setData({ visibility: e.currentTarget.dataset.visibility });
  },

  onSaveDraft() {
    const { images, heritageId, heritageName, title, content, locationName, visibility } = this.data;
    if (images.length === 0 && title.trim().length === 0 && content.trim().length === 0) {
      wx.showToast({ title: '请添加内容', icon: 'none' }); return;
    }
    const draft = {
      id: Date.now(), images, heritageId, heritageName,
      title: title.trim(), content: content.trim(),
      locationName, visibility,
      createdAt: new Date().toLocaleString('zh-CN')
    };
    const drafts = wx.getStorageSync('draft_posts') || [];
    const existIdx = this.data.editingDraftId ? drafts.findIndex(d => d.id === this.data.editingDraftId) : -1;
    if (existIdx >= 0) { draft.id = drafts[existIdx].id; drafts[existIdx] = draft; }
    else { drafts.unshift(draft); }
    if (drafts.length > 20) drafts.pop();
    wx.setStorageSync('draft_posts', drafts);
    wx.showToast({ title: '已保存', icon: 'success' });
    setTimeout(() => { wx.navigateBack(); }, 1500);
  },

  onSubmit() {
    const userId = wx.getStorageSync('userId');
    if (!userId) { wx.showToast({ title: '请先登录', icon: 'none' }); return; }
    const { images, heritageId, heritageName, title, content, locationName, visibility } = this.data;
    if (images.length === 0 && content.trim().length === 0 && title.trim().length === 0) {
      wx.showToast({ title: '请添加内容', icon: 'none' }); return;
    }
    this.setData({ submitting: true });
    const postData = { images, title: title.trim(), content: content.trim(), locationName, visibility };
    if (heritageId) { postData.heritageId = heritageId; postData.heritageName = heritageName; }
    const promise = this.data.editingPostId ? api.updatePost(this.data.editingPostId, postData) : api.createPost(postData);
    promise.then(() => {
      this.setData({ submitting: false });
      wx.showToast({ title: '发布成功！', icon: 'success' });
      if (this.data.editingDraftId) {
        let drafts = wx.getStorageSync('draft_posts') || [];
        drafts = drafts.filter(d => d.id !== this.data.editingDraftId);
        wx.setStorageSync('draft_posts', drafts);
      }
      setTimeout(() => { wx.navigateBack(); }, 1500);
    }).catch(() => {
      this.setData({ submitting: false });
      wx.showToast({ title: '发布失败', icon: 'none' });
    });
  }
});
