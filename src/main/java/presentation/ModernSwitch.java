package presentation;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ModernSwitch extends JComponent {

    private boolean selected = false;
    private boolean hover = false;

    private float animation = 0f; // 0 = OFF, 1 = ON
    private Timer timer;

    public ModernSwitch() {
        setPreferredSize(new Dimension(52, 28));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setOpaque(false);

        // Animation fluide (60 FPS)
        timer = new Timer(16, e -> animate());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selected = !selected;
                timer.start();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                repaint();
            }
        });
    }

    // =====================================================================================
    // LOGIQUE D'ANIMATION (EASING)
    // =====================================================================================
    private void animate() {
        float target = selected ? 1f : 0f;
        animation += (target - animation) * 0.2f;

        if (Math.abs(target - animation) < 0.01f) {
            animation = target;
            timer.stop();
        }
        repaint();
    }

    // =====================================================================================
    // API PUBLIQUE
    // =====================================================================================
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean value) {
        selected = value;
        animation = value ? 1f : 0f;
        repaint();
    }

    // =====================================================================================
    // RENDU PREMIUM
    // =====================================================================================
    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // INTERPOLATION POSITION KNOB
        int padding = 3;
        int knobSize = h - padding * 2;
        int xOff = padding + Math.round((w - knobSize - padding * 2) * animation);

        // COULEUR DE FOND
        Color offColor = new Color(210, 210, 210);
        Color onColor  = Theme.PRIMARY;
        Color bgColor  = blend(offColor, onColor, animation);

        // FOND SWITCH
        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, w, h, h, h);

        // GLOW LÉGER QUAND ACTIF
        if (animation > 0.2f) {
            g2.setColor(new Color(
                    Theme.PRIMARY.getRed(),
                    Theme.PRIMARY.getGreen(),
                    Theme.PRIMARY.getBlue(),
                    (int) (60 * animation)));
            g2.fillRoundRect(-2, -2, w + 4, h + 4, h + 4, h + 4);
        }

        // OMBRE DU KNOB
        g2.setColor(new Color(0, 0, 0, 40));
        g2.fillOval(xOff, padding + 1, knobSize, knobSize);

        // KNOB BLANC
        g2.setColor(Color.WHITE);
        g2.fillOval(xOff, padding, knobSize, knobSize);

        // HOVER OUTLINE
        if (hover) {
            g2.setColor(new Color(0, 0, 0, 40));
            g2.drawRoundRect(0, 0, w - 1, h - 1, h, h);
        }

        g2.dispose();
    }

    // =====================================================================================
    // UTILS
    // =====================================================================================
    private Color blend(Color c1, Color c2, float ratio) {
        float ir = 1f - ratio;
        return new Color(
                (int) (c1.getRed()   * ir + c2.getRed()   * ratio),
                (int) (c1.getGreen() * ir + c2.getGreen() * ratio),
                (int) (c1.getBlue()  * ir + c2.getBlue()  * ratio)
        );
    }
}
