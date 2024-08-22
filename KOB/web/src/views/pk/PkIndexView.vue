<template>
  <PlayGround v-if="$store.state.pk.status === 'playing'"></PlayGround>
  <MatchGround v-if="$store.state.pk.status === 'matching'"></MatchGround>
  <ResultBoard v-if="$store.state.pk.loser !== 'none'"></ResultBoard>
</template>

<script>
import MatchGround from "@/components/MatchGround.vue";
import PlayGround from "@/components/PlayGround.vue";
import ResultBoard from "@/components/ResultBoard.vue";
import { onMounted, onUnmounted } from "vue";
import { useStore } from "vuex";

export default {
  name: "PkIndexView",
  components: {
    PlayGround,
    MatchGround,
    ResultBoard,
  },
  setup() {
    const store = useStore();
    const socketUrl = `ws://localhost:3000/websocket/${store.state.user.token}/`;

    store.commit("updateLoser", "none");
    store.commit("updateIsRecord", false);


    let socket = null;
    // 当组件被挂载创建链接
    onMounted(() => {
      // 设置默认匹配页面
      store.commit("updateOpponent", {
        username: "我的对手",
        photo: require("@/assets/images/pk.png"),
      });

      socket = new WebSocket(socketUrl);

      // 打开组件时触发
      socket.onopen = () => {
        console.log("connected!");
        store.commit("updateSocket", socket);
      };

      // 收到信息时调用
      socket.onmessage = (msg) => {
        // Spring是将数据放入data中
        const data = JSON.parse(msg.data);
        // 接受到匹配成功信息后
        if (data.event === "start-matching") {
          // 匹配成功
          store.commit("updateOpponent", {
            username: data.opponent_username,
            photo: data.opponent_photo,
          });
          setTimeout(() => {
            // 匹配成功后跳转到游戏页面延迟1秒
            store.commit("updateStatus", "playing");
          }, 500); // game.start要sleep几秒钟不然会吞代码
          // 接受信息后更新地图
          store.commit("updateGame", data.game);
        } else if (data.event === "move") {
          console.log(data);

          const game = store.state.pk.gameObject;
          const [snake0, snake1] = game.snakes;
          snake0.set_direction(data.a_direction);
          snake1.set_direction(data.b_direction);
        } else if (data.event === "result") {
          console.log(data);

          const game = store.state.pk.gameObject;
          const [snake0, snake1] = game.snakes;

          if (data.loser === "all" || data.loser === "A") {
            snake0.status = "die";
          }

          if (data.loser === "all" || data.loser === "B") {
            snake1.status = "die";
          }
          store.commit("updateLoser", data.loser);
        }
      };

      // 关闭时触发
      socket.onclose = () => {
        console.log("disconnected!");
        store.commit("updateStatus", "matching");
      };
    });

    // 当当前界面被卸载
    onUnmounted(() => {
      socket.close();
    });
  },
};
</script>

<style scoped></style>
