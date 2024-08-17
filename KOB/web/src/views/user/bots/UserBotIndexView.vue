<template>
    <!-- container 可以动态调整范围 -->
    <div class="container">
        <div class="row">
            <div class="col-3">
                <div class="card photo_card">
                    <div class="crad-body">
                        <!-- :src= ""表示里面是表达式 -->
                        <img :src="$store.state.user.photo" alt="">
                    </div>
                </div>
            </div>

            <div class="col-9">
                <div class="card" style="margin-top: 20px;">
                    <div class="card-header" style="box-sizing: border-box;">
                        <span style="font-size: 130%; line-height: 37.6px;">我的Bot</span>
                        <button type="button" class="btn btn-primary float-end" data-bs-toggle="modal" data-bs-target="#add-bot-btn">
                            创建Bot
                        </button>
                        
                        <!-- Modal -->
                        <div class="modal fade" id="add-bot-btn" tabindex="-1">
                        <div class="modal-dialog modal-xl">
                            <div class="modal-content">
                            <div class="modal-header">
                                <h1 class="modal-title fs-5">创建Bot</h1>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                <div class="mb-3">
                                    <label for="add-bot-title" class="form-label">名称</label>
                                    <input v-model="botadd.title" type="text" class="form-control" id="add-bot-title" placeholder="请输入bot名称">
                                </div>
                                <div class="mb-3">
                                    <label for="add-bot-description" class="form-label">简介</label>
                                    <textarea v-model="botadd.description" class="form-control" id="add-bot-description" rows="3" placeholder="请输入bot简介"></textarea>
                                </div>
                                <div class="mb-3">
                                    <label for="add-bot-code" class="form-label">代码</label>
                                    <!-- 集成代码编辑器 -->
                                    <VAceEditor v-model:value="botadd.content" @init="editorInit" lang="c_cpp"
                                        theme="textmate" style="height: 300px" :options="{
                                            enableBasicAutocompletion: true, //启用基本自动完成
                                            enableSnippets: true, // 启用代码段
                                            enableLiveAutocompletion: true, // 启用实时自动完成
                                            fontSize: 18, //设置字号
                                            tabSize: 4, // 标签大小
                                            showPrintMargin: false, //去除编辑器里的竖线
                                            highlightActiveLine: true,
                                        }" />
                                </div>
                            </div>
                            <div class="modal-footer">
                                <div class="error-message">{{ botadd.error_message }}</div>
                                <button type="button" class="btn btn-primary" @click="add_bot">创建</button>
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                            </div>
                            </div>
                        </div>
                        </div>
                    </div>

                    <div class="card-body">
                        <table class="table table-striped table-hover">
                            <thead>
                                <tr>
                                    <th>名称</th>
                                    <th>创建时间</th>
                                    <th>操作</th>
                                </tr>
                            </thead>
                            
                            <tbody>
                                <tr v-for="bot in bots" :key="bot.id">
                                    <td>{{ bot.title }}</td>
                                    <td>{{ bot.createtime }}</td>
                                    <td>
                                        <button type="button" class="btn btn-secondary" style="margin-right: 10px" data-bs-toggle="modal" :data-bs-target="'#update-bot-modal-' + bot.id">修改</button>
                                        <button type="button" class="btn btn-danger" @click="remove_bot(bot)">删除</button>

                                        <div class="modal fade" :id="'update-bot-modal-' + bot.id" tabindex="-1">
                                        <div class="modal-dialog modal-xl">
                                            <div class="modal-content">
                                            <div class="modal-header">
                                                <h1 class="modal-title fs-5">修改Bot</h1>
                                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                            </div>
                                            <div class="modal-body">
                                                <div class="mb-3">
                                                    <label for="add-bot-title" class="form-label">名称</label>
                                                    <input v-model="bot.title" type="text" class="form-control" id="add-bot-title" placeholder="请输入bot名称">
                                                </div>
                                                <div class="mb-3">
                                                    <label for="add-bot-description" class="form-label">简介</label>
                                                    <textarea v-model="bot.description" class="form-control" id="add-bot-description" rows="3" placeholder="请输入bot简介"></textarea>
                                                </div>
                                                <div class="mb-3">
                                                    <label for="add-bot-code" class="form-label">代码</label>
                                                    <!-- 集成代码编辑器 -->
                                                    <VAceEditor v-model:value="bot.content" @init="editorInit"
                                                        lang="c_cpp" theme="textmate" style="height: 300px"
                                                        :options="{
                                                            enableBasicAutocompletion: true, //启用基本自动完成
                                                            enableSnippets: true, // 启用代码段
                                                            enableLiveAutocompletion: true, // 启用实时自动完成
                                                            fontSize: 18, //设置字号
                                                            tabSize: 4, // 标签大小
                                                            showPrintMargin: false, //去除编辑器里的竖线
                                                            highlightActiveLine: true,
                                                        }" />
                                                </div>
                                            </div>
                                            <div class="modal-footer">
                                                <div class="error-message">{{ bot.error_message }}</div>
                                                <button type="button" class="btn btn-primary" @click="update_bot(bot)">保存修改</button>
                                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" @click="refresh_bots">取消</button>
                                            </div>
                                            </div>
                                        </div>
                                        </div>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { Modal } from 'bootstrap/dist/js/bootstrap';
