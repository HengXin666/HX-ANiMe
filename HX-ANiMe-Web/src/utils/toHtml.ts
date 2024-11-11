export const toHtml = (htmlString: string) => {
    const container = document.getElementById('container');
    if (container) {
      container.innerHTML = htmlString; // 直接插入 HTML 字符串
    }
    return container;
}