package presentation;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class ViewDashboardReparateur extends JPanel {
    private ModernMainFrame frame;
    
    // --- VUES INTÉGRÉES ---
    private ViewRecette viewRecette;
    private ViewListeReparation viewListeReparation;
    private JPanel panelNouvelleReparation;

    public ViewDashboardReparateur(ModernMainFrame frame) {
        this.frame = frame;
        
        // Initialisation des sous-vues
        this.viewRecette = new ViewRecette(frame);
        this.viewListeReparation = new ViewListeReparation(); 
        
        // Création de la vue combinée "Nouvelle Réparation" (Client à gauche + Formulaire à droite)
        this.panelNouvelleReparation = createNouvelleReparationPanel();

        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        setBorder(new EmptyBorder(10, 20, 10, 20)); // Marges externes

        initLayout();
    }

    private void initLayout() {
        // En-tête simple
        JLabel lblTitre = new JLabel("Espace Atelier");
        lblTitre.setFont(Theme.FONT_HERO);
        lblTitre.setForeground(Theme.TEXT_HEADLINE);
        lblTitre.setBorder(new EmptyBorder(0, 0, 15, 0));
        add(lblTitre, BorderLayout.NORTH);

        // --- ONGLETS ---
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.setBackground(Color.WHITE);
        tabs.setFocusable(false);
        
        // Onglet 1 : Créer une réparation (Le cœur du métier)
        tabs.addTab("🛠️ Nouvelle Réparation", panelNouvelleReparation);
        
        // Onglet 2 : Historique et Suivi
        tabs.addTab("📋 Historique", viewListeReparation);
        
        // Onglet 3 : Argent
        tabs.addTab("💰 Mes Recettes", viewRecette);

        // Ajout d'un écouteur pour rafraîchir les données quand on change d'onglet
        tabs.addChangeListener(e -> {
            Component selected = tabs.getSelectedComponent();
            if (selected == viewRecette) {
                viewRecette.refresh();
            } else if (selected == viewListeReparation) {
                // viewListeReparation.refresh(); // Assurez-vous d'avoir une méthode refresh() dans cette classe
            }
        });

        add(tabs, BorderLayout.CENTER);
    }

    // --- CONSTRUCTION DU PANEL "NOUVELLE RÉPARATION" ---
    // C'est ici qu'on combine ClientPanel (Gauche) et ReparationPanel (Centre)
    private JPanel createNouvelleReparationPanel() {
        JPanel container = new JPanel(new BorderLayout(15, 0));
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(15, 0, 0, 0));

        // On instancie vos panels existants
        // Note: Idéalement, ReparationPanel devrait prendre une référence vers viewListeReparation
        // pour pouvoir la rafraîchir après un ajout, sans passer par du static.
        ClientPanel clientPanel = new ClientPanel();
        
        // Nous allons passer viewListeReparation au constructeur de ReparationPanel (voir étape 2)
        // Si vous ne voulez pas modifier ReparationPanel, utilisez le constructeur par défaut.
        ReparationPanel reparationPanel = new ReparationPanel(); 
        
        // Petite astuce : on peut lier les deux panels ici si nécessaire
        // Exemple : quand on sélectionne un client, reparationPanel se met à jour.
        
        container.add(clientPanel, BorderLayout.WEST);
        container.add(reparationPanel, BorderLayout.CENTER);
        
        return container;
    }

    public void refreshData() {
        viewRecette.refresh();
        // viewListeReparation.refresh();
    }
}