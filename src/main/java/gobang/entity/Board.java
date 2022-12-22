package gobang.entity;

import gobang.enums.ChessType;
import lombok.Data;

@Data
public class Board {

    public static final Integer WIDTH = 19;

    public static final Integer HEIGHT = 19;

    ChessType[][] chess = new ChessType[WIDTH][HEIGHT];
}
