### 开始接入

- API documentation provided by Jeffrey
  - Email: Jeffrey2971@outlook.com
  - WeChat: Jeffrey0203-
  - QQ: 664490254


#### ① 接入地址

- 图片去水印 API 请求地址
  - `http://120.76.100.173:8888/single-upload` 
  - `http://jeffrey2971.top:8888/single-upload`
  - `http://www.jeffrey2971.top:8888/single-upload`

#### ② 请求方式

- GET / POST
  - GET 请求将会跳转到上传页
  - POST 请求作为 API 调用方式

### 请求参数

#### ① 注意

- **null**

#### ② 头部信息（Header）

| 协议（参数名称）  | 参数类型 | 备注 |
| ----------------- | -------- | ---- |
| X-JEFFREY-API-KEY | String   | 密钥 |

#### ③ 请求参数

- 提示
  - 对于上传的文件优先级为： image_file > image_url > image_base64
  - 对于去除的水印优先级为：rectangles > markName > excludeKeywords > default




| 字段名称        | 类型    | 是否必填                                               | 默认  | 备注                                                         | 状态 |
| --------------- | ------- | ------------------------------------------------------ | ----- | ------------------------------------------------------------ | ---- |
| imageFile       | File    | 否，image_file / image_url / image_base64 必须提供一个 | 无    | 图片文件，仅支持格式为 `image/png` `image/bmp` `image/jpeg` 的图片，且大小不超过 4MB | 完成 |
| imageUrl        | String  | 否，image_file / image_url / image_base64 必须提供一个 | 无    | 图片链接，仅支持格式为 `image/png` `image/bmp` `image/jpeg` 的图片，且大小不超过 4MB | 完成 |
| imageBase64     | String  | 否，image_file / image_url / image_base64 必须提供一个 | 无    | 图片通过 base64 的方式进行编码后得到的字符串，仅支持格式为 `image/png` `image/bmp` `image/jpeg` 的图片，且大小不超过 4MB，不建议采用 Base64 编码 | 完成 |
| rectangles      | Json    | 否                                                     | 无    | 矩阵坐标，可为空，为空默认将识别到图中的文字信息作为矩阵值，如提供该参数，请正确填写，如提供有误则请求不成功，可提供多个矩阵值，例如：<br />[<br/>      {<br/>        "x": 120,<br/>        "y": 76,<br/>        "w": 100,<br/>        "h": 173<br/>      },<br/>      {<br/>        "x": 114,<br/>        "y": 80,<br/>        "w": 132,<br/>        "h": 206<br/>      }<br/>    ] | 完成 |
| markNames       | Json    | 否                                                     | 无    | 去除图中水印的名称，可提供多个，例如：{<br/>  "markNames": [<br/>    "名称A",<br/>    "名称B"<br/>  ]<br/>} |      |
| excludeKeywords | Json    | 否                                                     | 无    | 排除图片中的水印文字，用于在不指定去除水印名称时使用，可提供多个，例如：<br />{<br/>  "keywords": [<br/>    "路漫漫其修远兮",<br/>    "吾将上下而求索"<br/>  ]<br/>} |      |
| offset          | Json    | 否                                                     | 无    | 矩阵偏移量，在识别到的文本矩阵框额外缩小或放大矩阵大小，仅能使用 `+x + y +w +h -x -y -w -h` 指定缩小还是放大偏移量，如明确设定了 rectangles 则该参数不生效，例如：{<br/>  "+x": 114,<br/>  "-y": 80,<br/>  "-w": 132,<br/>  "+h": 206<br/>} | 完成 |
| sync            | Boolean | 否                                                     | false | 是否异步处理，如设定为 true 则表示启用，请先设置好 callback 回掉接口 |      |
| callback        | String  | sync 参数为 true 时必须                                | 无    | 如 sync 显示的设定为了 true，将立即响应状态信息，处理包含数据的结果将发送给指定的 callback 回掉接口，如首次请求 callback 失败，服务器会重新尝试响应 1 次，请注意，即使没有成功发送消息至 callback 指定的服务器，也依然会扣除使用次数，处理失败情况除外，**所以在提交大量异步请求前，请先确认 callback 接口是否能正常接收响应** |      |

