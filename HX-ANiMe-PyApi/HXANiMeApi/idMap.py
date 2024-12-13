from typing import TypeVar, Generic

from .DTO import *
from .api import ANiMeApi

# 定义泛型变量
T = TypeVar("T")
class IdMap(Generic[T]):
    def __init__(self):
        self.idMap: dict[int, T] = {}   # id -> 对象
        self.nameMap: dict[str, T] = {} # name -> 对象

    def findByName(self, name: str) -> T:
        """通过name查找

        Args:
            name (str): 名称

        Returns:
            T: 对象 | (查找不到: 抛异常)
        """
        return self.nameMap[name]

    def __getitem__(self, name: str) -> T:
        """重载`[]`运算符, 以`name`作为键查找

        Args:
            name (str): 名称

        Returns:
            T: 对象
        """
        return self.findByName(name)

    def findById(self, id: int) -> T:
        """通过id查找

        Args:
            id (int): ID

        Returns:
            T: 对象 | (查找不到: 抛异常)
        """
        return self.idMap[id]


class LegendIdMap(IdMap[Legend]):
    """图例映射, 提供 id -> 图例, name -> 图例
    """
    def __init__(self, legendList: list[Legend]):
        """图例映射构造

        Args:
            legendList (list[Legend]): 图例列表
        """
        super().__init__()
        for it in legendList:
            self.idMap[it.legendId] = it
            self.nameMap[it.legendName] = it 

    def add(self, legend: Legend) -> None:
        """添加图例

        Args:
            legend (Legend): 图例
        """
        self.idMap[legend.legendId] = legend
        self.nameMap[legend.legendName] = legend

    def ensure(self, api: ANiMeApi, legend: Legend) -> int:
        """确保该图例存在, 如果不存在则添加(先后端, 再往map添加)

        Args:
            api (ANiMeApi): api类, 需要提供`addLegend`方法
            legend (Legend): 图例

        Returns:
            int: `0`表示成功, `> 0`表示返回图例的id
        """
        try:
            self.findByName(legend.legendName)
            return 0
        except KeyError:
            legend.legendId  = api.addLegend(legend)
            self.add(legend)
            return legend.legendId
        
class NodeIdMap(IdMap[Node]):
    """结点映射, 提供 id -> 结点, name -> 结点
    """
    def __init__(self, nodeList: list[Node]):
        """结点映射构造

        Args:
            nodeList (list[Legend]): 结点列表
        """
        super().__init__()
        for it in nodeList:
            self.idMap[it.nodeId] = it
            self.nameMap[it.name] = it

    def add(self, node: Node) -> None:
        """添加图例

        Args:
            legend (Legend): 图例
        """
        self.idMap[node.nodeId] = node
        self.nameMap[node.name] = node

    def ensure(self, api: ANiMeApi, node: Node) -> int:
        """确保该结点存在, 如果不存在则添加(先后端, 再往map添加)

        Args:
            api (ANiMeApi): api类, 需要提供`addNode`方法
            node (Node): 结点

        Returns:
            int: `0`表示成功, `> 0`表示返回结点的id
        """
        try:
            self.findByName(node.name)
            return 0
        except KeyError:
            node.nodeId = api.addNode(node)
            self.add(node)
            return node.nodeId
