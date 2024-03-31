### 2024-03-31 🚀 UPDATE
#### ① 🎨 因修改 publicKey 和 privateSecret 名称为 appId 和 appSecret 引起的变更，整体功能不变，只修改了名称相关的代码
#### ② 🎨 因修改 publicKey 和 privateSecret 名称为 appId 和 appSecret 引起的数据库 authorized_accounts_list 表字段变更
#### ③ 🎨 修改登陆页中注册功能的正确和错误标识
#### ④ ⚡️ 全面优化支付功能，使用 oauth2 换取凭证以调用支付模块下单
#### ⑤ ⚡️ 登陆后 main 页更新，移除 bootstrap 采用 layui
#### ⑥ ⚡️ 给所有页面添加白天黑夜主题效果
#### ⑦ 🚀 更新在线接入测试页，使其融入 main 中
### 2024-1-5 TODO
#### ① 数据表更新
- 修改了 account 表，移除 call_limit 字段，保留 call_successful 字段，新增 long_term_usage_count、limited_term_usage_count、limited_term_expire_days 字段
  - long_term_usage_count 长期可调用次数
  - limited_term_usage_count 限期可调用次数
  - limited_term_expire_days 限期调用次数剩余时间，单位为天
- 新增 order_info 表
  - 存储本地订单信息
- 新增 product 表
  - 存储套餐相关信息

#### ② 表更新后相关映射的关系
- src/main/resources/mapper 下的相关 mapper
- src/main/java/com/jeffrey/processimageservice/mapper 下相关接口

#### ③ AOP 订单校验、处理、更新
- 使用自定义 @Around 对处理订单前的校验，订单处理、订单状态更新

### 2024-1-3 TODO
#### ① 支付成功后前端调起的 /order/process/{orderNo} 功能
#### ② 数据库表结构变更
- 套餐有多种，按需购买、限期购买等。需要对数据库进行重新设计
  - ~~移除 account 表中 call_limit 字段，保留 call_successful 字段~~
  - ~~初步表设计，重新按照不同套餐建表 on_demand_package / monthly_package~~
    ``` sql
    CREATE TABLE on_demand_package (
    id INT AUTO_INCREMENT PRIMARY KEY, # 主键，唯一标识每个购买记录的ID。
    user_id VARCHAR(255) NOT NULL, # 用户ID，关联到用户表。
    package_id VARCHAR(255) NOT NULL, # 套餐ID，关联到套餐表。
    purchase_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, # 购买日期，默认为当前时间戳。
    call_count INT NOT NULL, # 调用次数。
    total_price DECIMAL(10, 2) NOT NULL, # 总价格。
    UNIQUE KEY user_package_unique (user_id, package_id), # 用户ID和套餐ID的联合唯一索引，确保用户不会重复购买相同套餐。
    INDEX user_id_index (user_id),
    INDEX package_id_index (package_id)
    );
    ```
    ```sql
    CREATE TABLE monthly_package (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    package_id VARCHAR(255) NOT NULL,
    purchase_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expiration_date TIMESTAMP, # 套餐的过期日期
    call_count INT NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    UNIQUE KEY user_package_unique (user_id, package_id),
    INDEX user_id_index (user_id),
    INDEX package_id_index (package_id)
    );
    ```
  
- 需要考虑数据表字段修改后和代码中字段对应问题
  - 实体类、sql、mapper 等
- 需要考虑调用时不同套餐扣除次数的问题
  - 优先扣除限期套餐次数
- 需要考虑购买完成后套餐叠加的功能
  - 限期套餐重复购买叠加日期以及次数
### 2024-1-2 TODO

#### ① 支付页面订单超时后仍重复发起请求造成页面无限弹窗
- ~~已解决，没关闭定时器~~
#### ② API 接入文档优化
- ~~移除无关备注~~
#### ③ 页面主题切换
- ~~完成，添加浅色主题和暗色主题并跟随系统切换，涉及页面包括 ApiPrice / ApiDocument~~
- ~~完成，添加主题左侧大纲，涉及页面包括 ApiPrice / ApiDocument~~
### 2023-12-28 TODO
#### ① 支付页面订单超时后显示购买完成的问题
- ~~已解决，前后端状态码没对上~~

#### ② 前端 ui 重新设计
- 统一前端技术栈，目前采用了 jquery、axios、bootstrap、vue、jweixin、jsencrypt
- 寻找大佬共同完成 ... ...

#### ③ 检查代码健壮性问题
- 全局异常处理
- 重起类名
- 移除无引用依赖
- 类和包名问题

#### ④ sdk
- java-sdk-maven 只完成了一小部分
- python-sdk 目前只有 demo 版本

