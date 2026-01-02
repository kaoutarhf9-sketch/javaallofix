package presentation;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ClientMainFrame extends JFrame {

    public ClientMainFrame() {
        // 1. CONFIGURATION DE LA FENÊTRE
        setTitle("AlloFix | Réparation Express");
        setSize(1280, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrer
        
        // Appliquer le fond global
        getContentPane().setBackground(Theme.BACKGROUND);
        setLayout(new BorderLayout());

        // 2. NAVBAR (Bleu Marine, identique Admin)
        add(createNavbar(), BorderLayout.NORTH);

        // 3. CONTENU CENTRAL (Hero Section + Recherche)
        // On utilise un JScrollPane invisible au cas où l'écran est petit
        JScrollPane scroll = new JScrollPane(createHeroSection());
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16); // Scroll fluide
        add(scroll, BorderLayout.CENTER);

        // 4. FOOTER
        add(createFooter(), BorderLayout.SOUTH);
    }

    // ========================================================================================
    // SECTION CENTRALE : TITRE & BARRE DE RECHERCHE
    // ========================================================================================
    private JPanel createHeroSection() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(Theme.BACKGROUND);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; 
        gbc.anchor = GridBagConstraints.CENTER;

        // 1. Icône décorative
        JLabel iconHero = new JLabel("⚡");
        iconHero.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        gbc.gridy = 0;
        gbc.insets = new Insets(40, 0, 20, 0); // Marge haut
        container.add(iconHero, gbc);

        // 2. Gros Titre (Bleu Marine)
        JLabel title = new JLabel("Réparer n'a jamais été aussi simple");
        title.setFont(new Font("Segoe UI", Font.BOLD, 42));
        title.setForeground(Theme.NAVY);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 15, 0);
        container.add(title, gbc);

        // 3. Sous-titre (Gris)
        JLabel subtitle = new JLabel("Trouvez un expert certifié près de chez vous en 2 minutes.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitle.setForeground(Theme.TEXT_GRAY);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 50, 0); // Espace avant la barre
        container.add(subtitle, gbc);

        // 4. BARRE DE RECHERCHE FLOTTANTE
        JPanel searchBar = createFloatingSearchBar();
        
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 30, 0);
        container.add(searchBar, gbc);

        // 5. TAGS RAPIDES (Boutons sous la barre)
        JPanel tagsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        tagsPanel.setOpaque(false);
        tagsPanel.add(createQuickTag("📱 Smartphone"));
        tagsPanel.add(createQuickTag("💻 Ordinateur"));
        tagsPanel.add(createQuickTag("🎮 Console"));
        tagsPanel.add(createQuickTag("⌚ Objets Connectés"));

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 50, 0); // Marge bas
        container.add(tagsPanel, gbc);

        return container;
    }

    /**
     * Crée la barre de recherche avec ombre portée et coins arrondis
     * Design "Pill" moderne.
     */
    private JPanel createFloatingSearchBar() {
        // Panel avec dessin personnalisé (Ombre + Fond Blanc)
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre portée (Gris transparent décalé)
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 50, 50);
                
                // Fond Blanc
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 50, 50);
                g2.dispose();
            }
        };
        p.setLayout(new BorderLayout());
        p.setPreferredSize(new Dimension(800, 75));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(5, 30, 10, 10)); // Marges internes

        // Icône Loupe
        JLabel searchIcon = new JLabel("🔍  ");
        searchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        searchIcon.setForeground(Theme.PRIMARY);

        // Champ de saisie
        JTextField txtSearch = new JTextField("Ex: Écran iPhone 13, Batterie Samsung, PC lent...");
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtSearch.setBorder(null);
        txtSearch.setOpaque(false);
        txtSearch.setForeground(Theme.TEXT_GRAY);
        
        // Gestion du Placeholder
        txtSearch.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if(txtSearch.getText().startsWith("Ex:")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(Theme.TEXT_DARK);
                }
            }
            public void focusLost(FocusEvent e) {
                if(txtSearch.getText().isEmpty()) {
                    txtSearch.setText("Ex: Écran iPhone 13, Batterie Samsung, PC lent...");
                    txtSearch.setForeground(Theme.TEXT_GRAY);
                }
            }
        });

        // Bouton "Rechercher" (Style Pillule dans la barre)
        JButton btnSearch = new JButton("Trouver ma réparation");
        btnSearch.setBackground(Theme.PRIMARY);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnSearch.setFocusPainted(false);
        btnSearch.setBorderPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearch.setPreferredSize(new Dimension(220, 50));
        
        // Dessin arrondi spécifique pour le bouton interne
        btnSearch.setContentAreaFilled(false);
        btnSearch.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (btnSearch.getModel().isRollover()) g2.setColor(Theme.PRIMARY_HOVER);
                else g2.setColor(Theme.PRIMARY);
                
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 40, 40); // Arrondi
                
                super.paint(g2, c);
                g2.dispose();
            }
        });

        p.add(searchIcon, BorderLayout.WEST);
        p.add(txtSearch, BorderLayout.CENTER);
        p.add(btnSearch, BorderLayout.EAST);
        
        return p;
    }

    // ========================================================================================
    // NAVBAR & FOOTER
    // ========================================================================================
    private JPanel createNavbar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(Theme.NAVY); // Bleu Marine
        nav.setPreferredSize(new Dimension(1000, 70));
        nav.setBorder(new EmptyBorder(0, 40, 0, 40));

        // Logo
        JLabel logo = new JLabel("AlloFix");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        
        // Liens à droite
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 25, 15));
        rightPanel.setOpaque(false);
        
        // Lien "Aide"
        JLabel lblHelp = new JLabel("Comment ça marche ?");
        lblHelp.setForeground(new Color(203, 213, 225)); // Gris clair
        lblHelp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblHelp.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Bouton "Espace Pro"
        JButton btnPro = new JButton("Espace Pro");
        btnPro.setBackground(Theme.PRIMARY);
        btnPro.setForeground(Color.WHITE);
        btnPro.setFocusPainted(false);
        btnPro.setBorderPainted(false);
        btnPro.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnPro.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPro.setPreferredSize(new Dimension(130, 38));
        
        // ACTION : Basculer vers l'interface Admin
        btnPro.addActionListener(e -> {
            new ModernMainFrame().setVisible(true); // Ouvre l'admin
            this.dispose(); // Ferme le client
        });

        rightPanel.add(lblHelp);
        rightPanel.add(btnPro);

        nav.add(logo, BorderLayout.WEST);
        nav.add(rightPanel, BorderLayout.EAST);
        return nav;
    }

    private JPanel createFooter() {
        JPanel p = new JPanel();
        p.setBackground(Theme.BACKGROUND);
        p.setBorder(new EmptyBorder(30,0,20,0));
        JLabel l = new JLabel("© 2025 AlloFix Inc. - Plateforme certifiée");
        l.setForeground(Theme.TEXT_GRAY);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(l);
        return p;
    }

    // --- Helper pour créer les boutons tags ---
    private JButton createQuickTag(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBackground(Color.WHITE);
        btn.setForeground(Theme.TEXT_GRAY);
        
        // Bordure Gris Clair manuelle (sans dépendance Theme.INPUT_BORDER)
        btn.setBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1)); 
        
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 40));
        
        // Effet Hover
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBorder(BorderFactory.createLineBorder(Theme.PRIMARY, 1));
                btn.setForeground(Theme.PRIMARY);
                btn.setBackground(new Color(238, 242, 255)); // Bleu très pâle
            }
            public void mouseExited(MouseEvent e) {
                btn.setBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1));
                btn.setForeground(Theme.TEXT_GRAY);
                btn.setBackground(Color.WHITE);
            }
        });
        return btn;
    }

    public static void main(String[] args) {
        // Active le lissage de police
        System.setProperty("awt.useSystemAAFontSettings","on"); 
        System.setProperty("swing.aatext", "true");
        
        SwingUtilities.invokeLater(() -> new ClientMainFrame().setVisible(true));
    }
}