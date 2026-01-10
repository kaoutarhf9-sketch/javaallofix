package presentation;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

import dao.Boutique;
import dao.Proprietaire;
import metier.GestionBoutique;

public class ViewFormBoutique extends JPanel {

    private ModernMainFrame frame;
    private GestionBoutique metierBoutique;

    // --- VARIABLES GLOBALES ---
    private JTextField txtNom, txtAdresse, txtPatente, txtTel;
    private JLabel lblTitle;
    private JButton btnEnregistrer;
    private Boutique boutiqueEnEdition = null; 

    // --- STYLES ---
    private final Color COLOR_BORDER_DEFAULT = new Color(220, 220, 220);

    public ViewFormBoutique(ModernMainFrame frame) {
        this.frame = frame;
        try { this.metierBoutique = new GestionBoutique(); } catch (Exception e) { e.printStackTrace(); }

        initUI();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        setBackground(Theme.BACKGROUND);

        // --- CARTE HORIZONTALE (Format Paysage) ---
        JPanel card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(800, 450)); // Carte large
        card.setBackground(Color.WHITE);
        
        // Bordure propre
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1, true),
            new EmptyBorder(30, 40, 30, 40)
        ));

        // --- 1. EN-TÊTE ---
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2; // Prend toute la largeur
        gbc.insets = new Insets(0, 0, 30, 0); // Marge sous le titre

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        headerPanel.setOpaque(false);
        
        JLabel icon = new JLabel("🏪");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        
        lblTitle = UIFactory.createTitle("Nouvelle Boutique");
        
        headerPanel.add(icon);
        headerPanel.add(lblTitle);
        
        card.add(headerPanel, gbc);

        // --- 2. FORMULAIRE (GRILLE 2 COLONNES) ---
        // Init des champs
        txtNom = createStyledField();
        txtAdresse = createStyledField();
        txtPatente = createStyledField();
        txtTel = createStyledField();

        // Config Grille
        gbc.gridwidth = 1; // Retour à 1 colonne par champ
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        gbc.insets = new Insets(0, 10, 20, 10); // Espacement aéré

        // Ligne 1 : Enseigne & Adresse
        gbc.gridy = 1;
        gbc.gridx = 0; card.add(createFieldBlock("Nom de l'enseigne", txtNom), gbc);
        gbc.gridx = 1; card.add(createFieldBlock("Adresse complète", txtAdresse), gbc);

        // Ligne 2 : Patente & Téléphone
        gbc.gridy = 2;
        gbc.gridx = 0; card.add(createFieldBlock("Référence Patente", txtPatente), gbc);
        gbc.gridx = 1; card.add(createFieldBlock("Téléphone", txtTel), gbc);

        // --- 3. BOUTONS ---
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2; // Pleine largeur
        gbc.insets = new Insets(30, 10, 0, 10); // Marge haut

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        btnPanel.setOpaque(false);

        btnEnregistrer = UIFactory.createGradientButton("ENREGISTRER");
        btnEnregistrer.setPreferredSize(new Dimension(0, 45));
        btnEnregistrer.addActionListener(e -> validerFormulaire());

        JButton btnAnnuler = UIFactory.createOutlineButton("Annuler");
        btnAnnuler.addActionListener(e -> frame.changerVue(ModernMainFrame.VUE_LISTE_BOUTIQUE));

        btnPanel.add(btnAnnuler);
        btnPanel.add(btnEnregistrer);

        card.add(btnPanel, gbc);

        // Ajout final
        add(card);
    }

    // --- HELPER : BLOC CHAMP + LABEL (Identique à Reparateur) ---
    private JPanel createFieldBlock(String labelText, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);
        
        JLabel l = new JLabel(labelText);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(new Color(100, 116, 139)); // Gris lisible
        
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    // --- HELPER : STYLE CHAMP ---
    private JTextField createStyledField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDER_DEFAULT, 1, true),
            new EmptyBorder(8, 10, 8, 10)
        ));
        return f;
    }

    // =========================================================================
    // LOGIQUE MÉTIER
    // =========================================================================
    public void setBoutiqueEnEdition(Boutique b) {
        this.boutiqueEnEdition = b;
        
        if (b != null) {
            // MODE MODIF
            txtNom.setText(b.getNomB());
            txtAdresse.setText(b.getAdresse());
            txtPatente.setText(b.getPatente());
            txtTel.setText(b.getNumtel());
            
            lblTitle.setText("Modifier la Boutique");
            btnEnregistrer.setText("ENREGISTRER LES MODIFICATIONS");
        } else {
            // MODE CRÉATION
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

        try {
            if (boutiqueEnEdition == null) {
                // CRÉATION
                if (frame.getCurrentUser() instanceof Proprietaire) {
                    Proprietaire p = (Proprietaire) frame.getCurrentUser();
                    Boutique b = new Boutique();
                    b.setNomB(nom); b.setAdresse(adresse);
                    b.setPatente(patente); b.setNumtel(tel);

                    metierBoutique.creerBoutique(b, p.getIdU());
                    JOptionPane.showMessageDialog(this, "Boutique créée !");
                }
            } else {
                // MODIFICATION
                boutiqueEnEdition.setNomB(nom);
                boutiqueEnEdition.setAdresse(adresse);
                boutiqueEnEdition.setPatente(patente);
                boutiqueEnEdition.setNumtel(tel);
                
                // Appel Métier Modification (Assurez-vous d'avoir ajouté modifierBoutique dans GestionBoutique)
                metierBoutique.modifierBoutique(boutiqueEnEdition); 
                JOptionPane.showMessageDialog(this, "Modifications enregistrées !");
            }
            
            frame.changerVue(ModernMainFrame.VUE_LISTE_BOUTIQUE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }
    }
}