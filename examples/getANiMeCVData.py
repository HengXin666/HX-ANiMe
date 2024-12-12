import requests
import re

UserAgent = { # 现代反爬, 不屑于从'User-Agent'判断, 所以有就可以了..
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36 Edg/131.0.0.0'
}

"""
密钥设计:

主键 - 用户id - 图表id - 密匙md5 - 创建时间 - 最近修改时间
"""
mySecretApiKey = ""

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
    with open(f'./425998characters.html', 'r', encoding='utf-8') as f:
        getANiMeDataByCharactersSubjectHtml(f.read())
    pass