package presentation;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class UIFactory {

    // ========================================================================================
    // 1. BOUTON PRIMAIRE (BLEU VIF - FLAT DESIGN)
    // ========================================================================================
    public static JButton createGradientButton(String text) { // On garde le nom mais on modernise le style
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gestion de la couleur (Normal vs Hover vs Click)
                Color bgColor = Theme.PRIMARY;
                
                if (getModel().isPressed()) {
                    bgColor = Theme.PRIMARY.darker();
                } else if (getModel().isRollover()) {
                    bgColor = Theme.PRIMARY_HOVER;
                }

                g2.setColor(bgColor);
                // Arrondi plus subtil (12px) pour un look "Pro"
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // Dessin du texte géré par le super, mais on force la couleur avant
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
        btn.setPreferredSize(new Dimension(200, 45)); // Hauteur standardisée

        return btn;
    }

    // ========================================================================================
    // 2. BOUTON SECONDAIRE (OUTLINE / CONTOUR)
    // ========================================================================================
    public static JButton createOutlineButton(String text) {
        JButton btn = new JButton(text);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setFont(Theme.FONT_REGULAR);
        btn.setForeground(Theme.TEXT_BODY);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Bordure grise fine par défaut
        btn.setBorder(new LineBorder(new Color(203, 213, 225), 1, true)); // Gris slate clair
        btn.setPreferredSize(new Dimension(150, 40));

        // Interaction : Le contour devient Bleu (Primary) au survol
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBorder(new LineBorder(Theme.PRIMARY, 1, true));
                btn.setForeground(Theme.PRIMARY);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBorder(new LineBorder(new Color(203, 213, 225), 1, true));
                btn.setForeground(Theme.TEXT_BODY);
            }
        });

        return btn;
    }

    // ========================================================================================
    // 3. CARTE MODERNE (OMBRE DOUCE + FOND BLANC)
    // ========================================================================================
    public static JPanel createCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Ombre portée très douce (Soft Shadow)
                g2.setColor(new Color(148, 163, 184, 50)); // Gris bleuté transparent
                g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 15, 15);

                // Fond Blanc
                g2.setColor(Theme.PANEL_BG);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 15, 15);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 20, 20, 20)); 
        return card;
    }

    // ========================================================================================
    // 4. CHAMP DE TEXTE (INPUT)
    // ========================================================================================
    public static JTextField createModernField() {
        JTextField field = new JTextField();
        styleTextField(field);
        return field;
    }
    
    // Ajout pour les mots de passe !
    public static JPasswordField createModernPasswordField() {
        JPasswordField field = new JPasswordField();
        styleTextField(field);
        return field;
    }

    // Méthode privée pour appliquer le style aux deux types de champs
    private static void styleTextField(JTextField field) {
        field.setFont(Theme.FONT_REGULAR);
        field.setForeground(Theme.TEXT_HEADLINE);
        field.setCaretColor(Theme.PRIMARY);
        field.setBackground(Color.WHITE);
        
        // Bordure composée : Ligne grise + Padding interne
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(203, 213, 225), 1, true), // Gris clair
            new EmptyBorder(8, 12, 8, 12) // Padding
        ));

        // Interaction Focus : Bordure Bleue (Primary)
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(Theme.PRIMARY, 2, true), // Bordure bleue épaisse
                    new EmptyBorder(7, 11, 7, 11) // Ajustement padding pour compenser bordure
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(203, 213, 225), 1, true),
                    new EmptyBorder(8, 12, 8, 12)
                ));
            }
        });
    }

    // ========================================================================================
    // 5. LABELS & TITRES
    // ========================================================================================
    public static JLabel createTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(Theme.FONT_TITLE);
        lbl.setForeground(Theme.TEXT_HEADLINE);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }
    
    public static JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(Theme.FONT_REGULAR);
        lbl.setForeground(Theme.TEXT_BODY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }
}