### 2022-12-04 存在问题

#### ① 处理过程中发生异常未能撤回已用次数

- **正常逻辑**

  - 正常的逻辑是 `taskAllocation` 方法处理完成后（不论同步还是异步处理），如果 AOP 切入获取的返回值中的 Data 对象的处理状态码不为 0 且不为 52000 时，将已扣除的使用次数补回

  - 在调用 `taskAllocation` 处理任务调度之前，AOP 会先切入方法，并预扣除使用次数（不论处理失败与否）

- **问题**
  - 由于预先设计结构方式，导致当处理过程中抛出异常（包括运行时异常、错误异常、自定义异常）时，无法使 `taskAllocation` 方法正常处理完成，而导致方法为能完整执行，最终导致 AOP 无法切入
- **解决**
  - 尝试在 ExceptionHandler 异常管理器添加 AOP 切入

#### ② 同时间段遇大量异步请求无法确保账户使用次数准确性

- **正常逻辑**
  - 异步请求时，应一个请求及时扣除账户中一次使用次数，采用预先扣除方式，请求即扣除次数，当处理成功时不做次数处理，当处理失败时补回扣除次数
- **问题**
  - 原先的逻辑为当图片处理完成时再扣除使用次数，这将导致同一时间段提交了大量的异步请求，但这些请求都还未开始处理，即导致了无法准时扣除账户使用次数，最终的结果可能为用户的账户中剩余 1000 次使用次数，但在同一时间段提交了 10000 个请求，提交完成后服务器已处理完成 500 个请求，剩余 9500 个请求同样会正常处理并响应回给客户，同时由于 AOP 切入扣除问题，也会导致数据库可用次数为负数
- **解决**
  - 更改 Controller 中接入方法的前置 AOP 的逻辑，原先只检查账户可用次数，现在在原有的基础上添加扣除操作
  - 更改 `taskAllocation` 完成时 AOP 的逻辑，原完成后扣除改为出异常后补回

### 2023-2-7 存在问题

#### ① 指定参数 offset 后，可能导致返回的 json 数据中的 data 对象缺失 word 字段

- 问题定位
  - 看错了

- 解决
  - 经过几次测试发现确实看错

#### ② 前端页面框选时，有些照片框选后得到的坐标提交给服务器时处理无效（图片水印一般为斜着的字体）

- 问题定位
  - 原因可能是前端框选时并不能活没有做框选框的旋转功能，如水印字体如果是斜着的将导致坐标轴 x 将发生偏移
- 解决
  - 可能的解决方案是添加框选框转选功能并重试

#### ③ 服务器发送响应后，页面渲染为服务器发送的图片，如再框选应在处理后的基础上而不是原始图

- 问题定位
  - 可能是浏览器接收到响应后替换页面的原图后，再次请求依然是一开始上传的原图
- 解决
  - 

#### ④ 页面在线演示功能提交的图片以及响应的数据未进行缓存

#### ⑤ 尝试将 ffmpeg 更换为 ImageMagick 或 GraphicsMagick

#### ⑥ 处理之前获取图片的整体灰度，在处理完成后再获取一次图片的灰度，根据处理的差值可使用"brightness" 和 "contrast"进行调整

#### ⑦ 参数 offset 貌似不生效

#### ⑧ 参数 excludeKeywords 和 markNames 未完成

#### ⑨ `public_account` 表 sql 未完成

- 完成

  ```sql
  create table process_image_watermark.public_account
  (
      id               int auto_increment
          primary key,
      openid           varchar(32)    not null,
      can_use          int default 10 not null,
      all_used         int default 0  not null,
      last_modify_time datetime       not null,
      constraint openid
          unique (openid)
  );
  ```

  

#### ⑩ 公众号初始化用户功能未完成

- 完成
  - 在用户发起请求时先使用 @Before AOP ，在增强代码中查看用户是否首次访问，如果是则为该用户初始化一条数据

#### ⑪ URL 参数 openid 未携带时发生空指针异常

- 问题定位
  - Controller 校验参数如果非法时并不是直接返回，而是通过抛出自定义异常从而被自定义的 ExceptionHandlers 拦截，在拦截前会先被 @AfterThrowing 注解拦截，增强代码中的主要逻辑为对异常信息进行记录，其中有一条信息是加密后图片的信息，由于在线演示功能并没有对图片进行加密所以没有图片的加密信息，从而导致了空指针异常
- 解决
  - `int id = encryptedInfo == null ? -1 : encryptedInfo.getId();`

### 2023-2-8 存在问题

#### ① 新的错误 Error opening filters!

#### ② 公众号菜单栏点击 【去除水印】返回的链接进行访问后的页面中的 【在线演示】应该携带 openid 参数，否则即使关注了公众号的用户也无法进行在线体验

