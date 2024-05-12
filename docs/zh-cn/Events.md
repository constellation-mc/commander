# 事件

事件是游戏内，触发[命令](Commands)的节点。

## 订阅文件的引入

在订阅文件中声明一个事件，意味着标识你想要监听的事件。事件可以在 `parameters` 代码块中接受额外的形式参数，但（目前）内置事件并不要求具备它们。订阅文件将在 `commander/events` 下被读取。

```
|- recipes
|- commander
  |- events
    |- test_event.json
    |- folder
      |- nested.json
|- tags
```

声明事件的典型例子如下：

::: details 示例
```json
{
  "event": "commander:after_killed_by_other"
  "commands": [

  ]
}
```
<br/>

```json
{
  "event": "modid:custom_event",
  "parameters": {
  },
  "commands": [

  ]
}
```
:::

订阅文件可以监听不同的（或者一样的）事件。

::: details 示例
```json
{
  "events": [
    {
      "event": "commander:after_killed_by_other"
    },
    {
      "event": "commander:allow_damage"
    },
    {
      "event": "commander:player_attack/block"
    }
  ]
}
```
:::

## 内置事件

在 `commander` 命名空间下，本模组涵盖了绝大部分兼容的 fabric 事件。这里的返回指[取消类命令](Commands#commandercancel)。

当前可用的事件请见：[实体事件](https://github.com/constellation-mc/commander/blob/main/src/main/java/me/melontini/commander/impl/builtin/events/EntityEvents.java)，[玩家事件](https://github.com/constellation-mc/commander/blob/main/src/main/java/me/melontini/commander/impl/builtin/events/PlayerEvents.java)，[服务器生命周期](https://github.com/constellation-mc/commander/blob/main/src/main/java/me/melontini/commander/impl/builtin/events/ServerLifecycle.java)，[服务器刻](https://github.com/constellation-mc/commander/blob/main/src/main/java/me/melontini/commander/impl/builtin/events/ServerTick.java)。
