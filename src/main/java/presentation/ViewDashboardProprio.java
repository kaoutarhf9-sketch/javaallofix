package presentation;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import dao.Boutique;
import dao.Proprietaire;
import dao.Reparateur;
import metier.GestionBoutique;
import metier.GestionReparateur;
import metier.IGestionReparateur;

public class ViewDashboardProprio extends JPanel {

    private ModernMainFrame frame;
    
    // --- COUCHES MÉTIER ---
    private GestionBoutique metierBoutique;
    private IGestionReparateur metierReparateur;

    // --- COMPOSANTS UI ---
    private JLabel lblWelcome;
    private JTabbedPane tabbedPane;
    
    // Composants de l'onglet "Vue d'ensemble"
    private JLabel lblCountBoutiques;
    private JLabel lblCountReparateurs;
    
    // Les panneaux des autres onglets (Vues intégrées)
    private ViewBoutique viewBoutiquePanel;
    private ViewListReparateur viewReparateurPanel;
    private ViewProfile viewProfilePanel; // Le nouveau panel Profil

    public ViewDashboardProprio(ModernMainFrame frame) {
        this.frame = frame;
        
        // 1. Init Métier
        try {
            this.metierBoutique = new GestionBoutique();
            this.metierReparateur = new GestionReparateur();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. Configuration du Panel Principal
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND); // Gris clair
        setBorder(new EmptyBorder(20, 30, 20, 30)); // Marges externes

        // 3. Ajout de l'En-tête (Bonjour + Logout)
        add(createHeader(), BorderLayout.NORTH);

        // 4. Ajout des Onglets (Le cœur du dashboard)
        add(createTabs(), BorderLayout.CENTER);
        
        // 5. Chargement initial des stats
        updateStats();
    }

    // =========================================================================
    // 1. HEADER (Haut de page)
    // =========================================================================
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Message de bienvenue
        lblWelcome = new JLabel("Tableau de Bord");
        lblWelcome.setFont(Theme.FONT_HERO);
        lblWelcome.setForeground(Theme.TEXT_HEADLINE);

        header.add(lblWelcome, BorderLayout.WEST);
        
        // Bouton Déconnexion
        JButton btnLogout = UIFactory.createOutlineButton("Se déconnecter");
        btnLogout.addActionListener(e -> {
            int choix = JOptionPane.showConfirmDialog(this, "Voulez-vous vous déconnecter ?", "Déconnexion", JOptionPane.YES_NO_OPTION);
            if(choix == JOptionPane.YES_OPTION) {
                frame.changerVue(ModernMainFrame.VUE_LOGIN_PROPRIO);
            }
        });
        header.add(btnLogout, BorderLayout.EAST);

