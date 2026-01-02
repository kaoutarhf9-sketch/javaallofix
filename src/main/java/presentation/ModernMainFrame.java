package presentation;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import dao.User;
import metier.GestionUser; // Assurez-vous d'avoir cette classe ou l'interface
import metier.IGestionUser;

public class ModernMainFrame extends JFrame {

    // --- GESTION DE L'AFFICHAGE ---
    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private JPanel navbar; // On garde une réf pour pouvoir la modifier si besoin

    // --- SERVICES METIERS ---
    private IGestionUser metierUser;

    // --- SESSION ---
    private User currentUser;

    // --- CONSTANTES DE NAVIGATION ---
    public static final String VUE_ACCUEIL = "ACCUEIL";
    public static final String VUE_LOGIN_PROPRIO = "LOGIN_PROPRIO";
    public static final String VUE_REGISTER_PROPRIO = "REGISTER_PROPRIO";
    public static final String VUE_LOGIN_REPARATEUR = "LOGIN_REPARATEUR";
    public static final String VUE_DASHBOARD_PROPRIO = "DASH_PROPRIO";
    public static final String VUE_DASHBOARD_REPARATEUR = "DASH_REPARATEUR";

    public ModernMainFrame() {
        // 1. Initialisation Métier (avec Fallback pour éviter les crashs si pas de BDD)
        try {
            this.metierUser = new GestionUser(); 
        } catch (Exception e) {
            System.err.println("Info: Démarrage sans couche métier réelle.");
        }

        // 2. Configuration Fenêtre
        setTitle("AlloFix | Console d'Administration");
        setSize(1280, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BACKGROUND);
        setLayout(new BorderLayout());

        // 3. Navbar (Haut)
        add(createNavbar(), BorderLayout.NORTH);

        // 4. Contenu Central (CardLayout)
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(Theme.BACKGROUND);

        // --- INITIALISATION DES VUES ---
        initViews();

        add(mainContentPanel, BorderLayout.CENTER);
    }

    private void initViews() {
        // Vue 1 : Accueil (Choix du rôle)
        mainContentPanel.add(createWelcomePanel(), VUE_ACCUEIL);

        // Vue 2 & 3 : Login (On passe 'this' pour la navigation)
        mainContentPanel.add(new ViewLogin(this, metierUser, "Propriétaire"), VUE_LOGIN_PROPRIO);
        mainContentPanel.add(new ViewLogin(this, metierUser, "Réparateur"), VUE_LOGIN_REPARATEUR);

        // Vue 4 : Inscription
        mainContentPanel.add(new ViewRegister(this, metierUser), VUE_REGISTER_PROPRIO);

        // Vue 5 & 6 : Dashboards (Placeholders pour l'instant)
        mainContentPanel.add(createDashboardProprio(), VUE_DASHBOARD_PROPRIO);
        mainContentPanel.add(createDashboardReparateur(), VUE_DASHBOARD_REPARATEUR);
    }

    // ========================================================================================
    //                                  NAVIGATION & SESSION
    // ========================================================================================

    public void changerVue(String nomVue) {
        cardLayout.show(mainContentPanel, nomVue);
    }

