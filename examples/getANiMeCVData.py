import requests
import json
import re

UserAgent = { # 现代反爬, 不屑于从'User-Agent'判断, 所以有就可以了..
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36 Edg/131.0.0.0'
}

mySecretApiKey = ""

# === Begin === HXANiMe-py-class === Begin === {
class Legend:
    """图例"""
    def __init__(self):
        self.legendId = 0     # 图例id
        self.legendName = ""  # 图例名称
        self.legendColor = "" # 图例颜色

    def __repr__(self):
        return f"Legend(legendId={self.legendId}, legendName='{self.legendName}', legendColor='{self.legendColor}')"

class Node:
    """节点"""
    def __init__(self):
        self.nodeId = 0       # 节点id
        self.legendId = 0     # 所属图例id
        self.name = ""        # 节点名称
        self.imgUrl = ""      # 节点图片
        self.description = "" # 节点描述

    def __repr__(self):
        return (f"Node(nodeId={self.nodeId}, legendId={self.legendId}, "
                f"name='{self.name}', imgUrl='{self.imgUrl}', description='{self.description}')")

class Edge:
    """边"""
    def __init__(self):
        self.edgeId = 0     # 边id
        self.fromNodeId = 0 # 源节点id
        self.toNodeId = 0   # 目标节点id
    
    def __repr__(self):
        return (f"Edge(edgeId={self.edgeId}, fromNodeId={self.fromNodeId}, toNodeId='{self.toNodeId}')")
    
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

    def _post(self, endpoint):
        """通用 POST 方法"""
        url = f"{self.baseUrl}{endpoint}"
        try:
            response = requests.post(url, headers=self.headers)
            response.encoding = 'utf-8'
            data = response.json()
            if data.get("code") != 10000:
                raise ValueError(data.get("message", "未知错误"))
            return data.get("data")
        except (requests.RequestException, json.JSONDecodeError) as e:
            raise ValueError(f"请求失败: {e}")

    def getLegend(self) -> list[Legend]:
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
    
# } // === End === HXANiMe-py-class === End ===


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
    str = res.text  # 使用 .text 获取解码后的 HTML 内容
    print(str)
    with open(f'./{subjectId}characters.html', 'a+', encoding='utf-8') as f:
        f.write(str)

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

def getANiMeDataByCharactersSubjectHtml(html: str):
    """正则表达式解析[Subject的Characters]HTML获取番剧的角色名称、CV信息

    Args:
        html (str): 网页HTML
    """
    pattern = re.compile(
        r'<h2><a href="[^"]*" class="l">([^<]+)</a> <span class="tip"> / ([^<]+)</span></h2>.*?<p><a href="[^"]*" class="l">([^<]+)</a>',
        re.DOTALL
    )

    # 匹配内容
    matches = pattern.findall(html)

    # 输出结果
    for match in matches:
        japanese_name, chinese_name, cv_name = match
        print(f"日文名: {japanese_name}, 中文名: {chinese_name}, CV: {cv_name}")

if __name__ == '__main__':
    # 用户图表 apiKey
    apiKey = "A02bKH0tkfh2SIRwTLpv9s0woVYN7KYfae"

    # 后端服务器URL
    ANiMeUrl = "http://localhost:28205"

    HXANiMeHeaders = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36 Edg/131.0.0.0',
        "apiKey": apiKey
    }

    api = HX_ANiMe_Api(ANiMeUrl, HXANiMeHeaders)

    try:
        legends = api.getLegend()
        for legend in legends:
            print(legend)

        for it in api.getNodes():
            print(it)

        for it in api.getEdges():
            print(it)
    except ValueError as e:
        print(f"错误: {e}")