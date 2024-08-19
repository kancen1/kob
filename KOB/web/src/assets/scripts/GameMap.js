    import { AcGameObject } from "./AcGameObject";
    import { Snake } from "./Snake";
    import { Wall } from "./Wall";

    export class GameMap extends AcGameObject {
        constructor(ctx, parent, store) {
            super();

            this.ctx = ctx;
            this.parent = parent;
            this.store = store;
            // 存放绝对距离 以后则使用相对距离
            this.L = 0;

            this.rows = 13;
            // 为了不让头节点相重合
            this.cols = 14;

            // 内部障碍物数量
            this.inner_walls_count = 20;
            // 存放墙
            this.walls = [];

            // 创建两条蛇
            this.snakes = [
                new Snake({id: 0, color: '#4876EC', r: this.rows - 2, c: 1}, this),
                new Snake({id: 1, color: '#F94848', r: 1, c: this.cols - 2}, this),
            ]

        }

        // 已在后端实现

        // // 判断是否连通
        // create_connectivity(g, sx, sy, tx, ty){
        //     if (sx == tx && sy == ty)
        //         return true;
            
        //     // 标记当前位置
        //     g[sx][sy] = true;
        //     // 定义四个方向
        //     let dx = [-1, 0, 1, 0], dy = [0, 1, 0, -1];
        //     for (let i = 0; i < 4; i ++ ) {
        //         // 四个方向的下一点坐标
        //         let x = sx + dx[i], y = sy + dy[i];
        //         // 判断是否撞墙和能否到达终点
        //         if (!g[x][y] && this.create_connectivity(g, x, y, tx, ty))
        //             return true;
        //     }

        //     // 搜不到终点
        //     return false;
        // }

        // 创建障碍物
        create_walls() {
            // // 创建bool来记录有没有墙
            // const g =[];
            // for (let r = 0; r < this.rows; r ++ ) {
            //     g[r] = [];
            //     for (let c = 0; c < this.cols; c ++ ) {
            //         // 初始化为false
            //         g[r][c] = false;
            //     }
            // }

            // // 给四周加上障碍物
            // for (let r = 0; r < this.rows; r ++ ) {
            //     // 左右加墙
            //     g[r][0] = g[r][this.cols - 1] = true;
            // }

            // for (let c = 0; c < this.cols; c ++ ) {
            //     // 上下加墙
            //     g[0][c] = g[this.rows - 1][c] = true;
            // }

            // // 创建随机的障碍物
            // for (let i = 0; i < this.inner_walls_count / 2; i ++ ) {
            //     // 要是重复了就继续随机 这里定1000次
            //     for(let j = 0; j < 1000; j ++ ) {
            //         // 随机取值Math.random() [0, 1)
            //         let r = parseInt(Math.random() * this.rows);
            //         let c = parseInt(Math.random() * this.cols);
            //         // 如果重复了就继续循环  中心对称
            //         if (g[r][c] || g[this.rows - 1 - r][this.cols - 1 - c]) continue;
            //         // 保留右上角和左下角蛇出现的位置
            //         if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2) continue;

            //         // 对称放障碍物
            //         g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = true;
            //         break;
            //     }
            // }

            // // 复制状态防止被修改
            // const copy_g = JSON.parse(JSON.stringify(g));
            
            // // 不连通就false
            // if (!this.create_connectivity(copy_g, this.rows - 2, 1 ,1, this.cols - 2)) 
            //     return false;


            // 取出地图
            const g = this.store.state.pk.gamemap;

            // 放置障碍物
            for (let r = 0; r < this.rows; r ++ ) {
                for (let c = 0; c < this.cols; c ++ ) {
                    // true的话添加障碍物
                    if(g[r][c]) {
                        this.walls.push(new Wall(r, c, this))
                    }
                }
            }

            // // 如果两蛇连通返回
            // return true;
        }

        // 添加一个事件用于读取用户输入
        add_listening_events() {
            // 聚焦
            this.ctx.canvas.focus();

            this.ctx.canvas.addEventListener("keydown", e => {
                let d = -1; // -1表示没有按到
                if (e.key === 'w' || e.key === 'W') d = 0;
                else if (e.key === 'd' || e.key === 'D') d = 1;
                else if (e.key === 's' || e.key === 'S') d = 2;
                else if (e.key === 'a' || e.key === 'A') d = 3;

                if (d >= 0) {
                    this.store.state.pk.socket.send(JSON.stringify({ // 前端向后端发送信息
                        event: "move",
                        direction: d, // 0123 上下左右
                    }))
                }
            });
        }   

        start() {
            // 随机1000次直到找到连通 后端实现
            // for (let i = 0; i < 1000; i ++ )
            //     if(this.create_walls()) 
            //         break;
            this.create_walls();

            // 添加事件
            this.add_listening_events();
        }
        
        // 每一帧都更新边长
        update_size() {
            // client 用于获取元素的可见高宽 取整像素防止线问题
            this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows));
            this.ctx.canvas.width = this.L * this.cols;
            this.ctx.canvas.height = this.L * this.rows;
        }

        // 判断下一步是否可以走 蛇是否准备好下一回合
        check_ready() {
            for (const snake of this.snakes) {
                // 不是静止则返回
                if(snake.status !== "idle") return false;
                // 没有接受到下一个指令则返回
                if(snake.direction === -1) return false;
            }
            return true;
        }

        next_step() {
            // 让两条蛇进入下一回合
            for (const snake of this.snakes) {
                snake.next_step();
            }
        }

        // 检测目标点是否合法: 没有撞到蛇的身体和障碍物
        check_valid(cell) {
            // 是否撞墙
            for (let wall of this.walls) {
                if (wall.r === cell.r && wall.c === cell.c)
                    return false;
            }

            // 是否撞身体
            for (const snake of this.snakes) {
                let k = snake.cells.length;
                // 如果蛇不能变长则蛇尾可以走 蛇尾不要判断
                if (!snake.check_tail_increasing()) {
                    k -- ;
                }
                // 判断其他部分
                for (let i = 0; i < k; i ++ ) {
                    if (snake.cells[i].r === cell.r && snake.cells[i].c === cell.c)
                        return false;
                }
            }

            return true;
        }

        update() {
            this.update_size();
            if(this.check_ready()) {
                this.next_step();
            } 
            this.render();
        }

        // 渲染
        render() {
            const color_even = "#AAD751", color_odd = "#A2D149";
            for (let r = 0; r < this.rows; r++) {
                for (let c = 0; c < this.cols; c++) {
                    if ((r + c) % 2 == 0) {
                        this.ctx.fillStyle = color_even;
                    } else {
                        this.ctx.fillStyle = color_odd;
                    }
                    this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L);
                }
            }
        }
    }