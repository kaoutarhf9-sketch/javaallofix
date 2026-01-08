package presentation;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import dao.Proprietaire;
import metier.IGestionUser;

public class ViewRegister extends JPanel {

    private ModernMainFrame frame;
    private IGestionUser metier;

    // Champs déclarés au niveau classe pour accès facile
    private JTextField txtNom, txtPrenom, txtCin, txtTel, txtEmail;
    private JPasswordField txtMdp;
    private JToggleButton toggleReparateur;
    private JButton btnValider;

    public ViewRegister(ModernMainFrame frame, IGestionUser metier) {
        this.frame = frame;
        this.metier = metier;

        // 1. CONFIGURATION GÉNÉRALE
        setLayout(new GridBagLayout());
        setBackground(Theme.BACKGROUND);

        // 2. LA CARTE CENTRALE (Flottante)
        JPanel card = UIFactory.createCard();
        card.setLayout(new GridBagLayout()); 
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // --- A. HEADER (Bouton Retour) ---
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        header.setOpaque(false);
        
        JLabel lblBack = new JLabel("← Retour à l'accueil");
        lblBack.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBack.setForeground(Theme.TEXT_BODY);
        lblBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblBack.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { frame.changerVue(ModernMainFrame.VUE_ACCUEIL); }
            public void mouseEntered(MouseEvent e) { lblBack.setForeground(Theme.GRADIENT_START); }
            public void mouseExited(MouseEvent e) { lblBack.setForeground(Theme.TEXT_BODY); }
        });
        header.add(lblBack);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(header, gbc);

        // --- B. TITRE & SOUS-TITRE ---
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setOpaque(false);
        
        JLabel title = new JLabel("Créer un compte");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_HEADLINE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel subtitle = new JLabel("Rejoignez la communauté AlloFix en 2 minutes.");
        subtitle.setFont(Theme.FONT_REGULAR);
        subtitle.setForeground(Theme.TEXT_BODY);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        
        titlePanel.add(title);
        titlePanel.add(subtitle);

        gbc.gridy = 1; 
        gbc.insets = new Insets(10, 20, 30, 20); 
        card.add(titlePanel, gbc);

        // --- C. FORMULAIRE (Grille 2 Colonnes) ---
        gbc.gridwidth = 1; 
        gbc.insets = new Insets(5, 10, 15, 10); 

        // Ligne 1 : Nom / Prénom
        txtNom = UIFactory.createModernField();
        txtPrenom = UIFactory.createModernField();
        
        gbc.gridy = 2; gbc.gridx = 0; card.add(createFieldGroup("Nom", txtNom), gbc);
        gbc.gridy = 2; gbc.gridx = 1; card.add(createFieldGroup("Prénom", txtPrenom), gbc);

        // Ligne 2 : CIN / Téléphone
        txtCin = UIFactory.createModernField();
        txtTel = UIFactory.createModernField();
        
        gbc.gridy = 3; gbc.gridx = 0; card.add(createFieldGroup("Identifiant (CIN)", txtCin), gbc);
        gbc.gridy = 3; gbc.gridx = 1; card.add(createFieldGroup("Téléphone", txtTel), gbc);

        // Ligne 3 : Email / Mot de passe
        txtEmail = UIFactory.createModernField();
        txtMdp = createModernPasswordField(); 
        
        gbc.gridy = 4; gbc.gridx = 0; card.add(createFieldGroup("Email", txtEmail), gbc);
        gbc.gridy = 4; gbc.gridx = 1; card.add(createFieldGroup("Mot de passe", txtMdp), gbc);

        // --- D. OPTION RÉPARATEUR (SWITCH) ---
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 20, 20, 20);
        
        JPanel switchContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        switchContainer.setOpaque(false);
        switchContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(241, 245, 249), 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));

        toggleReparateur = createPremiumSwitch();
        JLabel lblSwitchTitle = new JLabel("Compte Réparateur");
        lblSwitchTitle.setFont(Theme.FONT_BOLD);
        lblSwitchTitle.setForeground(Theme.TEXT_HEADLINE);
        
        JLabel lblSwitchDesc = new JLabel(" (Cochez si vous proposez des services)");
        lblSwitchDesc.setFont(Theme.FONT_REGULAR);
        lblSwitchDesc.setForeground(Theme.TEXT_BODY);

        switchContainer.add(toggleReparateur);
        switchContainer.add(lblSwitchTitle);
        switchContainer.add(lblSwitchDesc);
        
        card.add(switchContainer, gbc);

        // --- E. BOUTON D'ACTION ---
        btnValider = UIFactory.createGradientButton("Confirmer l'inscription");
        btnValider.addActionListener(e -> traiterInscription()); // Appel méthode propre

        gbc.gridy = 6; 
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.NONE; 
        gbc.anchor = GridBagConstraints.CENTER;
        card.add(btnValider, gbc);

        // --- F. LIEN LOGIN ---
        JPanel loginLinkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loginLinkPanel.setOpaque(false);
        
        JLabel lblDeja = new JLabel("Déjà un compte ? ");
        lblDeja.setFont(Theme.FONT_REGULAR);
        lblDeja.setForeground(Theme.TEXT_BODY);
        
        JLabel lblLogin = new JLabel("Se connecter");
        lblLogin.setFont(Theme.FONT_BOLD);
        lblLogin.setForeground(Theme.GRADIENT_START);
        lblLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLogin.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { frame.changerVue(ModernMainFrame.VUE_LOGIN_PROPRIO); }
            public void mouseEntered(MouseEvent e) { lblLogin.setText("<html><u>Se connecter</u></html>"); }
            public void mouseExited(MouseEvent e) { lblLogin.setText("Se connecter"); }
        });

        loginLinkPanel.add(lblDeja);
        loginLinkPanel.add(lblLogin);

        gbc.gridy = 7;
        card.add(loginLinkPanel, gbc);

        add(card);
    }

    // =========================================================
    //                    LOGIQUE MÉTIER
    // =========================================================

    private void traiterInscription() {
        // 1. Validation basique
        if(txtNom.getText().isEmpty() || txtCin.getText().isEmpty() || new String(txtMdp.getPassword()).isEmpty()) {
             JOptionPane.showMessageDialog(this, "Veuillez remplir les champs obligatoires (Nom, CIN, Mot de passe).", "Attention", JOptionPane.WARNING_MESSAGE);
             return;
        }

        // 2. UI Loading
        btnValider.setText("Création en cours...");
        btnValider.setEnabled(false);

        // 3. Traitement en arrière-plan (SwingWorker)
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Proprietaire p = new Proprietaire();
                p.setNom(txtNom.getText());
                p.setPrenom(txtPrenom.getText());
                p.setCin(txtCin.getText());
                p.setNumtel(txtTel.getText());
                p.setEmail(txtEmail.getText());
                p.setMdp(new String(txtMdp.getPassword()));
                p.setEstReparateur(toggleReparateur.isSelected());
                p.setDateinscription(LocalDate.now());

                // C'est ici que l'exception "Duplicate Entry" peut survenir
                metier.inscriptionProprietaire(p); 
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); // Vérifie s'il y a eu une erreur
                    
                    // Si on arrive ici, tout s'est bien passé
                    JOptionPane.showMessageDialog(ViewRegister.this, "Bienvenue chez AlloFix !\nVotre compte a été créé.", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    frame.changerVue(ModernMainFrame.VUE_LOGIN_PROPRIO);

                } catch (ExecutionException e) {
                    // C'est ici qu'on récupère l'erreur de GestionUser (ex: "Ce CIN existe déjà")
                    Throwable cause = e.getCause();
                    JOptionPane.showMessageDialog(ViewRegister.this, cause.getMessage(), "Erreur d'inscription", JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ViewRegister.this, "Erreur technique : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                } finally {
                    // On rétablit le bouton
                    btnValider.setText("Confirmer l'inscription");
                    btnValider.setEnabled(true);
                }
            }
        }.execute();
    }

    // =========================================================
    //                  HELPER METHODS (DESIGN)
    // =========================================================

    private JPanel createFieldGroup(String labelText, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        
        JLabel l = new JLabel(labelText);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(Theme.TEXT_BODY); 
        
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private JPasswordField createModernPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(Theme.FONT_REGULAR);
        field.setForeground(Theme.TEXT_HEADLINE);
        field.setCaretColor(Theme.GRADIENT_START);
        field.setBorder(BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(new Color(226, 232, 240), 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new javax.swing.border.LineBorder(Theme.GRADIENT_START, 2, true),
                    new EmptyBorder(9, 14, 9, 14)
                ));
            }
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new javax.swing.border.LineBorder(new Color(226, 232, 240), 1, true),
                    new EmptyBorder(10, 15, 10, 15)
                ));
            }
        });
        return field;
    }

    private JToggleButton createPremiumSwitch() {
        JToggleButton btn = new JToggleButton();
        btn.setPreferredSize(new Dimension(50, 30));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        
        btn.addActionListener(e -> btn.repaint()); 

        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                boolean selected = btn.isSelected();
                int w = c.getWidth();
                int h = c.getHeight();
                
                if (selected) g2.setColor(Theme.SUCCESS); 
                else g2.setColor(new Color(203, 213, 225));
                
                g2.fillRoundRect(0, 0, w, h, 30, 30);
                
                g2.setColor(Color.WHITE);
                int size = h - 6;
                int x = selected ? (w - size - 3) : 3; 
                g2.fillOval(x, 3, size, size);
                
                g2.dispose();
            }
        });
        return btn;
    }
}