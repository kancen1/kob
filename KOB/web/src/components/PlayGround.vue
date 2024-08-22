<template>
  <div class="playground">
    <GameMap></GameMap>
  </div>
  <PkUserInfo></PkUserInfo>
</template>

<script>
import GameMap from './GameMap.vue';
import PkUserInfo from './PkUserInfo.vue';

export default {
    name: 'PlayGround',
    components: {
        GameMap,
        PkUserInfo,
    }
}
</script>

<style scoped>
div.playground {
    width: 60vw;
    height: 70vh;
    margin: 40px auto;
}
</style>