### 响应码列表

- 响应码分为 HTTP 响应吗以及处理响应吗，HTTP 响应码更偏向于本次是否能处理，处理响应码更屁更偏向于是否已处理

#### ① HTTP 响应码列表

| 响应码 | 含义                                                         | 备注                                       |
| ------ | ------------------------------------------------------------ | ------------------------------------------ |
| 200    | 请求成功，但处理不一定成功，具体看 data 对象中的 processStatus | 处理成功                                   |
| 400    | 请求参数错误                                                 | 检查是否少传了必要参数，或指定了错误的参数 |
| 401    | 未授权，或授权失败                                           | 检查提供的账户标识                         |
| 405    | 请求方法不被允许                                             | 检查请求方法                               |
| 413    | 上传文件大小超出限制                                         | 请检查文件大小，最大 4 MB                  |
| 429    | 请求频率超出 QPS 限制（默认 QPS 为 2）                       | 请放缓请求速率                             |
| 500    | 服务端系统出错                                               | 重试或联系管理员                           |

#### ② 处理响应码列表

| 响应码 | 含义               | 备注                                                         |
| ------ | ------------------ | ------------------------------------------------------------ |
| 0      | 处理成功           | -                                                            |
| 52000  | 异步调用返回状态码 | 异步调用的请求将立即响应状态信息，处理包含数据的结果将发送给指定的 callback 回掉接口 |
| 52001  | 未处理             | 没有进行图片处理，原因可能是没有通过校验，例如上传的文件超出大小限制、非法请求、提供的参数有误等等，该响应吗不会扣除账户使用次数 |
| 52002  | 请求超时           | 重试或联系管理员，不消耗使用次数                             |
| 52003  | 服务端系统出错     | 重试或联系管理员，不消耗使用次数                             |

### 响应数据列表

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
| processLocation | 本次处理的矩阵坐标，为一个 json list，list 中的每一项依次代表着 left、top、width、height，而 word 为四个坐标轴框选的文字 |      |
| processBase64   | 本次去水印后图片的 Base64 编码数据                           |      |
| processUrl      | 本次去水印后图片的 URL 地址，URL 在服务端仅保存 5 分钟，超时将自动销毁，请及时下载获取 |      |



### 响应数据例子

#### ① 成功

- 以下为调用成功响应的数据例子

```json
{
  "httpCode": 200,
  "httpMsg": "SUCCESS::请求成功",
  "returnType": "json",
  "location": "0:0:0:0:0:0:0:1",
  "msgInnerReferer": "",
  "data": {
    "processStatus": 0,
    "processMsg": "SUCCESS::处理成功",
    "processLocation": [
      {
        "x": 120,
        "y": 76,
        "w": 100,
        "h": 173,
        "word": "logo"
      },
      {
        "x": 114,
        "y": 80,
        "w": 132,
        "h": 206,
        "word": "水印"
      }
    ],
    "processBase64": "data:image/png;base64,... ..."
  }
}
```

#### ② 异步

- 以下为异步调用返回的数据例子

  ```json
  {
    "httpCode": 200,
    "httpMsg": "SUCCESS::请求成功",
    "returnType": "json",
    "location": "0:0:0:0:0:0:0:1",
    "msgInnerReferer": "",
    "data": {
      "processStatus": 0,
      "processMsg": "WAITING::异步请求",
      "processLocation": [
        {
          "x": 120,
          "y": 76,
          "w": 100,
          "h": 173,
          "word": "logo"
        },
        {
          "x": 114,
          "y": 80,
          "w": 132,
          "h": 206,
          "word": "水印"
        }
      ],
      "base64": "data:image/png;base64,... ..."
    }
  }
  ```

  

#### ③ 失败

- 以下为调用失败返回的数据例子

```json
{
  "httpCode": 401,
  "httpMsg": "FAILED::用户未授权",
  "returnType": "json",
  "location": "0:0:0:0:0:0:0:1",
  "msgInnerReferer": "ExceptionHandler[AuthorizationException.class]",
  "data": {
  	  
  }
}
```

