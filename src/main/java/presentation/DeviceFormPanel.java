package presentation;

import dao.*;
import metier.*;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate; // ✅ Import indispensable pour la date

public class DeviceFormPanel extends JPanel {

    private JComboBox<String> cbType;
    private JComboBox<String> cbMarque;

    private JTextField txtAutreType;
    private JTextField txtAutreMarque;

    private JTextField txtCause;
    private JTextField txtPrixTotal;
    private JTextField txtAvance;
    private JTextField txtReste;

    // ✅ Référence vers l'historique pour le mettre à jour
    private ViewListeReparation historiqueRef;

    public DeviceFormPanel() {

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Réparation"));

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

        // ===== BOUTON =====
        gbc.gridx = 1;
        gbc.gridy++;
        JButton btnSave = new JButton("Enregistrer");
        add(btnSave, gbc);

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

        btnSave.addActionListener(e -> saveReparation());
    }

    // ✅ SETTER pour recevoir la vue Historique
    public void setCallbackHistorique(ViewListeReparation vue) {
        this.historiqueRef = vue;
    }

    // ================== DYNAMIQUE ==================
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

    // ================== CALCUL RESTE ==================
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

    // ================== SAVE ==================
    private void saveReparation() {

        if (ClientPanel.txtNom.getText().isEmpty()
                || ClientPanel.txtPrenom.getText().isEmpty()
                || txtCause.getText().isEmpty()
                || txtPrixTotal.getText().isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Champs obligatoires manquants",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double prixTotal;
        double avance;

        try {
            prixTotal = Double.parseDouble(txtPrixTotal.getText());
            avance = txtAvance.getText().isEmpty()
                    ? 0
                    : Double.parseDouble(txtAvance.getText());

            if (avance > prixTotal) {
                JOptionPane.showMessageDialog(this,
                        "L'avance ne peut pas dépasser le prix total",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Les montants doivent être numériques",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String type = cbType.getSelectedItem().equals("Autre")
                ? txtAutreType.getText()
                : cbType.getSelectedItem().toString();

        String marque = cbMarque.getSelectedItem().equals("Autre")
                ? txtAutreMarque.getText()
                : cbMarque.getSelectedItem().toString();

        // 1. CLIENT
        Client client = Client.builder()
                .nom(ClientPanel.txtNom.getText())
                .prenom(ClientPanel.txtPrenom.getText())
                .telephone(ClientPanel.txtTel.getText())
                .email(ClientPanel.txtEmail.getText())
                .codeClient("CL-" + System.currentTimeMillis())
                .build();

        // 2. DEVICE
        Device device = Device.builder()
                .type(type)
                .marque(marque)
                .client(client)
                .build();

        // 3. REPARATION
        Reparation r = Reparation.builder()
                .cause(txtCause.getText())
                .prixTotal(prixTotal)
                .avance(avance)
                .reste(prixTotal - avance)
                .dateDepot(LocalDate.now()) // ✅ DATE DU JOUR AJOUTÉE
                .device(device)
                .build();

        new GestionReparation().save(r);

        JOptionPane.showMessageDialog(this,
                "Réparation enregistrée\nReste à payer : " + (prixTotal - avance) + " DH");

        // ✅ RAFRAÎCHISSEMENT AUTOMATIQUE DE L'HISTORIQUE
        if (historiqueRef != null) {
            historiqueRef.refreshTable();
        }

        clearForm();
        ClientPanel.clearClientForm();
    }

    private void clearForm() {
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