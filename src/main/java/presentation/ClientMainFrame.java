package presentation;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import dao.User; 
import dao.Proprietaire;

public class ClientMainFrame extends JFrame {

    // --- DONNÉES DE SESSION ---
    private User currentUser; // Si null = Visiteur, sinon = Connecté

    // --- COULEURS & THEME ---
    public static class Theme {
        public static final Color BACKGROUND = new Color(248, 250, 252); // Slate 50
        public static final Color TEXT_HEADLINE = new Color(15, 23, 42); // Slate 900
        public static final Color TEXT_BODY = new Color(100, 116, 139);  // Slate 500
        public static final Color PRIMARY = new Color(79, 70, 229);      // Indigo 600
        public static final Color GRADIENT_START = new Color(79, 70, 229);
        public static final Color GRADIENT_END = new Color(168, 85, 247); // Purple 500
        public static final Font FONT_HERO = new Font("Segoe UI", Font.BOLD, 48);
        public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    }

    // --- CONSTRUCTEUR ---
    public ClientMainFrame(User user) {
        this.currentUser = user;

        setTitle("AlloFix | Réparation d'Appareils & Expertise");
        setSize(1280, 950);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BACKGROUND);
        setLayout(new BorderLayout());

        // 1. NAVBAR
        add(createNavbar(), BorderLayout.NORTH);

