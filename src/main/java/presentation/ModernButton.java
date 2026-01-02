package presentation;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ModernButton extends JButton {

    private boolean hover = false;
    private boolean pressed = false;
    private int arc = 20;

    public ModernButton(String text) {
        super(text);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setForeground(Color.WHITE);
        setFont(new Font("Inter", Font.BOLD, 14));

        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
            public void mouseExited(MouseEvent e) { hover = false; pressed = false; repaint(); }
            public void mousePressed(MouseEvent e) { pressed = true; repaint(); }
            public void mouseReleased(MouseEvent e) { pressed = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Gradient subtil pour relief
        Color top = pressed ? getBackground().darker() : getBackground();
        Color bottom = pressed ? getBackground() : getBackground().brighter();
        g2.setPaint(new GradientPaint(0, 0, top, 0, h, bottom));
        g2.fillRoundRect(0, 0, w, h, arc, arc);

        // Ombre interne (pour relief)
        g2.setColor(new Color(0,0,0,30));
        g2.drawRoundRect(1,1, w-2, h-2, arc, arc);

        // Texte centré
        FontMetrics fm = g2.getFontMetrics();
        int tx = (w - fm.stringWidth(getText())) / 2;
        int ty = (h + fm.getAscent()) / 2 - 2;
        g2.setColor(getForeground());
        g2.drawString(getText(), tx, ty);

        g2.dispose();
    }
}
