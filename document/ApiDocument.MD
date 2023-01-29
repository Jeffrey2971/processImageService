###  一、开始接入

- API documentation provided by Jeffrey
  - **Email:** Jeffrey2971@outlook.com
  - **WeChat:** Jeffrey0203-
  - **QQ:** 664490254


#### ① 接入地址

- 图片去水印 API 请求地址
  - https://www.processimage.cn/access


#### ② 请求方式

- GET / POST
  - GET 请求将会跳转到上传测试页
  - POST 请求作为 API 调用方式

#### ③ 接入事项

- 关于发起请求前的提醒事项：在发起请求前，请主动在客户端做好上传参数检查，例如签名信息、图片大小等，虽然服务端也会进行校验，但这无疑更加耗时且浪费服务器资源
- 关于提交异步请求缓存的提醒事项：服务缓存逻辑为当图片成功处理完成后对图片进行缓存处理，在缓存期内再次提交相同的请求将直接响应缓存的数据，不扣除账户使用次数，但同一账户在同一时间段内大量提交异步请求可能导致仍然扣除一部分少量的使用次数，因为所有的任务请求使用队列排队处理，当首个任务未处理完毕时服务器无法存储缓存的结果

### 二、请求参数

#### ① 注意事项

- 推荐使用 SDK 的基础上进行开发，位于底部

- 请求方式请使用 `multipart/form-data` 格式
- 对于上传的文件优先级为： `image_file > image_url > image_base64`
- 对于去除的水印优先级为：`rectangles > markName > excludeKeywords > default`

#### ② 头部信息

| 协议（参数名称）   | 是否必填 | 参数类型 | 备注                                                         |
| ------------------ | -------- | -------- | ------------------------------------------------------------ |
| X-JEFFREY-API-SIGN | 是       | String   | 1. 签名规则为 **md5(publicKey + md5(image) + salt + privateSecret)** <br />2. md5(image) 中的 image 是原始图片数据，不加任何编码，32位小写<br />3. salt 为随机数，由 8 位随机 int 类型的数字组合而成，注意生成随机数的第一个字符不要为 0 ，否则造成签名失败，例如 `01234567`<br />3. publicKey 为你的公钥，由数字、大小写字母随机组合而成<br />4. privateSecret 为你的密钥，由 128 位数字、大小写字母、特殊字符组合而成，**确保不在请求参数中携带该参数，仅在生成签名时使用**<br />5. 最终的 sign 为 32 位小写字符串 |

#### ③ 请求参数


