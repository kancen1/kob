import { AcGameObject } from '/static/js/ac_game_object/base.js';
import { Controller } from '/static/js/controller/base.js';

// 定义函数继承AcGameObject 并暴露
export class GameMap extends AcGameObject {
    // 继承一定要写构造函数
    constructor(root) {

        // 调用父类构造函数
        super();

        // 将传入的参数赋值给 GameMap 类实例的一个属性 this.root
        this.root = root;

        // 游戏状态
        this.game_over = false;
        this.winner_id = null; // 0: 蓝方胜, 1: 红方胜, -1: 平局
        this.game_over_shown = false; // 防止重复显示

        // 定义canvas
        this.$canvas = $(`<canvas width="1280" height="720" tabindex=0></canvas>`);

        // 取出对象 在这canvas是数组
        this.ctx = this.$canvas[0].getContext('2d');

        // 加入到kof中 (base传入了root 对象)
        this.root.$kof.append(this.$canvas);

        // 聚焦为了获取输入
        this.$canvas.focus();

        this.controller = new Controller(this.$canvas);

        // 添加血条
        this.root.$kof.append($(`<div class="kof-head">
            <div class="kof-head-hp-0"><div><div></div></div></div>
            <div class="kof-head-timer">60</div>
            <div class="kof-head-hp-1"><div><div></div></div></div>
            </div>`
        ));

        // 当前剩余时间
        this.time_left = 60000;
        // 设定时间
        this.$timer = this.root.$kof.find('.kof-head-timer')
    }

    // 对象一般都需要定义start update 初始和每一帧的执行
    start() {

    }

    // 检测游戏是否结束
    check_game_over() {
        if (this.game_over) return;

        let [a, b] = this.root.players;

        // 判断两位玩家的死亡动画是否都播放完毕
        let a_dead = a.status === 6 && a.death_anim_done;
        let b_dead = b.status === 6 && b.death_anim_done;

        if (a_dead || b_dead) {
            this.game_over = true;

            if (a_dead && b_dead) {
                this.winner_id = -1; // 平局
            } else if (a_dead) {
                this.winner_id = 1; // 红方胜
            } else {
                this.winner_id = 0; // 蓝方胜
            }

            // 延迟显示结算界面（模拟K.O.动画）
            setTimeout(() => {
                this.show_game_over();
            }, 1500);
        }
    }

    // 显示结算界面
    show_game_over() {
        if (this.game_over_shown) return;
        this.game_over_shown = true;

        let result_text, result_sub;
        if (this.winner_id === -1) {
            result_text = 'DRAW';
            result_sub = '平局!';
        } else if (this.winner_id === 0) {
            result_text = 'K.O.';
            result_sub = '蓝方 获胜!';
        } else {
            result_text = 'K.O.';
            result_sub = '红方 获胜!';
        }

        let $overlay = $(`
            <div class="kof-game-over-overlay">
                <div class="kof-game-over-box">
                    <div class="kof-game-over-result">${result_text}</div>
                    <div class="kof-game-over-sub">${result_sub}</div>
                    <button class="kof-game-over-btn">再来一局</button>
                </div>
            </div>
        `);

        $overlay.find('.kof-game-over-btn').click(() => {
            this.root.reset();
        });

        this.root.$kof.append($overlay);
    }

    // 重写update
    update() {
        // 检测游戏结束
        this.check_game_over();

        // 游戏结束后停止计时器
        if (!this.game_over) {
            // 更新时间
            this.time_left -= this.timedelta;
            if (this.time_left < 0) {
                this.time_left = 0;

                // 时间到了平局
                let [a, b] = this.root.players;
                if (a.status !== 6 && b.status !== 6) {
                    a.status = 6, b.status = 6;
                    a.frame_current_cnt = b.frame_current_cnt = 0;
                    a.vx = b.vx = 0;
                    a.vy = b.vy = 1000;
                }
            }
        }

        // 渲染进计时器
        this.$timer.text(parseInt(this.time_left / 1000));

        // update一般不写逻辑一般用函数调用
        this.render();
    }

    // 清空 不然移动会变成拖尾
    render() {
        // 清理矩阵 0 0到宽高 使用canvas.width方法获得宽度
        this.ctx.clearRect(0, 0, this.ctx.canvas.width, this.ctx.canvas.height);
    }
}