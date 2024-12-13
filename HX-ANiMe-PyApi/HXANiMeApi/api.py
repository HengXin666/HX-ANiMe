import requests, json

# === Begin === HXANiMe-py-class === Begin === {
class Legend:
    """图例"""
    def __init__(self, id: int = 0, name: str = "", color: str = ""):
        self.legendId = id       # 图例id
        self.legendName = name   # 图例名称
        self.legendColor = color # 图例颜色

    def toDict(self):
        return {
            "legendId": self.legendId,
            "legendName": self.legendName,
            "legendColor": self.legendColor
        }

    def __repr__(self):
        return json.dumps(self.toDict())

class Node:
    """节点"""
    def __init__(self, id: int = 0, legendId: int = 0, name: str = "", imgUrl: str = "", description: str = ""):
        self.nodeId = id               # 节点id
        self.legendId = legendId       # 所属图例id
        self.name = name               # 节点名称
        self.imgUrl = imgUrl           # 节点图片
        self.description = description # 节点描述

    def toDict(self):
        return {
            "nodeId": self.nodeId,
            "legendId": self.legendId,
            "name": self.name,
            "imgUrl": self.imgUrl,
            "description": self.description
        }

    def __repr__(self):
        return json.dumps(self.toDict())

class Edge:
    """边"""
    def __init__(self, id: int = 0, fromNodeId: int = 0, toNodeId: int = 0):
        self.edgeId = id             # 边id
        self.fromNodeId = fromNodeId # 源节点id
        self.toNodeId = toNodeId     # 目标节点id
    
    def toDict(self):
        return {
            "edgeId": self.edgeId,
            "fromNodeId": self.fromNodeId,
            "toNodeId": self.toNodeId
        }

    def __repr__(self):
        return json.dumps(self.toDict())

class LegendIdMap:
    """图例映射, 提供 id -> 图例, name -> 图例
    """
    def __init__(self, legendList: list[Legend]):
        """图例映射构造

        Args:
            legendList (list[Legend]): 图例列表
        """
        self.idMap = {}
        self.nameMap = {}
        for it in legendList:
            self.idMap[it.legendId] = it
            self.nameMap[it.legendName] = it
    
    def findByName(self, name: str) -> Legend:
        """通过name查找

        Args:
            name (str): 图例名称

        Returns:
            Legend: 图例对象 | (查找不到: 抛异常)
        """
        return self.nameMap[name]
    
    def __getitem__(self, name: str) -> Legend:
        """重载`[]`运算符, 以`name`作为键查找

        Args:
            name (str): 图例name

        Returns:
            Legend: 图例
        """
        return self.findByName(name)
    
    def findById(self, id: int) -> Legend:
        """通过id查找

        Args:
            id (int): 图例id

        Returns:
            Legend: 图例 | (查找不到: 抛异常)
        """
        return self.idMap[id]
    
    def ensure(self, api: object, legend: Legend) -> int:
        """确保该图例存在, 如果不存在则添加

        Args:
            api (后端api类): api类, 需要提供`addLegend`方法
            legend (Legend): 图例

        Returns:
            int: `0`则是成功, `> 0`则是返回图例的id
        """
        try:
            self.findByName(legend.legendName)
            return 0
        except:
            return api.addLegend(legend)

def mapToObject(data, cls):
    """将字典数据映射到指定类的实例中"""
    obj = cls()
    for key, value in data.items():
        if hasattr(obj, key):
            setattr(obj, key, value)
    return obj

class HX_ANiMe_Api:
    def __init__(self, baseUrl: str, apiKey: str):
        """后端 API 封装

        Args:
            baseUrl (str): 后端域名 (如: http://localhost:28205)
            apiKey (str): api密匙
        """
        self.baseUrl = baseUrl
        self.headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36 Edg/131.0.0.0',
            "apiKey": apiKey,
        }

    def _post(self, endpoint: str, data=None):
        """私有post方法

        Args:
            endpoint (str): 端点
            data (鸭子类型(需要可以调用`.toDict()`方法), 可选): 需要传输的对象. Defaults to None.

        Raises:
            ValueError: 请求报错信息

        Returns:
            _type_: 请求结果.data的内容
        """
        url = f"{self.baseUrl}{endpoint}"
        try:
            if data != None:
                data = data.toDict() # 如果不是本库封装的类, 则没有这个; 则无法使用

            response = requests.post(url, headers=self.headers, json=data)
            response.encoding = 'utf-8'
            responseData = response.json()

            if responseData.get("code") != 10000:
                raise ValueError(responseData.get("message", "未知错误"))
            
            return responseData.get("data")
        except (requests.RequestException, json.JSONDecodeError) as e:
            raise ValueError(f"请求失败: {e}")

    def getLegends(self) -> list[Legend]:
        """获取图例列表

        Returns:
            list[Legend]: 图例列表
        """
        data = self._post("/force-graph-api/get-legend")
        return [mapToObject(item, Legend) for item in data]
    
    def getNodes(self) -> list[Node]:
        """获取结点列表

        Returns:
            list[Node]: 结点列表
        """
        data = self._post("/force-graph-api/get-nodes")
        return [mapToObject(item, Node) for item in data]
    
    def getEdges(self) -> list[Edge]:
        """获得边列表

        Returns:
            list[Edge]: 边列表
        """
        data = self._post("/force-graph-api/get-edges")
        return [mapToObject(item, Edge) for item in data]
    
    def addLegend(self, legend: Legend) -> int:
        """添加图例

        Args:
            legend (Legend): 图例对象

        Returns:
            int: 图例id
        """
        return self._post("/force-graph-api/add-legend", legend)
    
    def addNode(self, node: Node) -> int:
        """添加节点

        Args:
            node (Node): 节点

        Returns:
            int: 节点id
        """
        return self._post("/force-graph-api/add-node", node)
    
    def addEdge(self, edge: Edge) -> int:
        """添加边

        Args:
            edge (Edge): 边

        Returns:
            int: 边id
        """
        return self._post("/force-graph-api/add-edge", edge)
    
# } // === End === HXANiMe-py-class === End ===