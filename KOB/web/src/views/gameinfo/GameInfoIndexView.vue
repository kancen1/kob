<template>
    <ContentField>
        <div class="card">
            <div class="card-header">
                游戏信息
            </div>
            <div class="card-body">
                <div class="list-group">
                    <div class="list-group-item">
                        <div class="row">
                            <div class="col-4">游戏版本：</div>
                            <div class="col-8">1.0</div>
                        </div>
                    </div>
                    <div class="list-group-item">
                        <div class="row">
                            <div class="col-4">游戏开发环境：</div>
                            <div class="col-8">Java 17</div>
                        </div>
                    </div>
                    <div class="list-group-item">
                        <div class="row">
                            <div class="col-4">使用框架：</div>
                            <div class="col-8">Vue，Spring Boot</div>
                        </div>
                    </div>
                    <div class="list-group-item">
                        <div class="row">
                            <div class="col-4">内存大小：</div>
                            <div class="col-8">2G</div>
                        </div>
                    </div>
                    <div class="list-group-item">
                        <div class="row">
                            <div class="col-4">服务器地址：</div>
                            <div class="col-8">kancen.fun</div>
                        </div>
                    </div>
                    <div class="list-group-item">
                        <div class="row">
                            <div class="col-4">游戏简介:</div>
                            <div>
                              <p style="text-align: justify; margin: auto;">
                                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;这是一个基于Java开发的游戏服务器，使用Spring Boot框架进行开发，支持Minecraft 1.18.2版本的游戏。
                              </p>
                              <p>
                                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;本游戏分为两种参赛模式，其一为本人参赛模式可以使用WSAD控制角色移动，其二为bot模式，bot模式使用AI算法控制角色移动（bot编写详细请查看个人中心我的bot页面的编写注意事项），游戏胜利增加5分天梯分失败则扣两分，匹配超过十五秒且当前只有一个玩家在匹配的话系统会自动安排机器人进行对战。
                              </p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from '@/components/ContentField.vue';

export default {
  name: "GameInfoIndexView",
  components: {
    ContentField,
  }
};
</script>

<style scoped>
</style>
