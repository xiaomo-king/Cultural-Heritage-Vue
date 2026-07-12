const api = require('../../utils/api');

Page({
  data: {
    tabs: [
      { key: 'all', label: '全部' },
      { key: 'paid', label: '待发货' },
      { key: 'shipped', label: '待收货' },
      { key: 'received', label: '待评价' }
    ],
    currentTab: 'all',
    orders: [],
    page: 0,
    size: 10,
    hasMore: true,
    loading: false,
    searchKeyword: '',
    searchKeywords: { all: '', paid: '', shipped: '', received: '' },
    statusMap: {
      'pending': '待付款',
      'paid': '待发货',
      'shipped': '待收货',
      'received': '待评价',
      'completed': '已完成',
      'cancelled': '已取消'
    }
  },

  onLoad(options) {
    if (options.status) {
      this.setData({ currentTab: options.status });
    }
    this.loadOrders(true);
    this._timer = setInterval(() => this.onTimerTick(), 1000);
  },

  onUnload() {
    if (this._timer) {
      clearInterval(this._timer);
      this._timer = null;
    }
  },

  onShow() {
    if (!this.data.searchKeyword) {
      this.loadOrders(true);
    }
  },

  onPullDownRefresh() {
    this.loadOrders(true, () => {
      wx.stopPullDownRefresh();
    });
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadOrders();
    }
  },

  onSearchInput(e) {
    this.setData({ searchKeyword: e.detail.value });
  },

  doSearch() {
    const kw = this.data.searchKeyword.trim();
    const tab = this.data.currentTab;
    // Save per-tab keyword
    const key = 'searchKeywords.' + tab;
    this.setData({ [key]: kw, orders: [], page: 0, hasMore: true });
    this.loadOrders(true);
  },

  clearSearch() {
    const tab = this.data.currentTab;
    const key = 'searchKeywords.' + tab;
    this.setData({ searchKeyword: '', [key]: '', orders: [], page: 0, hasMore: true });
    this.loadOrders(true);
  },

  switchTab(e) {
    const key = e.currentTarget.dataset.key;
    if (key === this.data.currentTab) return;
    // Switch keyword to this tab's saved keyword
    const savedKw = this.data.searchKeywords[key] || '';
    this.setData({
      currentTab: key,
      searchKeyword: savedKw,
      orders: [],
      page: 0,
      hasMore: true
    });
    this.loadOrders(true);
  },

  loadOrders(refresh = false, callback) {
    if (this.data.loading) return;
    const page = refresh ? 0 : this.data.page;
    this.setData({ loading: true });

    const kw = this.data.searchKeyword.trim();
    const tab = this.data.currentTab;

    const loadPromise = kw
      ? api.searchOrders({ keyword: kw, status: tab, page: page, size: this.data.size })
      : api.getOrderList({ status: tab === 'all' ? undefined : tab, page: page, size: this.data.size });

    loadPromise.then(res => {
      const content = res.list || res.content || res || [];
      const total = res.total !== undefined ? res.total : content.length;

      const orders = content.map(item => {
        const order = item.order ? { ...item.order, items: item.items || [] } : { ...item, items: [] };
        // 转换商品图为完整URL
        if (order.items) {
          order.items = order.items.map(sub => ({
            ...sub,
            productImage: api.getImageUrl(sub.productImage || '')
          }));
        }
        if (order.status === 'pending' && order.createdAt) {
          const created = new Date(order.createdAt.replace(' ', 'T') + '+08:00').getTime();
          const deadline = created + 30 * 60 * 1000;
          const now = Date.now();
          const remain = Math.max(0, Math.floor((deadline - now) / 1000));
          order.countdown = this.formatCountdown(remain);
          order._deadline = deadline;
        }
        return order;
      });

      this.setData({
        orders: refresh ? orders : this.data.orders.concat(orders),
        page: page + 1,
        hasMore: orders.length === this.data.size,
        loading: false
      });
      if (callback) callback();
    }).catch(() => {
      this.setData({ loading: false });
      if (callback) callback();
    });
  },

  goToDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: '/pages/order-detail/order-detail?id=' + id });
  },

  updateStatus(e) {
    const id = e.currentTarget.dataset.id;
    const status = e.currentTarget.dataset.status;
    const statusText = status === 'shipped' ? '提醒发货' : '确认收货';

    wx.showModal({
      title: '提示',
      content: '确定' + statusText + '？',
      success: (res) => {
        if (res.confirm) {
          api.updateOrderStatus(id, status).then(() => {
            wx.showToast({ title: '操作成功', icon: 'success' });
            this.loadOrders(true);
          }).catch(() => {
            wx.showToast({ title: '操作失败', icon: 'none' });
          });
        }
      }
    });
  },

  goToEvaluation(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: '/pages/evaluation/evaluation?id=' + id });
  },

  goToShop() {
    wx.switchTab({ url: '/pages/shop/shop' });
  },

  formatCountdown(seconds) {
    if (seconds <= 0) return '已超时';
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return m + '分' + (s < 10 ? '0' : '') + s + '秒';
  },

  goToPay(e) {
    const id = e.currentTarget.dataset.id;
    const orderNo = e.currentTarget.dataset.orderno;
    wx.navigateTo({ url: '/pages/payment/payment?orderId=' + id + '&orderNo=' + orderNo });
  },

  onTimerTick() {
    const orders = this.data.orders.map(o => {
      if (o.status === 'pending' && o._deadline) {
        const remain = Math.max(0, Math.floor((o._deadline - Date.now()) / 1000));
        o.countdown = this.formatCountdown(remain);
        if (remain <= 0) o.status = 'cancelled';
      }
      return o;
    });
    this.setData({ orders });
  }
});
