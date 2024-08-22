<template>
  <ContentField>
    <table class="table table-striped table-hover" style="text-align: center">
      <thead>
        <tr>
          <th>A</th>
          <th>B</th>
          <th>对战结果</th>
          <th>对战时间</th>
          <th>操作</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="record in records" :key="record.record.id">
          <td class="col-md-2">
            <div class="row">
              <div class="col-md-6">
               <img :src="record.a_photo" alt="" class="record-user-photo" />
              </div>
              <div class="col-md-6">
                <span class="record-user-username">{{ record.a_username }}</span>
              </div>
            </div>
          </td>
          <td class="col-md-2">
            <div class="row">
              <div class="col-md-6">
                <img :src="record.b_photo" alt="" class="record-user-photo" />
              </div>
              <div class="col-md-6">
                <span class="record-user-username">{{ record.b_username }}</span>
              </div>
            </div>
          </td>
          <td>{{ record.result }}</td>
          <td>{{ record.record.createtime }}</td>
          <td>
            <button
              @click="open_record_content(record.record.id)"
              type="button"
              class="btn btn-secondary"
            >
              查看录像
            </button>
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
import router from "@/router";
import $ from "jquery";
import { ref } from "vue";
import { useStore } from "vuex";

export default {
  name: "RecordIndexView",
  components: {
    ContentField,
  },

  setup() {
    const store = useStore();
    let records = ref([]);
    let current_page = 1;
    let total_records = 0;
    let pages = ref([]);
    let cnt = ref('');

    const click_page = (page) => {
      if (page === -2) page = current_page - 1;
      else if (page === -1) page = current_page + 1;
      let max_pages = parseInt(Math.ceil(total_records / 10));

      if (page >= 1 && page <= max_pages) {
        // 如果合法
        pull_page(page); // 加载一个新分页  可以用此传入页数进行搜索
      }
      cnt.value = "";
    };

    const update_pages = () => {
      let max_pages = parseInt(Math.ceil(total_records / 10));
      let new_pages = [];
      for (let i = current_page - 2; i <= current_page + 2; i++) {
        if (i >= 1 && i <= max_pages) {
          new_pages.push({
            number: i,
            is_active: i === current_page ? "active" : "", // 判断当前是不是这一页
          }); // 表示应该有这一项
        }
      }
      pages.value = new_pages;
    };

    console.log(total_records);

    const pull_page = (page) => {
      current_page = page;
      $.ajax({
        url: "http://localhost:3000/api/record/getlist/",
        data: {
          page,
        },
        type: "get",
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        success(resp) {
          records.value = resp.records;
          total_records = resp.records_count;
          update_pages();
        },
        error(resp) {
          console.log(resp);
        },
      });
    };

    pull_page(current_page);

    const stringTo2D = (map) => {
      // 将数据库的地图转换为数组
      let g = [];
      for (let i = 0, k = 0; i < 13; i++) {
        let row = [];
        for (let j = 0; j < 14; j++, k++) {
          if (map[k] == "0") row.push(0);
          else row.push(1);
        }
        g.push(row);
      }
      return g;
    };

    const open_record_content = (recordId) => {
      // 打开recordcontent页面 并传入一个参数
      for (const record of records.value) {
        // 寻找recordId对应的record
        if (record.record.id === recordId) {
          store.commit("updateIsRecord", true);
          store.commit("updateGame", {
            map: stringTo2D(record.record.map),
            a_id: record.record.aid,
            a_sx: record.record.asx,
            a_sy: record.record.asy,
            b_id: record.record.bid,
            b_sx: record.record.bsx,
            b_sy: record.record.bsy,
          });
          store.commit("updateSteps", {
            a_steps: record.record.asteps,
            b_steps: record.record.bsteps,
          });
          store.commit("updateRecordLoser", record.record.loser);
          router.push({
            name: "record_content",
            params: {
              // 参数
              recordId: recordId,
            },
          });
          break;
        }
      }
    };

    const jump_page = () => {
      click_page(parseInt(cnt.value));
    };

    return {
      records,
      open_record_content,
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

tbody > tr {
  height: 8vh;
}

.record-user-username {
  float: left;
}
</style>
