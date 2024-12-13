<h1 align="center">HX-ANiMe-PyApi</h1> 

## 一、介绍

本PyApi包只是为了方便添加结点和边, 因为手动导入实在是太慢了; 计佬(计算机老哥)肯定懒得手动搞...

于是编写了本包, 并且Java后端提供有对应的api接口, 仅仅只需要一个`apiKey`即可使用!

接口非常简洁易用, 让我们一起来学习一下吧~

## 二、食用说明

### 2.1 导包

```py
# 导入 HX-ANiMe-PyApi 包
from HXANiMeApi.api import *
```

### 2.2 接口说明
#### 2.2.1 初始化api
```py
# 用户图表 apiKey
apiKey = "请填写你复制过来的apiKey"

# 后端服务器URL (就是你java服务器的ip或者域名)
ANiMeUrl = "http://localhost:28205"

# 构造得出 api
api = HX_ANiMe_Api(ANiMeUrl, apiKey)
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
# 添加结点, 如果添加成功, 则返回结点id; 否则同上, 会抛出异常信息
nodeId: int = api.addNode(Node(legendId=1, name="结点名称", ...))
```