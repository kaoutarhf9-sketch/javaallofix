package presentation;

import java.awt.*;
import javax.swing.*;

public class ShadowPanel extends JPanel {
    private int arc = 18;
    private int elevation = 8;
    private Color shadowColor = new Color(0, 0, 0, 35);

    public ShadowPanel() {
        setOpaque(false);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(
                elevation, elevation, elevation + 2, elevation));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        for (int i = 0; i < elevation; i++) {
            float opacity = (float) shadowColor.getAlpha() / 255f;
            opacity *= (1f - (float)i/elevation);
            g2.setColor(new Color(shadowColor.getRed(), shadowColor.getGreen(), shadowColor.getBlue(), (int)(opacity*255)));
            g2.fillRoundRect(i, i, w - i*2, h - i*2, arc, arc);
        }

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, w - elevation, h - elevation, arc, arc);

        g2.dispose();
        super.paintComponent(g);
    }
}
