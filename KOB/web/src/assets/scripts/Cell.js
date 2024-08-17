export class Cell {
    // 表示蛇的一个格子
    constructor(r, c) {
        // 保存行列
        this.r = r;
        this.c = c;

        // 在画布中x是横坐标 y是列坐标
        this.x = c + 0.5;
        this.y = r + 0.5;
    }
}