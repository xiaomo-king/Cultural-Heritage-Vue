const api = require('../../utils/api');

Page({
  data: {
    addressList: [],
    loading: false,
    fromPage: ''  // 从结算页跳来时用于返回
  },

  onLoad(options) {
    if (options.from) {
      this.setData({ fromPage: options.from });
    }
  },

  onShow() {
    this.loadAddresses();
  },

  loadAddresses() {
    const userId = wx.getStorageSync('userId');
    if (!userId) {
      this.setData({ addressList: [] });
      return;
    }
    this.setData({ loading: true });
    api.getAddressList().then(list => {
      this.setData({ addressList: list || [] });
    }).catch(() => {}).finally(() => {
      this.setData({ loading: false });
    });
  },

  addAddress() {
    wx.navigateTo({ url: '/pages/address-edit/address-edit' });
  },

  editAddress(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: '/pages/address-edit/address-edit?id=' + id });
  },

  deleteAddress(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '提示',
      content: '确定删除该地址？',
      success: (res) => {
        if (res.confirm) {
          api.deleteAddress(id).then(() => {
            this.loadAddresses();
            wx.showToast({ title: '已删除', icon: 'success' });
          }).catch(() => {
            wx.showToast({ title: '删除失败', icon: 'none' });
          });
        }
      }
    });
  },

  selectAddress(e) {
    // 如果是从结算页来的，选择地址后返回
    const pages = getCurrentPages();
    const prevPage = pages[pages.length - 2];
    if (prevPage && prevPage.route === 'pages/checkout/checkout') {
      const id = e.currentTarget.dataset.id;
      const addr = this.data.addressList.find(a => a.id === id);
      if (addr) {
        prevPage.setData({ selectedAddress: addr });
        wx.navigateBack();
      }
    }
  }
});
