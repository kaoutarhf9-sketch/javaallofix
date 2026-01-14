package presentation;

import dao.Client;
import dao.Proprietaire; 
import dao.Reparation;
import dao.User; 
import dao.Reparateur; 
import metier.GestionReparation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ReparationPanel extends JPanel {

    private JPanel devicesContainer;
    private ViewListeReparation historiqueVue;
    private GestionReparation gestionReparation;
    private ModernMainFrame frame; 

    public ReparationPanel(ModernMainFrame frame) { 
        this.frame = frame;
        setLayout(new BorderLayout());
        gestionReparation = new GestionReparation();

        // --- HAUT : Bouton pour ajouter des lignes d'appareils ---
        JButton btnAddDevice = new JButton("➕ Ajouter un autre appareil");
        btnAddDevice.addActionListener(e -> addDeviceForm());
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        topPanel.add(btnAddDevice);
        add(topPanel, BorderLayout.NORTH);

        // --- CENTRE : Liste des formulaires ---
        devicesContainer = new JPanel();
        devicesContainer.setLayout(new BoxLayout(devicesContainer, BoxLayout.Y_AXIS));
        devicesContainer.setBackground(Color.WHITE); 
        
        JScrollPane scroll = new JScrollPane(devicesContainer);
        scroll.setBorder(null); 
        scroll.getVerticalScrollBar().setUnitIncrement(16); 
        add(scroll, BorderLayout.CENTER);

        // --- BAS : BOUTON ENREGISTRER ---
        JButton btnSaveAll = new JButton("💾 ENREGISTRER TOUT (Client + Réparations)");
        btnSaveAll.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnSaveAll.setBackground(new Color(46, 204, 113)); // Vert
        btnSaveAll.setForeground(Color.WHITE);
        btnSaveAll.setPreferredSize(new Dimension(0, 50));
        btnSaveAll.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSaveAll.setFocusPainted(false);
        
        btnSaveAll.addActionListener(e -> saveAll());
        
        add(btnSaveAll, BorderLayout.SOUTH);

        // Formulaire par défaut
        addDeviceForm();
    }

    private void saveAll() {
        // 0. Récupérer l'utilisateur connecté
        User currentUser = frame.getCurrentUser();
        
        // Vérification des droits
        boolean isReparateur = (currentUser instanceof Reparateur);
        boolean isProprioReparateur = (currentUser instanceof Proprietaire && ((Proprietaire) currentUser).isEstReparateur());

        if (!isReparateur && !isProprioReparateur) {
             JOptionPane.showMessageDialog(this, "Erreur : Vous n'avez pas les droits pour enregistrer une réparation.", "Accès Refusé", JOptionPane.ERROR_MESSAGE);
             return;
        }

        // 1. Récupérer le Client
        Client client = ClientPanel.getClientFromForm();
        if (client == null) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir les infos du Client (Nom, Prénom).", "Info Manquante", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Générer Code Client
        String codeUnique = "CL-" + System.currentTimeMillis();
        client.setCodeClient(codeUnique);

        // 3. Préparer les réparations
        List<Reparation> reparationsASauvegarder = new ArrayList<>();
        
        try {
            for (Component comp : devicesContainer.getComponents()) {
                if (comp instanceof DeviceFormPanel) {
                    DeviceFormPanel panel = (DeviceFormPanel) comp;
                    
                    // --- LOGIQUE D'ASSIGNATION ---
                    // Si c'est un vrai Reparateur, on le passe à la méthode (pour qu'elle remplisse 'reparateur')
                    // Si c'est un Proprietaire, on passe null pour l'instant (on remplira 'proprietaire' juste après)
                    Reparateur reparateurArg = (isReparateur) ? (Reparateur) currentUser : null;
                    
                    // Création de l'objet Réparation via le formulaire
                    Reparation r = panel.getReparationReadyToSave(client, reparateurArg);
                    
                    // 🔥 CORRECTION CRITIQUE : Séparation des rôles
                    if (currentUser instanceof Proprietaire) {
                        // C'est le PATRON qui répare -> On remplit le champ 'proprietaire'
                        r.setProprietaire((Proprietaire) currentUser);
                        r.setReparateur(null); // On s'assure que l'autre est vide
                    } 
                    else if (currentUser instanceof Reparateur) {
                        // C'est un EMPLOYÉ -> On s'assure que le champ 'proprietaire' est vide
                        r.setProprietaire(null);
                        // Le champ 'reparateur' a déjà été rempli par panel.getReparationReadyToSave
                    }

                    reparationsASauvegarder.add(r);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur de saisie : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (reparationsASauvegarder.isEmpty()) return;

        // 4. Sauvegarde BDD
        for (Reparation r : reparationsASauvegarder) {
            gestionReparation.save(r); 
        }

        // 5. Feedback & Reset
        JOptionPane.showMessageDialog(this, "✅ Succès ! " + reparationsASauvegarder.size() + " réparation(s) enregistrée(s).");

        if (historiqueVue != null) historiqueVue.refreshTable();

        ClientPanel.clearClientForm();
        devicesContainer.removeAll();
        addDeviceForm();
        devicesContainer.revalidate();
        devicesContainer.repaint();
    }

    public void setHistoriqueVue(ViewListeReparation vue) {
        this.historiqueVue = vue;
    }

    private void addDeviceForm() {
        DeviceFormPanel form = new DeviceFormPanel();
        devicesContainer.add(form);
        devicesContainer.add(Box.createVerticalStrut(10)); 
        
        devicesContainer.revalidate();
        devicesContainer.repaint();
        
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = ((JScrollPane) devicesContainer.getParent().getParent()).getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }
}