package presentation;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import dao.Proprietaire;
import metier.IGestionUser;

public class ViewRegister extends JPanel {

    private ModernMainFrame frame;
    private IGestionUser metier;

    // Champs du formulaire
    private JTextField txtNom, txtPrenom, txtCin, txtTel, txtEmail;
    private JPasswordField txtMdp;
    private JToggleButton toggleReparateur;
    private JButton btnValider;

    public ViewRegister(ModernMainFrame frame, IGestionUser metier) {
        this.frame = frame;
        this.metier = metier;

        // 1. CONFIGURATION GLOBALE
        setLayout(new GridBagLayout()); // Pour centrer la carte
        setBackground(Theme.BACKGROUND);

        // 2. LA CARTE (Plus large que le login pour tenir les 2 colonnes)
        JPanel card = UIFactory.createCard();
        card.setPreferredSize(new Dimension(600, 720)); 

        // Nous utilisons un layout interne en Y_AXIS pour empiler les blocs (Header, Form, Footer)
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // --- A. HEADER (Retour + Logo) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(600, 30));
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblBack = new JLabel("← Retour");
        lblBack.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBack.setForeground(Theme.TEXT_BODY);
        lblBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblBack.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { frame.changerVue(ModernMainFrame.VUE_ACCUEIL); }
            public void mouseEntered(MouseEvent e) { lblBack.setForeground(Theme.PRIMARY); }
            public void mouseExited(MouseEvent e) { lblBack.setForeground(Theme.TEXT_BODY); }
        });
        headerPanel.add(lblBack, BorderLayout.WEST);
        
        // --- B. TITRES ---
        JLabel lblLogo = new JLabel("⚡ AlloFix");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblLogo.setForeground(Theme.TEXT_HEADLINE);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("Créer un nouveau compte");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Theme.PRIMARY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Rejoignez la plateforme de gestion #1");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(Theme.TEXT_BODY);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- C. LE FORMULAIRE (GRILLE) ---
        // On utilise un JPanel intermédiaire avec GridBagLayout pour aligner les champs proprement
        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setOpaque(false);
        formGrid.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 10, 15, 10); // Marges entre les champs
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;

        // Init des champs
        txtNom = UIFactory.createModernField();
        txtPrenom = UIFactory.createModernField();
        txtCin = UIFactory.createModernField();
        txtTel = UIFactory.createModernField();
        txtEmail = UIFactory.createModernField();
        txtMdp = UIFactory.createModernPasswordField();

        // Ligne 1 : Nom & Prénom
        gbc.gridy = 0; 
        gbc.gridx = 0; formGrid.add(createInputBlock("Nom", txtNom), gbc);
        gbc.gridx = 1; formGrid.add(createInputBlock("Prénom", txtPrenom), gbc);

        // Ligne 2 : CIN & Téléphone
        gbc.gridy = 1;
        gbc.gridx = 0; formGrid.add(createInputBlock("CIN", txtCin), gbc);
        gbc.gridx = 1; formGrid.add(createInputBlock("Téléphone", txtTel), gbc);

        // Ligne 3 : Email (Pleine largeur)
        gbc.gridy = 2; 
        gbc.gridx = 0; gbc.gridwidth = 2; // Span 2 colonnes
        formGrid.add(createInputBlock("Email professionnel", txtEmail), gbc);

        // Ligne 4 : Mot de passe (Pleine largeur)
        gbc.gridy = 3;
        formGrid.add(createInputBlock("Mot de passe", txtMdp), gbc);

        // --- D. SWITCH REPARATEUR (ENCADRÉ) ---
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 10, 20, 10); // Plus d'espace avant/après
        
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        switchPanel.setOpaque(false);
        // Bordure subtile pour délimiter cette option importante
        switchPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(226, 232, 240), 1, true),
            new EmptyBorder(12, 12, 12, 12)
        ));

        toggleReparateur = createCustomSwitch();
        JLabel lblSwitchTitle = new JLabel("Compte Réparateur");
        lblSwitchTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSwitchTitle.setForeground(Theme.TEXT_HEADLINE);
        
        JLabel lblSwitchDesc = new JLabel(" (Je propose des services)");
        lblSwitchDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSwitchDesc.setForeground(Theme.TEXT_BODY);

        switchPanel.add(toggleReparateur);
        switchPanel.add(lblSwitchTitle);
        switchPanel.add(lblSwitchDesc);
        
        formGrid.add(switchPanel, gbc);

        // --- E. BOUTON ACTION ---
        btnValider = UIFactory.createGradientButton("Confirmer l'inscription");
        btnValider.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnValider.setMaximumSize(new Dimension(400, 50)); // Pas trop large

        // --- F. FOOTER ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        
        JLabel lblDeja = new JLabel("Déjà membre ? ");
        lblDeja.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDeja.setForeground(Theme.TEXT_BODY);

        JLabel lblLogin = new JLabel("Se connecter");
        lblLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblLogin.setForeground(Theme.PRIMARY);
        lblLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLogin.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { frame.changerVue(ModernMainFrame.VUE_LOGIN_PROPRIO); }
            public void mouseEntered(MouseEvent e) { lblLogin.setText("<html><u>Se connecter</u></html>"); }
            public void mouseExited(MouseEvent e) { lblLogin.setText("Se connecter"); }
        });

        footerPanel.add(lblDeja);
        footerPanel.add(lblLogin);

        // --- LOGIQUE METIER ---
        btnValider.addActionListener(e -> traiterInscription());

        // --- ASSEMBLAGE FINAL ---
        card.add(headerPanel);
        card.add(Box.createVerticalStrut(15));
        
        card.add(lblLogo);
        card.add(Box.createVerticalStrut(5));
        card.add(lblTitle);
        card.add(Box.createVerticalStrut(5));
        card.add(lblSubtitle);
        card.add(Box.createVerticalStrut(25));
        
        card.add(formGrid); // Ajout du formulaire grille
        
        card.add(btnValider);
        card.add(Box.createVerticalStrut(10));
        card.add(footerPanel);

        add(card);
    }

    // --- HELPER : Création bloc Label + Input ---
    private JPanel createInputBlock(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(Theme.TEXT_HEADLINE);
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    // --- HELPER : Switch Custom (Vert / Gris) ---
    private JToggleButton createCustomSwitch() {
        JToggleButton btn = new JToggleButton();
        btn.setPreferredSize(new Dimension(44, 24));
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
                boolean on = btn.isSelected();
                // Fond
                g2.setColor(on ? new Color(16, 185, 129) : new Color(203, 213, 225)); 
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 24, 24);
                // Rond blanc
                g2.setColor(Color.WHITE);
                int size = c.getHeight() - 4;
                int x = on ? (c.getWidth() - size - 2) : 2; 
                g2.fillOval(x, 2, size, size);
                g2.dispose();
            }
        });
        return btn;
    }

    // --- LOGIQUE INSCRIPTION ---
    private void traiterInscription() {
        if(txtNom.getText().isEmpty() || txtCin.getText().isEmpty() || new String(txtMdp.getPassword()).isEmpty()) {
             JOptionPane.showMessageDialog(this, "Champs obligatoires manquants (Nom, CIN, Mot de passe).", "Attention", JOptionPane.WARNING_MESSAGE);
             return;
        }

        btnValider.setText("Création du compte...");
        btnValider.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

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
                
                metier.inscriptionProprietaire(p); 
                return null;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    get(); // Check exceptions
                    JOptionPane.showMessageDialog(ViewRegister.this, "Compte créé avec succès !\nConnectez-vous maintenant.", "Bienvenue", JOptionPane.INFORMATION_MESSAGE);
                    frame.changerVue(ModernMainFrame.VUE_LOGIN_PROPRIO);
                } catch (ExecutionException e) {
                    JOptionPane.showMessageDialog(ViewRegister.this, "Erreur : " + e.getCause().getMessage(), "Echec", JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ViewRegister.this, "Erreur technique.", "Erreur", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnValider.setText("Confirmer l'inscription");
                    btnValider.setEnabled(true);
                }
            }
        }.execute();
    }
}