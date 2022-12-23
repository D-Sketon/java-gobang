package gobang.ui;


import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class Toast extends JWindow {

    private final String message;
    private final Insets insets = new Insets(12, 24, 12, 24);
    private final int period;
    private final Font font;
    public static final int success = 1;
    public static final int error = 2;
    private Color background;
    private Color foreground;

    public Toast(Window parent, String message, int period, int type, Font font) {
        super(parent);
        this.message = message;
        this.period = period;
        this.font = font;
        setSize(getStringSize(font, message));
        // 相对JFrame的位置
        setLocationRelativeTo(parent);
        installTheme(type);

    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Composite oldComposite = g2.getComposite();

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(background);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        g2.setColor(foreground);
        g2.drawString(message, insets.left, fm.getAscent() + insets.top);

        g2.setComposite(oldComposite);
    }

    public void start(Consumer<?> consumer) {
        this.setVisible(true);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                setVisible(false);
                if (consumer != null) {
                    consumer.accept(null);
                }
            }
        }, period);
    }

    private void installTheme(int type) {
        switch (type) {
            case success:
                background = new Color(223, 240, 216);
                foreground = new Color(49, 112, 143);
                break;
            case error:
                background = new Color(242, 222, 222);
                foreground = new Color(221, 17, 68);
                break;
            default:
                background = new Color(0x515151);
                foreground = Color.WHITE;
                break;
        }
    }

    private Dimension getStringSize(Font font, String text) {
        FontRenderContext renderContext = new FontRenderContext(null, true, false);
        Rectangle2D bounds = font.getStringBounds(text, renderContext);
        int width = (int) bounds.getWidth() + 2 * insets.left;
        int height = (int) bounds.getHeight() + insets.top * 2;
        return new Dimension(width, height);
    }

}
