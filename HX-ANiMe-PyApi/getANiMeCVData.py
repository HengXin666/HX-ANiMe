import requests
import re

# 导入 HX-ANiMe-PyApi 包
from HXANiMeApi.api import *

UserAgent = { # 现代反爬, 不屑于从'User-Agent'判断, 所以有就可以了..
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36 Edg/131.0.0.0'
}

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

    # [88287, 425998]
    main(ANiMeUrl, apiKey, [])

    api = HX_ANiMe_Api(ANiMeUrl, apiKey)
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
    except ValueError as e:
        print(f"错误: {e}")