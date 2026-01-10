package presentation;

import dao.Client; // ✅ Import nécessaire
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ClientPanel extends JPanel {

    // Champs statiques (accessibles depuis partout)
    public static JTextField txtNom = new JTextField();
    public static JTextField txtPrenom = new JTextField();
    public static JTextField txtTel = new JTextField();
    public static JTextField txtEmail = new JTextField();

    public static String photoPath;
    public static String currentClientCode; // Peut être utile, mais géré surtout par ReparationPanel maintenant

    private static JLabel lblPhotoPreview;

    public ClientPanel() {
        setLayout(new GridLayout(6, 2, 10, 10));
        setBorder(BorderFactory.createTitledBorder("Informations Client"));

        add(new JLabel("Nom *"));
        add(txtNom);

        add(new JLabel("Prénom *"));
        add(txtPrenom);

        add(new JLabel("Téléphone"));
        add(txtTel);

        add(new JLabel("Email"));
        add(txtEmail);

        // ===== PHOTO =====
        JButton btnPhoto = new JButton("📷 Photo");

        lblPhotoPreview = new JLabel();
        lblPhotoPreview.setPreferredSize(new Dimension(120, 120));
        lblPhotoPreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        lblPhotoPreview.setHorizontalAlignment(JLabel.CENTER);

        btnPhoto.addActionListener(e -> choisirPhoto());

        add(new JLabel("Photo"));
        add(btnPhoto);

        add(new JLabel("Aperçu"));
        add(lblPhotoPreview);
    }

    // 🔥 NOUVELLE MÉTHODE CRUCIALE : 
    // Elle transforme les champs de texte en objet Client pour le ReparationPanel
    public static Client getClientFromForm() {
        // 1. Validation basique
        if (txtNom.getText().trim().isEmpty() || txtPrenom.getText().trim().isEmpty()) {
            return null; // Indique qu'il manque des infos
        }

        // 2. Construction de l'objet (SANS le code client, qui sera généré après)
        return Client.builder()
                .nom(txtNom.getText())
                .prenom(txtPrenom.getText())
                .telephone(txtTel.getText())
                .email(txtEmail.getText())
                // .photo(photoPath) // Décommentez si votre entité Client a un champ 'photo'
                .build();
    }

    // ================== LOGIQUE PHOTO ==================
    private void choisirPhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Prendre une photo");
        chooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images", "jpg", "png"));
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File selectedFile = chooser.getSelectedFile();
                String fileName = "CL-" + System.currentTimeMillis() + ".jpg";
                
                File photosDir = new File("photos");
                if (!photosDir.exists()) photosDir.mkdir();

                File destination = new File(photosDir, fileName);
                java.nio.file.Files.copy(selectedFile.toPath(), destination.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                photoPath = "photos/" + fileName;

                // Affichage redimensionné
                ImageIcon icon = new ImageIcon(photoPath);
                Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                lblPhotoPreview.setIcon(new ImageIcon(img));

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur photo : " + ex.getMessage());
            }
        }
    }

    // ================== RESET FORMULAIRE ==================
    public static void clearClientForm() {
        txtNom.setText("");
        txtPrenom.setText("");
        txtTel.setText("");
        txtEmail.setText("");
        photoPath = null;
        currentClientCode = null;

        if (lblPhotoPreview != null) {
            lblPhotoPreview.setIcon(null);
        }
    }
}