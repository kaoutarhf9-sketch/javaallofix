package presentation;

import dao.Client;
import dao.Reparation;
import metier.GestionReparation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ReparationPanel extends JPanel {

    private JPanel devicesContainer;
    private ViewListeReparation historiqueVue;
    private GestionReparation gestionReparation;

    public ReparationPanel() {
        setLayout(new BorderLayout());
        gestionReparation = new GestionReparation();

        // --- HAUT : Bouton pour ajouter des lignes d'appareils ---
        JButton btnAddDevice = new JButton("➕ Ajouter un autre appareil");
        btnAddDevice.addActionListener(e -> addDeviceForm());
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(btnAddDevice);
        add(topPanel, BorderLayout.NORTH);

        // --- CENTRE : Liste des formulaires ---
        devicesContainer = new JPanel();
        devicesContainer.setLayout(new BoxLayout(devicesContainer, BoxLayout.Y_AXIS));
        add(new JScrollPane(devicesContainer), BorderLayout.CENTER);

        // --- BAS : LE GROS BOUTON ENREGISTRER TOUT ---
        JButton btnSaveAll = new JButton("💾 ENREGISTRER TOUT (Client + Réparations)");
        btnSaveAll.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnSaveAll.setBackground(new Color(46, 204, 113)); // Vert
        btnSaveAll.setForeground(Color.WHITE);
        btnSaveAll.setPreferredSize(new Dimension(0, 50));
        
        btnSaveAll.addActionListener(e -> saveAll());
        
        add(btnSaveAll, BorderLayout.SOUTH);

        // Ajouter un premier formulaire par défaut
        addDeviceForm();
    }

    private void saveAll() {
        // 1. Récupérer le Client
        Client client = ClientPanel.getClientFromForm();
        if (client == null) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir les infos du Client (Nom, Prénom).");
            return;
        }

        // 2. Générer le Code Client UNE SEULE FOIS pour tout le monde
        String codeUnique = "CL-" + System.currentTimeMillis();
        client.setCodeClient(codeUnique);

        // 3. Récupérer toutes les réparations
        List<Reparation> reparationsASauvegarder = new ArrayList<>();
        
        try {
            for (Component comp : devicesContainer.getComponents()) {
                if (comp instanceof DeviceFormPanel) {
                    DeviceFormPanel panel = (DeviceFormPanel) comp;
                    // On demande au panel de fabriquer l'objet en lui donnant le client
                    Reparation r = panel.getReparationReadyToSave(client);
                    reparationsASauvegarder.add(r);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur de saisie : " + ex.getMessage());
            return;
        }

        if (reparationsASauvegarder.isEmpty()) {
            return;
        }

        // 4. Sauvegarde en boucle (Idéalement, à faire en une seule transaction métier)
        for (Reparation r : reparationsASauvegarder) {
            gestionReparation.save(r);
        }

        // 5. Succès & Nettoyage
        JOptionPane.showMessageDialog(this, "✅ Succès ! " + reparationsASauvegarder.size() + " réparation(s) enregistrée(s) pour le client " + client.getNom());

        // Refresh Historique
        if (historiqueVue != null) historiqueVue.refreshTable();

        // Reset Interface
        ClientPanel.clearClientForm();
        devicesContainer.removeAll(); // On vide la liste des devices
        addDeviceForm(); // On en remet un vide
        devicesContainer.revalidate();
        devicesContainer.repaint();
    }

    public void setHistoriqueVue(ViewListeReparation vue) {
        this.historiqueVue = vue;
    }

    private void addDeviceForm() {
        DeviceFormPanel form = new DeviceFormPanel();
        devicesContainer.add(form);
        devicesContainer.add(Box.createVerticalStrut(10)); // Espace
        revalidate();
        repaint();
    }
}