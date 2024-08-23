<template>
  <!-- container 可以动态调整范围 -->
  <div class="container">
    <div class="row">
      <div class="col-3">
        <div class="card photo_card">
          <div class="crad-body">
            <!-- :src= ""表示里面是表达式 -->
            <img :src="$store.state.user.photo" alt="" />
          </div>
        </div>
      </div>

      <div class="col-9">
        <div class="card" style="margin-top: 20px">
          <div class="card-header" style="box-sizing: border-box">
            <span style="font-size: 130%; line-height: 37.6px">我的Bot</span>

            <button type="button" class="btn btn-warning code_considerations" data-bs-toggle="modal" data-bs-target="#exampleModal">
              编写bot注意事项
            </button>

            <!-- Modal -->
            <div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
              <div class="modal-dialog modal-xl">
                <div class="modal-content">
                  <div class="modal-header">
                    <h1 class="modal-title fs-5" id="exampleModalLabel">编写bot注意事项</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                  </div>
                  <div class="modal-body">
                    请注意，编写bot时需要遵守以下规则：
                    <br />
                    1. 目前暂时只允许使用java语言编写bot，且每个用户的bot数量不得多余十个。
                    <br />
                    2. 目前bot代码编写要遵循以下规则
                    <br />
                    &nbsp; 0,1,2,3 分别对应蛇的上右下左操作
                    <br />
                    &nbsp; 地图（障碍物用0 1表示） #隔开 自己的横坐标me.sx #隔开 # 自己的纵坐标me.sy #( 我的操作 )# 对手坐标 横坐标you.sx # 纵坐标you.sy #( 对手操作)
                    <br />
                    &nbsp; 需要导包:
                    <br />
                    &nbsp; package com.kob.botrunningsystem.utils;
                    <br />
                    &nbsp; import java.io.File;
                    <br />
                    &nbsp; import java.io.FileNotFoundException;
                    <br />
                    &nbsp; import java.util.ArrayList;
                    <br />
                    &nbsp; import java.util.List;
                    <br />
                    &nbsp; import java.util.Scanner;
                    <br />
                    <b>具体操作请看示例(本例实现简单的判断下一步是否有蛇以及障碍)</b>
                    <pre>
                      package com.kob.botrunningsystem.utils;

                      import java.io.File;
                      import java.io.FileNotFoundException;
                      import java.util.ArrayList;
                      import java.util.List;
                      import java.util.Scanner;


                      public class Bot implements com.kob.botrunningsystem.utils.BotInterface{
                          static class Cell {
                              public int x, y;
                              public Cell(int x, int y) {
                                  this.x = x;
                                  this.y = y;
                              }
                          }

                          private boolean check_tail_increasing(int step) { // 检查蛇什么时候会变长
                              if (step &lt; 10) return true;
                              return step % 3 == 1;
                          }

                          // 表示方向 每一步方向
                          private List&lt;Integer&gt; steps;

                          public List&lt;Cell&gt; getCells(int sx, int sy, String steps) { // 创建蛇的身体
                              steps = steps.substring(1, steps.length() - 1); // 去掉括号
                              List&lt;Cell&gt; res = new ArrayList&lt;&gt;();

                              int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1}; // 方向偏移量 0,1,2,3 分别对应上右下左
                              int x = sx, y = sy;
                              int step = 0;
                              res.add(new Cell(x, y));
                              for (int i = 0; i &lt; steps.length(); i ++ ) { // 遍历方向
                                  int d = steps.charAt(i) - '0'; // 转换为整数
                                  x += dx[d];
                                  y += dy[d];
                                  res.add(new Cell(x, y)); // 添加蛇的身体
                                  if (!check_tail_increasing( ++ step)) {
                                      res.remove(0); // 蛇头前移，蛇尾去掉
                                  }
                              }
                              return res;
                          }

                          public Integer nextMove(String input) {
                              // 地图（障碍物用0 1表示） #隔开 自己的横坐标me.sx #隔开 # 自己的横坐标me.sy #( 我的操作 )# 对手坐标横坐标you.sx # you.sy #( 对手操作 )
                              String[] strs = input.split("#"); // 解码为
                              int[][] g = new int[13][14];

                              // 解码地图
                              for (int i = 0, k = 0; i &lt; 13; i++) {
                                  for (int j = 0; j &lt; 14; j++, k ++) {
                                      if (strs[0].charAt(k) == '1') {
                                          g[i][j] = 1;
                                      }
                                  }
                              }

                              int aSx = Integer.parseInt(strs[1]), aSy = Integer.parseInt(strs[2]); // 自己的起点坐标
                              int bSx = Integer.parseInt(strs[4]), bSy = Integer.parseInt(strs[5]); // 对手的起点坐标

                              List&lt;Cell&gt; aCells = getCells(aSx, aSy, strs[3]); // 本人操作
                              List&lt;Cell&gt; bCells = getCells(bSx, bSy, strs[6]); // 对手操作

                              for (Cell c: aCells) g[c.x][c.y] = 1; // 存入自己的操作将已有部分变为1 （障碍物）
                              for (Cell c: bCells) g[c.x][c.y] = 1; // 存入对方的操作将已有部分变为1 （障碍物）

                              int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1}; // 方向偏移量 0,1,2,3 分别对应上右下左
                              for (int i = 0; i &lt; 4; i++) { // 遍历方向判断哪个可以走
                                  int x = aCells.get(aCells.size() - 1).x + dx[i];
                                  int y = aCells.get(aCells.size() - 1).y + dy[i];
                                  if (x>= 0 && x &lt; 13 && y >= 0 && y &lt; 14 && g[x][y] == 0) { // 判断是否越界
                                      return i; // 返回可以走的方向
                                  }
                              }
                              return 0; // 默认向上走
                          }

                          @Override
                          public Integer get() {
                              File file = new File("input.txt");
                              try {
                                  Scanner sc = new Scanner(file);
                                  return nextMove(sc.next());
                              } catch (FileNotFoundException e) {
                                  throw new RuntimeException(e);
                              }
                          }
                      }

                    </pre>
                  </div>
                  <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">了解了</button>
                  </div>
                </div>
              </div>
            </div>

            <button
              type="button"
              class="btn btn-primary float-end create_bot"
              data-bs-toggle="modal"
              data-bs-target="#add-bot-btn"
            >
              创建Bot
            </button>

            <!-- Modal -->
            <div class="modal fade" id="add-bot-btn" tabindex="-1">
              <div class="modal-dialog modal-xl">
                <div class="modal-content">
                  <div class="modal-header">
                    <h1 class="modal-title fs-5">创建Bot</h1>
                    <button
                      type="button"
                      class="btn-close"
                      data-bs-dismiss="modal"
                      aria-label="Close"
                    ></button>
                  </div>
                  <div class="modal-body">
                    <div class="mb-3">
                      <label for="add-bot-title" class="form-label">名称</label>
                      <input
                        v-model="botadd.title"
                        type="text"
                        class="form-control"
                        id="add-bot-title"
                        placeholder="请输入bot名称"
                      />
                    </div>
                    <div class="mb-3">
                      <label for="add-bot-description" class="form-label"
                        >简介</label
                      >
                      <textarea
                        v-model="botadd.description"
                        class="form-control"
                        id="add-bot-description"
                        rows="3"
                        placeholder="请输入bot简介"
                      ></textarea>
                    </div>
                    <div class="mb-3">
                      <label for="add-bot-code" class="form-label">代码</label>
                      <!-- 集成代码编辑器 -->
                      <VAceEditor
                        v-model:value="botadd.content"
                        @init="editorInit"
                        lang="c_cpp"
                        theme="textmate"
                        style="height: 300px"
                        :options="{
                          enableBasicAutocompletion: true, //启用基本自动完成
                          enableSnippets: true, // 启用代码段
                          enableLiveAutocompletion: true, // 启用实时自动完成
                          fontSize: 18, //设置字号
                          tabSize: 4, // 标签大小
                          showPrintMargin: false, //去除编辑器里的竖线
                          highlightActiveLine: true,
                        }"
                      />
                    </div>
                  </div>
                  <div class="modal-footer">
                    <div class="error-message">{{ botadd.error_message }}</div>
                    <button
                      type="button"
                      class="btn btn-primary"
                      @click="add_bot"
                    >
                      创建
                    </button>
                    <button
                      type="button"
                      class="btn btn-secondary"
                      data-bs-dismiss="modal"
                    >
                      取消
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="card-body">
            <table class="table table-striped table-hover">
              <thead>
                <tr>
                  <th>名称</th>
                  <th>创建时间</th>
                  <th>操作</th>
                </tr>
              </thead>

              <tbody>
                <tr v-for="bot in bots" :key="bot.id">
                  <td>{{ bot.title }}</td>
                  <td>{{ bot.createtime }}</td>
                  <td>
                    <button
                      type="button"
                      class="btn btn-secondary"
                      style="margin-right: 10px"
                      data-bs-toggle="modal"
                      :data-bs-target="'#update-bot-modal-' + bot.id"
                    >
                      修改
                    </button>
                    <button
                      type="button"
                      class="btn btn-danger"
                      @click="remove_bot(bot)"
                    >
                      删除
                    </button>

                    <div
                      class="modal fade"
                      :id="'update-bot-modal-' + bot.id"
                      tabindex="-1"
                    >
                      <div class="modal-dialog modal-xl">
                        <div class="modal-content">
                          <div class="modal-header">
                            <h1 class="modal-title fs-5">修改Bot</h1>
                            <button
                              type="button"
                              class="btn-close"
                              data-bs-dismiss="modal"
                              aria-label="Close"
                            ></button>
                          </div>
                          <div class="modal-body">
                            <div class="mb-3">
                              <label for="add-bot-title" class="form-label"
                                >名称</label
                              >
                              <input
                                v-model="bot.title"
                                type="text"
                                class="form-control"
                                id="add-bot-title"
                                placeholder="请输入bot名称"
                              />
                            </div>
                            <div class="mb-3">
                              <label
                                for="add-bot-description"
                                class="form-label"
                                >简介</label
                              >
                              <textarea
                                v-model="bot.description"
                                class="form-control"
                                id="add-bot-description"
                                rows="3"
                                placeholder="请输入bot简介"
                              ></textarea>
                            </div>
                            <div class="mb-3">
                              <label for="add-bot-code" class="form-label"
                                >代码</label
                              >
                              <!-- 集成代码编辑器 -->
                              <VAceEditor
                                v-model:value="bot.content"
                                @init="editorInit"
                                lang="c_cpp"
                                theme="textmate"
                                style="height: 300px"
                                :options="{
                                  enableBasicAutocompletion: true, //启用基本自动完成
                                  enableSnippets: true, // 启用代码段
                                  enableLiveAutocompletion: true, // 启用实时自动完成
                                  fontSize: 18, //设置字号
                                  tabSize: 4, // 标签大小
                                  showPrintMargin: false, //去除编辑器里的竖线
                                  highlightActiveLine: true,
                                }"
                              />
                            </div>
                          </div>
                          <div class="modal-footer">
                            <div class="error-message">
                              {{ bot.error_message }}
                            </div>
                            <button
                              type="button"
                              class="btn btn-primary"
                              @click="update_bot(bot)"
                            >
                              保存修改
                            </button>
                            <button
                              type="button"
                              class="btn btn-secondary"
                              data-bs-dismiss="modal"
                              @click="refresh_bots"
                            >
                              取消
                            </button>
                          </div>
                        </div>
                      </div>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { Modal } from "bootstrap/dist/js/bootstrap";
