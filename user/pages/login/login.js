// pages/login/login.js
const auth = require('../../utils/auth');

Page({
  data: {
    mode: 'login',
    account: '',
    password: '',
    confirmPassword: '',
    phone: '',
    accountError: '',
    passwordError: '',
    phoneError: '',
    errorMsg: '',
    canSubmit: false
  },

  switchMode(e) {
    const mode = e.currentTarget.dataset.mode;
    this.setData({ mode, errorMsg: '', account: '', password: '', confirmPassword: '', phone: '', accountError: '', passwordError: '', phoneError: '', canSubmit: false });
  },

  onAccountInput(e) { this.setData({ account: e.detail.value, accountError: '', errorMsg: '' }); this.checkSubmit(); },
  onPasswordInput(e) { this.setData({ password: e.detail.value, passwordError: '', errorMsg: '' }); this.checkSubmit(); },
  onConfirmPasswordInput(e) { this.setData({ confirmPassword: e.detail.value, errorMsg: '' }); this.checkSubmit(); },
  onPhoneInput(e) { this.setData({ phone: e.detail.value, phoneError: '', errorMsg: '' }); this.checkSubmit(); },

  // 账号失去焦点校验：8-15位数字或字母
  onAccountBlur() {
    const account = this.data.account.trim();
    if (account && account.length > 0) {
      if (!/^[a-zA-Z0-9]{8,15}$/.test(account)) {
        this.setData({ accountError: '账号需要8-15位数字或字母' });
      } else {
        this.setData({ accountError: '' });
      }
    } else {
      this.setData({ accountError: '' });
    }
  },

  // 密码失去焦点校验：6-20位数字或字母
  onPasswordBlur() {
    const password = this.data.password;
    if (password && password.length > 0) {
      if (!/^[a-zA-Z0-9]{6,20}$/.test(password)) {
        this.setData({ passwordError: '密码需要6-20位数字或字母' });
      } else {
        this.setData({ passwordError: '' });
      }
    } else {
      this.setData({ passwordError: '' });
    }
  },

  // 手机号失去焦点校验
  onPhoneBlur() {
    const phone = this.data.phone;
    if (phone && phone.length > 0) {
      if (!/^1[34578]\d{9}$/.test(phone)) {
        this.setData({ phoneError: '手机号格式不正确' });
      } else {
        this.setData({ phoneError: '' });
      }
    } else {
      this.setData({ phoneError: '' });
    }
  },

  checkSubmit() {
    const { account, password, mode, confirmPassword, accountError, passwordError, phoneError } = this.data;
    let ok = account.trim().length > 0 && password.length > 0 && !accountError && !passwordError;
    if (mode === 'register') ok = ok && confirmPassword.length > 0 && !phoneError;
    this.setData({ canSubmit: ok });
  },

  onSubmit() {
    const { account, password, mode, confirmPassword, phone } = this.data;

    // 账号校验：8-15位数字或字母
    if (!/^[a-zA-Z0-9]{8,15}$/.test(account.trim())) {
      this.setData({ errorMsg: '账号需要8-15位数字或字母' });
      return;
    }
    // 密码校验：6-20位数字或字母
    if (!/^[a-zA-Z0-9]{6,20}$/.test(password)) {
      this.setData({ errorMsg: '密码需要6-20位数字或字母' });
      return;
    }
    if (mode === 'register' && password !== confirmPassword) {
      this.setData({ errorMsg: '两次密码不一致' });
      return;
    }
    // 注册时校验手机号
    if (mode === 'register' && phone) {
      if (!/^1[34578]\d{9}$/.test(phone)) {
        this.setData({ errorMsg: '手机号格式不正确' });
        return;
      }
    }

    this.setData({ errorMsg: '', canSubmit: false });

    const request = mode === 'register'
      ? auth.register(account.trim(), password, phone)
      : auth.login(account.trim(), password);

    request.then(() => {
      wx.showToast({ title: mode === 'register' ? '注册成功' : '登录成功', icon: 'success' });
      setTimeout(() => wx.navigateBack(), 1500);
    }).catch(err => {
      this.setData({ errorMsg: err || '操作失败', canSubmit: true });
    });
  }
});
