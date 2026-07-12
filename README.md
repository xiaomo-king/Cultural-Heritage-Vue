# 江右拾遗 - 江西非遗文化传承平台

> 拾江西非遗之美，传千年匠心之韵

一款以"非遗发现 + 文化消费"为核心的江西非遗文化传承平台，包含**微信小程序端**（用户使用）和 **Web 管理后台**（运营管理），形成"发现 → 探访 → 带走"的完整体验闭环。

---

## 项目架构

```
江右拾遗
├── user/                      # 微信小程序端（微信原生）
├── admin-dashboard/           # 管理后台（Vue 3）
└── backend/                   # 后端接口（Spring Boot）
```

## 技术栈

| 端 | 技术 |
|------|------|
| **小程序端** | 微信原生小程序（WXML + WXSS + JavaScript） |
| **管理后台** | Vue 3 + Composition API + Element Plus + ECharts + Axios + Pinia |
| **后端** | Spring Boot 3.x + Spring Data JPA + MySQL 8.x + Maven |
| **鉴权** | JWT（jjwt 0.12.5） |
| **开发工具** | 微信开发者工具 + IntelliJ IDEA + Vite + Navicat |

## 功能模块

### 🎯 小程序端（用户）

| 模块 | 功能 |
|------|------|
| **首页发现** | 双列瀑布流展示打卡笔记，分页加载、下拉刷新、点赞互动 |
| **非遗探访** | 按城市分组展示非遗项目，分类筛选 |
| **打卡系统** | 发布打卡（多图上传 + 选择非遗 + 位置标记）、评论、点赞、关注 |
| **非遗商城** | 商品列表、规格选择、购物车、地址管理 |
| **订单系统** | 下单 → 余额支付 → 发货 → 收货 → 评价，完整订单流转 |
| **个人中心** | 我的打卡、收藏、历史记录、编辑资料、余额充值、关注/粉丝 |
| **搜索** | 搜索非遗项目、商品、打卡、用户 |

### 🛠️ 管理后台（运营）

| 模块 | 功能 |
|------|------|
| **仪表盘** | ECharts 数据可视化：用户趋势、非遗分类、订单金额、热门排行 |
| **非遗管理** | 增删改查 + 图片上传 + 弹窗组件复用 |
| **商品管理** | 增删改查 + 规格动态编辑 + 图片上传 |
| **订单管理** | 订单列表、详情查看、发货操作 |
| **用户管理** | 用户列表、启用/禁用 |
| **打卡管理** | 打卡列表、评论查看与删除（分页加载） |

## 小程序端截图示意

```
┌────────────┐  ┌────────────┐  ┌────────────┐
│  发现页     │  │  探访页    │  │  商城页    │
│  ┌──┐ ┌──┐ │  │  按城市分组 │  │  商品列表  │
│  │📸│ │📸│ │  │  分类筛选  │  │  规格选择  │
│  │👍 │ │👍 │ │  │  非遗详情  │  │  购物车    │
│  └──┘ └──┘ │  │            │  │           │
└────────────┘  └────────────┘  └────────────┘
```

## 快速启动

### 前置条件

- JDK 17+
- MySQL 8.x
- Node.js 18+
- 微信开发者工具

### 1. 初始化数据库

在 Navicat 中依次运行以下 SQL 脚本：

```sql
-- 按顺序执行
backend/src/main/resources/sql/init.sql
backend/src/main/resources/sql/seed_heritage.sql
backend/src/main/resources/sql/seed_heritage_more.sql
backend/src/main/resources/sql/seed_product.sql
backend/src/main/resources/sql/init_admin.sql
backend/src/main/resources/sql/seed_test_data.sql
```

### 2. 启动后端

```bash
cd backend
# 检查 application.yml 中数据库密码是否正确
# 用 IDEA 打开 backend/，运行 JiangyouApplication.java
# 后端启动在 http://localhost:8080
```

### 3. 启动管理后台

```bash
cd admin-dashboard
npm install
npm run dev
# 打开 http://localhost:5173
# 默认账号：admin / 123456
```

### 4. 运行小程序

- 用微信开发者工具打开 `user/` 目录
- 勾选"不校验合法域名"
- 小程序端测试账号：`zhangsan / 123456` 或 `lisi1111 / 123456`

## 项目结构

```
├── user/                          # 微信小程序端（24个页面）
│   ├── pages/                     # 页面
│   │   ├── index/                 # 首页（瀑布流）
│   │   ├── map/                   # 探访页
│   │   ├── shop/ shop-detail/     # 商城
│   │   ├── cart/ checkout/ payment/ # 购物车-结算-支付
│   │   ├── order/ order-detail/   # 订单
│   │   ├── profile/               # 个人中心
│   │   ├── checkin-post/ checkin-detail/ # 打卡
│   │   ├── login/                 # 登录注册
│   │   └── ...                    # 其他页面
│   ├── utils/
│   │   ├── api.js                 # API 封装（30+接口）
│   │   ├── auth.js                # 登录鉴权
│   │   └── history.js             # 浏览历史
│   └── app.js                     # 全局配置
│
├── admin-dashboard/               # Vue 3 管理后台（11个页面）
│   ├── src/
│   │   ├── views/                 # 页面
│   │   ├── components/            # 弹窗组件
│   │   ├── api/                   # API 模块
│   │   ├── router/                # 路由
│   │   └── stores/                # Pinia 状态
│   └── vite.config.js
│
├── backend/                       # Spring Boot 后端（15张表）
│   ├── src/main/java/com/jiangyou/
│   │   ├── controller/            # 12个控制器
│   │   ├── service/               # 14个服务
│   │   ├── repository/            # 15个仓库
│   │   ├── model/                 # 15个实体
│   │   └── config/                # 配置
│   └── src/main/resources/
│       ├── application.yml
│       └── sql/                   # SQL 脚本
│
└── README.md
```

## 数据库设计（15张表）

| 表 | 说明 |
|----|------|
| user | 用户表（账号+密码+手机号+余额） |
| heritage | 非遗项目（20+项，含分类/坐标/图文） |
| product | 商品表（关联非遗，规格JSON） |
| orders + order_item | 订单 + 订单商品明细 |
| post | 打卡笔记 |
| comment + likes + follow + favorite | 社交互动 |
| address + cart | 地址 + 购物车 |
| admin_user | 管理员 |
| route + route_point | 探访路线（暂未启用） |

## 项目亮点

- **双列瀑布流**：最短列优先算法实现类小红书内容发现页
- **图片分类管理**：上传图片按类型存入不同目录（heritage/product/post/avatar）
- **弹窗组件复用**：非遗/商品管理均采用弹窗组件统一新增/编辑交互
- **数据可视化**：ECharts 仪表盘含4种图表，东方文化色系
- **完整电商闭环**：从浏览 → 规格选择 → 购物车 → 支付 → 订单管理全流程
- **微信原生开发**：不依赖第三方框架，深入小程序 API

## 设计风格

- 主色：中国红 `#B63A2B` / 古铜金 `#D4AF72` / 青花蓝 `#315B7D`
- 背景：宣纸米白 `#F7F3EC`
- 风格：新中式 · 东方美学 · 数字展馆
