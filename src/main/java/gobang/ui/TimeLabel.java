package gobang.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.function.Consumer;

public class TimeLabel extends JLabel {

    private int countDown;
    private final DecimalFormat decimalFormat = new DecimalFormat("00");

    private final Consumer<?> consumer;

    Timer timer;

    public TimeLabel(Consumer<?> consumer) {
        countDown = 30;
        setText("倒计时：   00 : 30");
        this.consumer = consumer;
    }

    public void startCountDown() {
        countDown = 30;
        setText("倒计时：   00 : 30");
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (countDown != 0) {
                    countDown--;
                    String numFormat = decimalFormat.format(countDown);
                    setText("倒计时：   00 : " + numFormat);
                } else {
                    if (consumer != null) {
                        consumer.accept(null);
                    }
                    Timer s = (Timer) e.getSource();
                    s.stop();
                }
            }
        });
        timer.start();
    }

    public void stopCountDown() {
        if (timer != null) {
            timer.stop();
        }
    }
}
