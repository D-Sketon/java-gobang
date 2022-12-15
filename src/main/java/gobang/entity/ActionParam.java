package gobang.entity;

import gobang.enums.ChessType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActionParam {

    private Integer playerId;

    private Vector2D position;
}