| 字段名称        | 类型    | 是否必填                                               | 默认  | 备注                                                         | 状态                     |
| --------------- | ------- | ------------------------------------------------------ | ----- | ------------------------------------------------------------ | ------------------------ |
| imageFile       | File    | 否，image_file / image_url / image_base64 必须提供一个 | 无    | 图片文件，仅支持格式为 `image/png` `image/bmp` `image/jpeg` 的图片，且大小不超过 4MB | 可用                     |
| imageUrl        | String  | 否，image_file / image_url / image_base64 必须提供一个 | 无    | 图片链接，仅支持格式为 `image/png` `image/bmp` `image/jpeg` 的图片，且大小不超过 4MB | 因签名方式不同，暂不可用 |
| imageBase64     | String  | 否，image_file / image_url / image_base64 必须提供一个 | 无    | 图片通过 base64 的方式进行编码后得到的字符串，仅支持格式为 `image/png` `image/bmp` `image/jpeg` 的图片，且大小不超过 4MB，不建议采用 Base64 编码 | 因签名方式不同，暂不可用 |
| rectangles      | Json    | 否                                                     | 无    | 矩形坐标，为空默认将识别到图中的文字水印信息作为矩形坐标，如提供该参数，请正确填写，如提供有误则请求不成功，明确指定该参数后 `markNames / excludeKeywords / offset` 将无效且服务器不会识别出图片中的文字矩形区域，可提供多个矩形坐标，例如：<br />[<br/>      {<br/>        "x": 120,<br/>        "y": 76,<br/>        "w": 100,<br/>        "h": 173<br/>      },<br/>      {<br/>        "x": 114,<br/>        "y": 80,<br/>        "w": 132,<br/>        "h": 206<br/>      }<br/>    ] | 可用                     |
| markNames       | Json    | 否                                                     | 无    | 根据给定的水印名称去除识别到的图中的文字水印名称，该参数仅在不明确指定矩形坐标参数时生效，且该参数和 excludeKeywords 只能指定其中一个生效，参数可提供多个，例如：{<br/>  "markNames": [<br/>    "名称A",<br/>    "名称B"<br/>  ]<br/>} |                          |
| excludeKeywords | Json    | 否                                                     | 无    | 排除图片中的水印文字，该功能仅在不明确指定矩形坐标参数时生效，且该参数和 markNames 只能指定其中一个，参数可提供多个，例如：<br />{<br/>  "keywords": [<br/>    "小红书",<br/>    "id：10086"<br/>  ]<br/>} |                          |
| offset          | Json    | 否                                                     | 无    | 矩阵偏移量，在识别到的文本矩阵框**额外**缩小或放大矩阵大小，仅能使用 `+x + y +w +h -x -y -w -h` 指定缩小还是放大偏移量，如识别出的文字矩形区域额外添加偏移量后造成矩形超出图像框架范围，则忽略 offset 参数重试并响应。例子：{<br/>  "+x": 5,<br/>  "-y": 8,<br/>  "-w": 3,<br/>  "+h": 4<br/>} | 完成                     |
| sync            | Boolean | 否                                                     | false | 是否异步处理，如设定为 true 则表示启用，请先设置好 callback 回掉接口 | 完成                     |
| callback        | String  | sync 参数为 true 时必须                                | 无    | 如 sync 显示的设定为了 true，将立即响应状态信息，处理包含数据的结果将发送给指定的 callback 回掉接口，如首次请求 callback 失败，服务器会重新尝试响应 1 次，请注意，即使没有成功发送消息至 callback 指定的服务器，也依然会扣除使用次数，处理失败情况除外，服务器校验响应是否成功取决于目标回掉接口是否返回 HTTP 状态码 200，**所以在提交大量异步请求前，请先确认 callback 接口是否能正常接收响应** | 完成                     |
| ocrOnly         | Boolean | 否                                                     | false | 是否仅识别图片中的文字水印矩形坐标等信息，默认为 false ，详情参考响应数据例子 | 完成                     |
| show            | Boolean | 否                                                     | false | 是否标识出处理的矩形区域，默认为 false                       | 完成                     |

### 三、响应码列表

- 响应码分为 HTTP 响应吗以及处理响应吗，HTTP 响应码更偏向于本次是否能处理，处理响应码更屁更偏向于是否已处理

#### ① HTTP 响应码列表

| 响应码 | 含义                                                         | 备注                                                         |
| ------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 200    | 请求成功，但处理不一定成功，具体看 data 对象中的 processStatus | 处理成功                                                     |
| 304    | 自上次的请求以来本次响应未做任何修改                         | 自上一次请求后，响应处于缓存期中，本次响应为缓存数据，在缓存期间不扣除使用次数 |
| 400    | 请求参数错误                                                 | 检查是否少传了必要参数，或指定了错误的参数                   |
| 401    | 认证失败                                                     | 检查提供的签名参数，以及生成 sign 的方法                     |
| 403    | 拒绝请求                                                     | 服务器拒绝请求，可能是账户可使用次数不足                     |
| 405    | 请求方法不被允许                                             | 检查请求方法                                                 |
| 413    | 上传文件大小超出限制                                         | 请检查文件大小，最大 4 MB                                    |
| 429    | 请求频率超出 QPS 限制（默认 QPS 为 2）                       | 请放缓请求速率                                               |
| 500    | 服务端系统出错                                               | 重试或联系管理员                                             |

#### ② 处理响应码列表

