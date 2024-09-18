<template>
  <div class="matchground">
    <div class="row">
      <div class="col-4">
        <div class="user-photo">
          <img :src="$store.state.user.photo" alt="" />
        </div>
        <div class="user-username">
          {{ $store.state.user.username }}
        </div>
      </div>
      <div class="col-4">
        <div class="user-select-bot">
          <select
            v-model="select_bot"
            class="form-select"
            aria-label="Default select example"
          >
            <option value="-1" selected>亲自出马</option>
            <option v-for="bot in bots" :key="bot.id" :value="bot.id">
              {{ bot.title }}
            </option>
          </select>
        </div>
      </div>
      <div class="col-4">
        <div class="user-photo">
          <img :src="$store.state.pk.opponent_photo" alt="" />
        </div>

        <div class="user-username">
          {{ $store.state.pk.opponent_username }}
        </div>
      </div>
    </div>
    
    <div class="col-12 match-time" v-if="match_time !== 0" >
      {{ match_time }}
    </div>

    <div class="col-12" style="text-align: center; padding-top: 13vh">
      <button
        @click="click_match_btn"
        type="button"
        class="btn btn-info btn-lg"
      >
        {{ match_btn_info }}
      </button>
    </div>
  </div>
</template>

<script>
import { ref } from "vue";
import { useStore } from "vuex";
import $ from "jquery";

export default {
  name: "MatchGround",
  setup() {
    const store = useStore();
    let match_btn_info = ref("开始匹配");
    let bots = ref("");
    let select_bot = ref("-1");
    let match_time = ref(0);
    let interval_id = null; // 用来存储setInterval的返回值

    const start_match_time = () => {
      interval_id = setInterval(() => {
      match_time.value += 1;
      }, 1000);
    };

    const click_match_btn = () => {
      if (match_btn_info.value === "开始匹配") {
        match_btn_info.value = "取消匹配";
        // 开始匹配的话向后端发送一个请求 send API可以向后端通信
        store.state.pk.socket.send(
          JSON.stringify({
            // stringify将js对象转换为字符串
            // 表示开始匹配
            event: "start-matching",
            bot_id: select_bot.value,
          })
        );
        start_match_time();
      } else {
        match_btn_info.value = "开始匹配";
        // 取消匹配的话发送请求
        store.state.pk.socket.send(
          JSON.stringify({
            event: "stop-matching",
          })
        );
        clearInterval(interval_id);
        match_time.value = 0; // 重置match_time
      }
    };

    const refresh_bots = () => {
      $.ajax({
        url: "https://kancen.fun/api/user/bot/getlist/",
        type: "get",
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        success(resp) {
          bots.value = resp;
        },
      });
    };

    // 当组件挂载的时候，调用refresh_bots函数，从云端获取bot
    refresh_bots();

    return {
      match_btn_info,
      click_match_btn,
      bots,
      select_bot,
      match_time,
    };
  },
};
</script>

<style scoped>
div.matchground {
  width: 60vw;
  height: 70vh;
  margin: 40px auto;
  background-color: rgba(50, 50, 50, 0.5);
}

div.user-photo {
  text-align: center;
  padding-top: 10vh;
}

div.user-photo > img {
  border-radius: 50%;
  width: 20vh;
}

div.user-username {
  text-align: center;
  font-size: 24px;
  font-weight: 600;
  color: white;
  padding-top: 2vh;
}

div.user-select-bot {
  padding-top: 20vh;
}

div.user-select-bot {
  width: 60%;
  margin: auto;
}

.match-time {
  color: white;
  font-size: 40px;
  text-align: center;
  padding-top: 10vh;
  font-weight: 600;
  font-family: "Microsoft YaHei";
  color: #fff;
  text-shadow: 0 0 10px #fff;
  -webkit-text-stroke: 1px #000;
  -webkit-text-fill-color: transparent;
  position: absolute;
  top: 37vh;
  left: 0vw;
}
</style>
