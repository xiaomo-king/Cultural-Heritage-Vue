const api = require('../../utils/api');

Page({
  data: {
    routes: [],
    currentRoute: null,
    points: [],
    loading: false
  },

  onLoad() {
    this.loadRoutes();
  },

  onShow() {
    if (!this.data.currentRoute) {
      this.loadRoutes();
    }
  },

  loadRoutes() {
    this.setData({ loading: true });
    api.getRouteList().then(routes => {
      this.setData({ routes: routes || [], loading: false });
    }).catch(() => {
      this.setData({ loading: false });
    });
  },

  showDetail(e) {
    const id = e.currentTarget.dataset.id;
    this.setData({ loading: true });

    api.getRouteDetail(id).then(data => {
      const route = data.route || data;
      let points = data.points || [];
      
      // 获取每个节点的非遗名称
      const prom = points.length > 0 
        ? Promise.all(points.map(p => 
            api.getHeritageDetail(p.heritageId).then(h => {
              p.heritageName = h ? h.name : '非遗项目 #' + p.heritageId;
              return p;
            }).catch(() => {
              p.heritageName = '非遗项目 #' + p.heritageId;
              return p;
            })
          ))
        : Promise.resolve([]);

      prom.then(pts => {
        this.setData({
          currentRoute: route,
          points: pts,
          loading: false
        });
      });
    }).catch(() => {
      this.setData({ loading: false });
      wx.showToast({ title: '加载失败', icon: 'none' });
    });
  },

  backToList() {
    this.setData({ currentRoute: null, points: [] });
  },

  goToHeritage(e) {
    const id = e.currentTarget.dataset.id;
    if (id) {
      wx.navigateTo({ url: '/pages/detail/detail?id=' + id });
    }
  }
});