import $ from "jquery";
import { reactive, ref } from "vue";
import { useStore } from "vuex";

// 集成编辑器依赖
import { VAceEditor } from "vue3-ace-editor";
import ace from "ace-builds";
import "ace-builds/src-noconflict/mode-c_cpp";
import "ace-builds/src-noconflict/mode-json";
import "ace-builds/src-noconflict/theme-chrome";
import "ace-builds/src-noconflict/ext-language_tools";

export default {
  components: {
    VAceEditor,
  },

  setup() {
    // 集成代码编辑器
    ace.config.set(
      "basePath",
      "https://cdn.jsdelivr.net/npm/ace-builds@" +
        require("ace-builds").version +
        "/src-noconflict/"
    );

    const store = useStore();
    let bots = ref([]);

    // 绑定变量用ref 绑定对象用reactive
    const botadd = reactive({
      title: "",
      description: "",
      content: "",
      error_message: "",
    });

    const refresh_bots = () => {
      $.ajax({
        url: "http://localhost:3000/api/user/bot/getlist/",
        type: "get",
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        success(resp) {
          bots.value = resp;
        },
      });
    };

    // 执行一遍
    refresh_bots();

    const add_bot = () => {
      botadd.error_message = "";
      $.ajax({
        url: "http://localhost:3000/api/user/bot/add/",
        type: "post",
        data: {
          title: botadd.title,
          description: botadd.description,
          content: botadd.content,
        },
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        success(resp) {
          if (resp.error_message === "success") {
            botadd.title = "";
            botadd.description = "";
            botadd.content = "";
            // 关闭浮悬窗
            const modalInstance = Modal.getInstance("#add-bot-btn");
            if (modalInstance) {
              modalInstance.hide();
            }
            refresh_bots();
          } else {
            botadd.error_message = resp.error_message;
          }
        },
        error() {
          botadd.error_message = "系统错误请联系管理员";
        },
      });
    };

    const remove_bot = (bot) => {
      $.ajax({
        url: "http://localhost:3000/api/user/bot/remove/",
        type: "post",
        data: {
          bot_id: bot.id,
        },
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        success(resp) {
          if (resp.error_message === "success") {
            refresh_bots();
          }
        },
      });
    };

    const update_bot = (bot) => {
      botadd.error_message = "";
      $.ajax({
        url: "http://localhost:3000/api/user/bot/update/",
        type: "post",
        data: {
          bot_id: bot.id,
          title: bot.title,
          description: bot.description,
          content: bot.content,
        },
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        success(resp) {
          if (resp.error_message === "success") {
            // 关闭浮悬窗
            const modalInstance = Modal.getInstance(
              "#update-bot-modal-" + bot.id
            );
            if (modalInstance) {
              modalInstance.hide();
            }
            refresh_bots();
          } else {
            bot.error_message = resp.error_message;
          }
        },
        error() {
          botadd.error_message = "系统错误请联系管理员";
        },
      });
    };

    return {
      bots,
      botadd,
      add_bot,
      remove_bot,
      update_bot,
      refresh_bots,
    };
  },
};
</script>

<style scoped>
div.error-message {
  color: red;
}

.table thead + tbody {
  border-top: 2px solid #000; /* 加粗线宽2px，颜色为黑色 */
}

.card {
  background-color: rgba(255, 255, 255, 0.5); /* 半透明白色 */
}

.photo_card {
  margin-top: 20px;
  width: 200px;
  height: 200px;
  float: right;
}

.crad-body > img {
  width: 100%;
}

.code_considerations {
  margin-left: 20px;
}


</style>
