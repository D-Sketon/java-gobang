package gobang.ui;

import gobang.game.GameClient;
import gobang.game.GameServer;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private static final int WINDOW_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    private static final int WINDOW_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;

    private static final int FRAME_WIDTH = 950;
    private static final int FRAME_HEIGHT = 740;
    private static final int BOARD_SIZE = 630;

    public MainFrame() {
        setTitle("508五子棋");
        setSize(FRAME_WIDTH, FRAME_HEIGHT);

        setLocation((WINDOW_WIDTH - FRAME_WIDTH) / 2, (WINDOW_HEIGHT - FRAME_HEIGHT) / 2);
        setLayout(null);
        BoardPanel boardPanel = new BoardPanel(BOARD_SIZE, 8, 30, 30);
        boardPanel.setSize(new Dimension(FRAME_HEIGHT - 40, FRAME_HEIGHT));
        boardPanel.addMouseListener(boardPanel);
        add(boardPanel);
        ControlPanel controlPanel = new ControlPanel();
        controlPanel.setLocation(FRAME_HEIGHT - 40, 30);
        controlPanel.setSize(new Dimension(FRAME_WIDTH - FRAME_HEIGHT + 40, FRAME_HEIGHT));
        add(controlPanel);

        GameClient gameClient = new GameClient(boardPanel, controlPanel);
        GameServer gameServer = new GameServer();
        boardPanel.setGameClient(gameClient);
        boardPanel.setGameServer(gameServer);
        controlPanel.setGameClient(gameClient);
        controlPanel.setGameServer(gameServer);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);

    }

    public static void setErrorMsg(String s) {
        JOptionPane.showMessageDialog(null, s, "ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
    }

    public static void setInfoMsg(String s) {
        JOptionPane.showMessageDialog(null, s, "INFO_MESSAGE",JOptionPane.INFORMATION_MESSAGE);
    }

}
