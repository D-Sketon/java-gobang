package gobang.game;

import gobang.entity.Player;
import gobang.entity.Vector2D;
import gobang.enums.GameEvent;
import gobang.enums.ChessType;

public class AbstractGameEventHandler implements GameEventAware{

    protected GameContext gameContext;

    @Override
    public void onPlayerJoin(Player player) {

    }

    @Override
    public void onTurnStart(int playerId) {

    }

    @Override
    public void onTurnEnd(int playerId, Vector2D position) {

    }

    @Override
    public void onPlayerSurrender(int playerId) {

    }

    @Override
    public void onGameResult(int playerId) {

    }

    @Override
    public void onColorReset(Player player) {

    }

    @Override
    public void onPlayerLeave(Player player) {

    }

    @Override
    public void onError(int playerId) {

    }

    @Override
    public boolean beforeEvent(GameEvent event, Object data) {
        return true;
    }

    @Override
    public void afterEvent(GameEvent event, Object data) {

    }
}
