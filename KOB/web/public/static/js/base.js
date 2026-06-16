import { GameMap } from '/static/js/game_map/base.js';
import { Kyo } from '/static/js/player/kyo.js';

// 复杂项目先构建一个总类
class KOF {
    constructor(id) {
        this.$kof = $('#' + id);
        this.id = id;

        this.init();
    }

    // 初始化游戏
    init() {
        this.game_map = new GameMap(this);

        // 创建两名角色
        this.players = [
            new Kyo(this, {
                // 定义字典传入
                id: 0,
                x: 200,
                y: 0,
                width: 120,
                height: 200,
                color: 'blue',
            }),

            new Kyo(this, {
                id: 1,
                x: 900,
                y: 0,
                width: 120,
                height: 200,
                color: 'red',
            }),
        ];
    }

    // 重置游戏
    reset() {
        // 销毁所有游戏对象
        for (let player of this.players) {
            player.destroy();
        }
        this.game_map.destroy();

        // 清空 DOM
        this.$kof.empty();

        // 重新初始化
        this.init();
    }
}

// 暴露出KOF类
export {
    KOF
}