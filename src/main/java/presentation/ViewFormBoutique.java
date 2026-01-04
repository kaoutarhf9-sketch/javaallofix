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

    public ViewFormBoutique(ModernMainFrame frame) {
        this.frame = frame;
        this.metierBoutique = new GestionBoutique();

        setLayout(new GridBagLayout()); // Pour centrer le formulaire
        setBackground(Theme.BACKGROUND);

        // --- CARTE DU FORMULAIRE ---
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 30, 30);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 30, 30);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(450, 600));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(40, 40, 40, 40));

        // --- HEADER DU FORMULAIRE ---
        JLabel icon = new JLabel("🏪");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Nouvelle Boutique");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Theme.NAVY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- CHAMPS ---
        JTextField txtNom = createStyledField("Nom de la boutique");
        JTextField txtAdresse = createStyledField("Adresse (Ville, Rue)");
        JTextField txtPatente = createStyledField("Numéro de Patente (Unique)");
        JTextField txtTel = createStyledField("Contact Téléphonique");

        // --- BOUTONS ---
        JButton btnEnregistrer = createPrimaryButton("ENREGISTRER LA BOUTIQUE");
        
        JButton btnAnnuler = new JButton("Annuler");
        btnAnnuler.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnAnnuler.setForeground(Theme.TEXT_GRAY);
        btnAnnuler.setContentAreaFilled(false);
        btnAnnuler.setBorderPainted(false);
        btnAnnuler.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAnnuler.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- LOGIQUE D'ENREGISTREMENT ---
        btnEnregistrer.addActionListener(e -> {
            String nom = txtNom.getText().trim();
            String adresse = txtAdresse.getText().trim();
            String patente = txtPatente.getText().trim();
            String tel = txtTel.getText().trim();

            if (nom.isEmpty() || patente.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Le nom et la patente sont obligatoires.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Récupération du propriétaire via la session dans ModernMainFrame
            if (frame.getCurrentUser() instanceof Proprietaire) {
                Proprietaire p = (Proprietaire) frame.getCurrentUser();
                
                Boutique b = Boutique.builder()
                        .nomB(nom)
                        .adresse(adresse)
                        .patente(patente)
                        .numtel(tel)
                        .build();

                metierBoutique.creerBoutique(b, p.getIdU()); // Utilise l'ID du user connecté
                
                JOptionPane.showMessageDialog(this, "Boutique '" + nom + "' créée avec succès !");
                frame.changerVue(ModernMainFrame.VUE_DASHBOARD_PROPRIO); 
            }
        });

        btnAnnuler.addActionListener(e -> frame.changerVue(ModernMainFrame.VUE_DASHBOARD_PROPRIO));

        // --- ASSEMBLAGE ---
        card.add(icon);
        card.add(Box.createVerticalStrut(10));
        card.add(title);
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
        card.add(Box.createVerticalStrut(10));
        card.add(btnAnnuler);

        add(card);
    }

    // --- MÉTHODES UTILITAIRES DE STYLE ---

    private JLabel createInputLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(Theme.TEXT_GRAY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(new EmptyBorder(0, 0, 5, 0));
        return l;
    }

    private JTextField createStyledField(String placeholder) {
        JTextField f = new JTextField();
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        return f;
    }

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(Theme.PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        // On force le style car certains LookAndFeel écrasent les couleurs
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        return btn;
    }
}