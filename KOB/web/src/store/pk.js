export default {
    state: {
        status: "matching", // matching表示匹配界面, playing表示对战界面
        // 存放链接 
        socket: null,
        // 存放对手
        opponent_username: "",
        opponent_photo: "",
        // 存放地图 
        gamemap: null,
        a_id: 0,
        a_sx: 0,
        a_sy: 0,

        b_id: 0,
        b_sx: 0,
        b_sy: 0,

        gameObject: null,
        loser: "none", // none表示没人输 all表示平局，a表示a输，b表示b输
    },
    getters: {
    },
    mutations: {
        updateSocket(state, socket) {
            state.socket = socket;
        },
            
        updateOpponent(state, opponent) {
            state.opponent_username = opponent.username;
            state.opponent_photo = opponent.photo;
        },
        updateStatus(state, status) {
            state.status = status;
        },
        updateGame(state, game) {
            state.gamemap = game.map;
            // 传入game对象，更新a和b的id和坐标
            state.a_id = game.a_id;
            state.a_sx = game.a_sx;
            state.a_sy = game.a_sy;
        
            state.b_id = game.b_id;
            state.b_sx = game.b_sx;
            state.b_sy = game.b_sy;
        },
        updateGameObject(state, gameObject) {
            state.gameObject = gameObject;
        },
        updateLoser(state, loser) {
            state.loser = loser;
        }
    },
    actions: {
    },
    modules: {
    }
}