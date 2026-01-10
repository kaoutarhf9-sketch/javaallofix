package presentation;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import dao.Boutique;
import dao.User;
import dao.Proprietaire;
import dao.Reparateur;
import metier.GestionUser;
import metier.IGestionUser;

public class ModernMainFrame extends JFrame {

    // --- GESTION DE L'AFFICHAGE ---
    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private JPanel navbarPanel;
    
    // --- REFERENCES VERS LES VUES ---
    private ViewBoutique viewBoutique; 
    private ViewFormReparateur viewFormReparateur;
    private ViewDashboardProprio viewDashboardProprio; 
    private ViewFormBoutique viewFormBoutique; 
    private ViewListReparateur viewListReparateur; 
    private ViewDashboardReparateur viewDashboardReparateur; // Nouvelle vue dédiée

    // --- SERVICES METIERS ---
    private IGestionUser metierUser;

    // --- SESSION ---
    private User currentUser;

    // --- CONSTANTES DE NAVIGATION ---
    public static final String VUE_ACCUEIL = "ACCUEIL";
    public static final String VUE_LOGIN_PROPRIO = "LOGIN_PROPRIO";
    public static final String VUE_REGISTER_PROPRIO = "REGISTER_PROPRIO";
    public static final String VUE_LOGIN_REPARATEUR = "LOGIN_REPARATEUR";
    
    // Espace Propriétaire
    public static final String VUE_DASHBOARD_PROPRIO = "DASH_PROPRIO";
    public static final String VUE_FORM_BOUTIQUE = "FORM_BOUTIQUE";
    public static final String VUE_LISTE_BOUTIQUE = "LISTE_BOUTIQUE";
    public static final String VUE_FORM_REPARATEUR = "FORM_REPARATEUR";
    public static final String VUE_LISTE_REPARATEUR = "LISTE_REPARATEUR";
    
