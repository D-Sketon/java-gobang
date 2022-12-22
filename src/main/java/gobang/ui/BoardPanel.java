package gobang.ui;

import gobang.enums.ChessType;

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

    // only for test
    private ChessType currentStatus = ChessType.WHITE;

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
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX() - offsetX;
        int y = e.getY() - offsetY;
        if (x >= 0 && x <= boardSize && y >= 0 && y <= boardSize) {
            int roundX = (int) Math.round((double) x / (boardSize / 18));
            int roundY = (int) Math.round((double) y / (boardSize / 18));
            if (chess[roundX][roundY] != null) {
                return;
            }
            if (currentStatus == ChessType.BLACK) {
                currentStatus = chess[roundX][roundY] = ChessType.WHITE;
            } else {
                currentStatus = chess[roundX][roundY] = ChessType.BLACK;
            }
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
