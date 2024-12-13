import requests
import json
import re

UserAgent = { # 现代反爬, 不屑于从'User-Agent'判断, 所以有就可以了..
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36 Edg/131.0.0.0'
}

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
    def __init__(self, baseUrl: str, headers: map):
        """后端 API 封装

        Args:
            baseUrl (str): 后端域名 (如: http://localhost:28205)
            headers (map): 请求头哈希表
        """
        self.baseUrl = baseUrl
        self.headers = headers

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

class ANiMeData:
    def __init__(self, jpName: str = "", cnName: str = "", cvName: str = ""):
        self.jpName = jpName
        self.cnName = cnName
        self.cvName = cvName

def getSubject(subjectId: int):
    """获取主题

    Args:
        subjectId (int): 主题id
    """
    url = f"https://bangumi.tv/subject/{subjectId}"
    res = requests.get(url=url, headers=UserAgent)
    res.encoding = 'utf-8'  # 确保正确解码
    str = res.text  # 使用 .text 获取解码后的 HTML 内容
    print(str)
    with open(f'./{subjectId}.html', 'a+', encoding='utf-8') as f:
        f.write(str)

def getCharactersSubject(subjectId: int):
    """获取主题角色声优信息

    Args:
        subjectId (int): 主题id
    """
    url = f"https://bangumi.tv/subject/{subjectId}/characters"
    res = requests.get(url=url, headers=UserAgent)
    res.encoding = 'utf-8'  # 确保正确解码
    # 使用 .text 获取解码后的 HTML 内容
    return res.text

def getANiMeDataBySubjectHtml(html: str):
    """正则表达式解析[Subject]HTML获取番剧的角色名称、CV信息

    Args:
        html (str): 网页HTML
    """
    # 定义正则表达式，使用 re.DOTALL 处理跨行内容
    pattern = re.compile(
        r'<a href="/character/\d+" title=".*? / (.*?)" class="avatar l">.*?'
        r'<span class="tip">(.*?)</span><br />\s*CV:\s*<a href="/person/\d+" rel="v:starring">(.*?)</a>',
        re.DOTALL
    )

    # 匹配内容
    matches = pattern.findall(html)

    # 输出结果
    for match in matches:
        japanese_name, chinese_name, cv_name = match
        print(f"日文名: {japanese_name}, 中文名: {chinese_name}, CV: {cv_name}")

def getANiMeDataByCharactersSubjectHtml(html: str) -> list[ANiMeData]:
    """正则表达式解析[Subject的Characters]HTML获取番剧的角色名称、CV信息

    Args:
        html (str): 网页HTML
    """
    pattern = re.compile(
        r'<h2><a href="[^"]*" class="l">([^<]+)</a> <span class="tip"> / ([^<]+)</span></h2>.*?<p><a href="[^"]*" class="l">([^<]+)</a>',
        re.DOTALL
    )
    matches = pattern.findall(html)

    res:list[ANiMeData] = []
    for match in matches:
        japanese_name, chinese_name, cv_name = match
        res.append(ANiMeData(japanese_name, chinese_name, cv_name))
    return res

def getANiMeTitleByCharactersSubjectHtml(html: str):
    """正则表达式解析[Subject的Characters]HTML获取番剧名称

    Args:
        html (str): 网页HTML
    """
    pattern = re.compile(
        r'<title>(.*?)的角色介绍</title>',
        re.DOTALL
    )
    return pattern.findall(html)[0]

def main(ANiMeUrl: str, apiKey: str, idNums: list[int]) -> None:
    HXANiMeHeaders = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36 Edg/131.0.0.0',
        "apiKey": apiKey,
    }
    api = HX_ANiMe_Api(ANiMeUrl, HXANiMeHeaders)
    # 获取图例
    legendMap = LegendIdMap(api.getLegends())

    # 先判断图例是否存在, 如果不存在则向后端添加
    if (legendMap.ensure(api, Legend(name="番剧", color="#FF0099"))
        | legendMap.ensure(api, Legend(name="声优", color="#99FF00"))
        | legendMap.ensure(api, Legend(name="角色", color="#0099FF"))):
        # 有添加图例, 所以更新 legendMap
        legendMap = LegendIdMap(api.getLegends())
    
    # 爬取需要的内容, 然后存入这里
    # nodeList: list[Node] = []
    # edgeList: list[Edge] = []

    for num in idNums:
        # 爬取
        html = getCharactersSubject(num)

        # 解析 番剧名称
        title = getANiMeTitleByCharactersSubjectHtml(html)
        
        # 解析 角色 - CV 信息
        animeList = getANiMeDataByCharactersSubjectHtml(html)

        # 添加结点: 番剧信息结点; 并且获得结点id
        titleNodeId = api.addNode(Node(legendId=legendMap["番剧"].legendId, name=title))

        for it in animeList:
            # 添加CV结点
            cvNodeId = api.addNode(Node(legendId=legendMap["声优"].legendId, name=it.cvName))
            # 添加角色结点
            roleNodeId = api.addNode(Node(legendId=legendMap["角色"].legendId, name=f"{it.cnName} ({it.jpName})"))
            # 添加边: 番剧 -> 角色
            api.addEdge(Edge(fromNodeId=titleNodeId, toNodeId=roleNodeId))
            # 添加边: 声优 -> 角色
            api.addEdge(Edge(fromNodeId=cvNodeId, toNodeId=roleNodeId))




if __name__ == '__main__':
    # 用户图表 apiKey
    apiKey = "sXfjEJOiySe2wlEychkn4KsTvB0dRzqSr5s"
    # 测试: "sXfjEJOiySe2wlEychkn4KsTvB0dRzqSr5s"
    # 番剧: "TSEf3cbXP8t1XxQXEMQInUIOpQNVhhjM4h"

    # 后端服务器URL
    ANiMeUrl = "http://localhost:28205"

    HXANiMeHeaders = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36 Edg/131.0.0.0',
        "apiKey": apiKey,
    }

    # [88287, 425998]
    main(ANiMeUrl, apiKey, [])

    api = HX_ANiMe_Api(ANiMeUrl, HXANiMeHeaders)
    exit()
    
    map = LegendIdMap([])

    print(map["番剧"])
    
    try:
        legends = api.getLegends()
        for legend in legends:
            print(legend)

        for it in api.getNodes():
            print(it)

        for it in api.getEdges():
            print(it)

        """ 示例: 添加图例, 输出图例id
        legend = Legend()
        legend.legendName = "计算机书籍"
        legend.legendColor = "#990099"

        print(api.addLegend(legend))
        """
    except ValueError as e:
        print(f"错误: {e}")