import requests, json

from .DTO import *

# === Begin === HXANiMe-py-class === Begin === {

def mapToObject(data, cls):
    """将字典数据映射到指定类的实例中"""
    obj = cls()
    for key, value in data.items():
        if hasattr(obj, key):
            setattr(obj, key, value)
    return obj

class ANiMeApi:
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