| 响应码 | 含义                   | 备注                                                         |
| ------ | ---------------------- | ------------------------------------------------------------ |
| 0      | 处理成功               | -                                                            |
| 52000  | 异步调用返回状态码     | 异步调用的请求将立即响应状态信息，处理包含数据的结果将发送给指定的 callback 回掉接口 |
| 52001  | 未处理                 | 没有进行图片处理，原因可能是没有通过校验，例如上传的文件超出大小限制、非法请求、提供的参数有误等等，该响应码不会扣除账户使用次数 |
| 52002  | 未处理                 | 由于没有明确或正确指定水印矩形坐标，且服务器无法识别到图片中有任何的文字水印矩形坐标将返回该状态码，不消耗使用次数 |
| 52003  | 请求超时               | 重试或联系管理员，不消耗使用次数                             |
| 52004  | 服务端系统出错         | 重试或联系管理员，不消耗使用次数                             |
| 52005  | 提供的偏移量参数不正确 | 出现该状态码表示服务器依然完成了对图片的去水印操作，但提供的偏移量参数未能生效，原因是对原始图片的坐标轴信息进行坐标偏移操作后，超出了原始图片的边框限制。消耗使用次数 |
| 52006  | 未进行水印去除操作     | 由于指定了参数 ocrOnly 为 true，所以服务器仅识别图片中的文字水印矩形坐标等信息，不对识别到的区域进行水印去除操作，仍扣除使用次数 |
|        |                        |                                                              |

### 四、响应数据列表

| 字段            | 含义                                                         | 备注 |
| --------------- | ------------------------------------------------------------ | ---- |
| httpCode        | 本次请求状态，具体参照响应码列表                             |      |
| httpMsg         | 本次请求状态信息                                             |      |
| returnType      | 本次响应返回的数据类型                                       |      |
| remainingUsage  | 当前用户剩余可使用次数                                       |      |
| location        | 客户端请求发起 IP 地址                                       |      |
| msgInnerReferer | 内部消息来源，成功时为空，失败时可根据该信息进行定位         |      |
| data            | 为一个对象，封装了本次请求的具体响应数据，包括去水印后的数据。在处理异步请求时返回的 data 对象不包含处理数据 |      |
| processStatus   | 本次处理状态码，具体参照响应码列表                           |      |
| processMsg      | 本次处理状态信息                                             |      |
| processLocation | 本次处理的矩阵坐标，为一个 json list，list 中的每一项依次代表着 left、top、width、height，lineCount 为自动合并了多少行，watermark 为四个坐标轴框选的文字 |      |
| processCount    | 对本次请求上传图片的处理次数                                 |      |
| url             | 本次去水印后图片的 URL 地址，URL 在服务端仅保存 5 分钟，超时将自动销毁，请及时下载获取，注意：重复请求接口即使图片在缓存期内依然扣除使用次数，所以请使用缓存的 URL |      |



### 五、响应数据例子

#### ① 同步调用成功

- 以下为调用成功响应的数据例子

```json
{
  "httpCode": 200,
  "httpMsg": "SUCCESS::请求成功",
  "returnType": "json",
  "remainingUsage": 99,
  "msgInnerReferer": "",
  "location": "0:0:0:0:0:0:0:1",
  "data": {
    "processStatus": 0,
    "processCount": 3,
    "processMsg": "SUCCESS::调用成功",
    "processLocation": [
      {
        "x": 350,
        "y": 671,
        "w": 376,
        "h": 58,
        "lineCount": 2,
        "watermark": "深圳xxxx有限公司 公司编号: xxxxxxxxxxxxx "
      },
      {
        "x": 114,
        "y": 80,
        "w": 132,
        "h": 206,
        "lineCount": 2,
        "watermark": "商铺名称：xxxxx   商铺 id：xxxxx"
      },
      {
        "x": 341,
        "y": 265,
        "w": 53,
        "h": 21,
        "lineCount": 1,
        "watermark": "水印名称"
      }
    ],
    "url": "http://www.processimage.cn/targetImages/1669620124079.jpeg"
  }
}
```

