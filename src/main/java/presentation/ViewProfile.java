package presentation;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import dao.User;
import dao.Proprietaire;
import dao.Reparateur; // Import nécessaire
import metier.GestionUser;
import metier.IGestionUser;

public class ViewProfile extends JPanel {

    private ModernMainFrame frame;
    private IGestionUser metier;

    // Champs Formulaire
    private JTextField txtNom, txtPrenom, txtEmail, txtTel, txtCin;
    private JPasswordField txtMdp;
    private JButton btnSave;
    
    // Identité & Photo
    private JLabel lblInitials;
    private JLabel lblPhoto;
    private JLabel lblRole;
    private JPanel avatarCircle;
    
    // 🔥 NOUVEAU : Checkbox pour le double rôle
    private JCheckBox chkEstReparateur;
    
    private String currentPhotoPath = null;

    public ViewProfile(ModernMainFrame frame) {
        this.frame = frame;
        this.metier = new GestionUser();

        setLayout(new GridBagLayout()); 
        setBackground(Theme.BACKGROUND);

        add(createHorizontalCard());

        chargerDonnees();
    }

    private JPanel createHorizontalCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(850, 520)); // Un peu plus haut pour la checkbox
        
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(226, 232, 240), 1, true),
            new EmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();

        // =========================================================
        // 1. GAUCHE : IDENTITÉ VISUELLE + PHOTO
        // =========================================================
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(200, 300));

        // --- ZONE AVATAR ---
        avatarCircle = new JPanel();
        avatarCircle.setLayout(new OverlayLayout(avatarCircle)); 
        avatarCircle.setPreferredSize(new Dimension(100, 100));
        avatarCircle.setMaximumSize(new Dimension(100, 100));
        avatarCircle.setBackground(Theme.SIDEBAR_BG);
        avatarCircle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        lblPhoto = new JLabel();
        lblPhoto.setHorizontalAlignment(SwingConstants.CENTER);
        lblPhoto.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        lblInitials = new JLabel("XX");
        lblInitials.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblInitials.setForeground(Color.WHITE);
        lblInitials.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        avatarCircle.add(lblPhoto);
        avatarCircle.add(lblInitials);

        JButton btnChangePhoto = new JButton("📷 Changer photo");
        btnChangePhoto.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnChangePhoto.setBackground(new Color(241, 245, 249));
        btnChangePhoto.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        btnChangePhoto.setFocusPainted(false);
        btnChangePhoto.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnChangePhoto.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnChangePhoto.addActionListener(e -> choisirPhoto());

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
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(btnChangePhoto);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(lblTitle);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(lblRole);
        leftPanel.add(Box.createVerticalGlue());

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 40);
        card.add(leftPanel, gbc);

        // SÉPARATEUR
        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 300));
        sep.setForeground(new Color(226, 232, 240));
        
        gbc.gridx = 1; 
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.insets = new Insets(0, 0, 0, 40);
        card.add(sep, gbc);

        // =========================================================
        // 2. DROITE : FORMULAIRE
        // =========================================================
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        
        GridBagConstraints gbcForm = new GridBagConstraints();
        gbcForm.insets = new Insets(0, 10, 20, 10);
        gbcForm.fill = GridBagConstraints.HORIZONTAL;
        gbcForm.weightx = 1.0;

        txtNom = UIFactory.createModernField();
        txtPrenom = UIFactory.createModernField();
        txtEmail = UIFactory.createModernField();
        txtTel = UIFactory.createModernField();
        
        txtCin = UIFactory.createModernField();
        txtCin.setEditable(false);
        txtCin.setBackground(new Color(248, 250, 252));
        
        txtMdp = UIFactory.createModernPasswordField();

        // Lignes du formulaire
        gbcForm.gridy = 0;
        gbcForm.gridx = 0; rightPanel.add(createInputBlock("Nom", txtNom), gbcForm);
        gbcForm.gridx = 1; rightPanel.add(createInputBlock("Prénom", txtPrenom), gbcForm);

        gbcForm.gridy = 1;
        gbcForm.gridx = 0; rightPanel.add(createInputBlock("Email", txtEmail), gbcForm);
        gbcForm.gridx = 1; rightPanel.add(createInputBlock("Téléphone", txtTel), gbcForm);

        gbcForm.gridy = 2;
        gbcForm.gridx = 0; rightPanel.add(createInputBlock("CIN (Fixe)", txtCin), gbcForm);
        gbcForm.gridx = 1; rightPanel.add(createInputBlock("Mot de passe", txtMdp), gbcForm);

        // 🔥 LIGNE 3 : OPTION PROPRIÉTAIRE-RÉPARATEUR
        gbcForm.gridy = 3;
        gbcForm.gridx = 0; 
        gbcForm.gridwidth = 2;
        
        chkEstReparateur = new JCheckBox("Je répare aussi des appareils (Activer les outils Réparateur)");
        chkEstReparateur.setFont(new Font("Segoe UI", Font.BOLD, 13));
        chkEstReparateur.setForeground(new Color(71, 85, 105));
        chkEstReparateur.setOpaque(false);
        chkEstReparateur.setFocusPainted(false);
        // Caché par défaut, on l'affiche seulement si c'est un Proprio
        chkEstReparateur.setVisible(false); 
        
        rightPanel.add(chkEstReparateur, gbcForm);

        // Bouton Sauvegarder
        gbcForm.gridy = 4;
        gbcForm.insets = new Insets(10, 10, 0, 10);
        
        btnSave = UIFactory.createGradientButton("Enregistrer les modifications");
        btnSave.setPreferredSize(new Dimension(0, 45));
        btnSave.addActionListener(e -> sauvegarder());
        
        rightPanel.add(btnSave, gbcForm);

        gbc.gridx = 2; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.BOTH; gbc.insets = new Insets(0, 0, 0, 0);
        card.add(rightPanel, gbc);

        return card;
    }

    // ... [createInputBlock, choisirPhoto, updateAvatarDisplay INCHANGÉS] ...
    private JPanel createInputBlock(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 6)); p.setOpaque(false); JLabel l = new JLabel(label); l.setFont(new Font("Segoe UI", Font.BOLD, 12)); l.setForeground(Theme.TEXT_HEADLINE); field.setPreferredSize(new Dimension(0, 38)); p.add(l, BorderLayout.NORTH); p.add(field, BorderLayout.CENTER); return p;
    }
    
    private void choisirPhoto() {
        JFileChooser chooser = new JFileChooser(); chooser.setDialogTitle("Choisir une photo"); chooser.setFileSelectionMode(JFileChooser.FILES_ONLY); chooser.addChoosableFileFilter(new FileNameExtensionFilter("Images", "jpg", "png")); chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { try { File s = chooser.getSelectedFile(); File d = new File("photos"); if (!d.exists()) d.mkdir(); String n = "u_" + System.currentTimeMillis() + ".jpg"; File dest = new File(d, n); Files.copy(s.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING); this.currentPhotoPath = dest.getPath().replace("\\", "/"); updateAvatarDisplay(this.currentPhotoPath); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage()); } }
    }
    
    private void updateAvatarDisplay(String path) {
        if (path != null && new File(path).exists()) { ImageIcon i = new ImageIcon(path); Image m = i.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH); lblPhoto.setIcon(new ImageIcon(m)); lblInitials.setVisible(false); lblPhoto.setVisible(true); } else { lblPhoto.setIcon(null); lblPhoto.setVisible(false); lblInitials.setVisible(true); } avatarCircle.revalidate(); avatarCircle.repaint();
    }

    // =========================================================
    // CHARGEMENT
    // =========================================================
    public void chargerDonnees() {
        User u = frame.getCurrentUser();
        if (u != null) {
            txtNom.setText(u.getNom());
            txtPrenom.setText(u.getPrenom());
            txtEmail.setText(u.getEmail());
            txtTel.setText(u.getNumtel());
            txtCin.setText(u.getCin());
            txtMdp.setText(u.getMdp());

            // Initiales
            String initials = "";
            if(!u.getPrenom().isEmpty()) initials += u.getPrenom().charAt(0);
            if(!u.getNom().isEmpty()) initials += u.getNom().charAt(0);
            lblInitials.setText(initials.toUpperCase());

            // Rôle & Option Double Rôle
            if (u instanceof Proprietaire) {
                Proprietaire p = (Proprietaire) u;
                lblRole.setText("PROPRIÉTAIRE");
                lblRole.setForeground(Theme.PRIMARY);
                avatarCircle.setBackground(Theme.SIDEBAR_BG);
                
                // 🔥 Affiche la case et coche si vrai
                chkEstReparateur.setVisible(true);
                chkEstReparateur.setSelected(p.isEstReparateur()); // Getter de boolean
            } else {
                lblRole.setText("RÉPARATEUR");
                lblRole.setForeground(new Color(16, 185, 129));
                avatarCircle.setBackground(new Color(16, 185, 129));
                chkEstReparateur.setVisible(false); // Pas pour les employés
            }

            this.currentPhotoPath = u.getPhotoPath();
            updateAvatarDisplay(this.currentPhotoPath);
        }
    }

    // =========================================================
    // SAUVEGARDE
    // =========================================================
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
            u.setPhotoPath(this.currentPhotoPath); 

            // 🔥 Sauvegarde du rôle hybride
            boolean roleChanged = false;
            if (u instanceof Proprietaire) {
                Proprietaire p = (Proprietaire) u;
                boolean ancienEtat = p.isEstReparateur();
                boolean nouvelEtat = chkEstReparateur.isSelected();
                
                if (ancienEtat != nouvelEtat) {
                    p.setEstReparateur(nouvelEtat);
                    roleChanged = true;
                }
            }

            metier.modifierUtilisateur(u);
            
            JOptionPane.showMessageDialog(this, "Profil mis à jour !", "Succès", JOptionPane.INFORMATION_MESSAGE);
            
            chargerDonnees();
            
           
            frame.setCurrentUser(u); 
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}