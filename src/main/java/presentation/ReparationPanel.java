package presentation;

import javax.swing.*;
import java.awt.*;

public class ReparationPanel extends JPanel {

    private JPanel devicesContainer;
    
    // ✅ Référence qu'on va recevoir du Dashboard
    private ViewListeReparation historiqueVue;

    public ReparationPanel() {
        setLayout(new BorderLayout());

        JButton btnAdd = new JButton("➕ Ajouter device");
        btnAdd.addActionListener(e -> addDeviceForm());

        devicesContainer = new JPanel();
        devicesContainer.setLayout(new BoxLayout(devicesContainer, BoxLayout.Y_AXIS));

        add(btnAdd, BorderLayout.NORTH);
        add(new JScrollPane(devicesContainer), BorderLayout.CENTER);

        addDeviceForm();
    }

    // ✅ Méthode appelée par le Dashboard pour injecter l'historique
    public void setHistoriqueVue(ViewListeReparation vue) {
        this.historiqueVue = vue;
        // Mettre à jour les formulaires déjà existants
        for (Component comp : devicesContainer.getComponents()) {
            if (comp instanceof DeviceFormPanel) {
                ((DeviceFormPanel) comp).setCallbackHistorique(vue);
            }
        }
    }

    private void addDeviceForm() {
        DeviceFormPanel form = new DeviceFormPanel();
        
        // ✅ On transmet la référence au nouveau formulaire
        if (this.historiqueVue != null) {
            form.setCallbackHistorique(this.historiqueVue);
        }
        
        devicesContainer.add(form);
        // Petit espace visuel entre les formulaires
        devicesContainer.add(Box.createVerticalStrut(10)); 
        
        revalidate();
        repaint();
    }
}