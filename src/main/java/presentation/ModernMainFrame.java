package presentation;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import dao.User;
import dao.Proprietaire; // IMPERATIF : Importer la classe fille
import metier.GestionUser;
import metier.IGestionUser;

public class ModernMainFrame extends JFrame {

    // --- GESTION DE L'AFFICHAGE ---
    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    
    // --- REFERENCES VERS LES VUES (Pour pouvoir les rafraichir) ---
    // C'est cette variable qui permet de relancer le chargement des données
    private ViewBoutique viewBoutique; 

    // --- SERVICES METIERS ---
    private IGestionUser metierUser;

    // --- SESSION ---
    private User currentUser;

    // --- CONSTANTES DE NAVIGATION (Evite les fautes de frappe) ---
    public static final String VUE_ACCUEIL = "ACCUEIL";
    public static final String VUE_LOGIN_PROPRIO = "LOGIN_PROPRIO";
    public static final String VUE_REGISTER_PROPRIO = "REGISTER_PROPRIO";
    public static final String VUE_LOGIN_REPARATEUR = "LOGIN_REPARATEUR";
    public static final String VUE_DASHBOARD_PROPRIO = "DASH_PROPRIO";
    public static final String VUE_FORM_BOUTIQUE = "FORM_BOUTIQUE"; // Formulaire ajout
    public static final String VUE_LISTE_BOUTIQUE = "LISTE_BOUTIQUE"; // Liste (Tableau)
    public static final String VUE_DASHBOARD_REPARATEUR = "DASH_REPARATEUR";

    public ModernMainFrame() {
        // 1. Initialisation Métier
        try {
            this.metierUser = new GestionUser(); 
        } catch (Exception e) {
            System.err.println("Info: Démarrage sans couche métier réelle ou erreur DB.");
        }

        // 2. Configuration Fenêtre
        setTitle("AlloFix | Console d'Administration");
        setSize(1280, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BACKGROUND);
        setLayout(new BorderLayout());

        // 3. Navbar
        add(createNavbar(), BorderLayout.NORTH);

        // 4. Contenu Central (CardLayout)
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(Theme.BACKGROUND);

        initViews();

        add(mainContentPanel, BorderLayout.CENTER);
    }

    private void initViews() {
        // --- INSTANCIATION DES VUES ---
        
        // On crée l'instance de ViewBoutique et on la stocke dans la variable de classe
        this.viewBoutique = new ViewBoutique(this);

        // --- AJOUT AU CARDLAYOUT ---
        
        // Pages Publiques
        mainContentPanel.add(createWelcomePanel(), VUE_ACCUEIL);
        mainContentPanel.add(new ViewLogin(this, metierUser, "Propriétaire"), VUE_LOGIN_PROPRIO);
        mainContentPanel.add(new ViewLogin(this, metierUser, "Réparateur"), VUE_LOGIN_REPARATEUR);
        mainContentPanel.add(new ViewRegister(this, metierUser), VUE_REGISTER_PROPRIO);

        // Pages Propriétaire
        mainContentPanel.add(createDashboardProprio(), VUE_DASHBOARD_PROPRIO);
        mainContentPanel.add(new ViewFormBoutique(this), VUE_FORM_BOUTIQUE);
        
        // ICI : On ajoute la vue liste avec la clé "LISTE_BOUTIQUE"
        mainContentPanel.add(this.viewBoutique, VUE_LISTE_BOUTIQUE);

        // Pages Réparateur
        mainContentPanel.add(createDashboardReparateur(), VUE_DASHBOARD_REPARATEUR);
    }

    // ========================================================================================
    //                                  NAVIGATION & SESSION
    // ========================================================================================

    /**
     * Méthode centrale pour changer de page
     */
    public void changerVue(String nomVue) {
        // 1. Afficher la vue demandée
        cardLayout.show(mainContentPanel, nomVue);

        // 2. LOGIQUE DE RAFRAÎCHISSEMENT
        // Si on demande à voir la liste des boutiques, on force le rechargement des données
        if (nomVue.equals(VUE_LISTE_BOUTIQUE)) {
            if (viewBoutique != null) {
                System.out.println("Navigation vers LISTE_BOUTIQUE : Rafraîchissement des données...");
                viewBoutique.refreshTable(); // <--- C'EST LA LIGNE QUI FAIT MARCHER VOTRE TABLEAU
            }
        }
    }

