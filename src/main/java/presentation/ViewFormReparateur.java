package presentation;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

import dao.Boutique;
import dao.Proprietaire;
import dao.Reparateur;
import metier.GestionBoutique;
import metier.GestionReparateur;
import metier.IGestionReparateur;

public class ViewFormReparateur extends JPanel {

    private ModernMainFrame frame;
    
    // --- COUCHES MÉTIER ---
    private IGestionReparateur metierReparateur;
    private GestionBoutique metierBoutique;

    // --- COMPOSANTS UI ---
    private JLabel lblTitreForm;
    private JTextField txtNom, txtPrenom, txtCin, txtEmail, txtTel, txtPourcentage;
    private JComboBox<Boutique> comboBoutique;
    private JButton btnEnregistrer;

    // --- ETAT ---
    private Reparateur reparateurEnEdition = null;

    // --- STYLES ---
    private final Color COLOR_BORDER_DEFAULT = new Color(220, 220, 220);

    public ViewFormReparateur(ModernMainFrame frame) {
        this.frame = frame;
        
        try {
            this.metierReparateur = new GestionReparateur();
            this.metierBoutique = new GestionBoutique();
        } catch (Exception e) {
            e.printStackTrace();
        }

        initUI();
    }

    private void initUI() {
        setLayout(new GridBagLayout()); // Centrage général
        setBackground(Theme.BACKGROUND);

        // --- CARTE HORIZONTALE (Plus large que haute) ---
        JPanel card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(850, 550)); // Format paysage
        card.setBackground(Color.WHITE);
        
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230,230,230), 1, true),
            new EmptyBorder(30, 40, 30, 40)
        ));

        // --- 1. EN-TÊTE (Icône + Titre) ---
        GridBagConstraints gbcHeader = new GridBagConstraints();
        gbcHeader.gridx = 0; gbcHeader.gridy = 0;
        gbcHeader.gridwidth = 2; // Prend toute la largeur
        gbcHeader.insets = new Insets(0, 0, 30, 0);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        headerPanel.setOpaque(false);
        
        JLabel icon = new JLabel("👨‍🔧");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        
        lblTitreForm = UIFactory.createTitle("Nouveau Réparateur");
        
        headerPanel.add(icon);
        headerPanel.add(lblTitreForm);
        
        card.add(headerPanel, gbcHeader);

        // --- 2. CHAMPS (GRILLE DE FORMULAIRE) ---
        // On initialise les composants
        txtNom = createStyledField();
        txtPrenom = createStyledField();
        txtCin = createStyledField();
        txtEmail = createStyledField();
        txtTel = createStyledField();
        txtPourcentage = createStyledField();
        
        comboBoutique = new JComboBox<>();
        styleComboBox(comboBoutique);

        // Configuration de la grille de champs
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5; // Partage équitable de l'espace
        gbc.insets = new Insets(0, 10, 20, 10); // Marges entre les blocs (Haut, Gauche, Bas, Droite)

        // --- LIGNE 1 : Nom & Prénom ---
        gbc.gridy = 1;
        gbc.gridx = 0; card.add(createFieldBlock("Nom", txtNom), gbc);
        gbc.gridx = 1; card.add(createFieldBlock("Prénom", txtPrenom), gbc);

        // --- LIGNE 2 : CIN & Email ---
        gbc.gridy = 2;
        gbc.gridx = 0; card.add(createFieldBlock("CIN (Identifiant)", txtCin), gbc);
        gbc.gridx = 1; card.add(createFieldBlock("Email", txtEmail), gbc);

        // --- LIGNE 3 : Téléphone & Commission ---
        gbc.gridy = 3;
        gbc.gridx = 0; card.add(createFieldBlock("Téléphone", txtTel), gbc);
        gbc.gridx = 1; card.add(createFieldBlock("Commission (%)", txtPourcentage), gbc);

        // --- LIGNE 4 : Boutique (Pleine largeur) ---
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2; // Span sur 2 colonnes
        card.add(createFieldBlock("Affecter à la boutique", comboBoutique), gbc);

        // --- 3. BOUTONS (Bas de page) ---
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 0, 10); // Marge supérieure plus grande
        
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 20, 0)); // 2 boutons côte à côte
        btnPanel.setOpaque(false);
        
        btnEnregistrer = UIFactory.createGradientButton("Créer le compte");
        btnEnregistrer.setPreferredSize(new Dimension(0, 45));
        btnEnregistrer.addActionListener(e -> validerCreation());
        
        JButton btnCancel = UIFactory.createOutlineButton("Annuler");
        btnCancel.addActionListener(e -> frame.changerVue(ModernMainFrame.VUE_DASHBOARD_PROPRIO));

        btnPanel.add(btnCancel);     // Annuler à gauche
        btnPanel.add(btnEnregistrer); // Valider à droite

        card.add(btnPanel, gbc);

        // Ajout final
        add(card);
    }

    // --- HELPER : Création d'un bloc Label + Champ ---
    private JPanel createFieldBlock(String labelText, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 8)); // 8px d'écart vertical
        p.setOpaque(false);
        
        JLabel l = new JLabel(labelText);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(new Color(100, 116, 139)); // Gris bleuté moderne
        
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        
        return p;
    }

    // =============================================================
    // LOGIQUE MÉTIER (Identique à précédemment)
    // =============================================================

    public void setReparateurAModifier(Reparateur r) {
        this.reparateurEnEdition = r;
        
        lblTitreForm.setText("Modifier Réparateur");
        btnEnregistrer.setText("Enregistrer les modifications");
        
        txtNom.setText(r.getNom());
        txtPrenom.setText(r.getPrenom());
        txtCin.setText(r.getCin());
        txtCin.setEditable(false);
        txtCin.setBackground(new Color(245, 245, 245)); // Grisé
        txtEmail.setText(r.getEmail());
        txtTel.setText(r.getNumtel());
        txtPourcentage.setText(String.valueOf(r.getPourcentage()));
        
        chargerLesBoutiques();
        if (r.getBoutique() != null) {
            for (int i = 0; i < comboBoutique.getItemCount(); i++) {
                Boutique item = comboBoutique.getItemAt(i);
                if (item.getIdb() == r.getBoutique().getIdb()) {
                    comboBoutique.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    public void resetFormulaire() {
        this.reparateurEnEdition = null;
        lblTitreForm.setText("Nouveau Réparateur");
        btnEnregistrer.setText("Créer le compte");
        
        txtCin.setEditable(true);
        txtCin.setBackground(Color.WHITE);
        viderChamps();
        chargerLesBoutiques();
    }

    public void chargerLesBoutiques() {
        comboBoutique.removeAllItems();
        if (frame.getCurrentUser() instanceof Proprietaire) {
            Proprietaire p = (Proprietaire) frame.getCurrentUser();
            List<Boutique> boutiques = metierBoutique.listerBoutiquesDuProprietaire(p.getIdU());
            if (boutiques != null) {
                for (Boutique b : boutiques) comboBoutique.addItem(b);
            }
        }
    }

    private void validerCreation() {
        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        String cin = txtCin.getText().trim();
        String email = txtEmail.getText().trim();
        String tel = txtTel.getText().trim();
        String strPourc = txtPourcentage.getText().trim();
        Boutique selectedBoutique = (Boutique) comboBoutique.getSelectedItem();

        if (nom.isEmpty() || prenom.isEmpty() || cin.isEmpty() || selectedBoutique == null) {
            JOptionPane.showMessageDialog(this, "Champs obligatoires manquants.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Double pourcentage = Double.parseDouble(strPourc);
            
            if (reparateurEnEdition == null) {
                Reparateur r = Reparateur.builder()
                        .nom(nom).prenom(prenom).cin(cin)
                        .email(email).numtel(tel).pourcentage(pourcentage)
                        .boutique(selectedBoutique)
                        .build();
                metierReparateur.ajouterReparateur(r, selectedBoutique.getIdb());
                JOptionPane.showMessageDialog(this, "Compte créé !");
            } else {
                reparateurEnEdition.setNom(nom);
                reparateurEnEdition.setPrenom(prenom);
                reparateurEnEdition.setEmail(email);
                reparateurEnEdition.setNumtel(tel);
                reparateurEnEdition.setPourcentage(pourcentage);
                reparateurEnEdition.setBoutique(selectedBoutique);
                metierReparateur.modifierReparateur(reparateurEnEdition);
                JOptionPane.showMessageDialog(this, "Modifications enregistrées !");
            }

            viderChamps();
            frame.changerVue(ModernMainFrame.VUE_LISTE_REPARATEUR);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Pourcentage invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }

    private void viderChamps() {
        txtNom.setText(""); txtPrenom.setText(""); txtCin.setText("");
        txtEmail.setText(""); txtTel.setText(""); txtPourcentage.setText("");
    }

    private JTextField createStyledField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDER_DEFAULT, 1, true),
            new EmptyBorder(8, 10, 8, 10) // Padding plus confortable
        ));
        return f;
    }

    private void styleComboBox(JComboBox<Boutique> box) {
        box.setBackground(Color.WHITE);
        box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // Petit hack pour augmenter la hauteur
        box.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        box.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Boutique) setText(((Boutique) value).getNomB());
                return this;
            }
        });
    }
}