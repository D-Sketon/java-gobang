package gobang.game;

import gobang.entity.Board;
import gobang.player.Player;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    public GameContext() {
        this.gameId = UUID.randomUUID().toString().substring(0, 5);
        this.players = new HashMap<>();
        this.board = new Board();
        System.out.println(this.board.getChess()[0][0]);
    }
}





