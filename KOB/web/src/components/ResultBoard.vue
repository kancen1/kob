<template>
  <div class="result-borad">
    <div class="result-board-text" v-if="$store.state.pk.loser === 'all'">
        Draw
    </div>
    <div class="result-board-text" v-else-if="$store.state.pk.loser === 'A' && $store.state.pk.a_id === parseInt($store.state.user.id)">
        Lose
    </div>
    <div class="result-board-text" v-else-if="$store.state.pk.loser === 'B' && $store.state.pk.b_id === parseInt($store.state.user.id)">
        Lose
    </div>
    <div class="result-board-text" v-else>
        Win
    </div>
    <div class="result-board-btn">
        <button @click="restart" type="button" class="btn btn-info btn-lg">
            重新匹配
        </button>
    </div>
  </div>
</template>

<script>
import { useStore } from 'vuex';


export default {
    setup() {
        const store = useStore();
        const restart = () => {
            store.commit('updateStatus', 'matching');
            store.commit('updateLoser', 'none');
            // 设置默认匹配页面
            store.commit("updateOpponent", {
                username: "我的对手",
                photo: require('@/assets/images/pk.png'),
            });
        }

        return {
            restart
        }
    }
}
</script>

<style scoped>
div.result-borad {
  height: 30vh;
  width: 30vw;
  background-color: rgba(50, 50, 50, 0.5);
  position: absolute;
  top: 35vh;
  left: 35vw;
  border-radius: 10px;
}

div.result-board-text {
    text-align: center;
    color: white;
    margin: auto;
    font-size: 50px;
    font-weight: bold;
    margin-top: 5vh;
}

div.result-board-btn {
    text-align: center;
    padding-top: 5vh;
    margin: auto;
}
</style>