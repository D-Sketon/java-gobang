package gobang.game;

import gobang.entity.Board;
import gobang.entity.Player;
import gobang.enums.ChessType;
import lombok.Data;
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
     * 加入游戏的玩家
     */
    Map<Integer, Player> players;

    /**
     * 棋盘
     */
    Board board;
}





