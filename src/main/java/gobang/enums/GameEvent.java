package gobang.enums;

/**
 * 基础的游戏事件，应用于适配器通信和UI层
 */
public enum GameEvent {

    /**
     * 玩家加入
     */
    PLAYER_JOIN,
    /**
     * 玩家离开游戏
     */
    PLAYER_LEAVE,
    /**
     * 新回合开始
     */
    TURN_START,
    /**
     * 玩家下棋，一回合结束
     */
    TURN_END,
    /**
     * 游戏结束，结算
     */
    GAME_RESULT,
    /**
     * 修改玩家棋子颜色
     */
    COLOR_CHANGE,
    /**
     * 回传玩家Id
     */
    SEND_ID,
    /**
     * 重置游戏
     */
    GAME_RESET,
    /**
     * 玩家准备
     */
    PREPARE,

    // 操作
    /**
     * 玩家投降
     */
    PLAYER_SURRENDER,
    /**
     * 错误请求
     */
    ERROR_REQUEST
}