        // 2. CONTENU SCROLLABLE
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Theme.BACKGROUND);
        content.add(createHeroSection(), BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        add(scroll, BorderLayout.CENTER);
    }

    // ========================================================================================
    // NAVBAR : NAVIGATION VERS MODERNMAINFRAME
    // ========================================================================================
    private JPanel createNavbar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(Color.WHITE);
        nav.setPreferredSize(new Dimension(1000, 80));
        nav.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(241, 245, 249)),
            new EmptyBorder(0, 50, 0, 50)
        ));

        // LOGO
        JLabel logo = new JLabel("⚡ AlloFix");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logo.setForeground(Theme.GRADIENT_START);
        nav.add(logo, BorderLayout.WEST);

        // BOUTONS
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        actions.setOpaque(false);

        if (this.currentUser == null) {
            // --- SCÉNARIO 1 : VISITEUR ---
            // Le bouton redirige vers l'application principale (ModernMainFrame) pour se connecter
            JButton btnLogin = createNavButton("Espace Pro / Connexion", false);
            btnLogin.addActionListener(e -> {
                this.dispose(); // Ferme cette fenêtre
                new ModernMainFrame().setVisible(true); // Ouvre l'app Pro
            });
            actions.add(btnLogin);

        } else {
            // --- SCÉNARIO 2 : DÉJÀ CONNECTÉ ---
            
            // Bouton Dashboard : Ouvre ModernMainFrame et restaure la session
            JButton btnDash = createNavButton("Accéder à mon Espace", true);
            btnDash.addActionListener(e -> {
                this.dispose();
                ModernMainFrame frame = new ModernMainFrame();
                
                // On passe l'utilisateur et on redirige vers le bon dashboard
                frame.setCurrentUser(this.currentUser);
                if (currentUser instanceof Proprietaire) {
                    frame.changerVue(ModernMainFrame.VUE_DASHBOARD_PROPRIO);
                } else {
                    frame.changerVue(ModernMainFrame.VUE_DASHBOARD_REPARATEUR);
                }
                frame.setVisible(true);
            });
            actions.add(btnDash);

            // Bouton Déconnexion
            JButton btnLogout = createNavButton("Déconnexion", false);
            btnLogout.setForeground(new Color(239, 68, 68));
            btnLogout.setBorder(BorderFactory.createLineBorder(new Color(254, 202, 202)));
            btnLogout.addActionListener(e -> {
                this.dispose();
                new ClientMainFrame(null).setVisible(true); // Recharge en mode visiteur
            });
            actions.add(btnLogout);
        }

        nav.add(actions, BorderLayout.EAST);
        return nav;
    }

    // ========================================================================================
    // HERO SECTION
    // ========================================================================================
    private JPanel createHeroSection() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(Theme.BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.anchor = GridBagConstraints.CENTER;

        // Espace Haut
        gbc.gridy = 0; container.add(Box.createVerticalStrut(60), gbc);

        // Titre
        JLabel title = new JLabel("La réparation, réinventée.");
        title.setFont(Theme.FONT_HERO);
        title.setForeground(Theme.TEXT_HEADLINE);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 15, 0);
        container.add(title, gbc);

        // Sous-titre
        JLabel subtitle = new JLabel("Trouvez un expert certifié près de chez vous en quelques clics.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        subtitle.setForeground(Theme.TEXT_BODY);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 50, 0);
        container.add(subtitle, gbc);

        // Barre de recherche
        JPanel searchBar = createMagicSearchBar();
        gbc.gridy++; gbc.insets = new Insets(0, 0, 60, 0);
        container.add(searchBar, gbc);

        // Catégories
        JPanel grid = new JPanel(new GridLayout(1, 4, 20, 0));
        grid.setOpaque(false);
        grid.add(createCategoryCard("Smartphone", "📱"));
        grid.add(createCategoryCard("Ordinateur", "💻"));
        grid.add(createCategoryCard("Console", "🎮"));
        grid.add(createCategoryCard("Maison", "🏠"));
        
        gbc.gridy++; gbc.insets = new Insets(0, 0, 80, 0);
        container.add(grid, gbc);

        return container;
    }

    // --- COMPOSANTS UI ---

    private JPanel createMagicSearchBar() {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth()-5, getHeight()-5, 60, 60);
                g2.setColor(new Color(0,0,0,10));
                g2.drawRoundRect(0, 0, getWidth()-5, getHeight()-5, 60, 60);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(750, 75));
        p.setBorder(new EmptyBorder(10, 30, 15, 10));

        JTextField field = new JTextField("Quel appareil souhaitez-vous réparer ?");
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBorder(null);
        field.setOpaque(false);
        field.setForeground(Theme.TEXT_BODY);
        
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().startsWith("Quel")) field.setText("");
            }
        });

        JButton btnSearch = new GradientButton("Rechercher");
        btnSearch.setPreferredSize(new Dimension(180, 50));

        p.add(field, BorderLayout.CENTER);
        p.add(btnSearch, BorderLayout.EAST);
        return p;
    }

    private JPanel createCategoryCard(String title, String icon) {
        JPanel card = new JPanel() {
            private boolean hover = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hover = true; repaint(); setCursor(new Cursor(Cursor.HAND_CURSOR)); }
                    public void mouseExited(MouseEvent e) { hover = false; repaint(); setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int yOff = hover ? -5 : 0;
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 5 + yOff, getWidth(), getHeight()-10, 25, 25);
                if(hover) {
                    g2.setColor(Theme.PRIMARY);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(0, 5 + yOff, getWidth()-1, getHeight()-11, 25, 25);
                }
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(160, 160));
        card.setLayout(new GridBagLayout());
        
        JLabel lIcon = new JLabel(icon); lIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        JLabel lTitle = new JLabel(title); lTitle.setFont(Theme.FONT_BOLD); lTitle.setForeground(Theme.TEXT_HEADLINE);
        
        GridBagConstraints g = new GridBagConstraints();
        g.gridx=0; g.gridy=0; g.insets = new Insets(0,0,10,0); card.add(lIcon, g);
        g.gridy=1; g.insets = new Insets(0,0,0,0); card.add(lTitle, g);
        return card;
    }

    private JButton createNavButton(String text, boolean primary) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (primary) {
            btn.setBackground(Theme.PRIMARY);
            btn.setForeground(Color.WHITE);
            btn.setBorder(new EmptyBorder(8, 20, 8, 20));
            btn.setContentAreaFilled(true);
        } else {
            btn.setForeground(Theme.TEXT_BODY);
            btn.setContentAreaFilled(false);
            btn.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
            btn.setPreferredSize(new Dimension(200, 40));
        }
        return btn;
    }

    class GradientButton extends JButton {
        public GradientButton(String text) {
            super(text);
            setContentAreaFilled(false); setFocusPainted(false); setBorderPainted(false);
            setForeground(Color.WHITE); setFont(Theme.FONT_BOLD);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(0, 0, Theme.GRADIENT_START, getWidth(), 0, Theme.GRADIENT_END);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
            super.paintComponent(g);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(() -> new ClientMainFrame(null).setVisible(true));
    }
}