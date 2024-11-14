<template>
    <div class="login-container">
        <div class="slider">
            <div :class="active === 1 ? 'form' : 'form hidden'">
                <div class="title">欢迎<b>回来</b></div>
                <div class="subtitle">登录您的账户</div>
                <div class="input">
                    <input type="text" placeholder="用户名" v-model="loginData.userName" />
                    <span class="label">用户名</span>
                </div>
                <div class="input">
                    <input type="password" placeholder="密码" v-model="loginData.password" />
                    <span class="label">密码</span>
                </div>
                <el-button plain :disabled="!canLogin()" @click="submitLoginForm()">登录</el-button>
            </div>
            <div :class="active === 2 ? 'form' : 'form hidden'">
                <div class="title">开始</div>
                <div class="subtitle">创建您的账户</div>
                <div class="input">
                    <input type="text" placeholder="用户名" v-model="registerData.userName" />
                    <span class="label">用户名</span>
                    <div :class="userNameBlur() ? 'input-same' : 'input-no-same'">
                        用户名只能包含英文字母数字以及 _ 和 -
                    </div>
                </div>
                <div class="input">
                    <input type="text" placeholder="邮箱" v-model="registerData.email" />
                    <span class="label">邮箱</span>
                    <div :class="emailBlur() ? 'input-same' : 'input-no-same'">
                        请输入正确的邮箱
                    </div>
                </div>
                <div class="input">
                    <input type="password" placeholder="密码" v-model="registerData.password" />
                    <span class="label">密码</span>
                </div>
                <div class="input">
                    <input type="password" placeholder="确认密码" v-model="registerData._passwordReview" />
                    <span class="label">确认密码</span>
                    <div :class="passwordIsSame() ? 'input-same' : 'input-no-same'">
                        两次的密码不匹配
                    </div>
                </div>
                <el-button plain :disabled="!canRegister()" @click="submitRegisterForm()">注册</el-button>
            </div>
            <div :class="active === 1 ? 'card' : 'card active'">
                <div class="head">
                    <div class="name">HX<span>アニメ</span></div>
                </div>
                <div class="desc">
                    描述...描述...描述...描述...描述...描述...描述...描述...描述...描述...描述...描述...描述...描述...描述...描述...描述...描述...描述...描述...描述...描述...描述...描述...描述...描述...描述...描述...描述...描述...描述...描述...
                </div>
                <div class="btn" @click="active = (active === 1) ? 2 : 1">
                    {{ active === 1 ? '新用户?' : '已拥有账号' }}
                    <button>{{ active === 1 ? '去注册' : '去登录' }}</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
// import Verify from "@/components/verifition/Verify.vue";
// import Request from "@/apis/request";
import { ref, reactive } from "vue";
import { login, register } from "@/apis/login";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";

// 获取router对象
const $router = useRouter();

// 控制显示为登录还是注册
const active = ref(1);

// 定义登录数据对象
const loginData = reactive({
    userName: "",
    password: "",
});

// 定义注册数据对象
const registerData = reactive({
    userName: "",
    email: "",
    password: "",
    _passwordReview: "", // 只用于审查
});

// 判断是否两次密码相同
const passwordIsSame = () => {
    return registerData.password === registerData._passwordReview;
};

/**
 * @description 判断邮箱是否合法 (为空时候是合法的)
 * @return bool True 代表合法
 */
const emailBlur = () => {
    const verify = /^\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/;
    return registerData.email === "" || verify.test(registerData.email);
};

/**
 * 是否可以登录
 * @return bool True 可以登录
 */
const canLogin = () => loginData.userName !== ""
    && loginData.password !== "";

/**
 * 是否可以注册
 * @return bool True 可以注册
 */
const canRegister = () => registerData.userName !== ""
    && userNameBlur()
    && registerData.email !== ""
    && emailBlur()
    && registerData.password !== ""
    && passwordIsSame();

/**
 * 验证用户名是否合法
 */
const userNameBlur = () => {
    const verify = /^[A-Za-z0-9|_|-]+$/;
    return registerData.userName === "" || verify.test(registerData.userName);
};

/**
 * 执行登录
 * @param code 验证码字符串 (妹搞)
 */
const doLogin = (code) => {
    // 发送登录请求
    login(
        {
            ...loginData,
            // code: code,
        },
        () => {
            // 跳转到首页
            $router.push("/home");
            // 登录成功提示
            ElMessage.success("登录成功, 前往首页");
        },
        () => {
            // ElMessage.error("账号或密码错误");
        },
    );
};

/**
 * 执行注册
 * @param code 验证码字符串 (妹搞)
 */
const doRegister = (code) => {
    // 发送登录请求
    register(
        {
            userName: registerData.userName,
            email: registerData.email,
            password: registerData.password,
            // code: code,
        },
        () => {
            // 跳转到首页
            $router.push("/home");
            // 登录成功提示
            ElMessage.success("注册成功, 已登录, 正在前往首页...");
        },
        () => {
            // ElMessage.error("用户名已存在");
        },
    );
};