- 问题定位
  - 公众号模块服务处理菜单栏点击事件场景 process_watermark 没有携带 openid 参数，导致 API 调用测试页没有用户 id ，从而导致页面的在线演示功能同样没有携带 openid
- 解决
  - 公众号模块服务处理菜单栏点击事件场景 process_watermark 时携带 openid
  - 在页面中获取 URL 中的 openid 参数，并赋予给 `/demo` 路由

#### ③ 项目部署到服务器后发生以下异常

- 异常

  ```tex
  java.lang.ClassCastException: org.springframework.security.web.servletapi.HttpServlet3RequestFactory$Servlet3SecurityContextHolderAwareRequestWrapper cannot be cast to org.springframework.web.multipart.MultipartHttpServletRequest
  	at com.jeffrey.processimageservice.interceptor.SignatureVerificationInterceptor.preHandle(SignatureVerificationInterceptor.java:53)
  ```

- 异常定位
  - 猜测测可能是拦截器配置的问题

- 解决

  - 修改拦截器配置

#### ④ CORS 跨域问题

#### ⑤ 缓存清理功能

#### 2022-2-9 存在问题

#### ① 登陆页找回密码功能

#### ② 登陆后 main.html 页面修改密码功能

#### ② 注册拦截器，异步检查不应该走拦截器，而是整体表单发送时再走拦截器

- 完成

### 2022-2-10 存在问题

#### ① 替换所有 thymeleaf 模板中的固定 URL，根据项目环境动态替换

- 完成

#### ② 登陆成功后的 main.html 添加上次登陆 IP 以及时间

#### ③ 登陆成功后的 main.html 添加历史调用记录列表显示功能

- 配合 `layui.js` + `PageHelper` 框架完成

#### 2022-2-11

#### ① 前端请求参数使用 rsa 非对称加密

- thymeleaf 将公钥传递给页面，使用 jsencrypt.min.js 加密请求参数

#### ② 后端返回的数据使用 aes 非对称加密

- 如果采用 RSA 非对称加密将无法实现后端加密前端解密，所以使用 AES 加密方式配合 RSA 进行传输
- 需要引入 `Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files`

### 2022-2-12

#### ① 在线演示缓存功能

- 完成

#### ② 公众号用户调用演示记录功能

- 完成

#### ③ 公众号在线演示记录处理状态时，如果用户是首次使用调用，在记录表内 `aid` 字段为 0

- 问题定位
  - AOP 执行顺序的问题，应该先初始化用户数据再记录用于调用处理记录，避免在记录时没有用户信息导致 `aid` 字段为 0
- 解决
  - 使用 `@Oroder` 注解解决

#### ④ 公众号用户使用次数扣除功能

- 完成

### 2022-2-13

#### ① 当账户使用次数为 0 次后，调用还在缓存的请求被拒绝

- 问题定位
  - 检测账户可使用次数前，检查该请求是否缓存

### 2022-2-14

#### ① 使用策略模式修改复杂的 if 语句

- 完成



#### 2022-2-15

#### ① 新增登陆拦截器，关于需要登录的资源都应该先检查是否登陆

- 完成

#### ② 在线演示如未提供 openid 虽然会被拦截请求，但往数据库里初始化了一条新数据

- 完成

### 2022-2-20

#### ① 完成登录状态下密码重置功能

#### ② 完成未登录状态下密码重置功能

- 放行邮箱发送验证码页面的路由

- 放行发送邮件请求的路由

- 编写可根据填写邮箱地址以及验证码的页面

  - 使用 jsencrypt.min.js 以及公钥加密邮件地址发送至服务器
  - 用户收到验证码邮件后，输入验证码获取可直接重置的邮件

- 编写后端逻辑

  - 定义参数解析器，根据私钥解密前端邮箱参数

  - 根据邮箱地址查找用户

  - 用户存在发送验证码至目标邮箱

    - 引入自定义邮件模块
    - 编写验证码页面，用于用户收到时可显示
    - 生成由大小写字母、数字随机组合而成的字符串，并缓存至 redis 以及封装到 CreateMail 中的一部分
    - 用户不存在进行响应 ProcessStatusEnum.USER_NOT_EXISTS

    <img src="/Users/jeffrey/Documents/JeffreyNoteRemoteData/images/image-20230222201732189.png" alt="image-20230222201732189" style="zoom:50%;" />

- 编写用户填写完验证码后发送重置邮件的链接逻辑

  - 

- 

<img src="/Users/jeffrey/Documents/JeffreyNoteRemoteData/images/image-20230222201503810.png" alt="image-20230222201503810" style="zoom:50%;" />



