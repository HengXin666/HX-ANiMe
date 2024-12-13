import json

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