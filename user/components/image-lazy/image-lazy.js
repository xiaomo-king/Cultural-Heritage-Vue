// components/image-lazy/image-lazy.js
Component({
  properties: {
    src: { type: String, value: '' },
    width: { type: Number, value: 330 },
    height: { type: Number, value: 440 },
    mode: { type: String, value: 'aspectFill' },
    id: { type: String, value: '' }
  },
  data: {
    loaded: false
  },
  observers: {
    'src': function (newSrc) {
      if (newSrc) {
        // 使用 IntersectionObserver 懒加载
        this.createIntersectionObserver().relativeToViewport({ bottom: 100 }).observe('.image-lazy-container', (res) => {
          if (res.intersectionRatio > 0) {
            this.setData({ loaded: true });
            this.disconnect();
          }
        });
      }
    }
  },
  methods: {
    onLoad() {
      this.setData({ loaded: true });
    },
    onError() {
      this.setData({ loaded: true });
    }
  }
});
