package presentation;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class ServiceCard extends JPanel {

    private boolean hover = false;
    private String title;
    private String price;
    private String icon;
    private String delay;

    public ServiceCard(String icon, String title, String price, String delay) {
        this.icon = icon;
        this.title = title;
        this.price = price;
        this.delay = delay;

        setLayout(null); // Layout absolu pour un contrôle au pixel près
        setPreferredSize(new Dimension(280, 340)); // Taille fixe standard
        setOpaque(false); // Important pour la transparence des coins
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Gestion de l'animation Hover
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
            public void mouseExited(MouseEvent e) { hover = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        
        // Activation de l'anti-aliasing pour un rendu HD
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int shadowGap = 15;
        
        // Animation : La carte "monte" de 5 pixels au survol
        int yOffset = hover ? -5 : 0;

        // 1. Dessin de l'ombre portée (Drop Shadow)
        if(hover) {
            // Ombre bleue/violette au survol (effet néon subtil)
            g2.setColor(new Color(79, 70, 229, 40)); 
            g2.fillRoundRect(10, 20 + yOffset, w - 20, h - 30, 30, 30);
        } else {
            // Ombre grise classique au repos
            g2.setColor(new Color(0, 0, 0, 20));
            g2.fillRoundRect(10, 15, w - 20, h - 25, 30, 30);
        }

        // 2. Fond de la carte (Blanc)
        g2.setColor(Theme.SURFACE);
        g2.fillRoundRect(5, 0 + yOffset, w - 10, h - shadowGap, 25, 25);

        // 3. Dessin du contenu
        
        // Cercle de fond de l'icône
        g2.setColor(Theme.BACKGROUND);
        g2.fillOval(25, 25 + yOffset, 60, 60);
        
        // Emoji (Icone)
        g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        g2.setColor(Color.BLACK);
        
        // Centrage manuel de l'emoji
        FontMetrics fmIcon = g2.getFontMetrics();
        int iconX = 25 + (60 - fmIcon.stringWidth(icon)) / 2;
        int iconY = 25 + ((60 - fmIcon.getHeight()) / 2) + fmIcon.getAscent() + yOffset;
        g2.drawString(icon, iconX, iconY);

        // Titre du service
        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2.setColor(Theme.TEXT_DARK);
        g2.drawString(title, 25, 115 + yOffset);

        // Délai de réparation
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2.setColor(Theme.SUCCESS);
        g2.drawString("⚡ Réparation en " + delay, 25, 140 + yOffset);

        // Ligne de séparation décorative
        g2.setColor(Theme.BACKGROUND);
        g2.setStroke(new BasicStroke(1f));
        g2.drawLine(25, 170 + yOffset, w-35, 170 + yOffset);

        // Label "À partir de"
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.setColor(Theme.TEXT_GRAY);
        g2.drawString("À partir de", 25, 260 + yOffset);

        // Prix
        g2.setFont(new Font("Segoe UI", Font.BOLD, 26));
        g2.setColor(Theme.PRIMARY);
        g2.drawString(price, 25, 295 + yOffset);

        // Bouton "Go" (Cercle bleu en bas à droite)
        GradientPaint gp = new GradientPaint(0, 0, Theme.PRIMARY, w, h, Theme.PRIMARY_HOVER);
        g2.setPaint(gp);
        g2.fillRoundRect(w - 75, 250 + yOffset, 50, 50, 15, 15);
        
        // Flèche dans le bouton
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fmArrow = g2.getFontMetrics();
        int arrowX = (w - 75) + (50 - fmArrow.stringWidth("→")) / 2;
        int arrowY = (250 + yOffset) + ((50 - fmArrow.getHeight()) / 2) + fmArrow.getAscent();
        g2.drawString("→", arrowX, arrowY);

        // Bordure colorée au survol
        if (hover) {
            g2.setColor(Theme.PRIMARY);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(5, 0 + yOffset, w - 10, h - shadowGap, 25, 25);
        }

        g2.dispose();
    }
}