        return header;
    }

    // =========================================================================
    // 2. GESTION DES ONGLETS
    // =========================================================================
    private JTabbedPane createTabs() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setFocusable(false);
        
        // Application du style moderne (voir classe interne tout en bas)
        tabbedPane.setUI(new ModernTabUI());

        // --- ONGLET 1 : STATS (Vue d'ensemble) ---
        JPanel panelStats = createStatsPanel();
        tabbedPane.addTab("📊 Vue d'ensemble", panelStats);

        // --- ONGLET 2 : BOUTIQUES ---
        viewBoutiquePanel = new ViewBoutique(frame);
        // On enlève les grosses marges car on est déjà dans un onglet avec marges
        viewBoutiquePanel.setBorder(new EmptyBorder(10, 0, 0, 0)); 
        tabbedPane.addTab("🏪 Mes Boutiques", viewBoutiquePanel);

        // --- ONGLET 3 : ÉQUIPE ---
        viewReparateurPanel = new ViewListReparateur(frame);
        viewReparateurPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        tabbedPane.addTab("👨‍🔧 Mon Équipe", viewReparateurPanel);

        // --- ONGLET 4 : PROFIL (NOUVEAU) ---
        viewProfilePanel = new ViewProfile(frame);
        viewProfilePanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        tabbedPane.addTab("👤 Mon Profil", viewProfilePanel);

        // --- LOGIQUE DE RAFRAÎCHISSEMENT ---
        // Quand on clique sur un onglet, on recharge ses données spécifiques
        tabbedPane.addChangeListener(e -> {
            Component selected = tabbedPane.getSelectedComponent();
            
            if (selected == viewBoutiquePanel) {
                viewBoutiquePanel.refreshTable();
            } else if (selected == viewReparateurPanel) {
                viewReparateurPanel.refreshTable();
            } else if (selected == viewProfilePanel) {
                viewProfilePanel.chargerDonnees(); // Recharge les infos utilisateur
            } else {
                updateStats(); // Recharge les compteurs de l'accueil
            }
        });

        return tabbedPane;
    }

    // =========================================================================
    // 3. CONTENU DE L'ONGLET STATISTIQUES
    // =========================================================================
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(30, 20, 30, 20));

        // Grille de cartes
        JPanel grid = new JPanel(new GridLayout(1, 2, 40, 0));
        grid.setOpaque(false);

        lblCountBoutiques = new JLabel("-");
        lblCountReparateurs = new JLabel("-");

        // Carte 1 : Boutiques
        grid.add(buildStatCard("🏪", "Boutiques Actives", lblCountBoutiques));
        
        // Carte 2 : Réparateurs
        grid.add(buildStatCard("👨‍🔧", "Total Réparateurs", lblCountReparateurs));

        // Container pour coller les cartes en haut
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.add(grid, BorderLayout.NORTH);
        
        panel.add(container, BorderLayout.CENTER);
        return panel;
    }

    // Helper pour créer une jolie carte blanche
    private JPanel buildStatCard(String icon, String title, JLabel count) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0; gbc.gridy=0; gbc.insets = new Insets(5,5,5,5);

        JLabel lIcon = new JLabel(icon);
        lIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));
        card.add(lIcon, gbc);

        gbc.gridy++;
        JLabel lTitle = new JLabel(title);
        lTitle.setFont(Theme.FONT_TITLE);
        lTitle.setForeground(Theme.TEXT_BODY);
        card.add(lTitle, gbc);

        gbc.gridy++;
        count.setFont(new Font("Segoe UI", Font.BOLD, 48));
        count.setForeground(Theme.PRIMARY);
        card.add(count, gbc);

        return card;
    }

    // =========================================================================
    // 4. LOGIQUE DE MISE À JOUR (GLOBAL)
    // =========================================================================
    public void updateStats() {
        if (frame.getCurrentUser() == null || !(frame.getCurrentUser() instanceof Proprietaire)) return;
        
        Proprietaire p = (Proprietaire) frame.getCurrentUser();
        lblWelcome.setText("Bonjour, " + p.getNom() + " 👋");

        // Utilisation de threads pour ne pas figer l'interface si la BDD est lente
        new Thread(() -> {
            if (metierBoutique != null) {
                List<Boutique> list = metierBoutique.listerBoutiquesDuProprietaire(p.getIdU());
                int nb = (list != null) ? list.size() : 0;
                SwingUtilities.invokeLater(() -> lblCountBoutiques.setText(String.valueOf(nb)));
            }
        }).start();

        new Thread(() -> {
            if (metierReparateur != null) {
                List<Reparateur> list = metierReparateur.listerReparateursParProprietaire(p.getIdU());
                int nb = (list != null) ? list.size() : 0;
                SwingUtilities.invokeLater(() -> lblCountReparateurs.setText(String.valueOf(nb)));
            }
        }).start();
    }

    // =========================================================================
    // 5. CLASSE INTERNE : STYLE DES ONGLETS (MODERN UI)
    // =========================================================================
    private class ModernTabUI extends BasicTabbedPaneUI {
        @Override
        protected void installDefaults() {
            super.installDefaults();
            shadow = Theme.BACKGROUND;
            darkShadow = Theme.BACKGROUND;
            lightHighlight = Theme.BACKGROUND;
            contentBorderInsets = new Insets(0,0,0,0); // Pas de bordure autour du contenu
        }

        @Override
        protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
            Graphics2D g2 = (Graphics2D) g;
            if (isSelected) {
                g2.setColor(Theme.PRIMARY); // Couleur active (Violet)
            } else {
                g2.setColor(Color.WHITE);   // Couleur inactive
            }
            g2.fillRect(x, y, w, h);
        }

        @Override
        protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            // Pas de bordure moche autour du panel
        }
        
        @Override
        protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex, String title, Rectangle textRect, boolean isSelected) {
            if (isSelected) {
                g.setColor(Color.WHITE); 
            } else {
                g.setColor(Theme.TEXT_BODY); 
            }
            g.setFont(font);
            g.drawString(title, textRect.x, textRect.y + metrics.getAscent());
        }
    }
}