import $ from 'jquery';
import { reactive, ref } from 'vue';
import { useStore } from 'vuex';


// 集成编辑器依赖 
import { VAceEditor } from 'vue3-ace-editor';
import ace from 'ace-builds';
import 'ace-builds/src-noconflict/mode-c_cpp';
import 'ace-builds/src-noconflict/mode-json';
import 'ace-builds/src-noconflict/theme-chrome';
import 'ace-builds/src-noconflict/ext-language_tools';

export default {
    components: {
        VAceEditor
    },

    setup() {

        // 集成代码编辑器
        ace.config.set(
            "basePath",
            "https://cdn.jsdelivr.net/npm/ace-builds@" +
            require("ace-builds").version +
            "/src-noconflict/")


        const store = useStore();
        let bots = ref([]);

        // 绑定变量用ref 绑定对象用reactive
        const botadd = reactive({
            title: "",
            description: "",
            content: "",
            error_message: "",
        });

        const refresh_bots = () => {
            $.ajax({
                url: "http://localhost:3000/user/bot/getlist/",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    bots.value = resp;
                }
            });
        }

        // 执行一遍
        refresh_bots();

        const add_bot = () => {
            botadd.error_message = "";
            $.ajax({
                url: "http://localhost:3000/user/bot/add/",
                type: "post",
                data: {
                    title: botadd.title,
                    description: botadd.description,
                    content: botadd.content,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        botadd.title = "";
                        botadd.description = "";
                        botadd.content = "";
                        // 关闭浮悬窗
                        const modalInstance = Modal.getInstance('#add-bot-btn');
                        if (modalInstance) {
                        modalInstance.hide();
                        }
                        refresh_bots();
                    } else {
                        botadd.error_message = resp.error_message;
                    }
                },
                error() {
                    botadd.error_message = "系统错误请联系管理员";
                } 
            });
        }

        const remove_bot = (bot) => {
            $.ajax({
                url: "http://localhost:3000/user/bot/remove/",
                type: "post",
                data: {
                    bot_id: bot.id,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        refresh_bots();
                    }
                }
            })
        }

        const update_bot = (bot) => {
            botadd.error_message = "";
            $.ajax({
                url: "http://localhost:3000/user/bot/update/",
                type: "post",
                data: {
                    bot_id: bot.id,
                    title: bot.title,
                    description: bot.description,
                    content: bot.content,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        // 关闭浮悬窗
                        const modalInstance = Modal.getInstance('#update-bot-modal-' + bot.id);
                        if (modalInstance) {
                        modalInstance.hide();
                        }
                        refresh_bots();
                    } else {
                        bot.error_message = resp.error_message;
                    }
                },
                error() {
                    botadd.error_message = "系统错误请联系管理员";
                } 
            });
        }

        return{
            bots,
            botadd,
            add_bot,
            remove_bot,
            update_bot,
            refresh_bots,
        }
    }
}
</script>

<style scoped>
div.error-message {
    color: red;
}

.table thead + tbody {
    border-top: 2px solid #000; /* 加粗线宽2px，颜色为黑色 */
}

.card {
    background-color: rgba(255, 255, 255, 0.5); /* 半透明白色 */
}

.photo_card {
    margin-top: 20px; width: 200px; height: 200px; float: right;
}


.crad-body>img {
    width: 100%;
}
</style>