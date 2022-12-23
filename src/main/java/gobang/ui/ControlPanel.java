package gobang.ui;

import gobang.adapter.LocalGameAdapter;
import gobang.adapter.RemoteGameAdapter;
import gobang.enums.GameStatus;
import gobang.game.GameClient;
import gobang.game.GameServer;
import gobang.player.Player;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.function.Consumer;

public class ControlPanel extends JPanel {

    // --- 配置 相关
    public static final int FONT_SIZE = 18;
    public static final Font FONT = new Font("Microsoft Yahei", Font.BOLD, FONT_SIZE);
    public static final FontRenderContext RENDER_CONTEXT = new FontRenderContext(new AffineTransform(), true, true);

    // --- Swing 相关
    private final JButton startServer;
    private final JButton joinServer;
    private final JButton startGame;
    private final JButton surrender;

    private final JButton ready;
    private final TimeLabel countDown;

    private final JLabel blackPlayer;

    private final JLabel whitePlayer;

    // --- 游戏 相关
    @Setter
    private GameClient gameClient;
    @Setter
    private GameServer gameServer;
    private GameStatus gameStatus = GameStatus.INIT;

    // 长时间未准备的Consumer
    private final Consumer<Object> consumer = new Consumer<Object>() {
        @Override
        public void accept(Object o) {
            gameClient.getClientOnline().getEventExecutors().shutdownGracefully();
            MainFrame.setErrorMsg("长时间未准备，自动断开连接");

            startServer.setVisible(true);
            joinServer.setVisible(true);
            // 默认不可见
            countDown.setVisible(false);
            countDown.stopCountDown();
            startGame.setVisible(false);
            surrender.setVisible(false);
            blackPlayer.setVisible(false);
            whitePlayer.setVisible(false);
            ready.setVisible(false);
            gameStatus = GameStatus.INIT;
        }
    };

    public ControlPanel() {
        Box vBox = Box.createVerticalBox();
        int btnHeight = 50;
        int btnWidth = 200;
        startServer = new CustomButton(btnWidth, btnHeight, "开启服务器");
        joinServer = new CustomButton(btnWidth, btnHeight, "加入服务器");
        startGame = new CustomButton(btnWidth, btnHeight, "开始游戏");
        surrender = new CustomButton(btnWidth, btnHeight, "投降");
        ready = new CustomButton(btnWidth, btnHeight, "准备");
        countDown = new TimeLabel(new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                gameClient.surrender();
            }
        });
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
        vBox.add(surrender);
        vBox.add(ready);
        add(vBox);

        // 默认不可见
        countDown.setVisible(false);
        startGame.setVisible(false);
        surrender.setVisible(false);
        blackPlayer.setVisible(false);
        whitePlayer.setVisible(false);
        ready.setVisible(false);
    }

    private void initBinding() {
        startServer.addActionListener(e -> {
            new Thread(() -> gameServer.startServer(), "Server Netty Thread").start();
            LocalGameAdapter localGameAdapter = new LocalGameAdapter(gameServer);
            gameClient.setCommunicationAdapter(localGameAdapter);
            gameServer.onLocalJoin(gameClient);
        });
        joinServer.addActionListener(e -> {
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
        });
        startGame.addActionListener(e -> {
            if (gameClient.getGameContext().getPlayers().size() != 2) {
                MainFrame.setErrorMsg("有玩家未准备");

            } else {
                boolean b = true;
                for (Player player : gameClient.getGameContext().getPlayers().values()) {
                    if (!player.isPrepared()) {
                        MainFrame.setErrorMsg("有玩家未准备");
                        b = false;
                        break;
                    }
                }
                if (b) {
                    gameServer.startGame();
                    startGame.setVisible(false);
                }
            }
        });
        surrender.addActionListener(e -> {
            onSelfTurnEnd();
            gameClient.surrender();
        });
        ready.addActionListener(e -> {
            gameClient.prepare();
            countDown.stopCountDown();
            ready.setVisible(false);
            countDown.setVisible(false);
        });
    }

    public void onPlayerJoin(Player player) {
        if (gameStatus == GameStatus.INIT) {
            startServer.setVisible(false);
            joinServer.setVisible(false);
        }
        blackPlayer.setVisible(true);
        whitePlayer.setVisible(true);
        // 本地玩家才可以开始游戏
        if (gameClient.getCommunicationAdapter() instanceof LocalGameAdapter) {
            // 本地玩家自动准备
            if (!startGame.isVisible()) {
                gameClient.prepare();
                startGame.setVisible(true);
            }
        } else {
            if (!ready.isVisible()) {
                // 弹出计时器要求准备
                countDown.setVisible(true);
                countDown.startCountDown(consumer);
                ready.setVisible(true);
            }
        }
        gameStatus = GameStatus.BEFORE_START;
    }

    public void onTurnStart() {
        if (gameStatus != GameStatus.PLAYING) {
            gameStatus = GameStatus.PLAYING;
            surrender.setVisible(true);
        }
    }

    public void onSelfTurnStart() {
        countDown.setVisible(true);
        countDown.startCountDown(null);
    }

    public void onSelfTurnEnd() {
        countDown.setVisible(false);
        countDown.stopCountDown();
    }

    public void onPreGameResult() {
        countDown.setVisible(false);
        countDown.stopCountDown();
        surrender.setVisible(false);
    }

    public void onGameResult() {
        gameStatus = GameStatus.BEFORE_START;
        onSelfTurnEnd();
        surrender.setVisible(false);
        if (gameClient.getCommunicationAdapter() instanceof LocalGameAdapter) {
            gameClient.prepare();
            startGame.setVisible(true);
        } else {
            // 弹出计时器要求准备
            countDown.setVisible(true);
            countDown.startCountDown(consumer);
            ready.setVisible(true);
        }
    }

    public void changePlayerInfo(String black, String white) {
        blackPlayer.setText(black);
        whitePlayer.setText(white);
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
