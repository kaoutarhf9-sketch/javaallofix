package presentation;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import dao.User;
import dao.Proprietaire;
import dao.Reparateur;
import metier.GestionUser;
import metier.IGestionUser;

public class ViewProfile extends JPanel {

    private ModernMainFrame frame;
    private IGestionUser metier;

    // Champs
    private JTextField txtNom, txtPrenom, txtEmail, txtTel, txtCin;
    private JPasswordField txtMdp;
    private JButton btnSave;
    
    // Identité
    private JLabel lblInitials;
    private JLabel lblRole;
    private JPanel avatarCircle;

    public ViewProfile(ModernMainFrame frame) {
        this.frame = frame;
        this.metier = new GestionUser();

        // 1. CONFIGURATION
        setLayout(new GridBagLayout()); // Pour centrer la carte au milieu de l'écran
        setBackground(Theme.BACKGROUND);

        // 2. LA CARTE HORIZONTALE (Large)
        add(createHorizontalCard());

        // 3. CHARGEMENT
        chargerDonnees();
    }

    private JPanel createHorizontalCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(850, 450)); // Format rectangulaire large
        
        // Bordure fine + Ombre légère (via padding externe si besoin, ici simple bordure propre)
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(226, 232, 240), 1, true),
            new EmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();

        // =========================================================
        // PARTIE GAUCHE : IDENTITÉ VISUELLE
        // =========================================================
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(200, 300));

        // Cercle Avatar
        avatarCircle = new JPanel(new GridBagLayout());
        avatarCircle.setPreferredSize(new Dimension(100, 100));
        avatarCircle.setMaximumSize(new Dimension(100, 100));
        avatarCircle.setBackground(Theme.SIDEBAR_BG); // Bleu nuit par défaut
        avatarCircle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Astuce : On arrondira visuellement via un Panel rond si on voulait, 
        // ici on garde un carré couleur ou on peut utiliser un border radius sur le panel.
        // Pour faire simple et propre :
        
        lblInitials = new JLabel("XX");
        lblInitials.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblInitials.setForeground(Color.WHITE);
        avatarCircle.add(lblInitials);

        JLabel lblTitle = new JLabel("Mon Profil");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.TEXT_HEADLINE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        lblRole = new JLabel("Propriétaire");
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRole.setForeground(Theme.PRIMARY);
        lblRole.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Assemblage Gauche
        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(avatarCircle);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(lblTitle);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(lblRole);
        leftPanel.add(Box.createVerticalGlue());

        // Placement Gauche
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 40); // Espace à droite
        card.add(leftPanel, gbc);

        // =========================================================
        // SÉPARATEUR VERTICAL
        // =========================================================
        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 300));
        sep.setForeground(new Color(226, 232, 240)); // Gris très clair
        
        gbc.gridx = 1; 
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.insets = new Insets(0, 0, 0, 40); // Espace à droite
        card.add(sep, gbc);

        // =========================================================
        // PARTIE DROITE : FORMULAIRE
        // =========================================================
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        
        GridBagConstraints gbcForm = new GridBagConstraints();
        gbcForm.insets = new Insets(0, 10, 20, 10);
        gbcForm.fill = GridBagConstraints.HORIZONTAL;
        gbcForm.weightx = 1.0;

        // Init Champs (UIFactory pour le look moderne)
        txtNom = UIFactory.createModernField();
        txtPrenom = UIFactory.createModernField();
        txtEmail = UIFactory.createModernField();
        txtTel = UIFactory.createModernField();
        
        txtCin = UIFactory.createModernField();
        txtCin.setEditable(false);
        txtCin.setBackground(new Color(248, 250, 252));
        txtCin.setForeground(Color.GRAY);
        
        txtMdp = UIFactory.createModernPasswordField();

        // Ligne 1
        gbcForm.gridy = 0;
        gbcForm.gridx = 0; rightPanel.add(createInputBlock("Nom", txtNom), gbcForm);
        gbcForm.gridx = 1; rightPanel.add(createInputBlock("Prénom", txtPrenom), gbcForm);

        // Ligne 2
        gbcForm.gridy = 1;
        gbcForm.gridx = 0; rightPanel.add(createInputBlock("Email", txtEmail), gbcForm);
        gbcForm.gridx = 1; rightPanel.add(createInputBlock("Téléphone", txtTel), gbcForm);

        // Ligne 3
        gbcForm.gridy = 2;
        gbcForm.gridx = 0; rightPanel.add(createInputBlock("CIN (Fixe)", txtCin), gbcForm);
        gbcForm.gridx = 1; rightPanel.add(createInputBlock("Mot de passe", txtMdp), gbcForm);

        // Bouton Sauvegarder (Pleine largeur en bas)
        gbcForm.gridy = 3;
        gbcForm.gridx = 0; 
        gbcForm.gridwidth = 2;
        gbcForm.insets = new Insets(10, 10, 0, 10);
        
        btnSave = UIFactory.createGradientButton("Enregistrer les modifications");
        btnSave.setPreferredSize(new Dimension(0, 45));
        btnSave.addActionListener(e -> sauvegarder());
        
        rightPanel.add(btnSave, gbcForm);

        // Placement Droite
        gbc.gridx = 2; 
        gbc.weightx = 1.0; // Prend tout le reste de la largeur
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(rightPanel, gbc);

        return card;
    }

    // --- HELPER : BLOC LABEL + INPUT ---
    private JPanel createInputBlock(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(Theme.TEXT_HEADLINE);
        // On force une hauteur standard
        field.setPreferredSize(new Dimension(0, 38));
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
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

            // Initials
            String initials = "";
            if(!u.getPrenom().isEmpty()) initials += u.getPrenom().charAt(0);
            if(!u.getNom().isEmpty()) initials += u.getNom().charAt(0);
            lblInitials.setText(initials.toUpperCase());

            // Role & Couleur Avatar
            if (u instanceof Proprietaire) {
                lblRole.setText("PROPRIÉTAIRE");
                lblRole.setForeground(Theme.PRIMARY);
                avatarCircle.setBackground(Theme.SIDEBAR_BG);
            } else {
                lblRole.setText("RÉPARATEUR");
                lblRole.setForeground(new Color(16, 185, 129)); // Vert
                avatarCircle.setBackground(new Color(16, 185, 129));
            }
        }
    }

    private void sauvegarder() {
        User u = frame.getCurrentUser();
        if (u == null) return;

        if (txtNom.getText().isEmpty() || new String(txtMdp.getPassword()).isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nom et Mot de passe requis.", "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            u.setNom(txtNom.getText());
            u.setPrenom(txtPrenom.getText());
            u.setEmail(txtEmail.getText());
            u.setNumtel(txtTel.getText());
            u.setMdp(new String(txtMdp.getPassword()));

            metier.modifierUtilisateur(u);
            
            JOptionPane.showMessageDialog(this, "Modifications enregistrées !", "Succès", JOptionPane.INFORMATION_MESSAGE);
            chargerDonnees();
            frame.setCurrentUser(u); // Refresh Sidebar
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}