<template>
        <!-- 这样就产生关联了 -->
    <div ref="parent" class="gamemap">
        <!-- 加上聚焦以便于读取用户操作 -->
        <canvas ref="canvas" tabindex="0"></canvas>
    </div>
</template>

<script>
import { GameMap } from '@/assets/scripts/GameMap';
import { onMounted, ref } from 'vue';
import { useStore } from 'vuex';

export default {
    name: 'GameMap',
    setup() {
        const store = useStore();
        let parent = ref('');
        let canvas = ref('');

        // 组件挂载完执行哪些操作
        onMounted(() => {
            store.commit("updateGameObject",
            new GameMap(canvas.value.getContext('2d'), parent.value, store) 
        );
    });

        return {
            parent,
            canvas,
        }
    }
}
</script>

<style scoped>
div.gamemap {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
}
</style>