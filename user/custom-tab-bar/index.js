Component({
  data: {
    selected: 0,
    list: [
      { pagePath: '/pages/index/index', text: '发现', iconPath: '/images/tab_home.png', selectedIconPath: '/images/tab_home_hl.png' },
      { pagePath: '/pages/map/map', text: '探访', iconPath: '/images/tab_map.png', selectedIconPath: '/images/tab_map_hl.png' },
      { pagePath: '', text: '', iconPath: '', selectedIconPath: '', isCenter: true },
      { pagePath: '/pages/shop/shop', text: '好物', iconPath: '/images/tab_shop.png', selectedIconPath: '/images/tab_shop_hl.png' },
      { pagePath: '/pages/profile/profile', text: '我的', iconPath: '/images/tab_profile.png', selectedIconPath: '/images/tab_profile_hl.png' }
    ]
  },

  attached() {
    // 获取当前页面路径设置选中状态
    const pages = getCurrentPages();
    const currentPage = pages[pages.length - 1];
    if (currentPage) {
      const path = '/' + currentPage.route;
      const idx = this.data.list.findIndex(item => item.pagePath === path);
      if (idx >= 0) {
        this.setData({ selected: idx });
      }
    }
  },

  methods: {
    switchTab(e) {
      const index = e.currentTarget.dataset.index;
      const item = this.data.list[index];
      
      if (item.isCenter) {
        // 中间+按钮 - 跳转发布打卡
        wx.navigateTo({ url: '/pages/checkin-post/checkin-post' });
        return;
      }
      
      if (item.pagePath) {
        wx.switchTab({ url: item.pagePath });
      }
    }
  }
});
