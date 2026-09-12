一款简单但功能强大的 Android 应用，允许你创建可放置在主屏幕上的快捷方式和小部件。每个快捷方式在被点击时都会触发一次 HTTP 请求，并可以多种方式处理和显示响应。非常适合智能家居自动化项目。

<a href="https://play.google.com/store/apps/details?id=ch.rmy.android.http_shortcuts"><img alt="Get it on Google Play" src="../assets/play_store.svg" height="80" style="margin-right: 20px"></a><a href="https://f-droid.org/en/packages/ch.rmy.android.http_shortcuts/"><img alt="Get it on F-Droid" src="../assets/f_droid.svg" height="80" style="margin-right: 20px"></a><a href="https://github.com/Waboodoo/HTTP-Shortcuts/releases"><img alt="Get it on GitHub" src="../assets/github.svg" height="80" style="margin-right: 20px"></a><a href="https://http-request-shortcuts.updatestar.com/"><img alt="&quot;HTTP Request Shortcuts is an outstanding product and was given the 'Excellent' award by its users.&quot; - Michael Ganss, UpdateStar.com" src="../assets/documentation/updatestar_rating.png" height="80"></a>

本应用完全免费且开源，可在 [GitHub](https://github.com/Waboodoo/HTTP-Shortcuts) 上找到。
如果你发现了 bug、有问题或功能请求，欢迎随时[联系我](https://http-shortcuts.rmy.ch/contact)。
也可以查看[官方 subreddit](https://www.reddit.com/r/HTTP_Shortcuts/) 进行交流、讨论和获取最新消息。

如果你喜欢这个应用，请考虑[支持我的工作](https://http-shortcuts.rmy.ch/support-me)。谢谢。

<iframe src="https://github.com/sponsors/Waboodoo/button" title="Sponsor Waboodoo" height="32" width="114" style="border: 0; border-radius: 6px;"></iframe>

## 功能特性
- 100% 免费
- 开源
- 无广告
- 不耍流氓（我尽量）
- [(几乎) 无追踪](privacy-policy.md)
- 运行于 Android 8 (Oreo) 或更高版本。旧版本请见下文。

### 技术
- 支持 HTTP 和 HTTPS (TLSv1.3)，支持 GET、POST、PUT、DELETE、PATCH、HEAD、OPTIONS 和 TRACE 方法
- Basic 认证、Digest 认证、Bearer 认证和客户端证书认证
- 自定义请求头
- 自定义请求体（基于文本、来自静态文件、文件选择器、静态内容或相机）
- 将文件作为表单数据参数上传
- 支持自签名证书和证书固定
- 支持从 cURL 命令导入和导出到 cURL 命令

### 自定义
- 将响应显示为吐司提示、全屏窗口、对话框窗口、通知，或静默运行
- 将响应存储到文件
- 大量彩色内置图标，支持自定义图标
- 通过[局部和全局变量](variables.md)进行值注入，支持动态确定的值（如文本、数字、密码、选择、颜色、日期等）
- 在执行前和执行后运行任意 [JavaScript](scripting.md)，可通过以下功能实现大量自定义：
  - 计算时间戳、随机数、UUID、哈希、HMAC、Base64 等值
  - 解析 JSON、HTML 或 XML 并从中提取数据
  - 显示吐司消息或消息对话框
  - 振动或播放提示音
  - 读写文件
  - 触发其他快捷方式以串联多个 HTTP 请求
  - 通过发送 TCP 或 UDP 数据包、MQTT 消息或使用网络唤醒，与其他设备和服务交互

### 便捷功能
- 支持深色模式
- 通过分类到不同的[标签页和分组到分区](categories.md)来保持一切井然有序
- 以 ZIP 文件格式导入和导出所有数据
  - 可选密码保护
  - 手动或自动导出到本地文件或 Web 服务器
- 使用 [Web 编辑器](https://http-shortcuts.rmy.ch/editor) 从浏览器配置应用

### 更多
- 在主屏幕的小部件中显示变量值
- 与第三方应用集成，例如：
  - [Tasker](advanced.md#integrate-with-tasker)
  - [MacroDroid](advanced.md#integrate-with-macrodroid)
  - [Termux](scripting.md#run-termux-command)
  - [Wireguard](scripting.md#set-wireguard-tunnel-state)
  - [二维码扫描器](scripting.md#scan-barcode)
- 支持非 HTTP 用例：
  - [浏览器快捷方式](shortcuts.md#browser-shortcut) 允许在浏览器或自定义标签页中打开 URL
  - [MQTT 快捷方式](shortcuts.md#mqtt-shortcut) 允许发送 MQTT 消息
  - [网络唤醒快捷方式](shortcuts.md#wake-on-lan) 允许唤醒网络上的设备
  - [脚本快捷方式](shortcuts.md#scripting-shortcut) 允许使用 JavaScript 代码和大量内置函数定义自定义逻辑

## 截图
![主屏幕](../assets/screenshots/01.png)
![创建菜单](../assets/screenshots/02.png)
![快捷方式编辑器](../assets/screenshots/03.png)
![内置图标](../assets/screenshots/04.png)
![认证设置](../assets/screenshots/05.png)
![脚本编写](../assets/screenshots/06.png)
![代码片段选择器](../assets/screenshots/07.png)
![分类](../assets/screenshots/08.png)
![变量](../assets/screenshots/09.png)
![变量编辑器](../assets/screenshots/10.png)
![设置](../assets/screenshots/11.png)
![导入 / 导出](../assets/screenshots/12.png)
![显示带元数据的响应](../assets/screenshots/13.png)

## 对旧版 Android 的支持
- 对于 Android 6 或 7，下载并[安装 3.21.0 版本的 APK](https://github.com/Waboodoo/HTTP-Shortcuts/releases/tag/v3.21.0)。
- 对于 Android 5，下载并[安装 3.14.0 版本的 APK](https://github.com/Waboodoo/HTTP-Shortcuts/releases/tag/v3.14.0)。
- 对于 Android 4.0 - Android 4.4.4，下载并[安装 2.9.0 版本的 APK](https://github.com/Waboodoo/HTTP-Shortcuts/releases/tag/v2.9.0)。