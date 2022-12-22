package gobang.enums;

public enum GameStatus {

    /**
     * 初始状态，此时应该只显示加入/启动服务器按钮
     */
    INIT,
    /**
     * 玩家加入准备状态，加入/启动服务器按钮消失，房主显示开始游戏按钮
     */
    BEFORE_START,
    /**
     * 游玩状态，显示投降按钮
     */
    PLAYING,
}
