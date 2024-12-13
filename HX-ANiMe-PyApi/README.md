<h1 align="center">HX-ANiMe-PyApi</h1> 

## 一、介绍

本PyApi包只是为了方便添加结点和边, 因为手动导入实在是太慢了; 计佬(计算机老哥)肯定懒得手动搞...

于是编写了本包, 并且Java后端提供有对应的api接口, 仅仅只需要一个`apiKey`即可使用!

接口非常简洁易用, 让我们一起来学习一下吧~

## 二、食用说明

### 2.1 导包

```py
# 导入 HX-ANiMe-PyApi 包 (当然, 您也可以按需导入)
from HXANiMeApi import *
```

### 2.2 数据类型说明

- class: `Legend` / `Node` / `Edge`
- 所处包: `./HXANiMeApi/DTO.py`

#### 2.2.1 图例数据

```py
class Legend:
    """图例"""
    def __init__(self, id: int = 0, name: str = "", color: str = ""):
        self.legendId = id       # 图例id
        self.legendName = name   # 图例名称
        self.legendColor = color # 图例颜色
```

### 2.2.2 结点数据

```py
class Node:
    """节点"""
    def __init__(self, id: int = 0, legendId: int = 0, name: str = "", imgUrl: str = "", description: str = ""):
        self.nodeId = id               # 节点id
        self.legendId = legendId       # 所属图例id
        self.name = name               # 节点名称
        self.imgUrl = imgUrl           # 节点图片
        self.description = description # 节点描述
```

### 2.2.3 边数据

```py
class Edge:
    """边"""
    def __init__(self, id: int = 0, fromNodeId: int = 0, toNodeId: int = 0):
        self.edgeId = id             # 边id
        self.fromNodeId = fromNodeId # 源节点id
        self.toNodeId = toNodeId     # 目标节点id
```

### 2.3 接口说明

- class: `ANiMeApi`
- 所处包: `./HXANiMeApi/api.py`

#### 2.2.1 初始化api
```py
# 用户图表 apiKey
apiKey = "请填写你复制过来的apiKey"

# 后端服务器URL (就是你java服务器的ip或者域名)
ANiMeUrl = "http://localhost:28205"

# 构造得出 api
api = ANiMeApi(ANiMeUrl, apiKey)
```

#### 2.2.2 查询接口

- 通过构造得出的`api`, 提供有以下方法:

```py
try:
    for it in api.getLegends(): # 获取所有图例
        print(it)

    for it in api.getNodes():   # 获取所有结点
        print(it)

    for it in api.getEdges():   # 获取所有边
        print(it)
except ValueError as e:
    print(f"错误: {e}") # 如果请求异常, 会抛出异常信息; 此处接收异常并打印  
```

#### 2.2.3 添加数据接口

```py
# 添加图例, 如果添加成功, 则返回图例id; 否则同上, 会抛出异常信息
legendId: int = api.addLegend(Legend(...))

# 添加结点, 如果添加成功, 则返回结点id; 否则同上, 会抛出异常信息
nodeId: int = api.addNode(Node(...))

# 添加边
edgeIdL int = api.addEdge(Edge(...))
```

### 2.4 idMap工具类

- class: `LegendIdMap` / `NodeIdMap`
- 所处包: `./HXANiMeApi/idMap.py`

内部维护了: `id - 元素` 和 `name - 元素` 的`Map`

> [!WARNING]
> 因为`Map`的`key`是 **唯一** 的, 故当且仅当**元素**的`id`和`name`都不会重复的时候, 才可以使用这个工具类
>
> 如果`key`重新重复, 则会**抛出异常**!!!
>
> - *因为我认为一个图上不应该出现两个名称一样的结点*

#### 2.4.1 ensure方法

- 以 NodeIdMap 的 ensure 方法为例:

```py
def ensure(self, api: ANiMeApi, node: Node) -> int:
    """确保该结点存在, 如果不存在则添加(先后端, 再往map添加)

    Args:
        api (ANiMeApi): api类, 需要提供`addNode`方法
        node (Node): 结点

    Returns:
        int: `0`表示成功, `> 0`表示返回结点的id
    """
```

如果我们的`NodeIdMap`对象中没有这个`node`, 则会 **添加** 它(先添加到后端, 后端返回`id`后, 再添加到`idMap`)

使用示例:

```py
# 构建 ANiMeApi
api = ANiMeApi(ANiMeUrl, apiKey)

# 获取所有图例
legendMap = LegendIdMap(api.getLegends())

# 获取当前所有结点
nodeMap = NodeIdMap(api.getNodes())

# 先判断图例是否存在, 如果不存在则向后端添加
# 因为返回0是存在, 则可以这样写, 完全确保其存在
while (legendMap.ensure(api, Legend(name="番剧", color="#FF0099"))
    | legendMap.ensure(api, Legend(name="声优", color="#99FF00"))
    | legendMap.ensure(api, Legend(name="角色", color="#0099FF"))):
    print("添加图例...")
```

> *具体实例, 可以查看: [番剧信息爬虫, 并且使用keyApi提交到后端](./getANiMeCVData.py)*