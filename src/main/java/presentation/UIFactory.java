package presentation;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class UIFactory {

    // ========================================================================================
    // 1. BOUTON "MAGIC" (DÉGRADÉ + OMBRE)
    // ========================================================================================
    public static JButton createGradientButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Animation simple au clic (légèrement plus sombre)
                Color c1 = Theme.GRADIENT_START;
                Color c2 = Theme.GRADIENT_END;
                
                if (getModel().isPressed()) {
                    c1 = c1.darker();
                    c2 = c2.darker();
                }

                // Le Dégradé
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), 0, c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40); // Arrondi fort (Pill shape)

                super.paintComponent(g);
                g2.dispose();
            }
        };

        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(Theme.FONT_BOLD);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 50)); // Taille généreuse

        // Effet "Glow" au survol (Optionnel, changement de curseur déjà géré)
        return btn;
    }

    // ========================================================================================
    // 2. BOUTON SECONDAIRE (OUTLINE / CONTOUR)
    // ========================================================================================
    public static JButton createOutlineButton(String text) {
        JButton btn = new JButton(text);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setFont(Theme.FONT_BOLD);
        btn.setForeground(Theme.TEXT_BODY);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Bordure grise par défaut
        btn.setBorder(new LineBorder(new Color(226, 232, 240), 1, true));
        btn.setPreferredSize(new Dimension(150, 40));

        // Interaction : Le contour devient coloré au survol
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBorder(new LineBorder(Theme.GRADIENT_START, 1, true));
                btn.setForeground(Theme.GRADIENT_START);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBorder(new LineBorder(new Color(226, 232, 240), 1, true));
                btn.setForeground(Theme.TEXT_BODY);
            }
        });

        return btn;
    }

    // ========================================================================================
    // 3. CARTE MODERNE (AVEC OMBRE "ELEVATION")
    // ========================================================================================
    public static JPanel createCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Ombre portée très douce (Soft Shadow)
                g2.setColor(new Color(0, 0, 0, 15)); // 15/255 opacité
                g2.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 25, 25);

                // Fond Blanc
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 25, 25);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(25, 25, 25, 25)); // Padding interne confortable
        return card;
    }

    // ========================================================================================
    // 4. CHAMP DE TEXTE (MINIMALISTE)
    // ========================================================================================
    public static JTextField createModernField() {
        JTextField field = new JTextField();
        field.setFont(Theme.FONT_REGULAR);
        field.setForeground(Theme.TEXT_HEADLINE);
        field.setCaretColor(Theme.GRADIENT_START); // Le curseur prend la couleur de la marque !
        
        // Bordure composée : Ligne grise + Padding interne
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(226, 232, 240), 1, true),
            new EmptyBorder(10, 15, 10, 15) // Padding généreux
        ));

        // Interaction : La bordure devient violette au focus
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(Theme.GRADIENT_START, 2, true), // Bordure plus épaisse et colorée
                    new EmptyBorder(9, 14, 9, 14) // Ajustement pour compenser l'épaisseur
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(226, 232, 240), 1, true),
                    new EmptyBorder(10, 15, 10, 15)
                ));
            }
        });

        return field;
    }

    // ========================================================================================
    // 5. LABEL TITRE (Pour uniformiser les titres de sections)
    // ========================================================================================
    public static JLabel createTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(Theme.FONT_TITLE);
        lbl.setForeground(Theme.TEXT_HEADLINE);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }
}