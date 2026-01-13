package presentation;

import dao.*;
import metier.EtatReparation; // ✅ Import de l'Enum

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class DeviceFormPanel extends JPanel {

    private JComboBox<String> cbType;
    private JComboBox<String> cbMarque;

    private JTextField txtAutreType;
    private JTextField txtAutreMarque;

    private JTextField txtCause;
    private JTextField txtPrixTotal;
    private JTextField txtAvance;
    private JTextField txtReste;

    public DeviceFormPanel() {

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Appareil à réparer"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // ===== TYPE =====
        add(new JLabel("Type"), gbc);
        gbc.gridx = 1;
        cbType = new JComboBox<>(new String[]{
                "Téléphone", "PC", "Laptop", "Tablette",
                "Console", "Imprimante", "Autre"
        });
        add(cbType, gbc);

        // Autre type
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Autre type"), gbc);
        gbc.gridx = 1;
        txtAutreType = new JTextField();
        txtAutreType.setVisible(false);
        add(txtAutreType, gbc);

        // ===== MARQUE =====
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Marque"), gbc);
        gbc.gridx = 1;
        cbMarque = new JComboBox<>(new String[]{
                "Samsung", "Apple", "HP", "Dell",
                "Lenovo", "Asus", "Xiaomi", "Autre"
        });
        add(cbMarque, gbc);

        // Autre marque
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Autre marque"), gbc);
        gbc.gridx = 1;
        txtAutreMarque = new JTextField();
        txtAutreMarque.setVisible(false);
        add(txtAutreMarque, gbc);

        // ===== CAUSE =====
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Cause"), gbc);
        gbc.gridx = 1;
        txtCause = new JTextField();
        add(txtCause, gbc);

        // ===== PRIX TOTAL =====
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Prix total"), gbc);
        gbc.gridx = 1;
        txtPrixTotal = new JTextField();
        add(txtPrixTotal, gbc);

        // ===== AVANCE =====
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Avance"), gbc);
        gbc.gridx = 1;
        txtAvance = new JTextField();
        add(txtAvance, gbc);

        // ===== RESTE =====
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Reste"), gbc);
        gbc.gridx = 1;
        txtReste = new JTextField();
        txtReste.setEditable(false);
        add(txtReste, gbc);

        // ===== LISTENERS =====
        cbType.addActionListener(e -> toggleAutreType());
        cbMarque.addActionListener(e -> toggleAutreMarque());

        txtPrixTotal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                calculerReste();
            }
        });

        txtAvance.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                calculerReste();
            }
        });
    }

    // 🔥 MÉTHODE CLÉ : Elle fabrique la Réparation pour le compte du client fourni
    public Reparation getReparationReadyToSave(Client clientUnique) {
        
        // 1. Validation locale
        if (txtCause.getText().isEmpty() || txtPrixTotal.getText().isEmpty()) {
            throw new RuntimeException("Champs manquants (Cause ou Prix) sur l'un des appareils.");
        }

        double prix;
        double avance;

        try {
            prix = Double.parseDouble(txtPrixTotal.getText());
            avance = txtAvance.getText().isEmpty() ? 0 : Double.parseDouble(txtAvance.getText());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Erreur de format numérique sur un appareil.");
        }

        // 2. Construction Device
        String type = "Autre".equals(cbType.getSelectedItem()) 
                      ? txtAutreType.getText() 
                      : (String) cbType.getSelectedItem();
                      
        String marque = "Autre".equals(cbMarque.getSelectedItem()) 
                        ? txtAutreMarque.getText() 
                        : (String) cbMarque.getSelectedItem();

        Device device = Device.builder()
                .type(type)
                .marque(marque)
                .client(clientUnique) // ✅ On rattache cet appareil au client unique généré plus haut
                .build();

        // 3. Construction Reparation
        return Reparation.builder()
                .cause(txtCause.getText())
                .prixTotal(prix)
                .avance(avance)
                .reste(prix - avance)
                .dateDepot(LocalDate.now()) // ✅ Date du jour
                .etat(EtatReparation.EN_ATTENTE) // ✅ AJOUT CRUCIAL : Statut par défaut
                .device(device)
                .build();
    }

    // ================== LOGIQUE INTERNE ==================
    
    private void toggleAutreType() {
        txtAutreType.setVisible(cbType.getSelectedItem().equals("Autre"));
        revalidate();
        repaint();
    }

    private void toggleAutreMarque() {
        txtAutreMarque.setVisible(cbMarque.getSelectedItem().equals("Autre"));
        revalidate();
        repaint();
    }

    private void calculerReste() {
        try {
            double total = txtPrixTotal.getText().isEmpty()
                    ? 0
                    : Double.parseDouble(txtPrixTotal.getText());

            double avance = txtAvance.getText().isEmpty()
                    ? 0
                    : Double.parseDouble(txtAvance.getText());

            txtReste.setText(String.valueOf(total - avance));
        } catch (NumberFormatException e) {
            txtReste.setText("");
        }
    }

    // Utilitaire pour vider le formulaire après sauvegarde
    public void clearForm() {
        txtCause.setText("");
        txtPrixTotal.setText("");
        txtAvance.setText("");
        txtReste.setText("");
        txtAutreType.setText("");
        txtAutreMarque.setText("");
        txtAutreType.setVisible(false);
        txtAutreMarque.setVisible(false);
    }
}