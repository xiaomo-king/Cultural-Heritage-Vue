// 后端 API 地址
const BASE_URL = 'http://localhost:8080/api';
// 后端静态资源根地址（用于图片）
const IMAGE_BASE_URL = 'http://localhost:8080';

// 通用请求方法
const request = (url, method = 'GET', data = {}) => {
  return new Promise((resolve, reject) => {
    const userId = wx.getStorageSync('userId');
    const header = { 'Content-Type': 'application/json' };
    if (userId) {
      header['userId'] = userId;
    }

    wx.request({
      url: BASE_URL + url,
      method,
      data,
      header,
      success: (res) => {
        if (res.statusCode === 200 && res.data.code === 200) {
          resolve(res.data.data);
        } else {
          reject(res.data?.message || '请求失败');
        }
      },
      fail: (err) => {
        reject('网络错误，请检查后端是否启动');
      }
    });
  });
};

// 将相对图片路径转为完整 URL，供 <image> 标签使用
function getImageUrl(path) {
  if (!path) return '';
  if (path.startsWith('http://') || path.startsWith('https://')) return path;
  if (path.startsWith('/')) return IMAGE_BASE_URL + path;
  return path;
}

// 封装 API 方法
const api = {
  // ===== 用户 =====
  login(account, password) {
    return request('/user/login', 'POST', { account, password });
  },
  register(account, password, phone) {
    return request('/user/register', 'POST', { account, password, phone });
  },
  searchUsers(keyword) {
    return request('/user/search?keyword=' + encodeURIComponent(keyword));
  },
  getUser(id) {
    return request('/user/' + id);
  },
  getBalance(id) {
    return request('/user/' + id + '/balance');
  },
  recharge(id, amount) {
    return request('/user/' + id + '/recharge', 'POST', { amount });
  },
  updateProfile(id, data) {
    return request('/user/' + id + '/profile', 'PUT', data);
  },

  // ===== 非遗 =====
  getHeritageList(params = {}) {
    const { category, page = 0, size = 10 } = params;
    let url = '/heritage/list?page=' + page + '&size=' + size;
    if (category) url += '&category=' + encodeURIComponent(category);
    return request(url);
  },
  getHeritageDetail(id) {
    return request('/heritage/' + id);
  },
  searchHeritage(keyword, page = 0) {
    return request('/heritage/search?keyword=' + encodeURIComponent(keyword) + '&page=' + page);
  },
  getNearbyHeritage(lat, lng, limit = 20) {
    return request('/heritage/nearby?lat=' + lat + '&lng=' + lng + '&limit=' + limit);
  },

  // ===== 打卡笔记 =====
  getPostList(params = {}) {
    const { topic, page = 0, size = 10, userId } = params;
    let url = '/post/list?page=' + page + '&size=' + size;
    if (topic) url += '&topic=' + encodeURIComponent(topic);
    if (userId) url += '&userId=' + userId;
    return request(url);
  },
  getPostDetail(id, userId) {
    let url = '/post/' + id;
    if (userId) url += '?userId=' + userId;
    return request(url);
  },
  createPost(data) {
    return request('/post/create', 'POST', data);
  },
  updatePost(postId, data) {
    return request('/post/' + postId, 'PUT', data);
  },
  likePost(postId) {
    return request('/post/' + postId + '/like', 'POST');
  },
  getLikedPosts() {
    return request('/post/liked');
  },
  addComment(postId, data) {
    return request('/post/' + postId + '/comment', 'POST', data);
  },
  getPostComments(postId, page) {
    return request('/post/' + postId + '/comments?page=' + page + '&size=10');
  },
  searchPosts(keyword, page = 0) {
    return request('/post/search?keyword=' + encodeURIComponent(keyword) + '&page=' + page);
  },

  // ===== 商品 =====
  getProductList(params = {}) {
    const { category, heritageId, page = 0, size = 10 } = params;
    let url = '/product/list?page=' + page + '&size=' + size;
    if (category) url += '&category=' + encodeURIComponent(category);
    if (heritageId) url += '&heritageId=' + heritageId;
    return request(url);
  },
  getProductDetail(id) {
    return request('/product/' + id);
  },
  searchProducts(keyword, page = 0) {
    return request('/product/search?keyword=' + encodeURIComponent(keyword) + '&page=' + page);
  },

  // ===== 购物车 =====
  getCartList() {
    return request('/cart/list');
  },
  addToCart(data) {
    return request('/cart/add', 'POST', data);
  },
  updateCart(data) {
    return request('/cart/update', 'POST', data);
  },
  removeFromCart(cartId) {
    return request('/cart/remove', 'POST', { cartId });
  },
  clearCart() {
    return request('/cart/clear', 'POST');
  },

  // ===== 订单 =====
  createOrder(data) {
    return request('/order/create', 'POST', data);
  },
  getOrderList(params = {}) {
    const { status = 'all', page = 0, size = 10 } = params;
    return request('/order/list?status=' + status + '&page=' + page + '&size=' + size);
  },
  getOrderDetail(id) {
    return request('/order/' + id);
  },
  payOrder(id) {
    return request('/order/' + id + '/pay', 'POST');
  },
  updateOrderStatus(id, status) {
    return request('/order/' + id + '/status', 'POST', { status });
  },
  evaluateOrder(id, data) {
    return request('/order/' + id + '/evaluate', 'POST', data);
  },
  getOrderCount() {
    return request('/order/count');
  },
  searchOrders(params = {}) {
    const { keyword, status = 'all', page = 0, size = 10 } = params;
    return request('/order/search?keyword=' + encodeURIComponent(keyword) + '&status=' + status + '&page=' + page + '&size=' + size);
  },

  // ===== 关注 =====
  toggleFollow(targetUserId) {
    return request('/follow/toggle', 'POST', { targetUserId });
  },
  checkFollow(targetUserId) {
    return request('/follow/check?targetUserId=' + targetUserId);
  },
  getFollowings(userId) {
    return request('/follow/' + userId + '/followings');
  },
  getFollowers(userId) {
    return request('/follow/' + userId + '/followers');
  },

  // ===== 收藏 =====
  toggleFavorite(targetId, targetType) {
    return request('/favorite/toggle', 'POST', { targetId, targetType });
  },
  getFavorites(targetType) {
    return request('/favorite/list?targetType=' + targetType);
  },
  checkFavorite(targetId, targetType) {
    return request('/favorite/check?targetId=' + targetId + '&targetType=' + targetType);
  },

  // ===== 路线 =====
  getRouteList(city) {
    let url = '/route/list';
    if (city) url += '?city=' + encodeURIComponent(city);
    return request(url);
  },
  getRouteDetail(id) {
    return request('/route/' + id);
  },

  // ===== 地址 =====
  getAddressList() {
    return request('/address/list');
  },
  saveAddress(data, addressId) {
    let url = '/address/save';
    if (addressId) url += '?addressId=' + addressId;
    return request(url, 'POST', data);
  },
  deleteAddress(id) {
    return request('/address/delete/' + id, 'POST');
  },

  // ===== 上传 =====
  uploadImage(filePath, type) {
    return new Promise((resolve, reject) => {
      const userId = wx.getStorageSync('userId');
      let url = BASE_URL + '/upload/image';
      if (type) url += '?type=' + type;
      wx.uploadFile({
        url: url,
        filePath: filePath,
        name: 'file',
        header: { userId: userId || '' },
        success: (res) => {
          const data = JSON.parse(res.data);
          if (data.code === 200) {
            // 返回完整图片URL
            const relativeUrl = data.data.url || '';
            resolve(getImageUrl(relativeUrl));
          } else reject(data.message);
        },
        fail: () => reject('上传失败')
      });
    });
  }
};

// 导出 getImageUrl 供其他页面使用
api.getImageUrl = getImageUrl;

module.exports = api;
