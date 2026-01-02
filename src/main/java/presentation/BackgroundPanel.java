package presentation;

import java.awt.*;
import java.net.URL;
import javax.swing.*;

public class BackgroundPanel extends JPanel {
    private Image bg;

    public BackgroundPanel(String urlStr) {
        try {
            URL url = new URL(urlStr);
            bg = new ImageIcon(url).getImage();
        } catch (Exception e) {
            e.printStackTrace();
            bg = null;
        }
        setLayout(new GridBagLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bg != null) {
            g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
