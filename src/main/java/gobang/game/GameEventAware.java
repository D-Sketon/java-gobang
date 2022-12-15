package gobang.game;

import gobang.entity.Player;
import gobang.entity.Vector2D;

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
     * 玩家离开回调函数
     *
     * @param player 玩家信息
     */
    void onPlayerLeave(Player player);

    /**
     * 游戏结束回调函数
     * @param playerId      获胜的玩家ID
     */
    void onGameResult(int playerId);

    /**
     * playerId玩家的回合
     *
     * @param playerId 开始回合的玩家
     */
    void onPlayerTurn(int playerId);

    /**
     * playerId玩家在某个位置下棋
     * @param playerId
     * @param position
     */
    void onPlayerEnd(int playerId, Vector2D position);

    /**
     * 错误请求的回调
     *
     * @param playerId 玩家
     */
    void onError(int playerId);
}
