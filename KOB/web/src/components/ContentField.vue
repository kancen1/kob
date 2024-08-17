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
    margin: 20px;
}
</style>