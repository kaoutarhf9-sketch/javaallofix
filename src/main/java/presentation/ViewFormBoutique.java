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

    // --- VARIABLES GLOBALES (Pour y accéder dans les méthodes) ---
    private JTextField txtNom, txtAdresse, txtPatente, txtTel;
    private JLabel lblTitle;
    private JButton btnEnregistrer;
    private Boutique boutiqueEnEdition = null; // Si null = Mode Création, sinon = Mode Modif

    private final Color COLOR_BORDER_DEFAULT = new Color(220, 220, 220);
    private final Color COLOR_BORDER_FOCUS = Theme.PRIMARY;

    public ViewFormBoutique(ModernMainFrame frame) {
        this.frame = frame;
        try { this.metierBoutique = new GestionBoutique(); } catch (Exception e) { e.printStackTrace(); }

        // --- MISE EN PAGE CENTRÉE ---
        setLayout(new GridBagLayout());
        setBackground(Theme.BACKGROUND);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        // --- CARTE ---
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 15)); // Ombre
                g2.fillRoundRect(5, 8, getWidth() - 10, getHeight() - 10, 25, 25);
                g2.setColor(Color.WHITE); // Fond
                g2.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 25, 25);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(480, 650));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(60, 50, 40, 50));

        // --- COMPOSANTS ---
        JLabel icon = new JLabel("🏪");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblTitle = new JLabel("Nouvelle Boutique");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Theme.TEXT_BODY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitle = new JLabel("Saisissez les détails de votre point de vente");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(Theme.TEXT_LIGHT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Initialisation des champs globaux
        txtNom = createStyledField();
        txtAdresse = createStyledField();
        txtPatente = createStyledField();
        txtTel = createStyledField();

        // --- BOUTON D'ACTION ---
        btnEnregistrer = new JButton("ENREGISTRER") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? Theme.PRIMARY_HOVER : Theme.PRIMARY);
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
        btnAnnuler.setForeground(Theme.TEXT_LIGHT);
        btnAnnuler.setContentAreaFilled(false);
        btnAnnuler.setBorderPainted(false);
        btnAnnuler.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAnnuler.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- LOGIQUE DU BOUTON (CRÉATION OU MODIFICATION) ---
        btnEnregistrer.addActionListener(e -> validerFormulaire());

        btnAnnuler.addActionListener(e -> frame.changerVue(ModernMainFrame.VUE_LISTE_BOUTIQUE));

        // --- ASSEMBLAGE ---
        card.add(icon);
        card.add(Box.createVerticalStrut(10));
        card.add(lblTitle);
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

        add(card, gbc);
    }

    // =========================================================================
    // LA MÉTHODE MANQUANTE : setBoutiqueEnEdition
    // =========================================================================
    public void setBoutiqueEnEdition(Boutique b) {
        this.boutiqueEnEdition = b; // On stocke l'objet
        
        if (b != null) {
            // MODE MODIFICATION : On remplit les champs
            txtNom.setText(b.getNomB());
            txtAdresse.setText(b.getAdresse());
            txtPatente.setText(b.getPatente());
            txtTel.setText(b.getNumtel());
            
            lblTitle.setText("Modifier la Boutique");
            btnEnregistrer.setText("ENREGISTRER LES MODIFICATIONS");
        } else {
            // MODE CRÉATION (ou Reset) : On vide tout
            txtNom.setText("");
            txtAdresse.setText("");
            txtPatente.setText("");
            txtTel.setText("");
            
            lblTitle.setText("Nouvelle Boutique");
            btnEnregistrer.setText("ENREGISTRER");
        }
    }

    private void validerFormulaire() {
        String nom = txtNom.getText().trim();
        String adresse = txtAdresse.getText().trim();
        String patente = txtPatente.getText().trim();
        String tel = txtTel.getText().trim();

        if (nom.isEmpty() || patente.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nom et Patente obligatoires.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (boutiqueEnEdition == null) {
            // === CAS 1 : CRÉATION ===
            if (frame.getCurrentUser() instanceof Proprietaire) {
                Proprietaire p = (Proprietaire) frame.getCurrentUser();
                Boutique b = new Boutique();
                b.setNomB(nom);
                b.setAdresse(adresse);
                b.setPatente(patente);
                b.setNumtel(tel);

                metierBoutique.creerBoutique(b, p.getIdU());
                JOptionPane.showMessageDialog(this, "Boutique créée avec succès !");
            }
        } else {
            // === CAS 2 : MODIFICATION ===
            boutiqueEnEdition.setNomB(nom);
            boutiqueEnEdition.setAdresse(adresse);
            boutiqueEnEdition.setPatente(patente);
            boutiqueEnEdition.setNumtel(tel);

            // Assurez-vous d'avoir une méthode update/modifier dans votre couche métier
            // Exemple : metierBoutique.modifierBoutique(boutiqueEnEdition); 
            // Si vous n'en avez pas, il faudra l'ajouter dans GestionBoutique.
            try {
                // metierBoutique.update(boutiqueEnEdition); // Adaptez selon votre méthode métier
                 JOptionPane.showMessageDialog(this, "Modification simulée (ajoutez le code métier) !");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Retour à la liste
        frame.changerVue(ModernMainFrame.VUE_LISTE_BOUTIQUE);
    }

    // --- HELPER METHODS DE STYLE ---
    private JLabel createInputLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(Theme.TEXT_LIGHT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(new EmptyBorder(0, 2, 5, 0));
        return l;
    }

    private JTextField createStyledField() {
        JTextField f = new JTextField();
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setForeground(Theme.TEXT_BODY);
        CompoundBorder defaultBorder = new CompoundBorder(
                new LineBorder(COLOR_BORDER_DEFAULT, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        );
        f.setBorder(defaultBorder);
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBorder(new CompoundBorder(
                    new LineBorder(COLOR_BORDER_FOCUS, 2, true),
                    new EmptyBorder(4, 9, 4, 9)
                ));
            }
            public void focusLost(FocusEvent e) { f.setBorder(defaultBorder); }
        });
        return f;
    }
}