package gobang.player;

import gobang.enums.ChessType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Player {

    private int playerId;

    private ChessType type;
}