    public void setCurrentUser(User u) {
        this.currentUser = u;
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    // ========================================================================================
    //                                  COMPOSANTS UI
    // ========================================================================================

    /** Création de la barre de navigation supérieure */
    private JPanel createNavbar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(Color.WHITE); // Blanc pour faire pro
        nav.setPreferredSize(new Dimension(1000, 70));
        nav.setBorder(BorderFactory.createMatteBorder(0,0,1,0, new Color(230,230,230))); // Bordure bas

        // Logo
        JLabel logo = new JLabel("⚡ AlloFix Console");
        logo.setForeground(Theme.PRIMARY);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logo.setBorder(new EmptyBorder(0, 30, 0, 0));
        logo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Retour accueil au clic sur logo
        logo.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { changerVue(VUE_ACCUEIL); }
        });

        // Version Badge
        JLabel badge = new JLabel("v1.0 Enterprise  ");
        badge.setForeground(Theme.TEXT_LIGHT);
        badge.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        badge.setBorder(new EmptyBorder(0, 0, 0, 30));

        nav.add(logo, BorderLayout.WEST);
        nav.add(badge, BorderLayout.EAST);
        return nav;
    }

    /** Création de la page d'accueil avec les 2 cartes de choix */
    private JPanel createWelcomePanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Theme.BACKGROUND);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // Textes
        JLabel title = new JLabel("Bienvenue sur AlloFix");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Theme.TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Sélectionnez votre espace de travail pour continuer");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        sub.setForeground(Theme.TEXT_GRAY);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Conteneur des cartes (FlowLayout pour les mettre côte à côte)
        JPanel cardsContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 40));
        cardsContainer.setOpaque(false);

        // Carte 1 : Propriétaire
        cardsContainer.add(createRoleCard(
            "Espace Propriétaire", 
            "Gérez vos boutiques, vos réparateurs et suivez vos revenus.", 
            "🏢", 
            e -> changerVue(VUE_LOGIN_PROPRIO)
        ));

        // Carte 2 : Réparateur
        cardsContainer.add(createRoleCard(
            "Espace Réparateur", 
            "Accédez aux tickets, mettez à jour les status et consultez votre planning.", 
            "🛠️", 
            e -> changerVue(VUE_LOGIN_REPARATEUR)
        ));

        content.add(title);
        content.add(Box.createVerticalStrut(10));
        content.add(sub);
        content.add(Box.createVerticalStrut(20));
        content.add(cardsContainer);

        p.add(content);
        return p;
    }

    /** Crée une carte de choix de rôle (Design "ServiceCard") */
    private JPanel createRoleCard(String title, String desc, String icon, ActionListener action) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre
                g2.setColor(new Color(0,0,0,15));
                g2.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 30, 30);
                
                // Fond Blanc
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth()-5, getHeight()-5, 30, 30);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(300, 320));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Icone
        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Titre
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Theme.TEXT_DARK);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Description
        JTextArea txtDesc = new JTextArea(desc);
        txtDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDesc.setForeground(Theme.TEXT_GRAY);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setLineWrap(true);
        txtDesc.setEditable(false);
        txtDesc.setOpaque(false);
        txtDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtDesc.setBorder(new EmptyBorder(15, 0, 15, 0));

        // Bouton
        JButton btn = new JButton("Accéder");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(Theme.PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(action);

        // Interaction Clic sur toute la carte
        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { action.actionPerformed(null); }
            public void mouseEntered(MouseEvent e) { card.repaint(); } // Pourrait ajouter un effet hover ici
        });

        card.add(lblIcon);
        card.add(Box.createVerticalStrut(15));
        card.add(lblTitle);
        card.add(txtDesc);
        card.add(Box.createVerticalGlue()); // Pousse le bouton vers le bas
        card.add(btn);

        return card;
    }

    // ========================================================================================
    //                              DASHBOARDS (PLACEHOLDERS)
    // ========================================================================================

    private JPanel createDashboardProprio() {
        return createSimplePage("Tableau de Bord Propriétaire", "Gérez vos boutiques ici.", Theme.NAVY);
    }

    private JPanel createDashboardReparateur() {
        return createSimplePage("Espace Technique", "Vos tickets de réparation.", Theme.PRIMARY);
    }

    private JPanel createSimplePage(String title, String subtitle, Color color) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Theme.BACKGROUND);

        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lbl.setForeground(color);
        lbl.setBorder(new EmptyBorder(50, 0, 10, 0));

        JLabel sub = new JLabel(subtitle, SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        sub.setForeground(Theme.TEXT_GRAY);

        JButton logout = new JButton("Se Déconnecter");
        logout.setBackground(Theme.TEXT_LIGHT);
        logout.setForeground(Color.WHITE);
        logout.addActionListener(e -> {
            currentUser = null;
            changerVue(VUE_ACCUEIL);
        });

        JPanel center = new JPanel(); center.setOpaque(false); center.add(logout);

        p.add(lbl, BorderLayout.NORTH);
        p.add(sub, BorderLayout.CENTER);
        p.add(center, BorderLayout.SOUTH);
        return p;
    }

    public static void main(String[] args) {
        // Activation du lissage de texte
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(() -> new ModernMainFrame().setVisible(true));
    }
}