package gobang.ui;

import gobang.adapter.LocalGameAdapter;
import gobang.adapter.RemoteGameAdapter;
import gobang.enums.ChessType;
import gobang.enums.GameStatus;
import gobang.game.GameClient;
import gobang.game.GameServer;
import gobang.player.Player;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

public class ControlPanel extends JPanel {

    // --- 配置 相关
    private static final int FONT_SIZE = 18;
    private static final Font FONT = new Font("Microsoft Yahei", Font.BOLD, FONT_SIZE);
    private static final FontRenderContext RENDER_CONTEXT = new FontRenderContext(new AffineTransform(), true, true);

    // --- Swing 相关
    private final JButton startServer;
    private final JButton joinServer;
    private final JButton startGame;
    private final JButton surrender;
    private final TimeLabel countDown;

    private final JLabel blackPlayer;

    private final JLabel whitePlayer;

    // --- 游戏 相关
    @Setter
    private GameClient gameClient;
    @Setter
    private GameServer gameServer;

    private GameStatus gameStatus = GameStatus.INIT;

    public ControlPanel() {
        Box vBox = Box.createVerticalBox();
        int btnHeight = 50;
        int btnWidth = 200;
        startServer = new CustomButton(btnWidth, btnHeight, "开启服务器");
        joinServer = new CustomButton(btnWidth, btnHeight, "加入服务器");
        startGame = new CustomButton(btnWidth, btnHeight, "开始游戏");
        surrender = new CustomButton(btnWidth, btnHeight, "投降");
        countDown = new TimeLabel(null);
        countDown.setFont(FONT);
        blackPlayer = new JLabel("●      黑方玩家 　　　");
        blackPlayer.setFont(FONT);
        whitePlayer = new JLabel("○      白方玩家 　　　");
        whitePlayer.setFont(FONT);
        initBinding();

        vBox.add(startServer);
        vBox.add(blackPlayer);
        vBox.add(Box.createVerticalStrut(30));
        vBox.add(joinServer);
        vBox.add(whitePlayer);
        vBox.add(Box.createVerticalStrut(400));
        vBox.add(countDown);
        vBox.add(Box.createVerticalStrut(30));
        vBox.add(startGame);
        vBox.add(Box.createVerticalStrut(30));
        vBox.add(surrender);
        add(vBox);

        // 默认不可见
        countDown.setVisible(false);
        startGame.setVisible(false);
        surrender.setVisible(false);
        blackPlayer.setVisible(false);
        whitePlayer.setVisible(false);
    }

    private void initBinding() {
        startServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> gameServer.startServer(), "Server Netty Thread").start();
                LocalGameAdapter localGameAdapter = new LocalGameAdapter(gameServer);
                gameClient.setCommunicationAdapter(localGameAdapter);
                gameServer.onLocalJoin(gameClient);
            }
        });
        joinServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ipAddress = JOptionPane.showInputDialog(null, "请输入IP地址");
                RemoteGameAdapter remoteGameAdapter = new RemoteGameAdapter();
                gameClient.setCommunicationAdapter(remoteGameAdapter);
                remoteGameAdapter.setSelf(gameClient);
                new Thread(() -> {
                    try {
                        gameClient.getClientOnline().initNetty(gameClient, ipAddress);
                    } catch (Exception e0) {
                        e0.printStackTrace();
                    }
                }, "Client Netty Thread").start();
            }
        });
        startGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameClient.getGameContext().getPlayers().size() == 2) {
                    gameServer.startGame();
                    startGame.setVisible(false);
                } else {
                    MainFrame.setErrorMsg("有玩家未准备");
                }

            }
        });
        surrender.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameClient.surrender();
            }
        });

    }

    public void onPlayerJoin(Player player) {
        if (gameStatus == GameStatus.INIT) {
            startServer.setVisible(false);
            joinServer.setVisible(false);
        }
        blackPlayer.setVisible(true);
        whitePlayer.setVisible(true);
        if (player.getType() == ChessType.BLACK) {
            blackPlayer.setText("●      黑方玩家 已准备");
        } else {
            whitePlayer.setText("○      白方玩家 已准备");
        }
        // 本地玩家才可以开始游戏
        if (gameClient.getCommunicationAdapter() instanceof LocalGameAdapter) {
            startGame.setVisible(true);
        } else {
            // 远程玩家默认准备好了，本地玩家也是
            blackPlayer.setText("●      黑方玩家 已准备");
            whitePlayer.setText("○      白方玩家 已准备");
        }
        gameStatus = GameStatus.BEFORE_START;
    }

    static class CustomButton extends JButton {
        public int width;
        public int height;
        public String text;

        private final int centerX;
        private final int centerY;

        private boolean isPressed = false;
        private boolean isEntered = false;
        private boolean isEnabled = true;

        public CustomButton(int width, int height, String text) {
            this.width = width;
            this.height = height;
            this.text = text;

            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setPreferredSize(new Dimension(width, height));
            setMaximumSize(new Dimension(width, height));
            setMinimumSize(new Dimension(width, height));

            Rectangle rectangle = FONT.getStringBounds(text, RENDER_CONTEXT).getBounds();
            centerX = (width - rectangle.width) / 2;
            centerY = (int) ((height - rectangle.height) / 2 + rectangle.height * 0.8);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // do nothing
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    isPressed = true;
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    isPressed = false;
                    repaint();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    isEntered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isEntered = false;
                    repaint();
                }
            });

        }

        @Override
        public void paintBorder(Graphics g) {
            super.paintBorder(g);
            Graphics2D g2 = (Graphics2D) g;
            // 消除锯齿
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
            if (isEntered)
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7F));
            if (isPressed)
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4F));
            if (!isEnabled)
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4F));
            g2.setColor(new Color(0, 152, 208));
            g2.fillRoundRect(3, 3, width - 6, height - 6, 10, 10);
            g2.setColor(Color.WHITE);
            g2.setFont(FONT);
            g2.drawString(text, centerX, centerY);
        }

        @Override
        public void setEnabled(boolean b) {
            super.setEnabled(b);
            this.isEnabled = b;
            if (!isEnabled) {
                isEntered = true;
            } else {
                isPressed = isEntered = false;
            }
            repaint();
        }
    }

}
