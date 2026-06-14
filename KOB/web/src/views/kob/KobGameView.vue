<template>
  <div class="kof-container">
    <iframe src="/kof.html" class="kof-iframe"></iframe>
  </div>
</template>

<script>
export default {
    name: "KobGameView",
}
</script>

<style scoped>
.kof-container {
  position: fixed;
  top: 56px;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
  background: #000;
}
.kof-iframe {
  width: 100%;
  height: 100%;
  border: none;
  outline: none;
}
</style>