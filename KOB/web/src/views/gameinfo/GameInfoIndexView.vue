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
                              <p style="text-align: justify; margin: auto;">
                                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;本游戏分为两种参赛模式，其一为本人参赛模式可以使用WSAD控制角色移动，其二为bot模式，bot模式使用AI算法控制角色移动（bot编写详细请查看个人中心我的bot页面的编写注意事项），游戏胜利增加5分天梯分失败则扣两分，匹配超过十五秒且当前只有一个玩家在匹配的话系统会自动安排机器人进行对战。
                              </p>
                              <pse style="text-align: justify; margin: auto;"> 
                                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;确切地说，这并不是贪吃蛇。 与传统单人贪吃蛇不同的是，本贪吃蛇为双人对战，每回合玩家同时做出决策控制自己的蛇。
                                玩家在n*m的网格中操纵一条蛇(蛇是一系列坐标构成的有限不重复有顺序的序列，序列中相邻坐标均相邻，即两坐标的x轴坐标或y轴坐标相同，序列中第一个坐标代表蛇头)，玩家只能控制蛇头的朝向(东、南、西和北)来控制蛇。蛇以恒定的速度前进(前进即为序列头插入蛇头指向方向下一格坐标，并删除序列末尾坐标)。蛇的初始位置在网格中的左上角(地图位置[1,1])与右下角(地图位置[n,m])，初始长度为1格。与传统贪吃蛇不同，本游戏在网格中并没有豆子，但蛇会自动长大(长大即为不删除序列末尾坐标的前进)，前10回合每回合长度增加1，从第11回合开始，每3回合长度增加1。
                                地图为n*m的网格，由1*1的草地与障碍物构成。
                                蛇头在网格外、障碍物、自己蛇的身体(即序列重复)、对方蛇的身体(即与对方序列有相同坐标)，或非法操作均判定为死亡。任何一条蛇死亡时，游戏结束。若蛇同时死亡，判定为平局，否则先死的一方输，另一方赢。
                                其中n和m为随机参数，n为[10,16]间的整数，m为[10,12]间的整数，选手的程序应该对任意n,m均有效。
                              </pse>
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
