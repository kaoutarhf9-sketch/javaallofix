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
    private JPanel sidebarPanel;
    
    // --- VUES PROPRIÉTAIRE ---
    private ViewBoutique viewBoutique; 
    private ViewFormReparateur viewFormReparateur;
    private ViewDashboardProprio viewDashboardProprio; 
    private ViewFormBoutique viewFormBoutique; 
    private ViewListReparateur viewListReparateur; 
    
    // --- VUES RÉPARATEUR ---
    private ViewDashboardReparateur viewDashboardReparateur; // L'Atelier (Saisie)
    
    // 🔥 MODIFICATION : ON SÉPARE EN DEUX LISTES
    private ViewListeReparation viewListeActive;     // En cours / En attente / Terminée
    private ViewListeReparation viewListeHistorique; // Livrée (Archivée)
    
    private ViewRecette viewRecette;                 // Les Recettes

    // --- VUE COMMUNE ---
    private ViewProfile viewProfile; 

    private IGestionUser metierUser;
    private User currentUser;

    // --- CONSTANTES DE NAVIGATION ---
    public static final String VUE_ACCUEIL = "ACCUEIL";
    
    // Login / Register
    public static final String VUE_LOGIN_PROPRIO = "LOGIN_PROPRIO";
    public static final String VUE_REGISTER_PROPRIO = "REGISTER_PROPRIO";
    public static final String VUE_LOGIN_REPARATEUR = "LOGIN_REPARATEUR";
    
    // Espace Propriétaire
    public static final String VUE_DASHBOARD_PROPRIO = "DASH_PROPRIO";
    public static final String VUE_LISTE_BOUTIQUE = "LISTE_BOUTIQUE";
    public static final String VUE_LISTE_REPARATEUR = "LISTE_REPARATEUR";
    public static final String VUE_FORM_BOUTIQUE = "FORM_BOUTIQUE";
    public static final String VUE_FORM_REPARATEUR = "FORM_REPARATEUR";
    
    // Espace Réparateur
    public static final String VUE_REPARATEUR_ATELIER = "REP_ATELIER";
    public static final String VUE_REPARATEUR_LISTE_ACTIVE = "REP_ACTIVE"; // 🔥 NOUVELLE CLEF
    public static final String VUE_REPARATEUR_HISTORIQUE = "REP_HISTORIQUE";
    public static final String VUE_REPARATEUR_RECETTE = "REP_RECETTE";
    
    // Commun
    public static final String VUE_PROFIL = "VUE_PROFIL"; 

    public ModernMainFrame() {
        // 1. Init Métier
        try { metierUser = new GestionUser(); } catch (Exception e) {}

        // 2. Config Fenêtre
        setTitle("AlloFix | Manager");
        setSize(1280, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BACKGROUND);
        setLayout(new BorderLayout());

        // 3. Sidebar Initiale
        sidebarPanel = createSidebar();
        add(sidebarPanel, BorderLayout.WEST);

        // 4. Contenu Central
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(Theme.BACKGROUND);
        mainContentPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); 

        initViews();

        add(mainContentPanel, BorderLayout.CENTER);
        updateUIState(); 
    }
    
    private void initViews() {
        // --- INSTANCIATION DES VUES ---
        
        // Vues Propriétaire
        this.viewBoutique = new ViewBoutique(this);
        this.viewFormReparateur = new ViewFormReparateur(this);
        this.viewDashboardProprio = new ViewDashboardProprio(this);
        this.viewFormBoutique = new ViewFormBoutique(this);
        this.viewListReparateur = new ViewListReparateur(this);
        
        // Vues Réparateur
        // 🔥 ON INSTANCIE LES DEUX TYPES DE LISTES
        // false = En cours (Tout sauf Livrée)
        this.viewListeActive = new ViewListeReparation(this, false);
        // true = Historique (Seulement Livrée)
        this.viewListeHistorique = new ViewListeReparation(this, true);
        
        // L'atelier a besoin de la liste active pour rafraîchir après ajout
        this.viewDashboardReparateur = new ViewDashboardReparateur(this, this.viewListeActive);
        
        this.viewRecette = new ViewRecette(this);
        
        // Vue Commune
        this.viewProfile = new ViewProfile(this); 

        // --- AJOUT AU CARD LAYOUT ---
        
        // Accueil & Auth
        mainContentPanel.add(createWelcomePanel(), VUE_ACCUEIL);
        mainContentPanel.add(new ViewLogin(this, metierUser, "Propriétaire"), VUE_LOGIN_PROPRIO);
        mainContentPanel.add(new ViewLogin(this, metierUser, "Réparateur"), VUE_LOGIN_REPARATEUR);
        mainContentPanel.add(new ViewRegister(this, metierUser), VUE_REGISTER_PROPRIO);

        // Navigation Propriétaire
        mainContentPanel.add(this.viewDashboardProprio, VUE_DASHBOARD_PROPRIO);
        mainContentPanel.add(this.viewBoutique, VUE_LISTE_BOUTIQUE);
        mainContentPanel.add(this.viewListReparateur, VUE_LISTE_REPARATEUR);
        mainContentPanel.add(this.viewFormBoutique, VUE_FORM_BOUTIQUE);
        mainContentPanel.add(this.viewFormReparateur, VUE_FORM_REPARATEUR);
        
        // Navigation Réparateur
        mainContentPanel.add(this.viewDashboardReparateur, VUE_REPARATEUR_ATELIER);
        // 🔥 AJOUT DES DEUX VUES AU CARD LAYOUT
        mainContentPanel.add(this.viewListeActive, VUE_REPARATEUR_LISTE_ACTIVE);
        mainContentPanel.add(this.viewListeHistorique, VUE_REPARATEUR_HISTORIQUE);
        
        mainContentPanel.add(this.viewRecette, VUE_REPARATEUR_RECETTE);
        
        // Commun
        mainContentPanel.add(this.viewProfile, VUE_PROFIL);
    }
    
    // --- NAVIGATION ---
    public void changerVue(String nomVue) {
        cardLayout.show(mainContentPanel, nomVue);

        // Refresh PROPRIO
        if (nomVue.equals(VUE_LISTE_BOUTIQUE) && viewBoutique != null) viewBoutique.refreshTable();
        if (nomVue.equals(VUE_FORM_REPARATEUR) && viewFormReparateur != null) {
            viewFormReparateur.chargerLesBoutiques(); 
            viewFormReparateur.resetFormulaire();
        }
        if (nomVue.equals(VUE_DASHBOARD_PROPRIO) && viewDashboardProprio != null) viewDashboardProprio.updateStats(); 
        if (nomVue.equals(VUE_LISTE_REPARATEUR) && viewListReparateur != null) viewListReparateur.refreshTable();
        
        // Refresh REPARATEUR
        // 🔥 REFRESH SÉPARÉ POUR CHAQUE LISTE
        if (nomVue.equals(VUE_REPARATEUR_LISTE_ACTIVE) && viewListeActive != null) viewListeActive.refreshTable();
        if (nomVue.equals(VUE_REPARATEUR_HISTORIQUE) && viewListeHistorique != null) viewListeHistorique.refreshTable();
        
        if (nomVue.equals(VUE_REPARATEUR_RECETTE) && viewRecette != null) viewRecette.refresh();
        
        // Refresh COMMUN
        if (nomVue.equals(VUE_PROFIL) && viewProfile != null) viewProfile.chargerDonnees();
    }
    
    // --- GESTION DE SESSION ---
    public void setCurrentUser(User u) { 
        this.currentUser = u; 
        updateUIState();
    }
    
    public User getCurrentUser() { return this.currentUser; }
    
    public Proprietaire getProprietaireConnecte() { 
        return (currentUser instanceof Proprietaire) ? (Proprietaire) currentUser : null; 
    }
    
    private void updateUIState() {
        remove(sidebarPanel);
        sidebarPanel = createSidebar();
        add(sidebarPanel, BorderLayout.WEST);
        revalidate();
        repaint();
    }
    
    // ====================================================================================
    // 🔥 SIDEBAR INTELLIGENTE
    // ====================================================================================
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Theme.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBorder(new EmptyBorder(40, 30, 40, 30));

        // 1. LOGO
        JLabel textLogo = new JLabel("AlloFix");
        textLogo.setForeground(Color.WHITE);
        textLogo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        textLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(textLogo);
        sidebar.add(Box.createVerticalStrut(60));

        // 2. MENU
        if (currentUser != null) {
            
            // Badge User
            String role = (currentUser instanceof Proprietaire) ? "Propriétaire" : "Réparateur";
            JLabel lblUser = new JLabel(currentUser.getNom());
            lblUser.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lblUser.setForeground(Color.WHITE);
            lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            JLabel lblRole = new JLabel(role.toUpperCase());
            lblRole.setFont(new Font("Segoe UI", Font.BOLD, 10));
            lblRole.setForeground(new Color(148, 163, 184));
            lblRole.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            sidebar.add(lblUser);
            sidebar.add(lblRole);
            sidebar.add(Box.createVerticalStrut(40));

            // --- MENUS ---
            if (currentUser instanceof Proprietaire) {
                Proprietaire p = (Proprietaire) currentUser;

                sidebar.add(createSidebarButton("Tableau de bord", () -> changerVue(VUE_DASHBOARD_PROPRIO)));
                sidebar.add(Box.createVerticalStrut(5));
                sidebar.add(createSidebarButton("Mes Boutiques", () -> changerVue(VUE_LISTE_BOUTIQUE)));
                sidebar.add(Box.createVerticalStrut(5));
                sidebar.add(createSidebarButton("Mon Équipe", () -> changerVue(VUE_LISTE_REPARATEUR)));
                sidebar.add(Box.createVerticalStrut(20));

                // 🔥 MENU ATELIER (Si Activé)
                if (p.isEstReparateur()) {
                    JLabel lblAtelier = new JLabel("MON ATELIER");
                    lblAtelier.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    lblAtelier.setForeground(new Color(148, 163, 184));
                    lblAtelier.setAlignmentX(Component.LEFT_ALIGNMENT);
                    sidebar.add(lblAtelier);
                    sidebar.add(Box.createVerticalStrut(10));

                    sidebar.add(createSidebarButton("Nouvelle Réparation", () -> changerVue(VUE_REPARATEUR_ATELIER)));
                    sidebar.add(Box.createVerticalStrut(5));
                    
                    // 🔥 NOUVEL ONGLET AJOUTÉ ICI
                    sidebar.add(createSidebarButton("En Cours / À Traiter", () -> changerVue(VUE_REPARATEUR_LISTE_ACTIVE)));
                    sidebar.add(Box.createVerticalStrut(5));
                    
                    sidebar.add(createSidebarButton("Historique (Livrées)", () -> changerVue(VUE_REPARATEUR_HISTORIQUE)));
                    sidebar.add(Box.createVerticalStrut(5));
                    sidebar.add(createSidebarButton("Mes Recettes", () -> changerVue(VUE_REPARATEUR_RECETTE)));
                    sidebar.add(Box.createVerticalStrut(20));
                }
                
                sidebar.add(createSidebarButton("Mon Profil", () -> changerVue(VUE_PROFIL)));
            } 
            else if (currentUser instanceof Reparateur) {
                // ✅ MENU RÉPARATEUR STANDARD
                sidebar.add(createSidebarButton("Nouvelle Réparation", () -> changerVue(VUE_REPARATEUR_ATELIER)));
                sidebar.add(Box.createVerticalStrut(5));
                
                // 🔥 NOUVEL ONGLET AJOUTÉ ICI
                sidebar.add(createSidebarButton("En Cours / À Traiter", () -> changerVue(VUE_REPARATEUR_LISTE_ACTIVE)));
                sidebar.add(Box.createVerticalStrut(5));
                
                sidebar.add(createSidebarButton("Historique (Livrées)", () -> changerVue(VUE_REPARATEUR_HISTORIQUE)));
                sidebar.add(Box.createVerticalStrut(5));
                
                sidebar.add(createSidebarButton("Mes Recettes", () -> changerVue(VUE_REPARATEUR_RECETTE)));
                
                sidebar.add(Box.createVerticalStrut(20));
                sidebar.add(createSidebarButton("Mon Profil", () -> changerVue(VUE_PROFIL)));
            }
            
            sidebar.add(Box.createVerticalGlue());
            JButton btnLogout = createSidebarButton("Se déconnecter", () -> {
                setCurrentUser(null);
                changerVue(VUE_ACCUEIL);
            });
            btnLogout.setForeground(new Color(239, 68, 68));
            sidebar.add(btnLogout);
            
        } else {
            JLabel info = new JLabel("<html><div style='width:180px; color:#94a3b8'>Connectez-vous pour accéder à vos outils.</div></html>");
            sidebar.add(info);
            sidebar.add(Box.createVerticalGlue());
        }
        return sidebar;
    }

    private JButton createSidebarButton(String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btn.setForeground(Theme.TEXT_SIDEBAR);
        btn.setBackground(Theme.SIDEBAR_BG);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(300, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(Color.WHITE);
                btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
            }
            public void mouseExited(MouseEvent e) {
                if(!text.contains("déconnecter")) {
                    btn.setForeground(Theme.TEXT_SIDEBAR);
                    btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                }
            }
        });
        
        btn.addActionListener(e -> action.run());
        return btn;
    }
    
    // --- ACCUEIL ---
    private JPanel createWelcomePanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Theme.BACKGROUND);
        
        JPanel content = new JPanel(); 
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS)); 
        content.setOpaque(false);
        
        JLabel title = new JLabel("Bienvenue sur AlloFix"); 
        title.setFont(new Font("Segoe UI", Font.BOLD, 36)); 
        title.setForeground(Theme.TEXT_HEADLINE); 
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitle = new JLabel("Gérez vos réparations en toute simplicité");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(Theme.TEXT_BODY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel cardsContainer = new JPanel(new GridLayout(1, 2, 30, 0)); 
        cardsContainer.setOpaque(false);
        cardsContainer.setBorder(new EmptyBorder(40, 0, 0, 0));

        cardsContainer.add(createRoleCard("Propriétaire", "Gérez vos boutiques et C.A.", new Color(59, 130, 246), e -> changerVue(VUE_LOGIN_PROPRIO)));
        cardsContainer.add(createRoleCard("Réparateur", "Suivez vos tickets et travaux", new Color(16, 185, 129), e -> changerVue(VUE_LOGIN_REPARATEUR)));

        content.add(title);
        content.add(Box.createVerticalStrut(10));
        content.add(subtitle);
        content.add(cardsContainer);
        
        p.add(content);
        return p;
    }
    
    private JPanel createRoleCard(String title, String desc, Color accentColor, ActionListener action) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(280, 200));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, accentColor),
                new EmptyBorder(30, 30, 30, 30)
            )
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Theme.TEXT_HEADLINE);
        
        JLabel lblDesc = new JLabel("<html><body style='width:180px'>"+desc+"</body></html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDesc.setForeground(Theme.TEXT_BODY);

        JButton btn = new JButton("Connexion →");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(accentColor);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addActionListener(action);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(10));
        card.add(lblDesc);
        card.add(Box.createVerticalGlue());
        card.add(btn);

        return card;
    }
    
    public void ouvrirModificationBoutique(Boutique b) { changerVue(VUE_FORM_BOUTIQUE); if (this.viewFormBoutique != null) this.viewFormBoutique.setBoutiqueEnEdition(b); }
    public void ouvrirModificationReparateur(Reparateur r) { changerVue(VUE_FORM_REPARATEUR); if (this.viewFormReparateur != null) this.viewFormReparateur.setReparateurAModifier(r); }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(() -> new ModernMainFrame().setVisible(true));
    }
}