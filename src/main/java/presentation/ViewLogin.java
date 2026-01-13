package presentation;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import dao.User;
import dao.Proprietaire;
import dao.Reparateur;
import metier.IGestionUser;

public class ViewLogin extends JPanel {

    private ModernMainFrame frame;
    private IGestionUser metier;
    private String userType;

    // Composants
    private JTextField txtCinField;
    private JPasswordField txtMdpField;
    private JButton btnLogin;
    private JLabel lblError;

    public ViewLogin(ModernMainFrame frame, IGestionUser metier, String userType) {
        this.frame = frame;
        this.metier = metier;
        this.userType = userType;

        // 1. CONFIGURATION DU FOND
        setLayout(new GridBagLayout()); 
        setBackground(Theme.BACKGROUND);

        // 2. LA CARTE DE CONNEXION
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        
        int height = userType.equals("Propriétaire") ? 540 : 480;
        card.setPreferredSize(new Dimension(420, height));
        
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1, true),
            new EmptyBorder(30, 40, 30, 40)
        ));
        
        // --- A. HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
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

        // --- B. BRANDING ---
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setOpaque(false);
        brandPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblLogo = new JLabel("AlloFix");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 42)); 
        lblLogo.setForeground(Theme.SIDEBAR_BG); 
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblSubtitle = new JLabel("Espace " + userType);
        lblSubtitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblSubtitle.setForeground(Theme.PRIMARY); 
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        brandPanel.add(lblLogo);
        brandPanel.add(Box.createVerticalStrut(10));
        brandPanel.add(lblSubtitle);

        // --- C. FORMULAIRE ---
        txtCinField = UIFactory.createModernField();
        JPanel pnlCin = createInputBlock("Identifiant (CIN)", txtCinField);
        
        txtMdpField = new JPasswordField();
        stylePasswordField(txtMdpField);
        JPanel pnlMdp = createInputBlock("Mot de passe", txtMdpField);

        btnLogin = UIFactory.createGradientButton("Se connecter");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        lblError = new JLabel(" ");
        lblError.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblError.setForeground(new Color(239, 68, 68)); // Rouge
        lblError.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // --- ASSEMBLAGE ---
        card.add(headerPanel);
        card.add(Box.createVerticalStrut(30));
        card.add(brandPanel);
        card.add(Box.createVerticalStrut(40));
        card.add(pnlCin);
        card.add(Box.createVerticalStrut(15));
        card.add(pnlMdp);
        card.add(Box.createVerticalStrut(30));
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(10));
        card.add(lblError);
        card.add(Box.createVerticalGlue()); 

        // --- FOOTER CONDITIONNEL ---
        if (userType.equals("Propriétaire")) {
            JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            footerPanel.setOpaque(false);
            footerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel lblNoAccount = new JLabel("Pas de compte ? ");
            lblNoAccount.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblNoAccount.setForeground(Theme.TEXT_BODY);

            JLabel lblSignup = new JLabel("S'inscrire");
            lblSignup.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblSignup.setForeground(Theme.PRIMARY);
            lblSignup.setCursor(new Cursor(Cursor.HAND_CURSOR));
            lblSignup.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) { frame.changerVue(ModernMainFrame.VUE_REGISTER_PROPRIO); }
                public void mouseEntered(MouseEvent e) { lblSignup.setText("<html><u>S'inscrire</u></html>"); }
                public void mouseExited(MouseEvent e) { lblSignup.setText("S'inscrire"); }
            });

            footerPanel.add(lblNoAccount);
            footerPanel.add(lblSignup);
            card.add(footerPanel);
        } else {
            card.add(Box.createVerticalStrut(10));
        }

        // --- LOGIQUE DE CONNEXION ---
        ActionListener loginAction = e -> {
             String cin = txtCinField.getText().trim();
             String mdp = new String(txtMdpField.getPassword());
             
             lblError.setText(" ");

             if(cin.isEmpty() || mdp.isEmpty()) { 
                 lblError.setText("Veuillez remplir tous les champs"); 
                 return; 
             }
             
             btnLogin.setEnabled(false);
             btnLogin.setText("Connexion...");
             setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
             
             Timer t = new Timer(600, ev -> {
                 User u = metier.seConnecter(cin, mdp);
                 
                 btnLogin.setEnabled(true);
                 btnLogin.setText("Se connecter");
                 setCursor(Cursor.getDefaultCursor());
                 
                 if(u != null) {
                     frame.setCurrentUser(u);
                     
                     if(u instanceof Proprietaire && userType.equals("Propriétaire")) {
                         frame.changerVue(ModernMainFrame.VUE_DASHBOARD_PROPRIO);
                     } 
                     else if(u instanceof Reparateur && userType.equals("Réparateur")) {
                         // 🔥 C'EST ICI LA CORRECTION : On redirige vers l'ATELIER
                         frame.changerVue(ModernMainFrame.VUE_REPARATEUR_ATELIER);
                     } 
                     else {
                         lblError.setText("Compte non autorisé ici");
                         frame.setCurrentUser(null);
                     }
                 } else {
                     lblError.setText("Identifiants incorrects");
                     txtMdpField.setText("");
                 }
             });
             
             t.setRepeats(false); 
             t.start();
        };

        btnLogin.addActionListener(loginAction);
        txtMdpField.addActionListener(loginAction);

        add(card);
    }

    private JPanel createInputBlock(String label, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(Theme.TEXT_HEADLINE);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        ((JComponent)field).setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        p.add(l);
        p.add(Box.createVerticalStrut(5));
        p.add(field);
        return p;
    }

    private void stylePasswordField(JPasswordField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(Theme.TEXT_HEADLINE);
        field.setCaretColor(Theme.PRIMARY);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(203, 213, 225), 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(Theme.PRIMARY, 2, true),
                    new EmptyBorder(7, 11, 7, 11)
                ));
            }
            public void focusLost(FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(203, 213, 225), 1, true),
                    new EmptyBorder(8, 12, 8, 12)
                ));
            }
        });
    }
}