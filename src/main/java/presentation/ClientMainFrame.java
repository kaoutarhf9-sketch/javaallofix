package presentation;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import dao.User;
import dao.Proprietaire;
import dao.Reparateur;

public class ClientMainFrame extends JFrame {

    private User currentUser; 

    // Constructeur sans user (lancement par défaut)
    public ClientMainFrame() {
        this(null);
    }

    public ClientMainFrame(User user) {
        this.currentUser = user;

        setTitle("AlloFix | Portail Service");
        setSize(1280, 900);
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
    // 1. NAVBAR AVEC LOGIQUE DE BASCULE VERS MODERNMAINFRAME
    // ========================================================================================
    private JPanel createNavbar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(Theme.BACKGROUND);
        nav.setPreferredSize(new Dimension(1000, 90));
        nav.setBorder(new EmptyBorder(20, 50, 20, 50));

        // --- LOGO ---
        JLabel logo = new JLabel("AlloFix");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logo.setForeground(Theme.SIDEBAR_BG); 
        nav.add(logo, BorderLayout.WEST);

        // --- ACTIONS ---
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        actions.setOpaque(false);

        if (this.currentUser == null) {
            
            // CAS 1 : VISITEUR (NON CONNECTÉ)
            JButton btnPro = createNavButton("Espace Pro / Connexion", true); 
            
            btnPro.addActionListener(e -> {
                // Feedback visuel
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                btnPro.setText("Chargement...");
                btnPro.setEnabled(false);

                // 🔥 TRANSITION VERS MODERN MAIN FRAME
                new SwingWorker<ModernMainFrame, Void>() {
                    @Override
                    protected ModernMainFrame doInBackground() throws Exception {
                        // On instancie la "Grosse" application (Backend)
                        return new ModernMainFrame(); 
                    }

                    @Override
                    protected void done() {
                        try {
                            ModernMainFrame backend = get();
                            backend.setVisible(true);
                            // On ouvre directement la page de login PROPRIO par défaut
                            backend.changerVue(ModernMainFrame.VUE_LOGIN_PROPRIO); 
                            dispose(); // On ferme la fenêtre Client
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            setCursor(Cursor.getDefaultCursor());
                            btnPro.setText("Espace Pro / Connexion");
                            btnPro.setEnabled(true);
                        }
                    }
                }.execute();
            });
            
            actions.add(btnPro);
            
        } else {
            // CAS 2 : DÉJÀ CONNECTÉ (Retour au Dashboard)
            JButton btnDash = createNavButton("Accéder à mon Espace", true);
            
            btnDash.addActionListener(e -> {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                btnDash.setText("Ouverture...");
                
                new SwingWorker<ModernMainFrame, Void>() {
                    @Override
                    protected ModernMainFrame doInBackground() {
                        ModernMainFrame frame = new ModernMainFrame();
                        frame.setCurrentUser(currentUser); // On repasse l'user
                        
                        // Redirection intelligente
                        if (currentUser instanceof Proprietaire) {
                            frame.changerVue(ModernMainFrame.VUE_DASHBOARD_PROPRIO);
                        } else {
                            frame.changerVue(ModernMainFrame.VUE_REPARATEUR_ATELIER);
                        }
                        return frame;
                    }

                    @Override
                    protected void done() {
                        try {
                            get().setVisible(true);
                            dispose();
                        } catch(Exception ex) { ex.printStackTrace(); }
                    }
                }.execute();
            });
            actions.add(btnDash);
        }

        nav.add(actions, BorderLayout.EAST);
        return nav;
    }

    // ========================================================================================
    // 2. HERO SECTION (CONTENU CLIENT)
    // ========================================================================================
    private JPanel createHeroSection() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(Theme.BACKGROUND);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; 
        gbc.anchor = GridBagConstraints.CENTER;

        // Espace Haut
        gbc.gridy = 0; container.add(Box.createVerticalStrut(80), gbc);

