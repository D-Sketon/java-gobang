package gobang.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    int width = Toolkit.getDefaultToolkit().getScreenSize().width;
    int height = Toolkit.getDefaultToolkit().getScreenSize().height;

    int frameWidth = 950;
    int frameHeight = 740;

    int boardSize = 630;

    public MainFrame() {
        setTitle("508五子棋");
        setSize(frameWidth, frameHeight);

        setLocation((width - frameWidth) / 2, (height - frameHeight) / 2);
        setLayout(null);
        BoardPanel boardPanel = new BoardPanel(boardSize, 8, 30, 30);
        boardPanel.setSize(new Dimension(frameHeight - 40, frameHeight));
        boardPanel.addMouseListener(boardPanel);
        add(boardPanel);
        ControlPanel controlPanel = new ControlPanel();
        controlPanel.setLocation(frameHeight - 40, 30);
        controlPanel.setSize(new Dimension(frameWidth - frameHeight + 40, frameHeight));
        add(controlPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);

    }

}
