<template>
  <div class="left-info">
    <div v-if="parseInt($store.state.pk.a_id) === parseInt($store.state.user.id)" class="left-user">
      <div class="photo">
        <img :src="$store.state.user.photo" alt="">
      </div>
      <div class="prompt">
        您出生在蓝色方
      </div>
      <div class="username">
        {{$store.state.user.username}}
      </div>
    </div>

    <div v-else class="left-user">
      <div class="photo">
        <img :src="$store.state.pk.opponent_photo" alt="">
      </div>
      <div class="opponent-prompt">
        对手出生在蓝色方
      </div>
      <div class="username">
        {{$store.state.pk.opponent_username}}
      </div>
    </div>
    
  </div>

  <div class="right-info">
    <div v-if="parseInt($store.state.pk.a_id) !== parseInt($store.state.user.id)" class="right-user">
      <div class="photo">
        <img :src="$store.state.user.photo" alt="">
      </div>
      <div class="prompt">
        您出生在红色方
      </div>
      <div class="username">
        {{$store.state.user.username}}
      </div>
    </div>

    <div v-else class="right-user">
      <div class="photo">
        <img :src="$store.state.pk.opponent_photo" alt="">
      </div>
      <div class="opponent-prompt">
        对手出生在红色方
      </div>
      <div class="username">
        {{$store.state.pk.opponent_username}}
      </div>
    </div>
  </div>
</template>

<script>
export default {
    name: 'PkUserInfo',

}
</script>

<style scoped>
.left-info {
    position: absolute;
    top: 20vh;
    left: 0;
    width: 250px;
    height: 350px;
    margin: 20px;
    /* background-color: white; */
}

.left-user>.photo {
    width: 200px;
    height: 200px;
    background-color: rgba(50, 50, 50, 0.5);
}

.left-user>.photo>img {
    position: relative;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 70%;
    height: 70%;
    border-radius: 50%;
}

.left-user>.username {
    width: 200px;
    height: 50px;
    text-align: center;
    color: white;
    font-size: 20px;
    font-weight: 600;
    background-color: rgba(50, 50, 50, 0.5);
}

.left-user>.myusername {
    width: 200px;
    height: 50px;
    background-color: rgba(173, 216, 230, 0.5);
}

.right-info {
    position: absolute;
    top: 20vh;
    right: 0;
    width: 250px;
    height: 350px;
    margin: 20px;
    /* background-color: white; */
}

.right-user>.photo {
    width: 200px;
    height: 200px;
    background-color: rgba(50, 50, 50, 0.5);
}

.right-user>.photo>img {
    position: relative;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 70%;
    height: 70%;
    border-radius: 50%;
}

.right-user>.username {
    width: 200px;
    height: 50px;
    text-align: center;
    color: white;
    font-size: 20px;
    font-weight: 600;
    background-color: rgba(50, 50, 50, 0.5);
}

.right-user>.myusername {
    width: 200px;
    height: 50px;
    background-color: rgba(173, 216, 230, 0.5);
}

.prompt {
    width: 200px;
    height: 50px;
    text-align: center;
    color: rgba(255, 0, 64, 0.862);
    font-size: 20px;
    font-weight: 600;
    background-color: rgba(50, 50, 50, 0.5);
}

.opponent-prompt {
    width: 200px;
    height: 50px;
    text-align: center;
    color: rgba(0, 255, 166, 0.862);
    font-size: 20px;
    font-weight: 600;
    background-color: rgba(50, 50, 50, 0.5);
}
</style>