<template>
    <h1>这里是 Info</h1>
    <div class="box">
        <div id="preview" v-on:paste="handlePaste">
            <span>将图片按Ctrl+V 粘贴至此处</span>
        </div>
        <el-button v-on:click="uploadPlans">上传文件</el-button>
    </div>

</template>

<script setup lang="ts">
import { ElButton, ElMessage } from 'element-plus';

// 监听粘贴操作
function handlePaste(event: any) {
    const items = (event.clipboardData /*|| window.clipboardData*/).items;
    let file = null;

    if (!items || items.length === 0) {
        ElMessage.error("当前浏览器不支持本地");
        return;
    }
    // 搜索剪切板items
    for (let i = 0; i < items.length; i++) {
        if (items[i].type.indexOf("image") !== -1) {
            file = items[i].getAsFile();
            break;
        }
    }
    if (!file) {
        ElMessage.error("粘贴内容非图片");
        return;
    }
    // 此时file就是我们的剪切板中的图片对象
    // 如果需要预览，可以执行下面代码
    const reader = new FileReader();
    reader.onload = event => {
        preview.innerHTML = `<img src="${event.target.result}">`;
    };
    ElMessage.error("111");
    reader.readAsDataURL(file);
    // file = file;
};

// 上传文件成功后回调
function uploadPlans() {
    ElMessage.error("图片上传完了");
    return;
    let file = this.file;
    if (!file) {
       ElMessage.error("请粘贴图片后上传");
        return;
    }
    this.loading = true;
    let form = new FormData();
    form.append("file", file);
    form.append("type", this.type);
    // uploadCertificate是封装的axios请求，自己根据需求传参
    // uploadCertificate(form)
    //     .then(data => {
    //         if (data.data && data.data.success) {
    //             this.certificate_pic = data.data.data.source;
    //            ElMessage.success(this.name + "上传成功！");
    //         } else {
    //            ElMessage.error(this.name + "上传失败！");
    //         }
    //     }).catch(() => { });
};

</script>