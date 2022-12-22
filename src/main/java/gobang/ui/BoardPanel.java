package gobang.ui;

import gobang.entity.Vector2D;
import gobang.enums.ChessType;
import gobang.game.GameClient;
import gobang.game.GameServer;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class BoardPanel extends JPanel implements MouseListener {

    private final int boardSize;

    private final int ovalRadius;

    private final int chessRadius = 28;

    private final int offsetX;

    private final int offsetY;

    private final ChessType[][] chess = new ChessType[19][19];

    // --- 游戏 相关
    @Setter
    private GameClient gameClient;

    @Setter
    private GameServer gameServer;

    public BoardPanel(int borderSize, int ovalRadius, int offsetX, int offsetY) {
        super();
        this.setBackground(Color.orange);
        this.boardSize = borderSize;
        this.ovalRadius = ovalRadius;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // 消除锯齿
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setStroke(new BasicStroke(1.5f));
        for (int i = 0; i < 19; i++) {
            g.drawLine(offsetX, offsetY + boardSize / 18 * i, offsetX + boardSize, offsetY + boardSize / 18 * i);
            g.drawLine(offsetX + boardSize / 18 * i, offsetY, offsetX + boardSize / 18 * i, offsetY + boardSize);
        }

        int x1 = offsetX + boardSize / 18 * 3 - ovalRadius / 2;
        int x2 = offsetX + boardSize / 18 * 9 - ovalRadius / 2;
        int x3 = offsetX + boardSize / 18 * 15 - ovalRadius / 2;
        int y1 = offsetY + boardSize / 18 * 3 - ovalRadius / 2;
        int y2 = offsetY + boardSize / 18 * 9 - ovalRadius / 2;
        int y3 = offsetY + boardSize / 18 * 15 - ovalRadius / 2;
        g.fillOval(x1, y1, ovalRadius, ovalRadius);
        g.fillOval(x2, y1, ovalRadius, ovalRadius);
        g.fillOval(x3, y1, ovalRadius, ovalRadius);
        g.fillOval(x1, y2, ovalRadius, ovalRadius);
        g.fillOval(x2, y2, ovalRadius, ovalRadius);
        g.fillOval(x3, y2, ovalRadius, ovalRadius);
        g.fillOval(x1, y3, ovalRadius, ovalRadius);
        g.fillOval(x2, y3, ovalRadius, ovalRadius);
        g.fillOval(x3, y3, ovalRadius, ovalRadius);

        drawChess(g);
    }

    private void drawChess(Graphics g) {
        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 19; j++) {
                if (chess[i][j] == ChessType.BLACK) {
                    g.setColor(Color.BLACK);
                    g.fillOval(offsetX + boardSize / 18 * i - chessRadius / 2, offsetY + boardSize / 18 * j - chessRadius / 2, chessRadius, chessRadius);
                } else if (chess[i][j] == ChessType.WHITE) {
                    g.setColor(Color.WHITE);
                    g.fillOval(offsetX + boardSize / 18 * i - chessRadius / 2, offsetY + boardSize / 18 * j - chessRadius / 2, chessRadius, chessRadius);
                    g.setColor(Color.BLACK);
                    g.drawOval(offsetX + boardSize / 18 * i - chessRadius / 2, offsetY + boardSize / 18 * j - chessRadius / 2, chessRadius, chessRadius);
                }
            }
        }
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        // do nothing
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!gameClient.isTurn()) {
            return;
        }
        int x = e.getX() - offsetX;
        int y = e.getY() - offsetY;
        if (x >= 0 && x <= boardSize && y >= 0 && y <= boardSize) {
            int roundX = (int) Math.round((double) x / (boardSize / 18));
            int roundY = (int) Math.round((double) y / (boardSize / 18));
            if (chess[roundX][roundY] != null) {
                return;
            }
            gameClient.playerChess(new Vector2D(roundX, roundY));
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // do nothing
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // do nothing
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // do nothing
    }

    public void onTurnEnd(ChessType type, Vector2D position) {
        chess[position.getX()][position.getY()] = type;
        repaint();
    }

    public void onGameResult() {
        for (int i = 0; i < chess[0].length; i++) {
            for (int j = 0; j < chess.length; j++) {
                chess[i][j] = null;
            }
        }
        repaint();
    }
}
