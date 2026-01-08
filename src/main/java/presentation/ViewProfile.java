package presentation;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import dao.User;
import metier.GestionUser;
import metier.IGestionUser;

public class ViewProfile extends JPanel {

    private ModernMainFrame frame;
    private IGestionUser metier;

    // Champs
    private JTextField txtNom, txtPrenom, txtEmail, txtTel;
    private JTextField txtCin; 
    private JPasswordField txtMdp;
    private JButton btnSave;

    public ViewProfile(ModernMainFrame frame) {
        this.frame = frame;
        this.metier = new GestionUser(); 

        // 1. FOND GÉNÉRAL
        setLayout(new GridBagLayout()); 
        setBackground(Theme.BACKGROUND); 

        // 2. LA CARTE HORIZONTALE
        add(createHorizontalCard());

        // 3. CHARGEMENT
        chargerDonnees();
    }

    private JPanel createHorizontalCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        // Bordure grise et ombre simulée par les marges
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1, true),
            new EmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        
        // =========================================================
        // PARTIE GAUCHE : IDENTITÉ (Icône + Titre)
        // =========================================================
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setOpaque(false);

        JLabel icon = new JLabel("👤");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80)); // Icône plus grande
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Mon Profil");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitle = new JLabel("Propriétaire");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftPanel.add(icon);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(title);
        leftPanel.add(subtitle);

        // Placement Gauche
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 30); // Marge à droite pour ne pas coller la ligne
        card.add(leftPanel, gbc);

        // =========================================================
        // SÉPARATEUR VERTICAL
        // =========================================================
        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 250)); // Hauteur forcée
        sep.setForeground(new Color(230, 230, 230));
        
        gbc.gridx = 1; 
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.insets = new Insets(0, 0, 0, 30); // Marge à droite
        card.add(sep, gbc);

        // =========================================================
        // PARTIE DROITE : FORMULAIRE
        // =========================================================
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        
        GridBagConstraints gbcForm = new GridBagConstraints();
        gbcForm.insets = new Insets(5, 10, 15, 10);
        gbcForm.fill = GridBagConstraints.HORIZONTAL;
        gbcForm.weightx = 1.0;

        // --- Init Champs ---
        txtNom = createStyledField(true);
        txtPrenom = createStyledField(true);
        txtEmail = createStyledField(true);
        txtTel = createStyledField(true);
        txtCin = createStyledField(false); // Lecture seule
        txtMdp = createStyledPasswordField();

        // Ligne 1
        gbcForm.gridy = 0;
        gbcForm.gridx = 0; rightPanel.add(createFieldBlock("Nom", txtNom), gbcForm);
        gbcForm.gridx = 1; rightPanel.add(createFieldBlock("Prénom", txtPrenom), gbcForm);

        // Ligne 2
        gbcForm.gridy++;
        gbcForm.gridx = 0; rightPanel.add(createFieldBlock("Email", txtEmail), gbcForm);
        gbcForm.gridx = 1; rightPanel.add(createFieldBlock("Téléphone", txtTel), gbcForm);

        // Ligne 3
        gbcForm.gridy++;
        gbcForm.gridx = 0; rightPanel.add(createFieldBlock("CIN (Fixe)", txtCin), gbcForm);
        gbcForm.gridx = 1; rightPanel.add(createFieldBlock("Mot de passe", txtMdp), gbcForm);

        // Bouton (Pleine largeur sur le panneau de droite)
        gbcForm.gridy++;
        gbcForm.gridx = 0; 
        gbcForm.gridwidth = 2;
        gbcForm.insets = new Insets(20, 10, 0, 10);
        
        btnSave = UIFactory.createGradientButton("Enregistrer les modifications");
        btnSave.setPreferredSize(new Dimension(0, 45));
        btnSave.addActionListener(e -> sauvegarder());
        
        rightPanel.add(btnSave, gbcForm);

        // Placement Droite
        gbc.gridx = 2; 
        gbc.fill = GridBagConstraints.BOTH; // Remplir l'espace
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(rightPanel, gbc);

        return card;
    }

    // --- HELPERS (Style identique) ---
    private JPanel createFieldBlock(String labelText, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);
        JLabel l = new JLabel(labelText);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(Theme.TEXT_BODY);
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private JTextField createStyledField(boolean editable) {
        JTextField f = new JTextField(15);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            new EmptyBorder(8, 10, 8, 10)
        ));
        if (!editable) {
            f.setEditable(false);
            f.setBackground(new Color(240, 240, 240));
            f.setForeground(Color.GRAY);
        } else {
            f.setBackground(new Color(250, 252, 255));
        }
        return f;
    }
    
    private JPasswordField createStyledPasswordField() {
        JPasswordField f = new JPasswordField(15);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBackground(new Color(250, 252, 255));
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            new EmptyBorder(8, 10, 8, 10)
        ));
        return f;
    }

    // --- LOGIQUE MÉTIER ---
    public void chargerDonnees() {
        User u = frame.getCurrentUser();
        if (u != null) {
            txtNom.setText(u.getNom());
            txtPrenom.setText(u.getPrenom());
            txtEmail.setText(u.getEmail());
            txtTel.setText(u.getNumtel());
            txtCin.setText(u.getCin());
            txtMdp.setText(u.getMdp());
        }
    }

    private void sauvegarder() {
        User u = frame.getCurrentUser();
        if (u == null) return;

        if (txtNom.getText().isEmpty() || new String(txtMdp.getPassword()).isEmpty()) {
            JOptionPane.showMessageDialog(this, "Champs obligatoires manquants.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            u.setNom(txtNom.getText());
            u.setPrenom(txtPrenom.getText());
            u.setEmail(txtEmail.getText());
            u.setNumtel(txtTel.getText());
            u.setMdp(new String(txtMdp.getPassword()));

            metier.modifierUtilisateur(u);
            JOptionPane.showMessageDialog(this, "Profil mis à jour !", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}