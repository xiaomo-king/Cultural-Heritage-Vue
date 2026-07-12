const api = require('../../utils/api');

Page({
  data: {
    addressId: null,
    consignee: '',
    phone: '',
    region: '',
    detail: '',
    isDefault: false
  },

  onLoad(options) {
    if (options.id) {
      wx.setNavigationBarTitle({ title: '编辑地址' });
      this.setData({ addressId: parseInt(options.id) });
      this.loadAddress(parseInt(options.id));
    } else {
      wx.setNavigationBarTitle({ title: '新增地址' });
    }
  },

  loadAddress(id) {
    api.getAddressList().then(list => {
      const addr = (list || []).find(a => a.id === id);
      if (addr) {
        this.setData({
          consignee: addr.consignee || '',
          phone: addr.phone || '',
          region: addr.region || '',
          detail: addr.detail || '',
          isDefault: addr.isDefault == 1
        });
      }
    }).catch(() => {});
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({ [field]: e.detail.value });
  },

  toggleDefault(e) {
    this.setData({ isDefault: e.detail.value });
  },

  saveAddress() {
    const { consignee, phone, region, detail, isDefault, addressId } = this.data;
    if (!consignee.trim()) { wx.showToast({ title: '请输入收件人', icon: 'none' }); return; }
    if (!phone.trim() || phone.length < 11) { wx.showToast({ title: '请输入正确手机号', icon: 'none' }); return; }

    api.saveAddress({
      consignee: consignee.trim(),
      phone: phone.trim(),
      region: region.trim(),
      detail: detail.trim(),
      isDefault: isDefault ? true : false
    }, addressId).then(() => {
      wx.showToast({ title: '保存成功', icon: 'success' });
      setTimeout(() => { wx.navigateBack(); }, 1500);
    }).catch(() => {
      wx.showToast({ title: '保存失败', icon: 'none' });
    });
  }
});
