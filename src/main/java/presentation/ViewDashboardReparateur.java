package presentation;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ViewDashboardReparateur extends JPanel {
    
    private ModernMainFrame frame;
    
    // Plus besoin de ViewCaisse ici
    private JPanel panelNouvelleReparation;
    
    // Référence vers l'historique pour le rafraîchissement
    private ViewListeReparation viewHistoriqueRef; 

    public ViewDashboardReparateur(ModernMainFrame frame, ViewListeReparation viewHistoriqueRef) {
        this.frame = frame;
        this.viewHistoriqueRef = viewHistoriqueRef;

        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        setBorder(new EmptyBorder(10, 20, 10, 20));

        // 1. EN-TÊTE
        JLabel lblTitre = new JLabel("Atelier de Réparation");
        try {
            lblTitre.setFont(Theme.FONT_HERO);
        } catch (Exception e) {
            lblTitre.setFont(new Font("Segoe UI", Font.BOLD, 24));
        }
        
        lblTitre.setForeground(Theme.TEXT_HEADLINE);
        lblTitre.setBorder(new EmptyBorder(0, 0, 15, 0));
        add(lblTitre, BorderLayout.NORTH);

        // 2. CORPS (Directement le panel de réparation, sans onglets)
        this.panelNouvelleReparation = createNouvelleReparationPanel();
        add(panelNouvelleReparation, BorderLayout.CENTER);
    }

    /**
     * Méthode conservée pour compatibilité avec ModernMainFrame.
     * Elle est vide car il n'y a plus de caisse affichée ici.
     */
    public void refreshCaisseInterne() {
        // Ne fait rien, car la caisse n'est plus dans le dashboard.
        // La méthode existe juste pour éviter une erreur dans ModernMainFrame.
    }

    private JPanel createNouvelleReparationPanel() {
        JPanel container = new JPanel(new BorderLayout(15, 0));
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(15, 5, 5, 5));

        // Instanciation des sous-composants
        ClientPanel clientPanel = new ClientPanel();
        
        // On passe 'this.frame' au constructeur
        ReparationPanel reparationPanel = new ReparationPanel(this.frame); 
        
        // Liaison : Quand on valide une réparation, on met à jour la vue historique
        reparationPanel.setHistoriqueVue(this.viewHistoriqueRef);
        
        container.add(clientPanel, BorderLayout.WEST);
        container.add(reparationPanel, BorderLayout.CENTER);
        
        return container;
    }
}