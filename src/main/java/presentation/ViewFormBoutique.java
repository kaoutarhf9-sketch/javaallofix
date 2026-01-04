package presentation;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import dao.Boutique;
import dao.Proprietaire;
import metier.GestionBoutique;

public class ViewFormBoutique extends JPanel {

    private ModernMainFrame frame;
    private GestionBoutique metierBoutique;

    private final Color COLOR_BORDER_DEFAULT = new Color(220, 220, 220);
    private final Color COLOR_BORDER_FOCUS = Theme.PRIMARY;

    public ViewFormBoutique(ModernMainFrame frame) {
        this.frame = frame;
        try { this.metierBoutique = new GestionBoutique(); } catch (Exception e) { e.printStackTrace(); }

        // --- CORRECTION 1 : CENTRAGE PARFAIT ---
        setLayout(new GridBagLayout()); // On utilise toujours GridBagLayout
        setBackground(Theme.BACKGROUND);
        
        // On crée des contraintes explicites pour forcer le centrage
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0; // Prend tout l'espace horizontal
        gbc.weighty = 1.0; // Prend tout l'espace vertical
        gbc.fill = GridBagConstraints.NONE; // Ne pas étirer le composant
        gbc.anchor = GridBagConstraints.CENTER; // Le placer au centre absolu

        // --- CARTE CENTRALE ---
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre portée douce
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(5, 8, getWidth() - 10, getHeight() - 10, 25, 25);
                
                // Fond Blanc
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 25, 25);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(480, 650));
        card.setOpaque(false);
        
        // --- CORRECTION 2 : ICÔNE COUPÉE ---
        // On augmente la marge du haut de 40 à 60 pour que l'emoji ait de la place
        card.setBorder(new EmptyBorder(60, 50, 40, 50));

        // --- 1. EN-TÊTE ---
        JLabel icon = new JLabel("🏪");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Nouvelle Boutique");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Theme.TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitle = new JLabel("Saisissez les détails de votre point de vente");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(Theme.TEXT_GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- 2. CHAMPS DE SAISIE ---
        JTextField txtNom = createStyledField();
        JTextField txtAdresse = createStyledField();
        JTextField txtPatente = createStyledField();
        JTextField txtTel = createStyledField();

        // --- 3. BOUTONS ---
        JButton btnEnregistrer = new JButton("ENREGISTRER") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(Theme.PRIMARY_HOVER);
                } else {
                    g2.setColor(Theme.PRIMARY);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btnEnregistrer.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEnregistrer.setForeground(Color.WHITE);
        btnEnregistrer.setContentAreaFilled(false);
        btnEnregistrer.setBorderPainted(false);
        btnEnregistrer.setFocusPainted(false);
        btnEnregistrer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEnregistrer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnEnregistrer.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnAnnuler = new JButton("Annuler et retour");
        btnAnnuler.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnAnnuler.setForeground(Theme.TEXT_GRAY);
        btnAnnuler.setContentAreaFilled(false);
        btnAnnuler.setBorderPainted(false);
        btnAnnuler.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAnnuler.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- 4. LOGIQUE MÉTIER ---
        btnEnregistrer.addActionListener(e -> {
            String nom = txtNom.getText().trim();
            String adresse = txtAdresse.getText().trim();
            String patente = txtPatente.getText().trim();
            String tel = txtTel.getText().trim();

            if (nom.isEmpty() || patente.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Le nom et la patente sont obligatoires.", "Champ manquant", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (frame.getCurrentUser() instanceof Proprietaire) {
                Proprietaire p = (Proprietaire) frame.getCurrentUser();
                Boutique b = new Boutique();
                b.setNomB(nom);
                b.setAdresse(adresse);
                b.setPatente(patente);
                b.setNumtel(tel);

                metierBoutique.creerBoutique(b, p.getIdU()); 
                
                JOptionPane.showMessageDialog(this, "La boutique '" + nom + "' a été créée !");
                frame.changerVue(ModernMainFrame.VUE_LISTE_BOUTIQUE); 
            }
        });

        btnAnnuler.addActionListener(e -> frame.changerVue(ModernMainFrame.VUE_LISTE_BOUTIQUE));

        // --- 5. ASSEMBLAGE ---
        card.add(icon);
        card.add(Box.createVerticalStrut(10));
        card.add(title);
        card.add(Box.createVerticalStrut(5));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(30));
        card.add(createInputLabel("NOM DE L'ENSEIGNE"));
        card.add(txtNom);
        card.add(Box.createVerticalStrut(15));
        card.add(createInputLabel("ADRESSE POSTALE"));
        card.add(txtAdresse);
        card.add(Box.createVerticalStrut(15));
        card.add(createInputLabel("RÉFÉRENCE PATENTE"));
        card.add(txtPatente);
        card.add(Box.createVerticalStrut(15));
        card.add(createInputLabel("TÉLÉPHONE"));
        card.add(txtTel);
        card.add(Box.createVerticalStrut(30));
        card.add(btnEnregistrer);
        card.add(Box.createVerticalStrut(15));
        card.add(btnAnnuler);

        // On ajoute la carte avec les contraintes de centrage
        add(card, gbc);
    }

    // --- HELPER METHODS ---
    private JLabel createInputLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(Theme.TEXT_GRAY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(new EmptyBorder(0, 2, 5, 0));
        return l;
    }

    private JTextField createStyledField() {
        JTextField f = new JTextField();
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setForeground(Theme.TEXT_DARK);
        CompoundBorder defaultBorder = new CompoundBorder(
                new LineBorder(COLOR_BORDER_DEFAULT, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        );
        f.setBorder(defaultBorder);
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                f.setBorder(new CompoundBorder(
                    new LineBorder(COLOR_BORDER_FOCUS, 2, true),
                    new EmptyBorder(4, 9, 4, 9)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                f.setBorder(defaultBorder);
            }
        });
        return f;
    }
}