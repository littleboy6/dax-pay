## 单商户

2.0.9: 消息通知改版和系统优化
- [ ] 增加类似支付宝应用通知的方式, 首先支持http方式通信
- [ ] 优化前端各种状态颜色展示
- [ ] 修复功能优化, 更改为交易调整单
  - [x] 支付订单调整
  - [x] 退款订单调整
  - [ ] 分账订单调整
  - [ ] 转账订单调整
- [ ] 增加验签调试等功能的页面
- [ ] 聚合支付UA在前端判断, 提高响应速度
- [ ] 增加分账回调处理(支付宝)
- [ ] 转账同步接口
- [ ] 支持微信V3版本支付接口
- [ ] 平台配置拆分出接口地址配置项

2.1.0.0: 多商户系统统一改造

- 脚手架同步为多商户脚手架的jdk8实现

**任务池**
- [ ] 记录异常交易信息: 存储到订单金额与网关不一致
- [ ] 升级新版脚手架
  - [ ] 请求权限改版, 使用专用配置类
- [ ] 增加收单收银台功能
- [ ] 增加资金对账单功能
- [ ] 新增支付单预警功能, 处理支付单与网关状态不一致且无法自动修复的情况
- [ ] 差错单据处理
  - [ ] 支付成功回调订单已超时
  - [ ] 对账单据不平
- [ ] 特殊退款接口
- [ ] 统计报表功能
- [ ] 微信新增V3版本接口
- [ ] 增加各类日志记录,例如钱包的各项操作
- [ ] 针对同步/对账等出现脏数据导致阻塞的问题, 进行优化
    - [ ] 同步接口
    - [ ] 对账接口