        // Titre
        JLabel title = new JLabel("Réparer vos appareils, sans stress.");
        title.setFont(new Font("Segoe UI", Font.BOLD, 42));
        title.setForeground(Theme.TEXT_HEADLINE);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 15, 0);
        container.add(title, gbc);

        // Sous-titre
        JLabel subtitle = new JLabel("Suivez l'état de votre réparation en temps réel.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitle.setForeground(Theme.TEXT_BODY);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 60, 0);
        container.add(subtitle, gbc);
        
        // Barre de recherche (Suivi Réparation)
        JPanel searchBar = createFloatingSearchBar();
        gbc.gridy++; gbc.insets = new Insets(0, 0, 80, 0);
        container.add(searchBar, gbc);

        // Cartes Catégories
        JPanel grid = new JPanel(new GridLayout(1, 4, 20, 0));
        grid.setOpaque(false);
        grid.add(createSimpleCategoryCard("Smartphone"));
        grid.add(createSimpleCategoryCard("Ordinateur"));
        grid.add(createSimpleCategoryCard("Tablette"));
        grid.add(createSimpleCategoryCard("Console"));
        
        gbc.gridy++; gbc.insets = new Insets(0, 0, 100, 0);
        container.add(grid, gbc);

        return container;
    }

    // ========================================================================================
    // COMPOSANTS UI
    // ========================================================================================

    private JPanel createFloatingSearchBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setPreferredSize(new Dimension(700, 60));
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(226, 232, 240), 1, true),
            new EmptyBorder(5, 20, 5, 5)
        ));

        JTextField field = new JTextField();
        field.setText("Entrez votre Code Client (ex: CL-1789...)");
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setForeground(Color.GRAY);
        field.setBorder(null);
        field.setOpaque(false);
        
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if(field.getText().startsWith("Entrez")) {
                    field.setText("");
                    field.setForeground(Theme.TEXT_HEADLINE);
                }
            }
            public void focusLost(FocusEvent e) {
                if(field.getText().isEmpty()) {
                    field.setText("Entrez votre Code Client (ex: CL-1789...)");
                    field.setForeground(Color.GRAY);
                }
            }
        });

        JButton btnSearch = new JButton("Suivre");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setBackground(Theme.PRIMARY);
        btnSearch.setBorder(new EmptyBorder(0, 30, 0, 30));
        btnSearch.setFocusPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Action Suivi
        btnSearch.addActionListener(e -> {
            String code = field.getText().trim();
            if(code.isEmpty() || code.startsWith("Entrez")) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer un code valide.");
            } else {
                JOptionPane.showMessageDialog(this, "Recherche du dossier : " + code + "\n(Connectez ici votre méthode de recherche)");
            }
        });

        p.add(field, BorderLayout.CENTER);
        p.add(btnSearch, BorderLayout.EAST);
        return p;
    }

    private JPanel createSimpleCategoryCard(String title) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(160, 80));
        
        Border defaultBorder = BorderFactory.createCompoundBorder(
            new LineBorder(new Color(226, 232, 240), 1, true),
            new EmptyBorder(10, 10, 10, 10)
        );
        Border hoverBorder = BorderFactory.createCompoundBorder(
            new LineBorder(Theme.PRIMARY, 2, true),
            new EmptyBorder(9, 9, 9, 9)
        );
        
        card.setBorder(defaultBorder);
        JLabel lTitle = new JLabel(title);
        lTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lTitle.setForeground(Theme.TEXT_HEADLINE);
        card.add(lTitle);

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBorder(hoverBorder);
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
                card.setBackground(new Color(250, 252, 255));
                lTitle.setForeground(Theme.PRIMARY);
            }
            public void mouseExited(MouseEvent e) {
                card.setBorder(defaultBorder);
                card.setBackground(Color.WHITE);
                lTitle.setForeground(Theme.TEXT_HEADLINE);
            }
        });
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
            btn.setBorder(new EmptyBorder(10, 25, 10, 25));
            btn.setContentAreaFilled(false);
            btn.setOpaque(true); 
        } else {
            btn.setBackground(Theme.BACKGROUND);
            btn.setForeground(Theme.TEXT_HEADLINE);
            btn.setBorder(new LineBorder(new Color(203, 213, 225), 1)); 
            btn.setContentAreaFilled(false);
        }
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if(primary) btn.setBackground(Theme.PRIMARY.darker());
                else btn.setBorder(new LineBorder(Theme.PRIMARY, 1));
            }
            public void mouseExited(MouseEvent e) {
                if(primary) btn.setBackground(Theme.PRIMARY);
                else btn.setBorder(new LineBorder(new Color(203, 213, 225), 1));
            }
        });
        return btn;
    }

    // ========================================================================================
    // 🔥 POINT D'ENTRÉE DE L'APPLICATION
    // ========================================================================================
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        // On lance ClientMainFrame en premier !
        SwingUtilities.invokeLater(() -> new ClientMainFrame(null).setVisible(true));
    }
}