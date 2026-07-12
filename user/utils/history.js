// 浏览历史记录工具
// 记录浏览过的打卡笔记
function addPostHistory(id, title, image) {
  const history = wx.getStorageSync('history_posts') || [];
  // 去重：移除已存在的相同记录
  const filtered = history.filter(item => item.id !== id);
  filtered.unshift({
    id: id,
    title: title || '打卡笔记',
    image: image || '',
    timestamp: Date.now()
  });
  // 最多保留50条
  if (filtered.length > 50) filtered.pop();
  wx.setStorageSync('history_posts', filtered);
}

// 记录浏览过的商品
function addProductHistory(id, title, image) {
  const history = wx.getStorageSync('history_products') || [];
  const filtered = history.filter(item => item.id !== id);
  filtered.unshift({
    id: id,
    title: title || '非遗商品',
    image: image || '',
    timestamp: Date.now()
  });
  if (filtered.length > 50) filtered.pop();
  wx.setStorageSync('history_products', filtered);
}

module.exports = {
  addPostHistory,
  addProductHistory
};
