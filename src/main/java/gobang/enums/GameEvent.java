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
     * 游戏结束，结算
     */
    GAME_RESULT,

    // 操作
    /**
     * 玩家下棋
     */
    PLAYER_PLAY,
    /**
     * 玩家投降
     */
    PLAYER_SURRENDER,
}