#### ② 异步调用成功

- 以下为异步调用成功的响应，不包含图片是否处理成功

  ```json
  {
    "httpCode": 200,
    "httpMsg": "SUCCESS::请求成功",
    "returnType": "json",
    "remainingUsage": 99,
    "msgInnerReferer": "",
    "location": "0:0:0:0:0:0:0:1",
    "data": {
      "processStatus": 52000,
      "processMsg": "WAITING::异步请求"
    }
  }
  ```
  

#### ③ 仅识别文字水印矩阵（不处理水印）

- 以下为仅识别文字水印矩阵信息，不对水印做去除处理

  ```json
  {
    "httpCode": 200,
    "httpMsg": "SUCCESS::请求成功",
    "returnType": "json",
    "remainingUsage": 99,
    "msgInnerReferer": "",
    "location": "0:0:0:0:0:0:0:1",
    "data": {
      "processStatus": 0,
      "processCount": 3,
      "processMsg": "SUCCESS::调用成功",
      "processLocation": [
        {
          "x": 350,
          "y": 671,
          "w": 376,
          "h": 58,
          "lineCount": 2,
          "watermark": "深圳xxxx有限公司 公司编号: xxxxxxxxxxxxx "
        },
        {
          "x": 114,
          "y": 80,
          "w": 132,
          "h": 206,
          "lineCount": 2,
          "watermark": "商铺名称：xxxxx   商铺 id：xxxxx"
        },
        {
          "x": 341,
          "y": 265,
          "w": 53,
          "h": 21,
          "lineCount": 1,
          "watermark": "水印名称"
        }
      ]
    }
  }
  ```

#### ④ 缓存
- 以下为调用缓存结果返回数据的例子

  ```json
  {
    "httpCode": 304,
    "httpMsg": "本次响应为缓存数据，未作任何修改",
    "returnType": "json",
    "remainingUsage": 720,
    "allUsedCount": 2195,
    "data": {
      "processStatus": 0,
      "processCount": 3,
      "processMsg": "SUCCESS::调用成功",
      "processLocation": [
        {
          "x": 614,
          "y": 555,
          "w": 56,
          "h": 22,
          "lineCount": 1,
          "word": "肥场安全原则 "
        },
        {
          "x": 925,
          "y": 1309,
          "w": 90,
          "h": 36,
          "lineCount": 1,
          "word": "小红书 "
        },
        {
          "x": 732,
          "y": 1372,
          "w": 305,
          "h": 33,
          "lineCount": 1,
          "word": "小红书ID:602****66 "
        }
      ],
      "url": "http://www.processimage.cn/targetImages/1670915997800.jpeg"
    },
    "encryptedInfo": {
      "id": 1,
      "sign": "9a13b7487fb851ebbeca5cfa62ff2817",
      "imageUniqueIdentification": "87da658a9a065f205053d59386ce5dc2"
    }
  }
  
  ```

#### ⑤ 失败

- 以下为调用失败返回的数据例子

```json
{
  "httpCode": 401,
  "httpMsg": "FAILED::非法签名",
  "returnType": "json",
  "msgInnerReferer": "ExceptionHandler[SignatureFailedException.class]",
  "location": "0:0:0:0:0:0:0:1",
  "data": {
    "processStatus": 52001,
    "processMsg": "UNHANDLED::因其他问题导致未处理"
  }
}
```

### 六、耗时测试

#### ① 请求去水印耗时测试

| 图片 / 张 | 大小 / 张 | 耗时 / 秒 |
| --------- | --------- | --------- |
|           |           |           |
|           |           |           |
|           |           |           |

#### ② 请求去水印缓存耗时测试

| 图片 / 张 | 大小 / 张 | 耗时 / 秒 |
| --------- | --------- | --------- |
|           |           |           |
|           |           |           |
|           |           |           |

### 七、SDK

- [JavaSDK](https://www.processimage.cn/SDK/Java/java-sdk-maven.zip)

- [PythonSDK](https://www.processimage.cn/SDK/Python/PythonSDK.html)