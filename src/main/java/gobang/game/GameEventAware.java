package gobang.game;

import gobang.player.Player;
import gobang.entity.Vector2D;
import gobang.enums.GameEvent;

/**
 * << callback >>
 * 游戏事件回调
 */
public interface GameEventAware {

    /**
     * 玩家加入回调函数
     *
     * @param player 玩家信息
     */
    void onPlayerJoin(Player player);

    /**
     * 玩家准备
     * @param playerId
     */
    void onPlayerPrepare(int playerId);

    /**
     * playerId玩家的回合
     *
     * @param playerId 开始回合的玩家
     */
    void onTurnStart(int playerId);

    /**
     * playerId玩家在某个位置下棋
     * @param playerId
     * @param position
     */
    void onTurnEnd(int playerId, Vector2D position);

    /**
     * 玩家投降
     * @param playerId
     */
    void onPlayerSurrender(int playerId);

    /**
     * 游戏结束回调函数
     * @param playerId      获胜的玩家ID
     */
    void onGameResult(int playerId);

    /**
     * 重置玩家棋子颜色
     * @param player
     */
    void onColorChange(Player player);

    /**
     * 玩家离开回调函数
     *
     * @param playerId 玩家id
     */
    void onPlayerLeave(Integer playerId);

    /**
     * 错误请求的回调
     *
     * @param playerId 玩家
     */
    void onError(int playerId);

    /**
     * 回传远程玩家id
     * @param playerId
     */
    void onSendId(int playerId);

    /**
     * 重置游戏
     */
    void onGameReset();

    /**
     * 触发事件之前回调函数，如果返回false则不会执行实际事件的触发直接
     *
     * @param event 事件
     * @param data  事件参数
     * @return false则不会触发实际事件
     */
    boolean beforeEvent(GameEvent event, Object data);

    /**
     * 触发事件之后的回调函数，无论事件是否实际被触发
     *
     * @param event 事件
     * @param data  事件参数
     */
    void afterEvent(GameEvent event, Object data);
}
