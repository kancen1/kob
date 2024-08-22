<template>
    <!-- container自适应大小容器 -->
    <div class="container content-feild">
    <div class="card">
        <div class="card-body">
            <slot></slot>
        </div>
    </div>
  </div>
</template>

<script>
export default {
    name: 'ContentField'
}
</script>

<style>
div.content-feild {
    margin-top: 20px;
}

.card {
    border-radius: 8px;
    background-color: rgba(255, 255, 255, 0.5); /* 半透明白色 */
}

.card-body {
    padding: 20px;
}
</style>