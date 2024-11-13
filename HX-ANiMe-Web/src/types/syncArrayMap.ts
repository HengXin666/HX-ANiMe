type T = { id: number; [key: string]: any };

/**
 * 关联MapList和Map-id,
 * 传入一个`字典数组`, 其字典包含`id`键(number),
 * 类内部维护一个字典, 使其 id --映射--> 字典引用,
 * 方便O(1)的通过id查找元素
 */
export class SyncArrayMap {
    // 原数据
    private _mapList: T[] = [];

    // id-Map映射
    private _dataMap: { [key: string]: T } = {};

    constructor(initialData: T[] = []) {
        initialData.forEach((item) => this.push(item));
    }

    // 获取字典数组的只读副本
    public getMapList(): Readonly<T[]> {
        return this._mapList;
    }

    // 添加数据
    public push(item: T): void {
        if (!item.id) {
            throw new Error("Item must have an 'id' property.");
        }
        this._mapList.push(item);
        this._dataMap[item.id] = item;
    }

    // 更新数据
    public updateItem(id: string, newData: Partial<T>): void {
        const item = this._dataMap[id];
        if (item) {
            Object.assign(item, newData);
        } else {
            throw new Error("Item with the given ID does not exist.");
        }
    }

    // 删除数据
    public removeItem(id: number): void {
        const index = this._mapList.findIndex((item) => item.id === id);
        if (index !== -1) {
            this._mapList.splice(index, 1);
            delete this._dataMap[id];
        } else {
            throw new Error("Item with the given ID does not exist.");
        }
    }

    // 通过字典列表的索引删除数据 O(1)
    public removeItemByIndex(index: number, id: number): void {
        const item = this._mapList[index];
    
        if (item && item.id === id) {
            // 删除元素
            this._mapList.splice(index, 1);
            delete this._dataMap[id];
        } else {
            throw new Error("No item found at the given index or ID mismatch.");
        }
    }

    // 通过id查找元素, 如果查找不到则返回`null`
    public getItemById(id: number): T | null {
        return id in this._dataMap ? this._dataMap[id] : null;
    }

    // 获取dataMap中的引用
    public _getDataMap(): Readonly<{ [key: string]: T }> {
        return this._dataMap;
    }
}