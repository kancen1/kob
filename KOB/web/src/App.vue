<template>
  <NavBar></NavBar>

  <!-- :key="$route.fullPath" 用完整路径判断重复 -->
  <router-view :key="$route.fullPath"/>
</template>

<script>
import NavBar from './components/NavBar.vue';
// 报错没安装的依赖就安装 
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap'
// 调试后端POST请求
// import $ from 'jquery';

export default {
  name: "App",

  // 使用组件
  components: {
    NavBar, 
  },
  setup() {

    // $.ajax({
    //   url: "http://localhost:3000/user/account/register/",
    //   type: "post",
    //   data: {
    //     username: "gzh2",
    //     password: "123",
    //     confirmedPassword: "123",
    //   },
    //   success(resp) {
    //     console.log(resp);
    //   },
    //   error(resp) {
    //     console.log(resp);
    //   }
    // });
  }
}
</script>

<style>
body {
  background-image: url("@/assets/images/background.png");
  /* 百分百填充 */
  background-size: cover;
}
</style>
