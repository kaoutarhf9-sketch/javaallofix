package presentation;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ClientPanel extends JPanel {

    public static JTextField txtNom = new JTextField();
    public static JTextField txtPrenom = new JTextField();
    public static JTextField txtTel = new JTextField();
    public static JTextField txtEmail = new JTextField();

    // 🔥 chemin de la photo
    public static String photoPath;

    // 🔥 code client courant (TRÈS IMPORTANT)
    public static String currentClientCode;

    private static JLabel lblPhotoPreview;

    public ClientPanel() {

        setLayout(new GridLayout(6, 2, 10, 10));
        setBorder(BorderFactory.createTitledBorder("Client"));

        add(new JLabel("Nom"));
        add(txtNom);

        add(new JLabel("Prénom"));
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

    // ================== LOGIQUE PHOTO ==================
    private void choisirPhoto() {

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Prendre une photo (simulation caméra)");

        chooser.addChoosableFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter(
                        "Images", "jpg", "jpeg", "png"
                )
        );
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File selectedFile = chooser.getSelectedFile();

                String fileName = "CL-" + System.currentTimeMillis() + ".jpg";

                File photosDir = new File("photos");
                if (!photosDir.exists()) {
                    photosDir.mkdir();
                }

                File destination = new File(photosDir, fileName);

                java.nio.file.Files.copy(
                        selectedFile.toPath(),
                        destination.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );

                photoPath = "photos/" + fileName;

                ImageIcon icon = new ImageIcon(photoPath);
                Image img = icon.getImage().getScaledInstance(
                        120, 120, Image.SCALE_SMOOTH
                );
                lblPhotoPreview.setIcon(new ImageIcon(img));

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de la sélection de la photo",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ================== RESET FORMULAIRE CLIENT ==================
    public static void clearClientForm() {
        txtNom.setText("");
        txtPrenom.setText("");
        txtTel.setText("");
        txtEmail.setText("");
        photoPath = null;
        currentClientCode = null;   // 🔥 TRÈS IMPORTANT

        if (lblPhotoPreview != null) {
            lblPhotoPreview.setIcon(null);
        }
    }
}
