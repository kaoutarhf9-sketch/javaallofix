package presentation;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
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

    public ViewLogin(ModernMainFrame frame, IGestionUser metier, String userType) {
        this.frame = frame;
        this.metier = metier;
        this.userType = userType;

        // 1. CONFIGURATION DU FOND
        setLayout(new GridBagLayout());
        setBackground(Theme.BACKGROUND);

        // 2. CRÉATION DE LA CARTE CENTRALE
        JPanel card = UIFactory.createCard();
        card.setPreferredSize(new Dimension(400, 600)); // Taille fixe élégante

        // --- A. EN-TÊTE (BOUTON RETOUR) ---
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        header.setOpaque(false);
        
        JLabel lblBack = new JLabel("← Retour");
        lblBack.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBack.setForeground(Theme.TEXT_BODY);
        lblBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblBack.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { frame.changerVue(ModernMainFrame.VUE_ACCUEIL); }
            public void mouseEntered(MouseEvent e) { lblBack.setForeground(Theme.GRADIENT_START); }
            public void mouseExited(MouseEvent e) { lblBack.setForeground(Theme.TEXT_BODY); }
        });
        header.add(lblBack);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- B. ICÔNE & TITRE ---
        JLabel icon = new JLabel("🔐");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Espace " + userType);
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_HEADLINE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Veuillez vous identifier");
        subtitle.setFont(Theme.FONT_REGULAR);
        subtitle.setForeground(Theme.TEXT_BODY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- C. CHAMPS DE SAISIE ---
        // Login
        JLabel lblCin = UIFactory.createTitle("Identifiant (CIN/Email)");
        lblCin.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JTextField txtCin = UIFactory.createModernField();
        txtCin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        
        // Mot de passe (On doit le styliser manuellement car UIFactory ne fait que JTextField)
        JLabel lblMdp = UIFactory.createTitle("Mot de passe");
        lblMdp.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JPasswordField txtMdp = createModernPasswordField();
        txtMdp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        // --- D. BOUTON & ERREUR ---
        JButton btnLogin = UIFactory.createGradientButton("Se connecter");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); // Prend toute la largeur

        JLabel lblError = new JLabel(" ");
        lblError.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblError.setForeground(Theme.DANGER); // Rouge
        lblError.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- LOGIQUE DE CONNEXION ---
        btnLogin.addActionListener(e -> {
            lblError.setText(" ");
            String cin = txtCin.getText().trim();
            String mdp = new String(txtMdp.getPassword());

            if (cin.isEmpty() || mdp.isEmpty()) {
                lblError.setText("Veuillez remplir tous les champs");
                return;
            }

            // Animation visuelle
            btnLogin.setEnabled(false);
            btnLogin.setText("Connexion en cours...");

            Timer t = new Timer(500, ev -> {
                User u = metier.seConnecter(cin, mdp);
                btnLogin.setEnabled(true);
                btnLogin.setText("Se connecter");

                if (u == null) {
                    lblError.setText("Identifiants incorrects.");
                    // Petit effet visuel : vider le mdp
                    txtMdp.setText("");
                    return;
                }

                // Redirection
                frame.setCurrentUser(u);
                if (userType.equals("Propriétaire") && u instanceof Proprietaire) {
                    frame.changerVue(ModernMainFrame.VUE_DASHBOARD_PROPRIO);
                } else if (userType.equals("Réparateur") && u instanceof Reparateur) {
                    frame.changerVue(ModernMainFrame.VUE_DASHBOARD_REPARATEUR);
                } else {
                    lblError.setText("Rôle non autorisé.");
                }
            });
            t.setRepeats(false);
            t.start();
        });

        // Touche Entrée pour valider
        txtMdp.addActionListener(e -> btnLogin.doClick());

        // --- E. PIED DE CARTE (INSCRIPTION) ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        
        JLabel lblNoAccount = new JLabel("Pas de compte ? ");
        lblNoAccount.setFont(Theme.FONT_REGULAR);
        lblNoAccount.setForeground(Theme.TEXT_BODY);

        JLabel lblSignup = new JLabel("Créer un compte");
        lblSignup.setFont(Theme.FONT_BOLD);
        lblSignup.setForeground(Theme.GRADIENT_START);
        lblSignup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblSignup.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { frame.changerVue(ModernMainFrame.VUE_REGISTER_PROPRIO); }
            public void mouseEntered(MouseEvent e) { lblSignup.setText("<html><u>Créer un compte</u></html>"); }
            public void mouseExited(MouseEvent e) { lblSignup.setText("Créer un compte"); }
        });

        footerPanel.add(lblNoAccount);
        footerPanel.add(lblSignup);

        // --- ASSEMBLAGE FINAL DANS LA CARTE ---
        // On utilise Box.createVerticalStrut(x) pour gérer l'espacement vertical (Margin)
        
        card.add(header);
        card.add(Box.createVerticalStrut(20));
        
        card.add(icon);
        card.add(Box.createVerticalStrut(10));
        card.add(title);
        card.add(Box.createVerticalStrut(5));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(30));

        card.add(lblCin);
        card.add(Box.createVerticalStrut(5));
        card.add(txtCin);
        card.add(Box.createVerticalStrut(15));

        card.add(lblMdp);
        card.add(Box.createVerticalStrut(5));
        card.add(txtMdp);
        card.add(Box.createVerticalStrut(30));

        card.add(btnLogin);
        card.add(Box.createVerticalStrut(10));
        card.add(lblError);
        
        card.add(Box.createVerticalGlue()); // Pousse le footer vers le bas si besoin
        card.add(footerPanel);

        add(card);
    }

    /**
     * Méthode utilitaire locale pour styliser le PasswordField
     * (Car UIFactory ne retourne que des JTextField pour l'instant)
     */
    private JPasswordField createModernPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(Theme.FONT_REGULAR);
        field.setForeground(Theme.TEXT_HEADLINE);
        field.setCaretColor(Theme.GRADIENT_START);
        
        // Bordure composée : Ligne grise + Padding
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(226, 232, 240), 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));

        // Focus Effect
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(Theme.GRADIENT_START, 2, true),
                    new EmptyBorder(9, 14, 9, 14)
                ));
            }
            public void focusLost(FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(226, 232, 240), 1, true),
                    new EmptyBorder(10, 15, 10, 15)
                ));
            }
        });
        return field;
    }
}