    public void setCurrentUser(User u) {
        this.currentUser = u;
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    /**
     * Méthode utilitaire pour éviter les casts (User -> Proprietaire) partout dans le code
     */
    public Proprietaire getProprietaireConnecte() {
        if (currentUser != null && currentUser instanceof Proprietaire) {
            return (Proprietaire) currentUser;
        }
        return null;
    }

    // ========================================================================================
    //                                  COMPOSANTS UI
    // ========================================================================================

    private JPanel createNavbar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(Color.WHITE);
        nav.setPreferredSize(new Dimension(1000, 70));
        nav.setBorder(BorderFactory.createMatteBorder(0,0,1,0, new Color(230,230,230)));

        JLabel logo = new JLabel("⚡ AlloFix Console");
        logo.setForeground(Theme.PRIMARY);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logo.setBorder(new EmptyBorder(0, 30, 0, 0));
        logo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logo.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { changerVue(VUE_ACCUEIL); }
        });

        JButton btnLogout = new JButton("Déconnexion");
        btnLogout.setForeground(Color.RED);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            setCurrentUser(null); // On vide la session
            changerVue(VUE_ACCUEIL);
        });
        
        JPanel rightNav = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightNav.setOpaque(false);
        rightNav.add(btnLogout);

        nav.add(logo, BorderLayout.WEST);
        nav.add(rightNav, BorderLayout.EAST);
        return nav;
    }

    private JPanel createWelcomePanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Theme.BACKGROUND);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel title = new JLabel("Bienvenue sur AlloFix");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Theme.TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel cardsContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 40));
        cardsContainer.setOpaque(false);

        cardsContainer.add(createRoleCard("Espace Propriétaire", "Gérez vos boutiques et revenus.", "🏢", e -> changerVue(VUE_LOGIN_PROPRIO)));
        cardsContainer.add(createRoleCard("Espace Réparateur", "Accédez aux tickets et planning.", "🛠️", e -> changerVue(VUE_LOGIN_REPARATEUR)));

        content.add(title);
        content.add(Box.createVerticalStrut(20));
        content.add(cardsContainer);
        p.add(content);
        return p;
    }

    private JPanel createRoleCard(String title, String desc, String icon, ActionListener action) {
        JPanel card = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0,0,0,15));
                g2.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 30, 30);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth()-5, getHeight()-5, 30, 30);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(300, 320));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btn = new JButton("Accéder");
        btn.setBackground(Theme.PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(action);

        card.add(lblIcon);
        card.add(Box.createVerticalStrut(15));
        card.add(lblTitle);
        card.add(Box.createVerticalGlue());
        card.add(btn);
        return card;
    }

    // ========================================================================================
    //                         DASHBOARD PROPRIO (MENU PRINCIPAL)
    // ========================================================================================

    private JPanel createDashboardProprio() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Theme.BACKGROUND);

        JPanel centerContent = new JPanel();
        centerContent.setLayout(new BoxLayout(centerContent, BoxLayout.Y_AXIS));
        centerContent.setOpaque(false);

        JLabel icon = new JLabel("🏪");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Gestion des Boutiques");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Bouton 1 : Ajouter
        JButton btnAdd = new JButton(" + Ajouter ma première Boutique ");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnAdd.setBackground(Theme.PRIMARY);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setPreferredSize(new Dimension(350, 65));
        btnAdd.setMaximumSize(new Dimension(350, 65));
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAdd.addActionListener(e -> changerVue(VUE_FORM_BOUTIQUE));

        // Bouton 2 : Voir la liste (C'est celui qui posait problème)
        JButton btnList = new JButton("Voir mes boutiques existantes");
        btnList.setForeground(Theme.PRIMARY);
        btnList.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnList.setContentAreaFilled(false);
        btnList.setBorderPainted(false);
        btnList.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnList.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // ACTION : Aller vers la liste (ce qui déclenche refreshTable)
        btnList.addActionListener(e -> changerVue(VUE_LISTE_BOUTIQUE));

        centerContent.add(icon);
        centerContent.add(Box.createVerticalStrut(20));
        centerContent.add(title);
        centerContent.add(Box.createVerticalStrut(40));
        centerContent.add(btnAdd);
        centerContent.add(Box.createVerticalStrut(15));
        centerContent.add(btnList);

        p.add(centerContent);
        return p;
    }

    private JPanel createDashboardReparateur() {
        return createSimplePage("Espace Technique", "Tickets assignés", Theme.PRIMARY);
    }

    private JPanel createSimplePage(String title, String subtitle, Color color) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Theme.BACKGROUND);
        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lbl.setForeground(color);
        p.add(lbl, BorderLayout.CENTER);
        return p;
    }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(() -> new ModernMainFrame().setVisible(true));
    }
}