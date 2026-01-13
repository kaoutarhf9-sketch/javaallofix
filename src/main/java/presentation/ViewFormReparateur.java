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
    
    // Metier
    private IGestionReparateur metierReparateur;
    private GestionBoutique metierBoutique;

    // UI
    private JLabel lblTitreForm;
    private JTextField txtNom, txtPrenom, txtCin, txtEmail, txtTel, txtPourcentage;
    private JComboBox<Boutique> comboBoutique;
    private JButton btnEnregistrer;

    // État
    private Reparateur reparateurEnEdition = null;

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
        setLayout(new GridBagLayout());
        setBackground(Theme.BACKGROUND);

        JPanel card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(850, 550));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230,230,230), 1, true),
            new EmptyBorder(30, 40, 30, 40)
        ));

        // Header
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 30, 0);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        headerPanel.setOpaque(false);
        JLabel icon = new JLabel("👨‍🔧");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        lblTitreForm = UIFactory.createTitle("Nouveau Réparateur");
        headerPanel.add(icon);
        headerPanel.add(lblTitreForm);
        card.add(headerPanel, gbc);

        // Reset GBC pour les champs
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        gbc.insets = new Insets(0, 10, 20, 10);

        // Champs
        txtNom = createStyledField();
        txtPrenom = createStyledField();
        txtCin = createStyledField();
        txtEmail = createStyledField();
        txtTel = createStyledField();
        txtPourcentage = createStyledField();
        
        comboBoutique = new JComboBox<>();
        styleComboBox(comboBoutique);

        // Ligne 1
        gbc.gridy = 1;
        gbc.gridx = 0; card.add(createFieldBlock("Nom *", txtNom), gbc);
        gbc.gridx = 1; card.add(createFieldBlock("Prénom *", txtPrenom), gbc);

        // Ligne 2
        gbc.gridy = 2;
        gbc.gridx = 0; card.add(createFieldBlock("CIN (Identifiant Unique) *", txtCin), gbc);
        gbc.gridx = 1; card.add(createFieldBlock("Email (Unique) *", txtEmail), gbc);

        // Ligne 3
        gbc.gridy = 3;
        gbc.gridx = 0; card.add(createFieldBlock("Téléphone *", txtTel), gbc);
        gbc.gridx = 1; card.add(createFieldBlock("Commission (%)", txtPourcentage), gbc);

        // Ligne 4
        gbc.gridy = 4;
        gbc.gridx = 0; gbc.gridwidth = 2;
        card.add(createFieldBlock("Affecter à la boutique *", comboBoutique), gbc);

        // Boutons
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 10, 0, 10);
        
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        btnPanel.setOpaque(false);
        
        btnEnregistrer = UIFactory.createGradientButton("Créer le compte");
        btnEnregistrer.setPreferredSize(new Dimension(0, 45));
        btnEnregistrer.addActionListener(e -> validerCreation());
        
        JButton btnCancel = UIFactory.createOutlineButton("Annuler");
        btnCancel.addActionListener(e -> frame.changerVue(ModernMainFrame.VUE_DASHBOARD_PROPRIO));

        btnPanel.add(btnCancel);
        btnPanel.add(btnEnregistrer);

        card.add(btnPanel, gbc);
        add(card);
    }

    private JPanel createFieldBlock(String labelText, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);
        JLabel l = new JLabel(labelText);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(new Color(100, 116, 139));
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    public void setReparateurAModifier(Reparateur r) {
        this.reparateurEnEdition = r;
        lblTitreForm.setText("Modifier Réparateur");
        btnEnregistrer.setText("Enregistrer les modifications");
        
        txtNom.setText(r.getNom());
        txtPrenom.setText(r.getPrenom());
        txtCin.setText(r.getCin());
        // On bloque l'édition du CIN en mode modification (clé métier souvent immuable)
        txtCin.setEditable(false);
        txtCin.setBackground(new Color(245, 245, 245));
        
        txtEmail.setText(r.getEmail());
        txtTel.setText(r.getNumtel());
        txtPourcentage.setText(String.valueOf(r.getPourcentage()));
        
        chargerLesBoutiques();
        if (r.getBoutique() != null) {
            for (int i = 0; i < comboBoutique.getItemCount(); i++) {
                if (comboBoutique.getItemAt(i).getIdb() == r.getBoutique().getIdb()) {
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

        if (nom.isEmpty() || prenom.isEmpty() || cin.isEmpty() || email.isEmpty() || selectedBoutique == null) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs obligatoires (*).", "Données manquantes", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Vérification longueur Téléphone (max 10 selon votre @Column)
        if (tel.length() > 10) {
            JOptionPane.showMessageDialog(this, "Le numéro de téléphone ne doit pas dépasser 10 chiffres (Format 06...)", "Erreur Format", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Double pourcentage = strPourc.isEmpty() ? 0.0 : Double.parseDouble(strPourc);
            
            if (reparateurEnEdition == null) {
                // --- CREATION ---
                Reparateur r = new Reparateur();
                r.setNom(nom);
                r.setPrenom(prenom);
                r.setCin(cin);
                r.setEmail(email);
                r.setNumtel(tel);
                r.setPourcentage(pourcentage);
                
                // Appel métier qui fait les vérifs de doublons
                metierReparateur.ajouterReparateur(r, selectedBoutique.getIdb());
                
                JOptionPane.showMessageDialog(this, "Compte réparateur créé avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                
            } else {
                // --- MODIFICATION ---
                reparateurEnEdition.setNom(nom);
                reparateurEnEdition.setPrenom(prenom);
                reparateurEnEdition.setEmail(email);
                reparateurEnEdition.setNumtel(tel);
                reparateurEnEdition.setPourcentage(pourcentage);
                reparateurEnEdition.setBoutique(selectedBoutique);
                
                metierReparateur.modifierReparateur(reparateurEnEdition);
                JOptionPane.showMessageDialog(this, "Modifications enregistrées.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            }

            viderChamps();
            frame.changerVue(ModernMainFrame.VUE_LISTE_REPARATEUR);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Le pourcentage doit être un nombre valide (ex: 15.5).", "Format Invalide", JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException e) {
            // C'est ici que l'on capture l'erreur de doublon venant du métier
            JOptionPane.showMessageDialog(this, "Impossible d'enregistrer :\n" + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur technique : " + e.getMessage(), "Erreur Critique", JOptionPane.ERROR_MESSAGE);
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
            new LineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(8, 10, 8, 10)
        ));
        return f;
    }

    private void styleComboBox(JComboBox<Boutique> box) {
        box.setBackground(Color.WHITE);
        box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
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