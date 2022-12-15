package gobang.game;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 游戏上下文，包含一局游戏的基本信息
 */
@Data
public class GameContext {

    /**
     * 游戏ID
     */
    String gameId;

    /**
     * 加入牌局的玩家
     */
    Map<Integer, AbstractPlayer> players;

    /**
     * 棋盘
     */
    List<Card> deck;
}
