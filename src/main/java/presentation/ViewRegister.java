package presentation;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import dao.Proprietaire;
import metier.IGestionUser;

public class ViewRegister extends JPanel {

    public ViewRegister(ModernMainFrame frame, IGestionUser metier) {
        setLayout(new GridBagLayout()); // Centrage global
        setBackground(Theme.BACKGROUND); // Fond gris bleuté

        // --- Création de la Carte ---
        JPanel card = createCard();
        card.setLayout(new GridBagLayout()); // Grille pour aligner les champs

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 15, 5, 15); // Marges standards
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- 1. Header (Bouton Retour) ---
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        header.setOpaque(false);
        
        JLabel lblBack = new JLabel("← Retour");
        lblBack.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBack.setForeground(Theme.TEXT_GRAY);
        lblBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblBack.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { frame.changerVue(ModernMainFrame.VUE_ACCUEIL); }
            public void mouseEntered(MouseEvent e) { lblBack.setForeground(Theme.PRIMARY); }
            public void mouseExited(MouseEvent e) { lblBack.setForeground(Theme.TEXT_GRAY); }
        });
        header.add(lblBack);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 15, 10, 15);
        card.add(header, gbc);

        // --- 2. Titre & Sous-titre ---
        JLabel title = new JLabel("Nouveau Compte");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Theme.NAVY); // Bleu Marine
        title.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel subtitle = new JLabel("Rejoignez la communauté AlloFix");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(Theme.TEXT_GRAY);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridy = 1; 
        gbc.anchor = GridBagConstraints.CENTER;
        card.add(title, gbc);

        gbc.gridy = 2; 
        gbc.insets = new Insets(0, 15, 25, 15);
        card.add(subtitle, gbc);

        // --- 3. Champs de Saisie (Grille 2 colonnes) ---
        gbc.gridwidth = 1; 
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 15, 15, 15); // Espacement des champs

        // Ligne A : Nom & Prénom
        JTextField txtNom = createStyledField();
        JTextField txtPrenom = createStyledField();
        gbc.gridy = 3; 
        gbc.gridx = 0; card.add(createFieldGroup("Nom", txtNom), gbc);
        gbc.gridx = 1; card.add(createFieldGroup("Prénom", txtPrenom), gbc);

        // Ligne B : CIN & Téléphone
        JTextField txtCin = createStyledField();
        JTextField txtTel = createStyledField();
        gbc.gridy = 4; 
        gbc.gridx = 0; card.add(createFieldGroup("Identifiant (CIN)", txtCin), gbc);
        gbc.gridx = 1; card.add(createFieldGroup("Téléphone", txtTel), gbc);

        // Ligne C : Email & Password
        JTextField txtEmail = createStyledField();
        JPasswordField txtMdp = createStyledPasswordField();
        gbc.gridy = 5;
        gbc.gridx = 0; card.add(createFieldGroup("Email", txtEmail), gbc);
        gbc.gridx = 1; card.add(createFieldGroup("Mot de passe", txtMdp), gbc);

        // --- 4. Switch "Devenir Réparateur" ---
        gbc.gridy = 6; 
        gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 15, 15, 15);
        
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        switchPanel.setOpaque(false);
        
        JToggleButton toggleReparateur = createModernSwitch();
        JLabel lblSwitch = new JLabel("Je souhaite aussi proposer mes services de réparation");
        lblSwitch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSwitch.setForeground(Theme.TEXT_DARK);
        
        switchPanel.add(toggleReparateur);
        switchPanel.add(lblSwitch);
        card.add(switchPanel, gbc);

        // --- 5. Bouton Valider ---
        JButton btnValider = createPrimaryButton("CRÉER MON COMPTE");
        
        gbc.gridy = 7;
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        card.add(btnValider, gbc);

        // --- 6. Lien Login ---
        JLabel lblLogin = new JLabel("<html>Déjà inscrit ? <font color='#4F46E5'>Se connecter</font></html>");
        lblLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLogin.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                frame.changerVue(ModernMainFrame.VUE_LOGIN_PROPRIO);
            }
        });

        gbc.gridy = 8;
        card.add(lblLogin, gbc);

        // --- LOGIQUE MÉTIER ---
        btnValider.addActionListener(e -> {
            // Validation basique
            if(txtNom.getText().isEmpty() || txtCin.getText().isEmpty() || new String(txtMdp.getPassword()).isEmpty()) {
                 JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs obligatoires.", "Erreur", JOptionPane.WARNING_MESSAGE);
                 return;
            }

            btnValider.setText("CRÉATION EN COURS...");
            btnValider.setEnabled(false);

            Timer t = new Timer(300, evt -> {
                try {
                    Proprietaire p = new Proprietaire();
                    p.setNom(txtNom.getText());
                    p.setPrenom(txtPrenom.getText());
                    p.setCin(txtCin.getText());
                    p.setNumtel(txtTel.getText());
                    p.setEmail(txtEmail.getText());
                    p.setMdp(new String(txtMdp.getPassword()));
                    p.setEstReparateur(toggleReparateur.isSelected());
                    p.setDateinscription(LocalDate.now());

                    metier.inscriptionProprietaire(p);
                    
                    JOptionPane.showMessageDialog(this, "Compte créé avec succès !", "Bienvenue", JOptionPane.INFORMATION_MESSAGE);
                    frame.changerVue(ModernMainFrame.VUE_LOGIN_PROPRIO);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de l'inscription : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                    btnValider.setText("CRÉER MON COMPTE");
                    btnValider.setEnabled(true);
                }
            });
            t.setRepeats(false);
            t.start();
        });

        add(card);
    }

    // =========================================================
    //              COMPOSANTS GRAPHIQUES MODERNES
    // =========================================================

    private JPanel createCard() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Ombre
                g2.setColor(new Color(0,0,0,20));
                g2.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 30, 30);
                // Fond
                g2.setColor(Theme.SURFACE);
                g2.fillRoundRect(0, 0, getWidth()-5, getHeight()-5, 30, 30);
                g2.dispose();
            }
        };
    }

    private JPanel createFieldGroup(String labelText, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        JLabel l = new JLabel(labelText);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(Theme.TEXT_GRAY);
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private JTextField createStyledField() {
        JTextField field = new JTextField();
        styleInput(field);
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        styleInput(field);
        return field;
    }

    private void styleInput(JTextField field) {
        field.setPreferredSize(new Dimension(220, 40)); 
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(Theme.TEXT_DARK);
        field.setBackground(Color.WHITE);
        field.setCaretColor(Theme.PRIMARY);
        
        field.setBorder(new CompoundBorder(
            new LineBorder(new Color(209, 213, 219), 1, true),
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                field.setBorder(new CompoundBorder(new LineBorder(Theme.PRIMARY, 2, true), new EmptyBorder(4,9,4,9)));
            }
            public void focusLost(FocusEvent e) {
                field.setBorder(new CompoundBorder(new LineBorder(new Color(209, 213, 219), 1, true), new EmptyBorder(5,10,5,10)));
            }
        });
    }

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) g2.setColor(Theme.PRIMARY_HOVER);
                else if (getModel().isRollover()) g2.setColor(Theme.PRIMARY_HOVER);
                else g2.setColor(Theme.PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(250, 45));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JToggleButton createModernSwitch() {
        JToggleButton btn = new JToggleButton();
        btn.setPreferredSize(new Dimension(50, 28));
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
                else g2.setColor(new Color(209, 213, 219));
                
                g2.fillRoundRect(0, 0, w, h, 28, 28);
                
                g2.setColor(Color.WHITE);
                int size = h - 4;
                int x = selected ? (w - size - 2) : 2; 
                g2.fillOval(x, 2, size, size);
                g2.dispose();
            }
        });
        return btn;
    }
}