package presentation;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ViewDashboardReparateur extends JPanel {
    private ModernMainFrame frame;
    private ViewRecette viewRecette; // Intégration de la vue des recettes

    public ViewDashboardReparateur(ModernMainFrame frame) {
        this.frame = frame;
        this.viewRecette = new ViewRecette(frame);
        
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);

        initLayout();
    }

    private void initLayout() {
        // Sidebar ou Onglets pour le réparateur
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Theme.FONT_BOLD);
        
        // Onglet 1 : Gestion des Recettes (Ton nouveau module)
        tabs.addTab(" 💰 Mes Recettes & Échanges ", viewRecette);
        
        // Onglet 2 : Travaux en cours (Exemple)
        tabs.addTab(" 🛠️ Réparations en cours ", createPlaceholder("Liste des réparations..."));

        add(tabs, BorderLayout.CENTER);
    }

    public void refreshData() {
        viewRecette.refresh();
    }

    private JPanel createPlaceholder(String text) {
        JPanel p = new JPanel(new GridBagLayout());
        p.add(new JLabel(text));
        return p;
    }
}