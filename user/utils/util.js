// 工具函数库
const formatTime = date => {
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  return `${year}-${month}-${day}`;
};

const formatNumber = n => {
  n = n.toString();
  return n[1] ? n : '0' + n;
};

// 显示加载提示
const showLoading = (title = '加载中...') => {
  wx.showLoading({ title, mask: true });
};

// 隐藏加载提示
const hideLoading = () => {
  wx.hideLoading();
};

// 显示提示信息
const showToast = (title, icon = 'none') => {
  wx.showToast({ title, icon, duration: 2000 });
};

module.exports = {
  formatTime,
  showLoading,
  hideLoading,
  showToast
};
