import $ from 'jquery';

export default {
    state: {
        id: "",
        username: "",
        photo: "",
        token: "",
        is_login: false,
        // 表示是不是在获取信息中
        pulling_info: true,
    },
    getters: {
    },
    mutations: {
        // 更改state时候需要使用
        updateUser(state, user) {
            state.id = user.id;
            state.username = user.username;
            state.photo = user.photo;
            state.is_login = user.is_login;
        },
        updateToken(state, token) {
            state.token = token;
        },
        logout(state) {
            state.id= "",
            state.username= "",
            state.photo= "",
            // 令牌
            state.token= "",
            // 是否登入
            state.is_login= false;
        },
        updatePullingInfo(state, pulling_info) {
            state.pulling_info = pulling_info;
        }
    },
    actions: {
        login(context, data) {
            // 报错405 方法错误
            $.ajax({
                url: "http://localhost:3000/api/user/account/token/",
                type: "post",
                data: {
                username: data.username,
                password: data.password,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        // 登入成功后信息存入浏览器中 登入常态化
                        localStorage.setItem("jwt_token", resp.token)
                        context.commit("updateToken", resp.token);
                        data.success(resp);
                    } else {
                        data.error(resp);
                    }
                },
                error(resp) {
                    data.error(resp);
                }
            });
        },

        getinfo(context, data) {
            $.ajax({
            url: "http://localhost:3000/api/user/account/info/",
            type: "get",
            // 不被公开要使用表头授权
            headers: {
                // 传到这 授权 取出token
                Authorization: "Bearer " + context.state.token,
            },
            success(resp) {
                // 判断成功的话
                if (resp.error_message === "success") {
                    context.commit("updateUser", {
                        ...resp,
                        is_login: true,
                    });
                    data.success(resp);
                } else {
                    data.error(resp);
                }
            },
            error(resp) {
                data.error(resp);
            }
            });
        },

        logout(context) {
            // 退出的话把常态化内容删除
            localStorage.removeItem("jwt_token");
            context.commit("logout");
        }
    },
    modules: {
    }
}