const AC_GAME_OBJECTS = [];

export class AcGameObject {
    constructor () {
        // 加入这个
        AC_GAME_OBJECTS.push(this);

        // 存放距离上一帧的时间间隔
        this.timedelta = 0;

        // 是否初始化
        this.has_call_start = false;
    }

    start() { // 只执行一次 用于初始化

    }

    update() { //除了第一帧每一帧执行

    }

    on_destroy() { //删除之前执行

    }

    destroy() { //删除当前对象
        this.on_destroy();

        for (let i in AC_GAME_OBJECTS) {
            if (AC_GAME_OBJECTS[i] === this) {
                // 删除一个元素
                AC_GAME_OBJECTS.splice(i, 1);
                break;
            }
        }
    }
}

// 记录上一次执行时间
let last_timestamp;
const step = (timestamp) => {
    for (let obj of AC_GAME_OBJECTS) {
        if(!obj.has_call_start) {
            obj.has_call_start = true;
            obj.start();
        } else {
            obj.timedelta = timestamp - last_timestamp;
            obj.update();
        }
    }   

    last_timestamp = timestamp;
    requestAnimationFrame(step)
}
// 刷新函数
requestAnimationFrame(step) 