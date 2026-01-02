package presentation;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import dao.*;
import metier.IGestionUser;

public class ViewLogin extends JPanel {

    public ViewLogin(ModernMainFrame frame, IGestionUser metier, String userType) {

        setLayout(new GridBagLayout());
        setBackground(Theme.BACKGROUND);

        JPanel card = createLoginCard();

        /* ================= HEADER ================= */
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        header.setOpaque(false);

        JLabel lblBack = new JLabel("← Retour");
        lblBack.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBack.setForeground(Theme.TEXT_GRAY);
        lblBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblBack.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                frame.changerVue(ModernMainFrame.VUE_ACCUEIL);
            }
            public void mouseEntered(MouseEvent e) {
                lblBack.setForeground(Theme.PRIMARY);
            }
            public void mouseExited(MouseEvent e) {
                lblBack.setForeground(Theme.TEXT_GRAY);
            }
        });
        header.add(lblBack);

        /* ================= TITRE ================= */
        JLabel icon = new JLabel("🔒");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 46));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Espace " + userType);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Theme.NAVY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Veuillez vous identifier pour continuer");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(Theme.TEXT_GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        /* ================= CHAMPS ================= */
        JTextField txtCin = createStyledField();
        JPasswordField txtMdp = createStyledPasswordField();

        addPlaceholder(txtCin, "Identifiant (CIN)");
        addPlaceholder(txtMdp, "Mot de passe");

        /* ================= BOUTON ================= */
        JButton btnLogin = createPrimaryButton("SE CONNECTER");

        JLabel lblError = new JLabel(" ");
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblError.setForeground(new Color(220, 38, 38));
        lblError.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnLogin.addActionListener(e -> {
            lblError.setText(" ");

            String cin = txtCin.getText().trim();
            String mdp = new String(txtMdp.getPassword());

            if (cin.isEmpty() || mdp.isEmpty()
                    || cin.equals("Identifiant (CIN)")
                    || mdp.equals("Mot de passe")) {
                lblError.setText("Veuillez remplir tous les champs");
                return;
            }

            btnLogin.setEnabled(false);
            btnLogin.setText("CONNEXION...");

            Timer t = new Timer(300, ev -> {
                User u = metier.seConnecter(cin, mdp);
                btnLogin.setEnabled(true);
                btnLogin.setText("SE CONNECTER");

                if (u == null) {
                    lblError.setText("Identifiant ou mot de passe incorrect");
                    return;
                }

                frame.setCurrentUser(u);

                if (userType.equals("Propriétaire") && u instanceof Proprietaire) {
                    frame.changerVue(ModernMainFrame.VUE_DASHBOARD_PROPRIO);
                } else if (userType.equals("Réparateur") && u instanceof Reparateur) {
                    frame.changerVue(ModernMainFrame.VUE_DASHBOARD_REPARATEUR);
                } else {
                    lblError.setText("Accès non autorisé pour ce rôle");
                }
            });
            t.setRepeats(false);
            t.start();
        });

        txtMdp.addActionListener(e -> btnLogin.doClick());

        /* ================= INSCRIPTION ================= */
        JPanel signupPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        signupPanel.setOpaque(false);

        JLabel lblQuestion = new JLabel("Vous n’avez pas de compte ?");
        lblQuestion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblQuestion.setForeground(Theme.TEXT_GRAY);

        JLabel lblSignup = new JLabel("Créer un compte " + userType.toLowerCase());
        lblSignup.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSignup.setForeground(Theme.PRIMARY);
        lblSignup.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        lblSignup.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                lblSignup.setText("<html><u>" + lblSignup.getText() + "</u></html>");
            }
            public void mouseExited(MouseEvent e) {
                lblSignup.setText("Créer un compte " + userType.toLowerCase());
            }
            public void mouseClicked(MouseEvent e) {
                frame.changerVue(ModernMainFrame.VUE_REGISTER_PROPRIO);
            }
        });

        signupPanel.add(lblQuestion);
        signupPanel.add(lblSignup);

        /* ================= ASSEMBLAGE ================= */
        card.add(header);
        card.add(Box.createVerticalStrut(15));
        card.add(icon);
        card.add(Box.createVerticalStrut(15));
        card.add(title);
        card.add(Box.createVerticalStrut(5));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(30));

        card.add(createLabel("Identifiant"));
        card.add(Box.createVerticalStrut(5));
        card.add(txtCin);
        card.add(Box.createVerticalStrut(15));

        card.add(createLabel("Mot de passe"));
        card.add(Box.createVerticalStrut(5));
        card.add(txtMdp);
        card.add(Box.createVerticalStrut(25));

        card.add(btnLogin);
        card.add(Box.createVerticalStrut(10));
        card.add(lblError);
        card.add(Box.createVerticalStrut(20));
        card.add(signupPanel);

        add(card);
    }

    /* ===================================================== */

    private JPanel createLoginCard() {
        JPanel p = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 30, 30);
                g2.setColor(Theme.SURFACE);
                g2.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 30, 30);
            }
        };
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setPreferredSize(new Dimension(380, 550));
        p.setBorder(new EmptyBorder(30, 40, 40, 40));
        p.setOpaque(false);
        return p;
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(Theme.TEXT_GRAY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField createStyledField() {
        JTextField f = new JTextField();
        styleInput(f);
        return f;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField f = new JPasswordField();
        styleInput(f);
        return f;
    }

    private void styleInput(JTextField field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(new CompoundBorder(
                new LineBorder(new Color(209, 213, 219), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? Theme.PRIMARY_HOVER : Theme.PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent()) / 2 - 2);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        return btn;
    }

    private void addPlaceholder(JTextField field, String text) {
        field.setText(text);
        field.setForeground(Theme.TEXT_GRAY);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(text)) {
                    field.setText("");
                    field.setForeground(Theme.TEXT_DARK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(text);
                    field.setForeground(Theme.TEXT_GRAY);
                }
            }
        });
    }
}
