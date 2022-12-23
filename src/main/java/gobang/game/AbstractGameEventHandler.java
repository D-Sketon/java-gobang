package gobang.game;

import gobang.adapter.CommunicationAdapter;
import gobang.player.Player;
import gobang.entity.Vector2D;
import gobang.enums.GameEvent;
import lombok.Getter;

public class AbstractGameEventHandler implements GameEventAware{

    @Getter
    protected GameContext gameContext;

    @Override
    public void onPlayerJoin(Player player) {

    }

    @Override
    public void onPlayerPrepare(int playerId) {

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
    public void onColorChange(Player player) {

    }

    @Override
    public void onPlayerLeave(int playerId) {

    }

    @Override
    public void onError(int playerId) {

    }

    @Override
    public void onSendId(int playerId) {

    }

    @Override
    public void onGameReset() {

    }


    @Override
    public boolean beforeEvent(GameEvent event, Object data) {
        return true;
    }

    @Override
    public void afterEvent(GameEvent event, Object data) {

    }
}
