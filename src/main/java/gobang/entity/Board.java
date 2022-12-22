package gobang.entity;

import gobang.enums.ChessType;
import lombok.Data;

@Data
public class Board {
    ChessType[][] chess = new ChessType[19][19];
}
