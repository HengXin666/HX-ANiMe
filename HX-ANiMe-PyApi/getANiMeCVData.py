import requests
import re

# 导入 HX-ANiMe-PyApi 包
from HXANiMeApi import *

UserAgent = { # 现代反爬, 不屑于从'User-Agent'判断, 所以有就可以了..
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36 Edg/131.0.0.0'
}

class ANiMeData:
    def __init__(self, jpName: str = "", cnName: str = "", cvName: str = "", imgSrc: str = ""):
        self.jpName = jpName
        self.cnName = cnName
        self.cvName = cvName
        self.imgSrc = imgSrc

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

    matches = pattern.findall(html)

    for match in matches:
        japanese_name, chinese_name, cv_name = match
        print(f"日文名: {japanese_name}, 中文名: {chinese_name}, CV: {cv_name}")

def getANiMeDataByCharactersSubjectHtml(html: str) -> list[ANiMeData]:
    """
    正则表达式解析 [Subject的Characters] HTML 获取番剧的角色名称、CV信息和角色图片 URL

    Args:
        html (str): 网页 HTML

    Returns:
        List[ANiMeData]: 包含角色信息和图片 URL 的数据列表
    """
    # 更新正则表达式，增加 img src 的匹配
    pattern = re.compile(
        r'<a[^>]*class="avatar"[^>]*><img src="([^"]+)"[^>]*></a>.*?'  # 匹配角色 img 的 src
        r'<h2><a href="[^"]*" class="l">([^<]+)</a> <span class="tip"> / ([^<]+)</span></h2>.*?'
        r'<p><a href="[^"]*" class="l">([^<]+)</a>',  # 匹配角色日文名、中文名和 CV
        re.DOTALL
    )
    matches = pattern.findall(html)

    res: list[ANiMeData] = []
    for match in matches:
        img_src, japanese_name, chinese_name, cv_name = match
        res.append(ANiMeData(japanese_name, chinese_name, cv_name, img_src))
    return res

def getANiMeTitleByCharactersSubjectHtml(html: str) -> tuple[str, str]:
    """正则表达式解析[Subject的Characters]HTML获取番剧名称, 和番剧图片

    Args:
        html (str): 网页HTML
    """
    # 正则表达式
    pattern = r'<a href="[^"]*" title="([^"]+)" class="avatar"><img src="([^"]+)"'

    # 匹配
    match = re.search(pattern, html)

    if match:
        title = match.group(1)
        src = re.sub(r'r/100x100/', '', match.group(2)) # 去掉 r/100x100/
        # print(f"title: {title}")
        # print(f"src: {src}")
        return title, src
    else:
        raise Exception("未找到匹配内容")

def main(ANiMeUrl: str, apiKey: str, idNums: list[int]) -> None:
    api = ANiMeApi(ANiMeUrl, apiKey)
    # 获取所有图例
    legendMap = LegendIdMap(api.getLegends())

    # 获取当前所有结点
    nodeMap = NodeIdMap(api.getNodes())

    # 先判断图例是否存在, 如果不存在则向后端添加
    while (legendMap.ensure(api, Legend(name="番剧", color="#FF0099"))
        | legendMap.ensure(api, Legend(name="声优", color="#99FF00"))
        | legendMap.ensure(api, Legend(name="角色", color="#0099FF"))):
        print("添加图例...")

    for num in idNums:
        # 爬取
        html = getCharactersSubject(num)

        # 解析 番剧名称
        title, titleImgUrl = getANiMeTitleByCharactersSubjectHtml(html)
        
        # 解析 角色 - CV 信息
        animeList = getANiMeDataByCharactersSubjectHtml(html)

        # 添加结点: 番剧信息结点; 并且获得结点id
        titleNodeId = api.addNode(Node(legendId=legendMap["番剧"].legendId, name=title, imgUrl=titleImgUrl))

        for it in animeList:
            # 添加CV结点
            try:
                cvNodeId = nodeMap.findByName(it.cvName).nodeId
            except:
                cvNodeId = nodeMap.ensure(api, Node(legendId=legendMap["声优"].legendId, name=it.cvName))
            # 添加角色结点
            roleNodeId = nodeMap.ensure(api, Node(legendId=legendMap["角色"].legendId, name=f"{it.cnName} ({it.jpName})", imgUrl=it.imgSrc))
            # 添加边: 角色 -> 番剧
            api.addEdge(Edge(fromNodeId=roleNodeId, toNodeId=titleNodeId))
            # 添加边: 声优 -> 角色
            api.addEdge(Edge(fromNodeId=cvNodeId, toNodeId=roleNodeId))


if __name__ == '__main__':
    # 用户图表 apiKey
    apiKey = "QSGl9EGGEhjdy59RBqEqXdXsmVFJoGhUIyp"
    # 测试: "sXfjEJOiySe2wlEychkn4KsTvB0dRzqSr5s"
    # 番剧: "QSGl9EGGEhjdy59RBqEqXdXsmVFJoGhUIyp"

    # 后端服务器URL
    ANiMeUrl = "http://localhost:28205"

    # [88287, 425998, 385209]
    main(ANiMeUrl, apiKey, [385209])