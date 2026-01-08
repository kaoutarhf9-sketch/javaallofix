package presentation;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import dao.Boutique;
import metier.GestionBoutique;

public class ViewEditBoutique extends JPanel {

    private ModernMainFrame frame;
    private GestionBoutique metierBoutique;
    private Boutique boutiqueActuelle;

    // Champs du formulaire
    private JTextField txtNom;
    private JTextField txtAdresse;
    private JTextField txtPatente;
    private JTextField txtTel;

    public ViewEditBoutique(ModernMainFrame frame, Boutique b) {
        this.frame = frame;
        this.boutiqueActuelle = b;
        this.metierBoutique = new GestionBoutique();

        setLayout(new GridBagLayout());
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

        // --- HEADER ---
        JLabel icon = new JLabel("📝");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Modifier la Boutique");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Theme.PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- INITIALISATION ET PRÉ-REMPLISSAGE ---
        txtNom = createStyledField();
        txtNom.setText(b.getNomB());

        txtAdresse = createStyledField();
        txtAdresse.setText(b.getAdresse());

        txtPatente = createStyledField();
        txtPatente.setText(b.getPatente());

        txtTel = createStyledField();
        txtTel.setText(b.getNumtel());

        // --- BOUTONS ---
        JButton btnUpdate = createPrimaryButton("ENREGISTRER LES MODIFICATIONS");
        JButton btnCancel = new JButton("Annuler et retourner à la liste");
        styleSecondaryButton(btnCancel);

        // --- LOGIQUE DE MISE À JOUR ---
        btnUpdate.addActionListener(e -> {
            // Mise à jour de l'objet boutique
            boutiqueActuelle.setNomB(txtNom.getText().trim());
            boutiqueActuelle.setAdresse(txtAdresse.getText().trim());
            boutiqueActuelle.setPatente(txtPatente.getText().trim());
            boutiqueActuelle.setNumtel(txtTel.getText().trim());

            try {
                metierBoutique.modifierBoutique(boutiqueActuelle);
                JOptionPane.showMessageDialog(this, "Boutique mise à jour avec succès !");
                frame.changerVue(ModernMainFrame.VUE_LISTE_BOUTIQUE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la modification : " + ex.getMessage());
            }
        });

        btnCancel.addActionListener(e -> frame.changerVue(ModernMainFrame.VUE_LISTE_BOUTIQUE));

        // --- ASSEMBLAGE ---
        card.add(icon);
        card.add(Box.createVerticalStrut(10));
        card.add(title);
        card.add(Box.createVerticalStrut(5));
        JLabel idLabel = new JLabel("ID: #" + b.getIdb());
        idLabel.setForeground(Theme.TEXT_BODY);
        idLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(idLabel);
        card.add(Box.createVerticalStrut(30));

        card.add(createInputLabel("NOM DE L'ENSEIGNE"));
        card.add(txtNom);
        card.add(Box.createVerticalStrut(15));

        card.add(createInputLabel("ADRESSE"));
        card.add(txtAdresse);
        card.add(Box.createVerticalStrut(15));

        card.add(createInputLabel("PATENTE"));
        card.add(txtPatente);
        card.add(Box.createVerticalStrut(15));

        card.add(createInputLabel("TÉLÉPHONE"));
        card.add(txtTel);
        card.add(Box.createVerticalStrut(35));

        card.add(btnUpdate);
        card.add(Box.createVerticalStrut(10));
        card.add(btnCancel);

        add(card);
    }

    // --- UTILS (Design identique à l'ajout) ---
    private JLabel createInputLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(Theme.TEXT_BODY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField createStyledField() {
        JTextField f = new JTextField();
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBorder(new CompoundBorder(new LineBorder(new Color(230, 230, 230)), new EmptyBorder(5, 10, 5, 10)));
        return f;
    }

    private JButton createPrimaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(Theme.PRIMARY);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        return b;
    }

    private void styleSecondaryButton(JButton b) {
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        b.setForeground(Theme.TEXT_BODY);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
}