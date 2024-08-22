<template>
  <ContentField>
    <table class="table table-striped table-hover" style="text-align: center">
      <thead>
        <tr>
          <th>玩家</th>
          <th>天梯分</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="user in users" :key="user.id">
          <td class="col-md-6">
            <div class="row">
              <div class="col-md-6">
                <img :src="user.photo" alt="" class="record-user-photo" />
              </div>
              <div class="col-md-6">
                <span class="record-user-username">{{ user.username }}</span>
              </div>
            </div>
          </td>
          <td class="col-md-6">
            <span class="record-user-rating">{{ user.rating }}</span>
          </td>
        </tr>
      </tbody>
    </table>
    <div class="row">
      <div class="col-6">
        <div class="row">
          <div class="col-3">
            <div class="mb-3">
              <label for="pege_goto" class="visually-hidden">页码</label>
              <input
                type="number"
                class="form-control"
                id="pege_goto"
                placeholder="请输入页码"
                v-model="cnt"
              />
            </div>
          </div>
          <div class="col-2">
            <button class="btn btn-primary mb-3" @click="jump_page()">跳转</button>
          </div>
        </div>
      </div>
      <div class="col-6">
        <nav aria-label="..." class="float-end">
          <ul class="pagination">
            <li class="page-item" @click="click_page(-2)">
              <a class="page-link" href="#">前一页</a>
            </li>
            <li
              :class="'page-item ' + page.is_active"
              v-for="page in pages"
              :key="page.number"
              @click="click_page(page.number)"
            >
              <a class="page-link" href="#">{{ page.number }}</a>
            </li>
            <li class="page-item" @click="click_page(-1)">
              <a class="page-link" href="#">后一页</a>
            </li>
          </ul>
        </nav>
      </div>
    </div>
  </ContentField>
</template>

<script>
import ContentField from "@/components/ContentField.vue";
import $ from "jquery";
import { ref } from "vue";
import { useStore } from "vuex";

export default {
  name: "RanklistIndexView",
  components: {
    ContentField,
  },

  setup() {
    const store = useStore();
    let users = ref([]);
    let current_page = 1;
    let total_users = 0;
    let pages = ref([]);
    let cnt = ref('');

    const click_page = page => {
      if (page === -2) page = current_page - 1;
      else if (page === -1) page = current_page + 1;
      let max_pages = parseInt(Math.ceil(total_users / 10)); // 最大页数

      if (page >= 1 && page <= max_pages) { // 如果合法
        pull_page(page); // 加载一个新分页  可以用此传入页数进行搜索
      }
      cnt.value = '';
    }

    const jump_page = () => {
      click_page(parseInt(cnt.value));
    };

    const update_pages = () => {
      let max_pages = parseInt(Math.ceil(total_users / 10));
      let new_pages = [];
      for (let i = current_page - 2; i <= current_page + 2; i ++ ) {
        if (i >= 1 && i <= max_pages) {
          new_pages.push({
            number: i,
            is_active: i === current_page ? "active" : "" // 判断当前是不是这一页
          }); // 表示应该有这一项
        }
      }
      pages.value = new_pages;
    }

    const pull_page = (page) => {
      current_page = page;
      $.ajax({
        url: "http://localhost:3000/ranklist/getlist/",
        data: {
          page,
        },
        type: "get",
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        success(resp) {
          users.value = resp.users; // 本页user数
          total_users = resp.users_count; // 用户总数
          update_pages();
        },
        error(resp) {
          console.log(resp);
        },
      });
    };

    pull_page(current_page);

    return {
      users,
      pages,
      cnt,
      click_page,
      jump_page,
    };
  },
};
</script>

<style scoped>
img.record-user-photo {
  width: 4vh;
  border-radius: 5px;
  float: right;
}

.table {
  margin-top: 2vh;
  margin-bottom: 2vh;
}

tbody>tr {
  height: 8vh;
}

.record-user-username {
  float: left;
}
</style>
