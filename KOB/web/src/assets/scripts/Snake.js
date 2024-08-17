import { AcGameObject } from "./AcGameObject";
import { Cell } from "./Cell";

// 定义蛇对象 每帧会变化继承AcGameObject
export class Snake extends AcGameObject {
    constructor(info, gamemap) {
        super();

        this.id = info.id;
        this.color = info.color;
        this.gamemap = gamemap;

        // 蛇的身体部分 
        this.cells = [new Cell(info.r, info.c)]; //存放蛇的身体 cells[0]放蛇头

        // 记录蛇的下一步位置
        this.next_cell = null;

        // 定义蛇的速度
        this.speed = 5; //每秒五个格子

        // 读取下一步指令
        this.direction = -1; // -1表示没指令, 0、1、2、3表示上右下左

        this.status = "idle"; // 表示静止, move表示正在移动, die表示死亡

        // 行方向偏移量
        this.dr = [-1, 0, 1, 0];
        // 列方向偏移量
        this.dc = [0, 1, 0, -1];

        this.step = 0; //表示回合数
        
        this.eps = 1e-2; //允许的误差

        // 存蛇头的方向用来加眼睛
        this.eye_direction = 0;
        // 默认一个向上一个向下
        if (this.id === 1) {
            // 另一个向下
            this.eye_direction = 2; // 左下角的蛇初始向上 右上角向下
        }

        // 蛇眼偏移量
        this.eye_dx = [
            [-1, 1],
            [1, 1],
            [1, -1],
            [-1, -1],
        ];

        this.eye_dy = [
            [-1, -1],
            [-1, 1],
            [1, 1],
            [1, -1],
        ];
    }

    start() {

    }

    // 设置方向
    set_direction(d) {
        this.direction = d;
    }

    // 判断蛇尾是否需要增加
    check_tail_increasing() { // 检测当前回合蛇的长度是否需要增加
        // 如果回合数小于十回合一定要变长
        if (this.step <= 10) return true;
        // 十回合以后每三步变长一次
        if (this.step % 3 === 1) return true;
        // 不满足以上回
        return false;
    }

    // 更新蛇的状态变为走下一步
    next_step() {
        const d = this.direction;
        // 读取蛇下一步位置
        this.next_cell = new Cell(this.cells[0].r + this.dr[d], this.cells[0].c + this.dc[d]);
        // 更新蛇眼的方向
        this.eye_direction = d;
        this.direction = -1; // 清空操作(方向)
        this.status = "move";
        // 增加回合数
        this.step ++ ;

        // 加蛇的身体 除了头复制以外每个元素向后移动一位
        const k = this.cells.length;
        for (let i = k; i > 0; i -- ) {
            // 为了不影响使用JSON复制
            this.cells[i] = JSON.parse(JSON.stringify(this.cells[i - 1]));
        }
    }

    update_move() {
        // // 蛇头的移动距离
        // this.cells[0].y -= this.speed * this.timedelta / 1000;
        // 每两帧之间移动距离

        // 求出下一步走距离头的位置 
        const dx = this.next_cell.x - this.cells[0].x;
        const dy = this.next_cell.y - this.cells[0].y;
        // 求出距离(斜线)
        const distance = Math.sqrt(dx * dx + dy * dy);

        // 判断是否走到
        if (distance < this.eps) { // 走到目标点了
            // 目标点变为头
            this.cells[0] = this.next_cell;
            this.next_cell = null;
            // 移动到目标点
            this.status = "idle"; //走完了

            if (!this.check_tail_increasing()) {
                // 蛇没变长砍掉尾巴
                this.cells.pop();
            }

        } else {
            const move_distance = this.speed * this.timedelta / 1000;
            // x走的距离
            this.cells[0].x += move_distance * dx / distance;
            // y走的距离
            this.cells[0].y += move_distance * dy / distance;

            // 如果蛇尾不变长则需要跟着头动 否则不动
            if (!this.check_tail_increasing()) {    
                // 不变长走到下一个目的地
                const k = this.cells.length;
                // 取出蛇尾和蛇尾的下一个目标
                const tail = this.cells[k - 1], tail_target = this.cells[k - 2];
                // 横纵坐标差值
                const tail_dx = tail_target.x - tail.x;
                const tail_dy = tail_target.y - tail.y;
                const tail_distance = Math.sqrt(tail_dx * tail_dx + tail_dy * tail_dy);
                tail.x += move_distance * tail_dx / tail_distance;
                tail.y += move_distance * tail_dy / tail_distance;
            }
        }   
    }

    update() {
        if (this.status === 'move') {
            this.update_move();
        }
        
        this.render();
    }

    render() {
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;

        // 设置填充颜色
        ctx.fillStyle = this.color;

        // 如果蛇死亡则变色
        if (this.status === "die") {
            ctx.fillStyle = "white";
        }

        // 画蛇 枚举蛇的每一个格子
        for (const cell of this.cells) {
            ctx.beginPath();
            // 圆心 半径 起始角度 终止角度
            ctx.arc(cell.x * L, cell.y * L, L / 2 * 0.8, 0, Math.PI * 2);
            // 填充颜色
            ctx.fill(); 
        }

        for (let i = 1; i < this.cells.length; i ++ ) {
            // 记录相连的圈
            const a = this.cells[i - 1], b = this.cells[i];
            // 如果重合就不需要画
            if (Math.abs(a.x - b.x) < this.eps && Math.abs(a.y - b.y) < this.eps)
                continue;
            // 如果竖方向重合
            if (Math.abs(a.x - b.x) < this.eps) {
                // 补一个偏移量
                ctx.fillRect((a.x - 0.5 + 0.1) * L, Math.min(a.y, b.y) * L, L * 0.8, Math.abs(a.y - b.y) * L);
            } else {
            // 横方向重合
                ctx.fillRect(Math.min(a.x, b.x) * L, (a.y - 0.5 + 0.1) * L, Math.abs(a.x - b.x) * L, L * 0.8);
            }  
        }

        // 画蛇的眼
        ctx.fillStyle = "black";
        for (let i = 0; i < 2; i ++ ) {
            const eye_x = (this.cells[0].x + this.eye_dx[this.eye_direction][i] * 0.2) * L;
            const eye_y = (this.cells[0].y + this.eye_dy[this.eye_direction][i] * 0.2) * L;
            ctx.beginPath();
            ctx.arc(eye_x, eye_y, L * 0.05  , 0, Math.PI * 2);
            ctx.fill();
        }
    }
}