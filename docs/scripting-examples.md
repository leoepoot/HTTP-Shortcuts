<a id="examples"></a>
# 脚本编写示例

本页列出了代码执行功能的一些典型用例示例。

### 生成请求中使用的值

有时你需要生成一个值，例如当前日期或随机数，以便在请求中使用。你可以通过生成该值，然后将其存储到一个变量中，再在请求中使用该变量来实现。下面是一个示例：

```js
const randomNumber = Math.floor(Math.random() * 10);
setVariable('myNumber', randomNumber);

const currentDate = (new Date()).toDateString();
setVariable('today', currentDate);
```

### 解析 JSON 响应

下面是一个示例，展示如何解析快捷方式的响应，并显示结果或将其存储到变量中以供日后使用。

```js
const temperature = JSON.parse(response.body).temperature;
showDialog(`当前温度是 ${temperature}°C`, '温度');

setVariable('temperature', temperature);
```

### 根据响应更改图标和标签

此示例展示了如何根据收到的响应更改快捷方式的图标和标签。该示例假设如果请求成功，服务器会返回 'OK'。

```js
if (response.body == 'OK') {
  renameShortcut('', 'Success');
  changeIcon('', 'freepik_check'); // 将当前快捷方式的图标更改为绿色对勾
} else {
  renameShortcut('', 'Failure');
  changeIcon('', 'freepik_close'); // 将当前快捷方式的图标更改为红色叉号
}
```

### 外出或在家时使用不同的地址

此示例展示了如何设置快捷方式，根据你当前是否连接到家庭 Wi-Fi 网络来使用不同的地址。

该示例假设你已设置一个名为 "base_url" 的静态全局变量，该变量用于快捷方式的 URL 字段，并且你的家庭 Wi-Fi 名为 "Home Sweet Home"。

然后可以在脚本编写屏幕的"执行前运行"块中使用以下代码片段：

```js
const baseUrlHome = "http://192.168.1.123";
const baseUrlAway = "https://my-service.example.com";
const old = getVariable("base_url");
if (getWifiSSID() == "Home Sweet Home") {
  if (old !== baseUrlHome) {
    setVariable("base_url", baseUrlHome);
  }
} else {
  if (old !== baseUrlAway) {
    setVariable("base_url", baseUrlAway);
  }
}
```

### 执行快捷方式前要求确认

此示例展示了如何在快捷方式执行前显示自定义确认消息，并仅在用户点击"确定"确认时才执行。

```js
if (!confirm('我要做这件事吗？')) {
  showToast('不做这件事。');
  abort();
}
```

或者，如果你在家庭网络中，可能希望绕过确认步骤：

```js
if (getWifiSSID() != 'My Home Network') {
  if (!confirm('我要做这件事吗？')) {
    showToast('不做这件事。');
    abort();
  }
}
```

### 大声朗读响应

此示例展示了如何让收到的响应被大声朗读（使用文本转语音）。该示例假设响应是纯文本（即不是 HTML、JSON 等）。

```js
speak(response.body);
```
<a id="split-color"></a>
### 显示颜色选择器并将结果拆分为 R、G、B

此示例展示了如何打开颜色选择器对话框，然后将所选颜色分离为红、绿、蓝分量，并将它们转换为 0 到 255 的数字。它假设你已创建变量 "red"、"green" 和 "blue"，结果存储在这些变量中，以便你可以在快捷方式中使用它们，例如在 URL 中的查询参数里。

```js
const myColor = promptColor();
if (!myColor) {
  abort();
}
const red = parseInt(myColor.substring(0, 2), 16);
const green = parseInt(myColor.substring(2, 4), 16);
const blue = parseInt(myColor.substring(4, 6), 16);
setVariable("red", red);
setVariable("green", green);
setVariable("blue", blue);
```