    // Espace Réparateur
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
        setSize(1280, 950);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BACKGROUND);
        setLayout(new BorderLayout());

        // 3. Navbar
        navbarPanel = createNavbar();
        add(navbarPanel, BorderLayout.NORTH);

        // 4. Contenu Central
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(Theme.BACKGROUND);

        initViews();

        add(mainContentPanel, BorderLayout.CENTER);
        updateUIState(); 
    }

    private void initViews() {
        // --- INSTANCIATION DES VUES ---
        this.viewBoutique = new ViewBoutique(this);
        this.viewFormReparateur = new ViewFormReparateur(this);
        this.viewDashboardProprio = new ViewDashboardProprio(this);
        this.viewFormBoutique = new ViewFormBoutique(this);
        this.viewListReparateur = new ViewListReparateur(this);
        this.viewDashboardReparateur = new ViewDashboardReparateur(this); // Init de la classe dédiée

        // --- AJOUT AU CARDLAYOUT ---
        mainContentPanel.add(createWelcomePanel(), VUE_ACCUEIL);
        mainContentPanel.add(new ViewLogin(this, metierUser, "Propriétaire"), VUE_LOGIN_PROPRIO);
        mainContentPanel.add(new ViewLogin(this, metierUser, "Réparateur"), VUE_LOGIN_REPARATEUR);
        mainContentPanel.add(new ViewRegister(this, metierUser), VUE_REGISTER_PROPRIO);

        // Vues Propriétaire
        mainContentPanel.add(this.viewDashboardProprio, VUE_DASHBOARD_PROPRIO);
        mainContentPanel.add(this.viewFormBoutique, VUE_FORM_BOUTIQUE);
        mainContentPanel.add(this.viewBoutique, VUE_LISTE_BOUTIQUE);
        mainContentPanel.add(this.viewListReparateur, VUE_LISTE_REPARATEUR);
        mainContentPanel.add(this.viewFormReparateur, VUE_FORM_REPARATEUR);
        
        // Vue Réparateur (Classe dédiée)
        mainContentPanel.add(this.viewDashboardReparateur, VUE_DASHBOARD_REPARATEUR);
    }

    public void changerVue(String nomVue) {
        cardLayout.show(mainContentPanel, nomVue);

        // Rafraichissement intelligent
        if (nomVue.equals(VUE_LISTE_BOUTIQUE) && viewBoutique != null) viewBoutique.refreshTable();
        
        if (nomVue.equals(VUE_FORM_REPARATEUR) && viewFormReparateur != null) {
            viewFormReparateur.chargerLesBoutiques(); 
            viewFormReparateur.resetFormulaire();
        }

        if (nomVue.equals(VUE_DASHBOARD_PROPRIO) && viewDashboardProprio != null) viewDashboardProprio.updateStats(); 
        
        if (nomVue.equals(VUE_LISTE_REPARATEUR) && viewListReparateur != null) viewListReparateur.refreshTable();

        // Rafraichissement de l'espace réparateur
        if (nomVue.equals(VUE_DASHBOARD_REPARATEUR) && viewDashboardReparateur != null) {
            viewDashboardReparateur.refreshData();
        }
    }

    // --- HELPER MODIFICATION ---
    public void ouvrirModificationBoutique(Boutique b) {
        changerVue(VUE_FORM_BOUTIQUE);
        if (this.viewFormBoutique != null) this.viewFormBoutique.setBoutiqueEnEdition(b);
    }

    public void ouvrirModificationReparateur(Reparateur r) {
        changerVue(VUE_FORM_REPARATEUR);
        if (this.viewFormReparateur != null) this.viewFormReparateur.setReparateurAModifier(r);
    }

    // --- GESTION SESSION ---
    public void setCurrentUser(User u) { 
        this.currentUser = u; 
        updateUIState();
    }

    public User getCurrentUser() { return this.currentUser; }
    
    public Proprietaire getProprietaireConnecte() {
        return (currentUser instanceof Proprietaire) ? (Proprietaire) currentUser : null;
    }

    private void updateUIState() {
        remove(navbarPanel);
        navbarPanel = createNavbar();
        add(navbarPanel, BorderLayout.NORTH);
        revalidate();
        repaint();
    }

    // --- UI COMPONENTS ---
    private JPanel createNavbar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(Color.WHITE);
        nav.setPreferredSize(new Dimension(1000, 80));
        nav.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
            new EmptyBorder(0, 40, 0, 40)
        ));

        JLabel logo = new JLabel("⚡ AlloFix Console");
        logo.setForeground(Theme.GRADIENT_START);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logo.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { 
                if(currentUser != null) {
                    changerVue(currentUser instanceof Proprietaire ? VUE_DASHBOARD_PROPRIO : VUE_DASHBOARD_REPARATEUR);
                } else {
                    changerVue(VUE_ACCUEIL); 
                }
            }
        });

        nav.add(logo, BorderLayout.WEST);

        JPanel rightNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        rightNav.setOpaque(false);

        if (currentUser != null) {
            JLabel lblUser = new JLabel("Bonjour, " + currentUser.getNom());
            lblUser.setFont(Theme.FONT_REGULAR);
            rightNav.add(lblUser);

            JButton btnLogout = new JButton("Déconnexion");
            btnLogout.setForeground(Theme.DANGER);
            btnLogout.setFont(Theme.FONT_BOLD);
            btnLogout.setContentAreaFilled(false);
            btnLogout.setBorderPainted(false);
            btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnLogout.addActionListener(e -> {
                setCurrentUser(null);
                changerVue(VUE_ACCUEIL);
            });
            rightNav.add(btnLogout);
        }

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
        title.setFont(Theme.FONT_HERO);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitle = new JLabel("Choisissez votre espace de connexion");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel cardsContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 40));
        cardsContainer.setOpaque(false);

        cardsContainer.add(createRoleCard("Propriétaire", "Gérez vos boutiques et techniciens", "🏢", e -> changerVue(VUE_LOGIN_PROPRIO)));
        cardsContainer.add(createRoleCard("Réparateur", "Traitez les demandes de réparation", "🛠️", e -> changerVue(VUE_LOGIN_REPARATEUR)));

        content.add(title);
        content.add(Box.createVerticalStrut(10));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(30));
        content.add(cardsContainer);
        p.add(content);
        return p;
    }

    private JPanel createRoleCard(String title, String desc, String icon, ActionListener action) {
        JPanel card = UIFactory.createCard();
        card.setPreferredSize(new Dimension(320, 320));

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = UIFactory.createTitle(title);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblDesc = new JLabel(desc);
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btn = UIFactory.createGradientButton("Accéder");
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(action);

        card.add(Box.createVerticalStrut(30));
        card.add(lblIcon);
        card.add(Box.createVerticalStrut(20));
        card.add(lblTitle);
        card.add(Box.createVerticalStrut(10));
        card.add(lblDesc);
        card.add(Box.createVerticalGlue());
        card.add(btn);
        card.add(Box.createVerticalStrut(30));
        return card;
    }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(() -> new ModernMainFrame().setVisible(true));
    }
}