// 定义登录提交函数
const submitLoginForm = () => {
    // TODO[TEST_CODE]:测试直接进入主界面
    // ElMessage.success("登录成功, 前往首页");
    // $router.push('/home')

    // 弹出验证码框
    // useVerify('clickWord')

    // TODO[TEST_CODE]:测试直接登录
    doLogin("");
};

// 定义注册提交函数
const submitRegisterForm = () => {
    // 弹出验证码框
    // useVerify('clickWord')

    doRegister("");
};
</script>

<style lang="scss">
.login-container {
    width: 100%;
    min-height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    background: url("../assets/sliding-back.jpg") no-repeat center center;
    background-size: cover;

    .slider {
        position: relative;
        display: flex;
        justify-content: center;
        align-items: center;

        .form {
            width: 400px;
            height: 560px;
            background: rgba(17, 25, 40, 0.75);
            backdrop-filter: blur(16px) saturate(0);
            border-radius: 10px;
            border: 1px solid rgba(255, 255, 255, 0.15);
            padding: 40px 60px;
            box-shadow: rgba(50, 50, 93, 0.25) 50px 50px 100px -20px,
                rgba(0, 0, 0, 0.5) 30px 30px 60px -30px,
                rgba(212, 217, 222, 0.35) 2px -2px 6px 0px inset;
            display: flex;
            justify-content: center;
            flex-direction: column;
            align-items: flex-start;
            margin: 0 10px;
            transition: 0.5s ease-in-out;
            z-index: 3;

            &.hidden {
                height: 430px;
                box-shadow: none;
                z-index: 1;
            }

            .title {
                font-size: 24px;
                color: rgb(246, 240, 255);
                letter-spacing: 1px;
                font-weight: 300;
            }

            .subtitle {
                font-size: 14px;
                color: rgb(246, 240, 255);
                letter-spacing: 1px;
                margin-bottom: 35px;
            }

            .input {
                width: 100%;
                position: relative;
                margin-bottom: 35px;

                input {
                    width: 100%;
                    height: 35px;
                    border: none;
                    outline: 1.5px solid rgb(200, 200, 220);
                    background: transparent;
                    border-radius: 8px;
                    font-size: 16px;
                    padding: 0 15px;
                    color: rgb(246, 249, 255);

                    &::placeholder {
                        color: rgb(175, 180, 190);
                    }

                    &:focus {
                        outline: 1.5px solid rgb(224, 229, 240);

                        &::placeholder {
                            opacity: 0;
                        }

                        &+.label {
                            opacity: 1;
                            top: -20px;
                        }
                    }

                    &:not(:placeholder-shown)+.label {
                        opacity: 1;
                        top: -20px;
                    }
                }
            }

            // 获取输入焦点时候的悬浮字体
            .label {
                position: absolute;
                top: 0;
                left: 0;
                color: rgb(246, 249, 255);
                font-size: 12px;
                font-weight: bold;
                transition: 0.5s ease-out;
                opacity: 0;
            }

            .input-same {
                position: absolute;
                color: rgb(20, 114, 255);
                opacity: 0;
                right: 0;
                top: 40px;
                font-size: 10px;
                text-align: right;
                transition: 0.5s ease-in-out;
                white-space: nowrap;
            }

            .input-no-same {
                position: absolute;
                right: 0;
                top: 40px;
                color: rgb(233, 58, 58);
                font-weight: bold;
                font-size: 14px;
                text-align: right;
                transition: 0.5s ease-in-out;
                white-space: nowrap;
            }

            button {
                width: 100%;
                height: 35px;
                background-color: #2dcd3a32;
                color: #c1ff18ce;
                border-color: #1cff5c87;
                font-size: 18px;
                cursor: pointer;
                transition: 0.5s ease-in-out;
            }

            button:hover {
                background-color: #19fc7f73;
                color: #edff29;
                border-color: #da0aff87;
                transition: 0.5s ease-in-out;
            }

            button.is-disabled {
                background-color: #bb323273;
                color: #ecff18a4;
                border-color: #fffb1c87;
                cursor: not-allowed; // 鼠标样式
            }
        }

        .card {
            position: absolute;
            right: 0;
            top: 50%;
            transform: translate(0, -50%);
            width: 430px;
            height: 430px;
            color: red;
            background: url("../assets/sliding-form.png") white;
            background-size: 430px 430px;
            padding: 35px;
            border-radius: 0 10px 10px 0;
            transition: 0.5s ease-in-out;
            z-index: 2;

            &.active {
                right: calc(100% - 430px);
                border-radius: 10px 0 0 10px;
            }

            .head {
                font-size: 34px;
                margin-bottom: 35px;

                .name {
                    font-weight: 300;

                    span {
                        color: rgb(36, 217, 127);
                        font-weight: bold;
                    }
                }
            }

            .desc {
                font-size: 16px;
                text-align: justify;
                margin-bottom: 35px;
            }

            .btn {
                font-size: 16px;
                color: #5777C6;

                button {
                    background: rgb(36, 217, 127);
                    font-size: 14px;
                    padding: 5px 15px;
                    border: none;
                    outline: none;
                    border-radius: 5px;
                    cursor: pointer;
                    margin-left: 10px;
                }
            }
        }
    }
}
</style>