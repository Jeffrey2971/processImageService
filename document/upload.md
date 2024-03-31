### API 接入测试

#### 填写协议信息

- 签名规则为 `md5(appId + md5(image) + salt + appSecret)`
- 应用 id（appId）以及应用私钥（appSecret）请 [点击此处获取](https://www.processimage.cn/watermark/user/main)
- 签名信息（sign）在 API 调用时，请参照 API 文档计算出 sign 后在 Header 中指定
- [API 接入文档](https://www.processimage.cn/watermark/ApiDocument.html)
- [sign 在线生成工具](http://localhost:8082/watermark/access/upload#sign-生成工具)

| 参数    | 描述 | 是否必须 | 值   |
|-------|--| -------- | ---- |
| salt  | 随机数 | 是       |      |
| sign  | 签名 | 是       |      |
| appId | 应用 id | 是       |      |



#### 填写基本信息

- 以下参数不是必须的，在接入时根据需要配置

| 参数            | 描述                 | 是否必须 | 值   |
| --------------- | -------------------- | -------- | ---- |
| sync            | 描述较长，详情见文档 | 否       |      |
| show            | 描述较长，详情见文档 | 否       |      |
| offset          | 描述较长，详情见文档 | 否       |      |
| ocrOnly         | 描述较长，详情见文档 | 否       |      |
| callback        | 描述较长，详情见文档 | 否       |      |
| rectangles      | 描述较长，详情见文档 | 否       |      |
| watermarkNames  | 描述较长，详情见文档 | 否       |      |
| excludeKeywords | 描述较长，详情见文档 | 否       |      |



#### 选择图片

- 目前不支持 URL 和 Base64 方式接入

- 文件上传的优先级为 `File > URL > Base64`
- 当前测试页中**，**不建议使用超过 512 kb 的 Base64 编码，避免页面卡顿

| 参数       | 描述            | 值     |
| ---------- | --------------- | ------ |
| File       | 图片            |        |
| ~~URL~~    | ~~图片 URL~~    | 不可用 |
| ~~Base64~~ | ~~图片 Base64~~ | 不可用 |



#### 点击提交请求

------



### Sign 生成工具

- sign 生成工具可为你快速生成签名
- 随机数 salt 可点击刷新并填充到协议信息 salt 框内
- **请注意，账户登录时，你的公钥和私钥会被自动填充到下方对应的输入框中，请注意私钥泄露**

| 所需参数      | 描述                                     |      |
|-----------|----------------------------------------| ---- |
| image     | 图片数据，需手动上传                             |      |
| salt      | 随机数，范围是 [10^7, 10^8-1] ，长度为 8 位，首位不为 0 |      |
| appId     | 应用 id，登陆状态下自动获取                        |      |
| appSecret | 应用私钥，登陆状态下自动获取                         |      |



#### 生成签名

------



### SDK & DEMO

| 语言   | sdk & demo 下载                                              |
| ------ | ------------------------------------------------------------ |
| Java   | [java-sdk-maven.zip](https://www.processimage.cn/watermark/SDK/Java/java-sdk-maven.zip) |
| Python | [python-demo](https://www.processimage.cn/watermark/SDK/Python/PythonSDK.html) |

------



page provided by jeffrey, concat wechat Jeffrey0203- or email jeffrey2971@outlook.com
