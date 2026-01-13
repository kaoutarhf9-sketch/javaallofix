package presentation;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ViewDashboardReparateur extends JPanel {
    
    private ModernMainFrame frame;
    
    // On garde juste les panels nécessaires pour une nouvelle réparation
    private JPanel panelNouvelleReparation;
    
    // Référence vers l'historique pour le rafraîchissement après enregistrement
    private ViewListeReparation viewHistoriqueRef; 

    public ViewDashboardReparateur(ModernMainFrame frame, ViewListeReparation viewHistoriqueRef) {
        this.frame = frame;
        this.viewHistoriqueRef = viewHistoriqueRef;

        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        setBorder(new EmptyBorder(10, 20, 10, 20));

        // 1. EN-TÊTE
        JLabel lblTitre = new JLabel("Atelier de Réparation");
        // Utilisation de la police du thème, ou fallback si Theme.FONT_HERO n'existe pas
        try {
            lblTitre.setFont(Theme.FONT_HERO);
        } catch (Exception e) {
            lblTitre.setFont(new Font("Segoe UI", Font.BOLD, 24));
        }
        
        lblTitre.setForeground(Theme.TEXT_HEADLINE);
        lblTitre.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(lblTitre, BorderLayout.NORTH);

        // 2. CORPS : Le Split Panel (Client + Panne)
        this.panelNouvelleReparation = createNouvelleReparationPanel();
        add(panelNouvelleReparation, BorderLayout.CENTER);
    }

    private JPanel createNouvelleReparationPanel() {
        JPanel container = new JPanel(new BorderLayout(15, 0));
        container.setOpaque(false);

        // Instanciation des sous-composants
        ClientPanel clientPanel = new ClientPanel();
        
        // 🔥 CORRECTION ICI : On passe 'this.frame' au constructeur
        ReparationPanel reparationPanel = new ReparationPanel(this.frame); 
        
        // Liaison : Quand on valide une réparation, on met à jour la vue historique en cache
        reparationPanel.setHistoriqueVue(this.viewHistoriqueRef);
        
        container.add(clientPanel, BorderLayout.WEST);
        container.add(reparationPanel, BorderLayout.CENTER);
        
        return container;
    }
}