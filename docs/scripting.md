<a id="scripting"></a>
# 脚本编写

执行快捷方式时，可以在执行前或执行后运行 JavaScript 代码片段。你可以在快捷方式编辑器的*"脚本编写"*部分找到相应的设置。

![快捷方式编辑器中的脚本编写选项](../assets/documentation/scripting/01.png)
![脚本编辑器](../assets/documentation/scripting/02.png)

此外，你会在应用设置中找到一个选项（标记为"全局脚本"），允许你在每个快捷方式执行前运行代码，这可以用来定义共享函数。

大多数 [JavaScript 的内置功能](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference) 都可以正常使用，例如操作 [字符串](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String) 或 [数组](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array)。此外，应用内置了一些方便的函数和工具，所有这些都在下面有文档说明。

使用这些代码块，你可以实现许多功能。有关灵感和一些常见用例，请参阅[示例页面](scripting-examples.md)。

**专业提示：** 在手机上写代码很繁琐。如果你打算写较长的代码块，我建议你查看 [Web 编辑器](https://http-shortcuts.rmy.ch/editor/) 以便更方便地配置你的快捷方式。

<a id="handle-response"></a>
## 处理响应

你可以通过 `response` 对象访问 HTTP 请求的响应。

> 请注意，`response` 对象仅在*"成功时运行"*代码块中可用，以及在有 HTTP 响应（即 4xx 或 5xx 状态码）的情况下在*"失败时运行"*代码块中可用，否则为 `null`。

### 获取响应体

响应体可以通过 `response.body` 作为字符串使用。

```js
const myBody = response.body;
```

如果你知道响应体是 JSON 格式的，可以使用 `JSON.parse()` 将其转换为 JS 对象、数组或原始类型。

```js
const myJson = JSON.parse(response.body);
```

<a id="response-headers"></a>
### 获取响应头

响应头可以通过 `response.headers` 作为对象（键值对）使用。每个键对应头的名称，其值是具有该名称的所有头的字符串数组。

还有一个便捷函数 `response.getHeader(name)`，可用于通过名称获取特定头的值。如果有多个同名头，则返回最后一个；如果没有，则返回 `null`。

```js
const contentType = response.headers['Content-Type'][0];

const contentLength = response.getHeader('Content-Length');
```

<a id="response-status"></a>
### 获取状态码

响应的 HTTP 状态码可以通过 `response.statusCode` 作为整数使用。

```js
const isNotFound = response.statusCode == 404;
```

<a id="response-cookies"></a>
### 获取 Cookie

响应的 Cookie 可以通过 `response.cookies` 作为对象（键值对）使用。每个键对应 Cookie 的名称，其值是具有该名称的所有 Cookie 的字符串数组。

还有一个便捷函数 `response.getCookie(name)`，可用于通过名称获取特定 Cookie 的值。如果有多个同名 Cookie，则返回最后一个；如果没有，则返回 `null`。

```js
const myCookieValue = response.cookies['MyCookieName'][0];

const myCookieValue2 = response.getCookie('MyCookieName2');
```

如果你需要关于 Cookie 的更多详细信息（例如其过期时间戳），可以使用 `response.headers['Set-Cookie']` 直接读取 Cookie 头。

<a id="response-errors"></a>
### 错误

请注意，如果没有来自服务器的响应，即网络错误的情况下，`response` 对象将为 `null`。在这种情况下，你可以检查 `networkError` 以获取描述错误的字符串。

<a id="variables"></a>
## 读写变量

在应用中，你可以创建[局部和全局变量](variables.md)以在快捷方式中使用，例如为 URL、查询参数或请求体的一部分动态插入值。你可以通过应用主屏幕菜单中的"全局变量"选项找到全局变量编辑器。

本节介绍如何从脚本中与这些变量交互。

<a id="get-variable"></a>
### getVariable

你可以通过 `getVariable()` 函数访问任何局部或全局变量的值。只需将变量的名称或 ID 作为第一个参数传入。

```js
const myValue = getVariable('myVariable');
```

> 请注意，返回的值始终是字符串。如果变量不存在，则会引发错误。

<a id="set-variable"></a>
### setVariable

你可以通过 `setVariable()` 函数将值作为字符串存储到变量中。只需将变量的名称或 ID 作为第一个参数传入，将你要存储的值作为第二个参数传入。

对于全局变量，如果其类型支持，该值将用于当前快捷方式执行，并且也会被存储。对于局部变量，该值仅用于当前执行，因为局部变量不会被持久化。

```js
setVariable('myVariable', 'Hello World');
```

> 请注意，大小限制为 30'000 个字符。如果你设置的值大于此，它将在当前执行中原样使用，但存储的值将被截断。

作为可选的第三个参数，你可以传入一个布尔值。如果为 `true`，新值将被存储但不会立即用于当前执行。这对于支持"记住值"功能的全局变量类型（如日期输入、时间输入、文本输入等）很有用，因为它允许更改存储的上一个值，该值用作默认选中值。此参数对局部变量没有影响，因为它们不会被持久化。

```js
setVariable('myVariable', 'Hello World', true); // 只更改存储的值，但如果之后使用该变量，它仍会正常解析
```

<a id="shortcut-info"></a>
## 获取当前快捷方式的信息

你可以从 `shortcut` 对象中轻松检索当前快捷方式及其所属分类的信息。这包括快捷方式的 ID、名称、描述、图标、是否隐藏，以及其分类的 ID 和名称。

```js
shortcut.id;
shortcut.name;
shortcut.description;
shortcut.icon;
shortcut.canonicalIcon; // 与 icon 相同，但如果有颜色后缀则不包含
shortcut.hidden;
shortcut.category.id;
shortcut.category.name;
```


<a id="files"></a>
## 文件

### 已选文件

如果你的快捷方式使用了文件参数或将文件内容用作请求体，那么你可以使用 `selectedFiles` 数组访问这些文件的信息。每个已选文件都有一个条目，允许你读取其文件名、大小（字节）、媒体类型以及可能的一些额外元信息。


```js
const numberOfFiles = selectedFiles.length;

selectedFiles[0].name;
selectedFiles[0].size; // 裁剪/旋转之后
selectedFiles[0].type;
selectedFiles[0].meta;
```

每个文件还有一个唯一 ID，可用于在应用内部在快捷方式执行期间唯一标识文件。如果你想使用 [`enqueueShortcut`](#trigger-shortcut) 函数将这些文件转发给另一个快捷方式，或者想[从文件读取](#read-selected-files)，这很有用。

```js
selectedFiles[0].id;

const allFileIds = selectedFiles.map(file => file.id);
```

`meta` 字段目前仅提供有关图像的信息，否则为空对象。它允许读取图像的方向以及创建时间戳（"yyyy-MM-dd HH:mm:ss" 格式）：

```js
const myMeta = selectedFiles[0].meta;

/*
myMeta 现在可能是这样的：
{
  'created': '2022-12-31 23:59:59',
  'orientation': 1,
}
*/
```

`orientation` 字段是一个整数，含义如下：

- 0 表示没有方向信息
- 1 表示旋转 0 度，即无需调整
- 2 表示旋转 0 度，且图像已镜像
- 3 表示旋转 180 度
- 4 表示旋转 180 度，且图像已镜像
- 5 表示旋转 90 度
- 6 表示旋转 90 度，且图像已镜像
- 7 表示旋转 270 度
- 8 表示旋转 270 度，且图像已镜像


<a id="read-write-files"></a>
### 读写文件

如果你想读取现有文件或将数据写入文件，首先需要挂载包含该文件的目录。这可以通过["（已挂载的）目录"屏幕](directories.md)完成。挂载目录后，你可以使用 `getDirectory()` 函数获取其句柄。将挂载目录的名称作为第一个参数传入。然后，该句柄允许你使用 `readFile()`、`writeFile()` 和 `appendFile()` 函数读写文件。

对于 `readFile()`，将你想要读取的文件的名称或路径作为第一个参数传入，相对于挂载目录。该文件必须存在，否则会引发错误。作为可选的第二个参数，你可以传入用于读取文件的编码，默认为 UTF-8。文件的全部内容作为字符串返回。

```js
const dir = getDirectory('myMountedDirectory');
const fileContent = dir.readFile('someDir/someFile.txt');
```

对于 `writeFile()` 和 `appendFile()`，将你想要写入的文件的名称或路径作为第一个参数传入，相对于挂载目录。如果文件或其路径上的目录尚不存在，它会自动创建。

> 对于 `writeFile()`，如果文件已存在，其内容将被替换，不会有警告！对于 `appendFile()`，新内容将被追加，现有文件内容将被保留。

作为第二个参数，传入你想要写入文件的内容。如果它是数组、`Uint8Array` 或 `Int8Array`，它将被作为字节写入。否则，它将被作为字符串写入，使用 UTF-8 编码。

```js
const dir = getDirectory('myMountedDirectory');
dir.writeFile('someFile.txt', 'New file content');
dir.appendFile('someFile.txt', [72, 101, 108, 108, 111]);
```

<a id="read-selected-files"></a>
### 读取已选文件

`getDirectory` 函数还可用于读取已选择或已共享的文件的内容，即 `selectedFiles` 中列出的文件。无需传入挂载目录的名称或 ID，直接省略参数即可。然后你可以对返回的对象调用 `readFile()` 函数，将文件名或 ID 作为第一个参数。

```js
const dir = getDirectory();
const fileContent = dir.readFile(selectedFiles[0].id);
```

> 无法写入已选文件。

<a id="user-interaction"></a>
## 用户交互

本节介绍如何在快捷方式执行期间与用户（即你）交互，例如询问额外输入、确认操作或显示信息。

<a id="show-toast"></a>
### showToast

使用此函数，你可以在屏幕上显示吐司提示消息。只需将你的消息作为第一个参数传入。

```js
showToast('Hello World');
```

> 请注意，如果你传入的字符串为空，则不会显示吐司。还要注意，吐司最多显示两行文本，因此最适合短消息。

<a id="show-dialog"></a>
### showDialog

使用此函数，你可以在屏幕上显示对话框窗口。只需将你的消息作为第一个参数传入，可选地将对话框标题作为第二个参数传入。对话框将一直显示，直到按下其*"确定"*按钮。

```js
showDialog('My Message', 'My Title');

showDialog('You can also use <b>basic</b> <i>HTML</i> for formatting the message.');
```

> 请注意，如果你传入的字符串为空，则不会显示对话框。

作为可选的第三个参数，你可以传入一个带有额外选项的对象。目前，唯一支持的选项是 `buttons`，它是一个包含 1-2 个字符串的数组，用于自定义对话框上显示的按钮。

该函数返回一个对象，其中包含 `result` 字段，其值为 "ok"（如果未定义自定义按钮且点击了默认的确定按钮）、"button1"（如果点击了第一个自定义按钮）、"button2"（如果点击了第二个自定义按钮）或 "canceled"（如果对话框被取消）。

```js
const dialogResult = showDialog('My Message', 'My Title', {buttons: ['Yay', 'Oh no']});
if (dialogResult.result == 'button1') {
  alert("Yay!");
}
```

<a id="show-window"></a>
### showWindow

使用此函数，你可以在全屏窗口中显示任意文本，方法是传入一个带有屏幕配置的对象。

配置对象支持以下字段：

|参数|描述|类型 / 值|
|---|---|---|
|title|定义窗口的标题|string|
|text|窗口中显示的主要内容。必须非空|string|
|mimeType|`text` 的类型，用于确定如何渲染它。支持的值有 "text/plain"（默认）、"text/html"、"text/xml"、"text/yaml" 和 "application/json"|string|
|monospace|是否对文本使用等宽字体。默认为 `false`|boolean|
|fontSize|文本的字体大小。必须是 5 到 50 之间的值。|int|

```js
showWindow({
  title: 'This is a window',
  text: 'Hello world',
  fontSize: 18,
});
```

<a id="prompt-confirm"></a>
### prompt, confirm

与 JavaScript 在浏览器中的工作方式类似，你可以使用 `prompt()` 和 `confirm()` 作为工作流的一部分向用户询问输入。

`prompt()` 将打开一个对话框窗口，要求输入文本。输入的文本随后作为字符串返回。如果对话框被取消，则返回 `null`。将消息作为第一个参数传入，可选地传入第二个参数来预填文本输入字段。作为可选的第三个参数，你可以传入一个带有额外选项的对象。目前仅支持 `multiline` 选项，当设置为 `true` 时，会将文本输入字段更改为允许多行文本。

`confirm()` 将打开一个确认对话框。如果对话框被确认，则返回 true；如果被取消，则返回 false。将消息作为第一个参数传入。

```js
if (confirm('Are you sure?')) {
  // 仅在用户点击'确定'时执行某些操作
}
```

```js
const myName = prompt('What is your name?');

prompt("What's your story?", "I was born in...", { multiline: true });
```

<a id="prompt-number"></a>
### promptNumber

你可以使用 `promptNumber` 函数打开一个要求输入数字的输入对话框。输入的数字将是返回值，如果对话框被取消则为 `null`。如果输入的值不是有效数字，则返回 `NaN`。

另请参阅 [promptNumberSlider](#prompt-number-slider)。

将要在对话框上显示的文本作为第一个参数传入。这不能为空。作为可选的第二个参数，你可以传入一个默认值，用于预填输入字段。

```js
const myNumber = promptNumber('What is your favorite number?', 42);
```

<a id="prompt-number-slider"></a>
### promptNumberSlider

`promptNumberSlider` 函数打开一个对话框窗口，显示一个水平数字滑块小部件。所选数字将是返回值，如果对话框被取消则为 `null`。

将一个对象作为第一个参数传入，该对象可能包含以下字段来配置对话框和滑块：

|参数|描述|类型 / 值|
|---|---|---|
|title|定义对话框的标题|string|
|text|对话框的主要内容|string|
|prefix|显示在数字之前的文本|string|
|suffix|显示在数字之后的文本|string|
|value|初始选中的值|number|
|min|滑块的最小值。默认为 0|number|
|max|滑块的最大值。默认为 100|number|
|stepSize|滑块的步长。默认为 1|number|

```js
const myNumber = promptNumberSlider({
  title: 'Temperature',
  text: 'Select the desired temperature',
  suffix: '°C',
  min: 20,
  max: 35,
  value: 24,
  stepSize: 0.5,
});
```

<a id="prompt-password"></a>
### promptPassword

`promptPassword()` 函数打开一个要求输入密码的文本输入对话框。输入的密码随后被返回，如果对话框被取消则返回 `null`。将消息作为第一个参数传入，可选地传入第二个参数来预填文本输入字段。

```js
const myPassword = promptPassword("Please enter your password:");
const myPassword2 = promptPassword("Please enter your password:", "secret123");
```

<a id="prompt-color"></a>
### promptColor

`promptColor()` 函数打开一个颜色选择器。所选颜色以十六进制 RGB 返回（例如红色为 FF0000），如果选择器被取消则返回 `null`。作为可选的第一个参数，你可以传入预选的颜色。作为可选的第二个参数，你可以为选择器对话框传入标题。

```js
const myColor = promptColor();
const myColor2 = promptColor("#FF0000");
const myColor3 = promptColor(null, "Pick a color");
```

如果你需要分别获取红色、绿色和蓝色分量，请查看[此示例](scripting-examples.md#split-color)。

<a id="prompt-date"></a>
### promptDate

`promptDate()` 函数打开一个日期选择器。所选日期被返回，如果选择器被取消则返回 `null`。作为第一个参数，你可以传入用于返回值的日期格式（默认为 yyyy-MM-dd），作为第二个参数，你可以传入预选日期（yyyy-MM-dd 格式）。作为可选的第三个参数，你可以为选择器对话框传入标题。

```js
const myDate = promptDate();
const myDate2 = promptDate("yyyy-MM-dd", "2050-12-31");
const myDate3 = promptDate(null, null, "Pick a date");
```

<a id="prompt-time"></a>
### promptTime

`promptTime()` 函数打开一个时间选择器。所选时间被返回，如果选择器被取消则返回 `null`。作为第一个参数，你可以传入用于返回值的时间格式（默认为 HH:mm），作为第二个参数，你可以传入预选时间（HH:mm 格式）。作为可选的第三个参数，你可以为选择器对话框传入标题。

```js
const myTime = promptTime();
const myTime2 = promptTime("HH/mm", "13:37");
const myTime3 = promptTime(null, null, "Pick a time");
```

<a id="show-selection"></a>
### showSelection & showMultiSelection

这些函数允许你显示一个带有多个选项可供选择的对话框。第一个参数必须是由键值字符串对组成的对象，或者是字符串数组。作为可选的第二个参数，你可以为对话框传入标题。

对于 `showSelection`，可以选择单个选项，函数返回其键或值，如果对话框在没有选择的情况下关闭（例如通过按返回按钮）则返回 `null`。

对于 `showMultiSelection`，可以选择多个选项，函数返回所选键或值的数组，如果对话框在没有选择的情况下关闭则返回 `null`。

```js
// 使用字符串数组，进行单选
const starterPokemon = showSelection(['Bulbasaur', 'Charmander', 'Squirtle']);

// 使用对象，进行单选
const favoriteColor = showSelection({
  '#ff0000': 'Red',
  '#00ff00': 'Green',
  '#0000ff': 'Blue',
}, 'Pick your favorite color');

// 使用字符串数组，进行多选
const favoriteSeasons = showMultiSelection(['spring', 'summer', 'fall', 'winter']);

// 使用对象，进行多选
const availableWeekdays = showMultiSelection({
  '0': 'Sunday',
  '1': 'Monday',
  '2': 'Tuesday',
  '3': 'Wednesday',
  '4': 'Thursday',
  '5': 'Friday',
  '6': 'Saturday',
}, 'Weekday availability?');
```

<a id="show-notification"></a>
### showNotification

`showNotification()` 函数允许你在通知中显示文本。将通知的标题作为第一个参数传入，作为可选的第二个参数，你可以传入一条消息。

> 第一次使用此函数时，系统会提示你授予通知权限。如果你拒绝，该函数将不会执行任何操作。要再次启用它，你需要前往应用的权限设置并手动授予通知权限。

```js
showNotification('Hello World');
showNotification('Hello World', 'This is a notification');
```

<a id="play-sound"></a>
### playSound

使用此函数，你可以播放通知声音。如果不传入任何参数，它将使用系统的默认通知声音。你可以使用应用内的代码片段选择器选择不同的声音。

```js
playSound();
```

<a id="speak"></a>
### speak

使用此函数，你可以使用设备的文本转语音引擎将一段文本大声朗读出来。只需将你想要朗读的文本作为第一个参数传入，可选地将语言标识符作为第二个参数传入。

> 请注意，只会朗读前 400 个字符。还要注意，如果不支持该语言，第二个参数将被忽略。

```js
speak('Hello World');

speak('Dieser Text ist deutsch', 'de');
```

> 并非所有设备都支持此函数。

<a id="vibrate"></a>
### vibrate

使用此函数，你可以使设备振动（如果支持）。作为可选的第一个参数，你可以传入你想要使用的振动模式的名称，作为可选的第二个参数，你可以传入一个布尔值，表示执行是否应等待振动模式完成。

振动模式：

| 名称 | 描述 |
| --- | --- |
| short | 1 个短脉冲。如果未提供模式，这是默认值 |
| long | 1 个长脉冲 |
| 3 pulses | 3 个短脉冲 |
| click | 一个非常短暂、轻柔的脉冲，通常用于 UI 元素的触觉反馈 |
| tick | 一个非常短暂、轻柔的脉冲。比 "click" 弱 |
| heavy click | 一个非常短暂、轻柔的脉冲。比 "click" 强 |
| double click | 2 个非常短暂、轻柔的脉冲 |

```js
vibrate("3 pulses", true);
vibrate("click");
```

<a id="scan-barcode"></a>
### scanBarcode

`scanBarcode` 函数允许你扫描条形码（例如二维码）。成功时，该函数将条形码的原始数据作为字符串返回。如果扫描器被取消，则返回 `null`。

```js
const code = scanBarcode();
```

扫描本身由外部应用完成，特别是 [QR Droid](https://play.google.com/store/apps/details?id=la.droid.qr)、[Barcode Scanner](https://play.google.com/store/apps/details?id=com.google.zxing.client.android) 或 [Binary Eye](https://play.google.com/store/apps/details?id=de.markusfisch.android.binaryeye)，这意味着你需要安装该应用才能使用此功能。这也意味着 HTTP Shortcuts 应用本身不需要直接访问你的相机。

<a id="modify-shortcuts"></a>
## 修改快捷方式

本节列出了所有可用于以编程方式修改现有快捷方式的内置函数。

<a id="rename-shortcut"></a>
### renameShortcut

使用此函数，你可以重命名快捷方式。只需将快捷方式的名称或 ID 作为第一个参数传入，将新名称作为第二个参数传入。你也可以将空字符串作为第一个参数传入，以针对当前快捷方式。

```js
renameShortcut('Old Name', 'New Name');
```

<a id="change-description"></a>
### changeDescription

使用此函数，你可以更改快捷方式的描述。只需将快捷方式的名称或 ID 作为第一个参数传入，将新描述作为第二个参数传入。你也可以将空字符串作为第一个参数传入，以针对当前快捷方式。

```js
changeDescription('My Shortcut', 'New Description');
```

> 快捷方式的描述仅在使用列表布局的分类中可见，在使用网格布局的分类中不可见。

<a id="change-icon"></a>
### changeIcon

使用此函数，你可以更改快捷方式的图标。只需将快捷方式的名称或 ID 作为第一个参数传入，将图标名称作为第二个参数传入。你也可以将空字符串作为第一个参数传入，以针对当前快捷方式。使用应用中的*"添加代码片段"*按钮选择一个图标。或者，你可以查看[源代码](https://github.com/Waboodoo/HTTP-Shortcuts/blob/develop/HTTPShortcuts/app/src/main/kotlin/ch/rmy/android/http_shortcuts/icons/Icons.kt)以获取所有可用图标名称（查找前缀 "R.drawable."，其后的所有内容都是有效的图标名称）。

```js
changeIcon('My Shortcut', 'bitsies_lightbulb');
```

<a id="set-shortcut-hidden"></a>
### setShortcutHidden

此函数允许你在应用内显示或隐藏单个快捷方式。只需将快捷方式的名称或 ID 作为第一个参数传入，将 `true` 或 `false` 作为第二个参数传入。你也可以将空字符串作为第一个参数传入，以针对当前快捷方式。

```js
setShortcutHidden('My Shortcut', true);
```

> 你可以通过设置屏幕上的一个选项让隐藏的快捷方式可见。

<a id="set-category-hidden"></a>
### setCategoryHidden

此函数允许你显示或隐藏分类。只需将分类的名称或 ID 作为第一个参数传入，将 `true` 或 `false` 作为第二个参数传入。

```js
setCategoryHidden('My Category', true);
```

> 必须始终至少有一个非隐藏分类。如果你尝试用此函数隐藏最后一个可见分类，什么也不会发生。

<a id="control-flow"></a>
## 控制流

本节列出了你可以用来控制脚本执行流程的一些选项。

<a id="wait"></a>
### wait

`wait` 函数允许你通过等待（也称为休眠）指定的毫秒数来延迟执行，然后再继续执行脚本。

```js
wait(3000); // 延迟执行 3 秒
```

> 请注意，这是一个阻塞操作，这意味着在等待期间你将无法与应用交互。

<a id="abort"></a>
### abort, abortAll and abortAndTreatAsFailure

使用 `abort` 函数，你可以中止快捷方式的执行。

```js
abort();
```

如果快捷方式是通过 [executeShortcut](#execute-shortcut) 函数从另一个快捷方式调用的，则只会中止当前快捷方式。如果你想同时中止调用的快捷方式，可以使用 `abortAll()`。

在"成功时运行"代码块中，你还可以使用 `abortAndTreatAsFailure()` 函数，它会跳过剩余的"成功"步骤，而是将执行视为失败，这意味着将运行"失败时运行"代码，以及任何其他与失败相关的步骤，如显示错误消息。你可以在仅检查 HTTP 状态码不足以确定请求是否应被视为成功的情况下使用它。作为可选参数，你可以传入一个字符串，该字符串将用作错误消息。

```js
// 基本示例
abortAndTreatAsFailure();

// 更实际的示例
const responseBody = JSON.parse(response.body);
if (responseBody.status === 'error') {
  abortAndTreatAsFailure(responseBody.error);
}
```

<a id="text-processing"></a>
## 文本处理

本节列出了一些内置的文本处理函数。

<a id="base-64"></a>
### base64encode and base64decode

使用 `base64encode` 和 `base64decode` 函数，你可以使用 Base64 对给定字符串进行编码或解码。

```js
const encoded = base64encode('Hello world');
const decoded = base64decode(encoded);
```

`base64encode` 的返回类型是字符串，`base64decode` 的返回值是 `Uint8Array`。如果需要，你可以使用 `toString()` 将其转换为字符串。

<a id="html-encode"></a>
### htmlEncode and htmlDecode

`htmlEncode` 函数允许你对文本进行编码，使其符合 HTML 安全，即某些字符将被转义，这样字符串就可以安全地嵌入 HTML 中。
`htmlDecode` 函数则相反，将 HTML 编码的文本转换为纯文本。这也会去除任何 HTML 标签并用其内部文本替换它们。

> 这些函数的转换是尽力而为的。

```js
htmlEncode("<b>Hello</b>"); // 返回 &lt;b&gt;Hello&lt;/b&gt;
htmlDecode("&lt;b&gt;Hello&lt;/b&gt"); // 返回 <b>Hello</b>
htmlDecode("<b>Hello</b>"); // 返回 Hello
```

<a id="hash"></a>
### hash

使用 `hash` 函数，你可以计算给定字符串的哈希值。第一个参数表示要使用的哈希算法（支持的算法有 `MD5`、`SHA-1`、`SHA-256` 和 `SHA-512`），第二个参数是要哈希的字符串。返回值为十六进制格式。

```js
const hashed = hash('SHA-256', 'Hello world');
// `hashed` 的值现在是 '64ec88ca00b268e5ba1a35678a1b5316d212f4f366b2477232534a8aeca37f3c'。
```

<a id="hmac"></a>
### hmac

使用 `hmac` 函数，你可以计算给定消息的 [HMAC](https://en.wikipedia.org/wiki/HMAC)。第一个参数表示要使用的哈希算法（支持的算法有 `MD5`、`SHA-1`、`SHA-256` 和 `SHA-512`），第二个参数是密钥，第三个参数是要计算其 HMAC 的消息（作为字符串或字节数组）。返回值是一个 `Uint8Array`。

```js
const myHMAC = hmac('SHA-256', 'my_key123', 'Hello world');
const myHMACasHex = toHexString(myHMAC);
// `myHMACasHex` 的值现在是 '34d60d40202ae16ae3dd70c9715b1900f9fe30cf10af483e74ea8f6bef18bd09'。
```

<a id="parse-html"></a>
### parseHTML

`parseHTML` 函数允许将 HTML 字符串解析为对象表示形式。每个 HTML 元素都被转换为一个 JS 对象，具有 `name`、`attributes`、`children` 和 `text`（如果有）属性，如以下示例所示：

```js
// 给定一些 XML 字符串
const myHTML = `<html lang="de">
  <head>
    <title>Hello World</title>
  </head>
  <body>
    <ul style="color: red">
      <li>Item 1</li>
      <li>Item 2</li>
    </ul>
  </body>
</html>`;

const result = parseHTML(myHTML);

/*
result 变量现在包含以下对象（为清晰起见省略了空白文本字段）：
{
  "name": "html",
  "attributes": {
    "lang": "de"
  },
  "children": [
    {
      "name": "head",
      "attributes": {},
      "children": [
        {
          "name": "title",
          "attributes": {},
           "children": [],
           "text": "Hello World"
        }
      ]
    },
    {
      "name": "body",
      "attributes": {},
      "children": [
        {
          "name": "ul",
          "attributes": {
            "style": "color: red"
          },
          "children": [
            {
              "name": "li",
              "attributes": {},
              "children": [],
              "text": "Item 1"
            },
            {
              "name": "li",
              "attributes": {},
              "children": [],
              "text": "Item 2"
            }
          ]
        }
      ]
    }
  ]
}
*/

// 我们现在可以轻松提取感兴趣的部分
const title = result.children[0].children[0].text;
```

作为第二个参数，你可以提供一个查询来搜索并仅解析 HTML 的特定部分。在这种情况下，返回值是所有找到的元素的数组。默认情况下，此查询字符串被解释为 CSS 风格的选择器，但你也可以通过传入字符串 "xpath" 作为第三个参数来提供 XPath 查询。
```js
const myHTML = '...'; // 与上面的示例相同

const result = parseHTML(myHTML, 'ul > li'); // 查询所有作为 `ul` 元素直接后代的 `li` 元素。

/*
result 变量现在包含以下对象数组：
[
  {
    "name": "li",
    "attributes": {},
    "children": [],
    "text": "Item 1"
  },
  {
    "name": "li",
    "attributes": {},
    "children": [],
    "text": "Item 2"
  }
]
*/
```

如果你不熟悉 CSS 选择器，这里有一些示例：
- `li` 会选择所有 `<li>` 元素
- `.my-class` 会选择所有具有 "my-class" 类的元素
- `p img` 会选择所有位于 `<p>` 元素内的 `<img>` 元素（不一定是直接子元素）
- `a[href]` 会选择所有具有 `href` 属性的 `<a>` 元素
- `[id="my-id"]` 会选择所有具有 `id="my-id"` 属性的元素
- `ul.my-class > li` 会选择所有作为具有 "my-class" 类的 `<ul>` 元素的直接子元素的 `<li>` 元素

<a id="parse-xml"></a>
### parseXML

`parseXML` 函数允许将 XML 字符串解析为对象表示形式。每个 XML 元素都被转换为一个 JS 对象，具有 `name`、`attributes`、`children` 和 `text`（如果有）属性，如以下示例所示：

```js
// 给定一些 XML 字符串
const myXML = `<element>
    <foo bar="123">Hello World</foo>
</element>`;

const result = parseXML(myXML);

/*
result 变量现在包含以下对象：
{
  "name": "element",
  "attributes": {},
  "children": [
    {
      "name": "foo",
      "attributes": {
        "bar": "123"
      },
      "children": [],
      "text": "Hello World"
    }
  ],
  "text": "\n    \n"
}
*/

// 我们现在可以轻松提取感兴趣的部分
const rootElement = result.name; // 将是 "element"
const childElement = result.children[0].name; // 将是 "foo"
const childAttributes = Object.keys(result.children[0].attributes); // 将是 ["bar"]
const childBarAttribute = result.children[0].attributes.bar; // 将是 "123"
const childText = result.children[0].text; // 将是 "Hello World"
```

<a id="to-string-to-hex-string"></a>
### toString and toHexString

`toString` 和 `toHexString` 函数可用于将 `Uint8Array` 转换为字符串，这在与 `hmac` 和 `base64decode` 函数结合使用时特别有用。

```js
const myValue = base64decode('SGVsbG8=');
const result = toString(myValue);
// `result` 的值现在是 'Hello'。
```

<a id="network"></a>
## 网络

<a id="get-wifi-ip-address"></a>
### getWifiIPAddress

使用此函数，你可以检索设备在当前 Wi-Fi 上的 IPv4 地址。如果当前没有 Wi-Fi 连接，它将返回 `null`。

```js
const myIP = getWifiIPAddress();
```

<a id="get-wifi-ssid"></a>
### getWifiSSID

使用此函数，你可以检索设备当前连接的 Wi-Fi 网络的 SSID（即名称）。如果当前没有 Wi-Fi 连接或无法确定 SSID，它将返回 `null`。

```js
const mySSID = getWifiSSID();
```

要使此函数正常工作，需要启用位置服务，并且应用需要被授予访问设备位置的权限。这是 Android 操作系统施加的技术限制。另请参阅[权限](permissions.md)页面了解详情。

<a id="wol"></a>
### Wake-on-LAN

你可以使用 `wakeOnLan` 函数发送魔术包来开启网络上的另一台设备。第一个参数必须是设备的 MAC 地址。作为可选的第二个参数，你可以传入要使用的网络/广播地址，作为第三个参数，你可以定义端口。

```js
wakeOnLan('01-23-45-67-89-ab');

wakeOnLan('01-23-45-67-89-ab', '255.255.255.255', 9);
```

<a id="send-http-request"></a>
### Send HTTP request

`sendHttpRequest` 函数允许你发送一个简单的 HTTP 请求。第一个参数是 URL，第二个（可选）参数提供一个带有额外选项的对象。选项对象中支持以下字段：

|参数|描述|类型 / 值|
|---|---|---|
|method|定义要使用的 HTTP 方法，例如 "GET" 或 "POST"。默认为 "GET"。|string|
|headers|要添加到请求的额外请求头，作为键值对。|object|
|body|请求中使用的请求体，用于发送数据时|string|
|formData|要添加到请求的表单参数，作为键值对。不能与 `body` 组合使用。|object|
|charset|定义用于解码响应体的字符集。如果未提供，则从响应头中推断字符集，或回退到 UTF-8|string|
|followRedirects|是否跟随 HTTP 重定向。默认启用。|boolean|

该函数返回一个对象，其中包含 `status` 字段，其值为 "success"、"httpError" 或 "networkError"。如果是 "networkError"，你可以检查 `networkError` 字段了解详情。否则，你可以检查 `response` 字段获取 HTTP 响应对象。它包含 `body`、`headers`、`cookies` 和 `statusCode` 字段。

> 如果你需要更多选项，请考虑为你的请求创建一个专用的 HTTP 快捷方式，然后使用 [enqueueShortcut](#trigger-shortcut) 或 [executeShortcut](#execute-shortcut) 来调用它。

```js
const result = sendHttpRequest(
  "https://example.com",
  {
    method: "POST",
    body: '{"data": 123}',
    headers: {
      "Content-Type": "application/json",
    },
  },
);

if (result.status == "success") {
  alert(result.response.body);
} else if (result.status == "httpError") {
  alert("Failed with status code " + result.response.statusCode);
} else {
  alert("Failed with network error: " + result.networkError);
}
```

<a id="send-mqtt-message"></a>
### Send MQTT message

`sendMQTTMessages` 函数允许你连接到 MQTT 代理，向其发送（即发布）一条或多条消息，然后再断开连接。第一个参数是服务器/代理的 URI，第二个（可选）参数提供连接选项（如用户名和密码），第三个参数是所有应该发送的消息的数组。

```js
sendMQTTMessages(
  "tcp://192.168.0.42:1234",
  {"username": "admin", "password": "1234"},
  [
    {"topic": "hallway-lamp/set", "payload": "{\"state\":\"ON\"}"},
    {"topic": "desk-lamp/set", "payload": "{\"state\":\"ON\", \"brightness\": 255}"},
  ]
);
```

> 请注意，这不提供任何特定的服务质量保证，并且无法通过这种方式订阅主题，这意味着你无法接收任何 MQTT 消息。

如果你使用 SSL，你还可以通过向第二个参数添加额外选项来配置主机名验证，要么通过包含 `"fingerprint": "AA:AA:..."` 选项来检查特定的 SHA-1 或 SHA-5 指纹，要么通过包含 `"verifyHostname": false` 选项来完全跳过主机名验证。这主要仅在使用自签名证书时有用，你应该只在知道自己在做什么的情况下才这样做。

```js
sendMQTTMessages(
  "ssl://192.168.0.42:1234",
  {"username": "admin", "password": "1234", "fingerprint": "AA:BB:CC:DD:EE:FF:00:11:22:33:44:55:66:77:88:99:AA:BB:CC:DD"},
  [
    {"topic": "my_topic", "payload": "my_payload"},
  ]
);
```

<a id="send-tcp-packet"></a>
### Send TCP Packet

你可以使用 `sendTCPPacket` 函数向网络上的另一台设备发送 TCP 数据包。这在与具有 telnet 接口的设备交互时很有用。

将数据包数据作为第一个参数传入（可以是字符串、`Uint8Array` 或表示字节的数字数组），将目标主机的名称或 IP 地址作为第二个参数，将其 TCP 端口作为第三个参数。

```js
sendTCPPacket('hello', '192.168.1.42', 1337);

sendTCPPacket([0x68, 0x65, 0x6C, 0x6C, 0x6F], 'example.com', 4242);
```

如果你想监听来自 TCP 连接的传入数据，可以通过传入一个配置对象作为第四个参数来指定。此对象可能包含以下字段：

- `read`：如果设置为 "text"，所有传入数据都将作为文本读取并作为字符串返回。如果设置为 "line"，则只读取一行文本并作为字符串返回。如果未指定，则不读取任何内容并返回 `null`。
- `timeout`：套接字自动关闭后的时间（毫秒）。最多为 30000，默认为 3000。如果 `read` 设置为 "text" 且达到此超时，则返回到此时点为止读取的所有数据。
- `charset`：用于解码传入数据的字符集。默认为 UTF-8。

```js
const reply = sendTCPPacket('hello', '192.168.1.42', 1337, {
  read: 'text',
  timeout: 300,
});
```

<a id="send-udp-packet"></a>
### Send UDP Packet

你可以使用 `sendUDPPacket` 函数向网络上的另一台设备发送 UDP 数据包。将数据包数据作为第一个参数传入（可以是字符串、`Uint8Array` 或表示字节的数字数组），将目标主机的名称或 IP 地址作为第二个参数，将其 UDP 端口作为第三个参数。

```js
sendUDPPacket('hello', '192.168.1.42', 1337);

sendUDPPacket([0x68, 0x65, 0x6C, 0x6C, 0x6F], 'example.com', 4242);
```

<a id="misc"></a>
## 其他内置函数

本节列出了所有不属于特定类别的内置函数。

<a id="trigger-shortcut"></a>
### enqueueShortcut

使用此函数，你可以将一个快捷方式排队以在当前快捷方式之后执行（或者如果已经有计划执行的快捷方式，则在最后一个排队的快捷方式之后执行）。只需将快捷方式的名称或 ID 作为第一个参数传入。

```js
enqueueShortcut('My Other Shortcut');
```

可选地，你可以传入一个对象作为第二个参数来提供变量值。这不会更改变量的存储值，但在另一个快捷方式执行时，它们将采用指定的值。这对于动态变量类型（如*"文本输入"*或*"多选"*）特别有用。

```js
enqueueShortcut('My Other Shortcut', {
  'My_Variable1': 'Hello World',
  'My_Variable2': ':D',
});
```

作为可选的第三个参数，你可以传入要延迟执行的毫秒数。这样，你可以安排一个快捷方式在稍后的时间点运行。

> 请注意，延迟不会精确，且不能超过 5 分钟。

```js
enqueueShortcut('My Other Shortcut', null, 2 * 60 * 1000); // 2 分钟后运行
```

请注意，只有在当前快捷方式（以及之前已排队的所有快捷方式）执行完成后，该快捷方式才会被执行。它*不会*立即执行。如果你需要立即运行快捷方式，请改用 `executeShortcut`。

还要注意，如果被排队的快捷方式也会排队快捷方式，这可能会导致无限循环。为了在意外发生时减少影响，应用会将每第 10 次执行延迟 5 秒，以便你有足够的时间手动停止执行。如果你真的确定*确实*想要无限循环，你可以通过设置至少 500 毫秒的延迟来绕过此保护。

<a id="cancel-shortcut"></a>
### cancelShortcut

`cancelShortcut` 函数可用于取消快捷方式的执行，即已排队稍后运行的快捷方式，例如因为它是通过 `enqueueShortcut` 安排的，或被配置为带延迟运行或重复运行。将 ID 或名称作为第一个参数传入。

```js
cancelShortcut('My Repeating Shortcut');
```

<a id="execute-shortcut"></a>
### executeShortcut

此函数允许你在当前快捷方式中执行另一个快捷方式并接收其结果。将快捷方式的名称或 ID 作为第一个参数传入。

```js
executeShortcut('My Other Shortcut');
```

可选地，你可以传入一个对象作为第二个参数来提供变量值。这不会更改变量的存储值，但在另一个快捷方式执行时，它们将采用指定的值。这对于动态变量类型（如*"文本输入"*或*"多选"*）特别有用。

```js
executeShortcut('My Other Shortcut', {
  'My_Variable1': 'Hello World',
  'My_Variable2': ':D',
});
```

该函数将返回一个对象，其中包含 `status` 字段，你可以查询该字段以查看快捷方式的执行是否成功。它可能包含 "success"、"failure"、"unknown" 或 "aborted" 值。

- "success" 表示快捷方式成功执行了 HTTP 请求。其响应在 `response` 字段中返回，使用与所有快捷方式执行可用的 `response` 对象相同的[格式](#handle-response)。
- "failure" 表示快捷方式的 HTTP 请求失败，无论是由于网络错误还是 HTTP 状态不是 2xx 或 3xx。在这种情况下，你可以通过 `response` 字段或 `networkError` 字段获取有关失败的更多信息，其中一个在这种情况下始终非空。
- "unknown" 表示快捷方式没有（直接）发出 HTTP 请求，因此没有响应。如果快捷方式不是 HTTP 快捷方式，或者 HTTP 请求被延迟或重新安排，就会发生这种情况。
- "aborted" 表示调用了 `abort()` 函数

```js
const result = executeShortcut('My Other Shortcut');
if (result.status === 'success') {
  const body = result.response.body;
  alert(body);
} else if (result.status === 'failure') {
  if (result.networkError) {
    alert(result.networkError);
  } else {
    alert(result.response.body);
  }
}
```

请注意以下技术限制：
- 以这种方式执行的快捷方式不能在全屏窗口中显示其响应。如果你需要显示其响应，请将响应显示类型更改为使用吐司提示或对话框窗口。
- 以这种方式执行的快捷方式与原始快捷方式（即调用快捷方式）共享变量值的解析。这意味着，如果你在两个快捷方式中都使用了例如一个多选变量，你只会被提示选择一次值（而不是两次），并且所选值将用于两次快捷方式执行。
- 最大递归深度为 3，这意味着你不能任意地在快捷方式执行中嵌套快捷方式执行。这是为了防止无限递归和堆栈溢出。如果你想串联更多快捷方式，请考虑使用 [enqueueShortcut()](#trigger-shortcut)。

<a id="set-result"></a>
#### 传回数据
如果你希望将数据从被调用的快捷方式传递给调用的快捷方式，你可以通过在被调用的快捷方式中（使用 [setVariable()](#set-variable)）将值存储到全局变量中，然后在调用的快捷方式中（使用 [getVariable()](#get-variable)）读取这些值来实现，或者你可以使用 `setResult` 函数。后者接受一个字符串参数。然后，调用快捷方式可以通过 `executeShortcut` 函数返回的对象的 `result` 键访问此字符串。

```js
// 被调用的快捷方式执行的操作：
setResult('Hello World');

// 调用的快捷方式执行的操作：
const resultObject = executeShortcut('My other shortcut');
const myResult = resultObject.result; // 现在值为 "Hello World"
```

在从 Tasker 触发快捷方式的情况下，同样的机制也允许将数据传回 Tasker。

#### 转发文件
在执行或排队另一个快捷方式时，可以将一个或多个已选文件转发给它。如果你有一个快捷方式在表单参数或其请求体中使用了文件，并且你想在另一个快捷方式中使用相同的文件以包含在另一个请求中，这会很有用。为此，请通过特殊的 `$files` 变量传入[文件的 ID](#files)。你可以传入单个文件 ID 或文件 ID 数组，如以下示例所示：

```js
// 传递单个文件
enqueueShortcut('My Other Shortcut', {
  '$files': selectedFiles[0].id,
});

// 传递 2 个文件
enqueueShortcut('My Other Shortcut', {
  '$files': [selectedFiles[0].id, selectedFiles[1].id],
});

// 传递所有文件
enqueueShortcut('My Other Shortcut', {
  '$files': selectedFiles.map(file => file.id),
});
```

此机制对 `enqueueShortcut` 和 `executeShortcut` 函数都有效。

<a id="log-event"></a>
### Log Event

`logEvent` 函数允许你将自定义事件记录到事件历史中（通过在应用主菜单中选择"故障排除"可以找到）。这可用于调试和故障排除。将事件标题作为第一个参数传入，可选地将带有详细信息的消息作为第二个参数传入。

```js
logEvent('Hello World');

logEvent('My title', 'My message');

logEvent('My complex event', {'foo': 'bar'});
```

<a id="device-id"></a>
### Get Device ID

首次打开时，应用会生成一个由字母和数字组成的随机设备 ID。`getDeviceId` 函数可用于读取此 ID。这在你在多台设备上使用同一个快捷方式并需要以某种方式区分设备的情况下很有用。

```js
const myId = getDeviceId();
```

<a id="uuid-v4"></a>
### Generate UUID

你可以使用 `uuidv4()` 函数生成随机 UUID（**U**niversal **U**nique **Id**entifier，通用唯一标识符，第 4 版）。返回值为字符串类型。

```js
const myUUID = uuidv4();
```

<a id="get-clipboard-content"></a>
### Get Clipboard Content

`getClipboardContent` 函数允许你查询设备剪贴板中的最新项，即你从某处复制的最后一段文本。如果剪贴板中没有内容，或者其内容不是文本（例如你复制的是图像），此函数将返回 null。

```js
const clipboardValue = getClipboardContent();
```

> 在后台执行快捷方式时不能使用此函数，因为 Android 操作系统（从 Android 10 开始）不允许后台应用访问剪贴板。在这种情况下，函数将返回 `null`。

<a id="copy-to-clipboard"></a>
### Copy to the Clipboard

使用 `copyToClipboard` 函数，你可以将一个值复制到设备的剪贴板。只需将要复制的值作为第一个参数传入。

```js
copyToClipboard('Hello World');
```

> 在后台执行快捷方式时不能使用此函数，因为 Android 操作系统（从 Android 10 开始）不允许后台应用访问剪贴板。

<a id="share-text"></a>
### Share Text with Another App

你可以使用 `shareText` 函数与另一个应用共享一段文本。只需将要共享的值作为第一个参数传入。这将打开系统的共享选择器，你可以在其中选择要共享到哪个应用。

```js
shareText('Hello World');
```

> 请注意，共享的文本最多为 200000 个字符，否则将被截断。

<a id="open-app"></a>
### Open another App

`openApp` 函数允许你通过包名打开另一个应用。如果没有安装具有给定包名的应用，则会显示错误。

```js
openApp('com.github.android'); // 打开 GitHub 应用
```

<a id="open-url"></a>
### Open a URL

此函数允许你在另一个应用中打开 URL。这通常会打开浏览器，但也可用于调用到另一个应用的深度链接。如果 URL 格式错误或没有安装可以处理该 URL 的应用，则会显示错误消息。

```js
openUrl('https://www.wikipedia.org/');
```

> 请注意，这不能用于打开文件。

作为第二个参数，你可以传入应处理该 URL 的浏览器或应用的包名。你也可以传入 "custom-tabs" 或 "custom-tabs(\[包名])" 来使用自定义标签页而不是独立的浏览器窗口打开 URL。

```js
openUrl('https://example.com', 'org.mozilla.firefox');
openUrl('https://example.com', 'custom-tabs(org.mozilla.firefox)');
```

<a id="send-intent"></a>
### Send Intent
使用 `sendIntent` 函数，你可以发送一个 [Intent](https://developer.android.com/guide/components/intents-filters)，这允许你与同一设备上的其他 Android 应用交互。它接受一个对象作为其唯一参数，该对象应具有下表中列出的一个或多个属性。使用哪些值取决于目标应用，因此请查阅其文档、源代码或开发者以获取更多信息。

|参数|描述|类型 / 值|
|---|---|---|
|type|定义 intent 的发送方式。|`'broadcast'`（默认）、`'activity'` 或 `'service'`|
|action|指定要执行的通用操作（如查看或选择）的字符串。|string|
|category|包含有关应处理 intent 的组件类型的附加信息的字符串。|string|
|categories|与 `category` 相同，但允许指定多个值。|字符串数组|
|dataUri|引用要操作的数据的 URI|string|
|dataType|数据的 MIME 类型|string|
|className|要由 Intent 启动的类的全名|string|
|packageName|要由 Intent 启动的应用程序包的名称|string|
|extras|extra 数组，即要发送的附加参数|对象数组（见下文）|
|clearTask|是否设置 [`FLAG_ACTIVITY_CLEAR_TASK`](https://developer.android.com/reference/android/content/Intent#FLAG_ACTIVITY_CLEAR_TASK) 标志|boolean|
|excludeFromRecents|是否设置 [`FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS`](https://developer.android.com/reference/android/content/Intent#FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS) 标志|boolean|
|newTask|是否设置 [`FLAG_ACTIVITY_NEW_TASK`](https://developer.android.com/reference/android/content/Intent#FLAG_ACTIVITY_NEW_TASK) 标志|boolean|
|noHistory|是否设置 [`FLAG_ACTIVITY_NO_HISTORY`](https://developer.android.com/reference/android/content/Intent#FLAG_ACTIVITY_NO_HISTORY) 标志|boolean|

每个 extra 由以下属性组成：

|参数|描述|类型 / 值|
|---|---|---|
|name|extra 的名称|string|
|type|extra 的类型|`'string'`（默认）、`'boolean'`、`'int'`、`'long'`、`'double'`、`'float'`|
|value|extra 的值|取决于 `type`|

> 请注意，不幸的是，*不可能*发送需要应用持有特定权限的 intent，因为没有办法动态地向应用添加这样的权限。这是应用本身无法解决的技术限制。建议的变通办法是使用第三方自动化应用（如 Tasker）来执行此类操作，并通过 intent 或 `triggerTaskerTask` 函数（见下文）触发其任务/工作流。另一种可能性是分叉应用，向其中添加所需权限并自行构建。

这是一个显示语法的通用示例：

```js
sendIntent({
  type: 'activity',
  action: 'my.special.action',
  packageName: 'com.example.foobar',
  className: 'com.example.foobar.MainActivity',
  extras: [
    {
      name: 'favorite_number',
      type: 'int',
      value: 42,
    },
  ],
});
```

以下示例显示如何使用此函数打开另一个应用程序，在本例中是浏览器以显示网站：

```js
sendIntent({
  type: 'activity',
  action: 'android.intent.action.VIEW',
  dataUri: 'https://example.com',
});
```
上面的示例等同于调用 `openUrl('https://example.com')`。

如果你只想打开一个特定的应用而不向其发送任何数据，可以通过指定应用的包名并使用 `android.intent.action.MAIN` action 来实现。以下示例将只打开 Google Chrome：

```js
sendIntent({
  type: 'activity',
  action: 'android.intent.action.MAIN',
  packageName: 'com.android.chrome',
});
```

上面的示例等同于调用 `openApp('com.android.chrome')`。

<a id="trigger-tasker-task"></a>
### Trigger Tasker Task
如果你的设备上安装了 [Tasker](https://play.google.com/store/apps/details?id=net.dinglisch.android.taskerm)，你可以使用此函数触发其某个任务。将任务名称作为第一个参数传入，可选地传入一个包含一些键值对的对象作为第二个参数，以作为局部变量传递。

```js
triggerTaskerTask('doStuff');

triggerTaskerTask('mytask', {
  myLocalVariable: 'hello',
  andAnother: 'world',
});
```

> 请注意，你可能需要手动前往应用的权限并允许应用运行 Tasker 任务才能正常工作，并且你还需要在 Tasker 的设置中"首选项 > 杂项 > 允许外部访问"下允许此操作。

<a id="run-termux-command"></a>
### Run Termux Command
如果你安装了 [Termux](https://github.com/termux/termux-app)，可以使用 `runTermuxCommand` 函数在 Termux 终端中运行命令。

要使其正常工作，你需要向应用授予"在 Termux 环境中运行命令"权限，并且需要在 `~/.termux/termux.properties` 中将 `allow-external-apps` 设置为 `true`。有关更多详细信息，请参阅 https://github.com/termux/termux-app/wiki/RUN_COMMAND-Intent#setup-instructions。

```js
// 简单语法
runTermuxCommand('/data/data/com.termux/files/usr/bin/top', ['-n', '5']);

// 完整选项集
runTermuxCommand({
  command: '/data/data/com.termux/files/usr/bin/top',
  arguments: ['-n', '5'],
  workingDir: '/data/data/com.termux/files/home',
  resultDir: '/data/data/com.termux/files/home',
  background: false,
  sessionAction: '0',
})
```

> 目前无法将数据从 Termux 传回应用。作为变通办法，你可以将数据写入文件，然后让应用从同一文件读取。

<a id="set-wireguard-tunnel-state"></a>
### Set Wireguard Tunnel State
如果你安装了 [Wireguard](https://play.google.com/store/apps/details?id=com.wireguard.android)，可以使用 `setWireguardTunnelState` 函数启用或禁用隧道。将隧道名称作为第一个参数传入，作为第二个参数传入 `true` 以启用隧道，或传入 `false` 以禁用隧道。

```js
setWireguardTunnelState('my-tunnel', true);
```

> 要使其正常工作，你需要向应用授予特殊权限，并且需要在 Wireguard 应用中启用"允许远程控制应用"设置。你可能还需要将 Wireguard 应用从电池优化中排除，并允许其无限制运行。


<a id="get-location"></a>
### Get Location
如果你想查询设备的物理位置，可以通过 `getLocation()` 函数实现。位置请求最多可能需要 20 秒才能完成，并且可能并不总是能够确定位置。

结果对象由以下字段组成：

|字段|描述|类型|
|---|---|---|
|status|指示是否可以确定位置。将为 `'success'` 或 `'unknown'`|string|
|latitude|纬度（度），如果位置未知则为 null|number|
|longitude|经度（度），如果位置未知则为 null|number|
|accuracy|在第 68 百分位置信水平下的估计水平精度半径（米），如果位置或精度未知则为 null|number|
|coordinates|为方便起见，纬度和经度用逗号连接|string|

```js
const myLocation = getLocation();
if (myLocation.status == 'success') {
  alert(`I am currently at ${myLocation.coordinates}`);
} else {
  alert('I am so lost right now');
}
```

> 请注意，此函数使用 Google Play 服务。如果设备上没有这些服务，或者你是从 F-Droid 安装的应用，则会使用回退方案，其精度可能较低、可靠性较差或查找位置所需时间较长。