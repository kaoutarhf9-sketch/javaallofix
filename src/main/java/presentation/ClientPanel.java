package presentation;

import dao.Client;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ClientPanel extends JPanel {

    // --- CHAMPS STATIQUES (Accessibles depuis ReparationPanel) ---
    public static JTextField txtNom;
    public static JTextField txtPrenom;
    public static JTextField txtTel;
    public static JTextField txtEmail;
    
    // Variables pour la photo
    public static String photoPath = null;
    private static JLabel lblPhotoPreview;

    public ClientPanel() {
        // 1. STYLE MODERNE
        setLayout(new BorderLayout());
        setOpaque(false);
        // Petite marge à droite pour séparer visuellement du panneau réparation
        setBorder(new EmptyBorder(0, 0, 0, 20)); 

        // 2. TITRE
        JLabel lblTitle = new JLabel("Informations Client");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Theme.TEXT_HEADLINE);
        lblTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        // 3. FORMULAIRE (GridBagLayout pour un alignement pro)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 15, 0); // Espace vertical entre champs
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        
        // Initialisation des champs avec le style moderne
        txtNom = createModernField();
        txtPrenom = createModernField();
        txtTel = createModernField();
        txtEmail = createModernField();

        // Ajout des composants
        int y = 0;
        gbc.gridy = y++; formPanel.add(createLabel("Nom *"), gbc);
        gbc.gridy = y++; formPanel.add(txtNom, gbc);

        gbc.gridy = y++; formPanel.add(createLabel("Prénom *"), gbc);
        gbc.gridy = y++; formPanel.add(txtPrenom, gbc);

        gbc.gridy = y++; formPanel.add(createLabel("Téléphone"), gbc);
        gbc.gridy = y++; formPanel.add(txtTel, gbc);

        gbc.gridy = y++; formPanel.add(createLabel("Email"), gbc);
        gbc.gridy = y++; formPanel.add(txtEmail, gbc);

        // --- ZONE PHOTO ---
        gbc.gridy = y++; 
        gbc.insets = new Insets(20, 0, 5, 0);
        formPanel.add(createLabel("Photo du client (Optionnel)"), gbc);

        JPanel photoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        photoPanel.setOpaque(false);

        // Aperçu carré avec bordure
        lblPhotoPreview = new JLabel("Pas d'image");
        lblPhotoPreview.setPreferredSize(new Dimension(100, 100));
        lblPhotoPreview.setHorizontalAlignment(SwingConstants.CENTER);
        lblPhotoPreview.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblPhotoPreview.setForeground(Color.GRAY);
        lblPhotoPreview.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        lblPhotoPreview.setOpaque(true);
        lblPhotoPreview.setBackground(Color.WHITE);

        // Bouton Upload
        JButton btnUpload = new JButton("Choisir...");
        btnUpload.setBackground(Color.WHITE);
        btnUpload.setFocusPainted(false);
        btnUpload.addActionListener(e -> choisirPhoto());

        photoPanel.add(lblPhotoPreview);
        photoPanel.add(Box.createHorizontalStrut(15));
        photoPanel.add(btnUpload);

        gbc.gridy = y++; 
        gbc.insets = new Insets(0, 0, 15, 0);
        formPanel.add(photoPanel, gbc);
        
        // Wrapper pour aligner en haut
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(formPanel, BorderLayout.NORTH);

        add(wrapper, BorderLayout.CENTER);
    }

    // =============================================================
    // 🔥 CORRECTION LOGIQUE BDD : REMPLISSAGE COMPLET DE L'OBJET
    // =============================================================
    public static Client getClientFromForm() {
        // Validation basique
        if (txtNom.getText().trim().isEmpty() || txtPrenom.getText().trim().isEmpty()) {
            return null; 
        }

        // On gère les noms de fichiers proprement
        String fileName = null;
        if (photoPath != null) {
            File f = new File(photoPath);
            fileName = f.getName();
        }

        return Client.builder()
                .nom(txtNom.getText().trim())
                .prenom(txtPrenom.getText().trim())
                .telephone(txtTel.getText().trim())
                .email(txtEmail.getText().trim())
                
                // ✅ ICI ON REMPLIT LES DEUX CHAMPS DE L'ENTITÉ
                .photo(fileName)     // Juste le nom : "CL-123.jpg"
                .photoPath(photoPath) // Le chemin relatif : "photos/CL-123.jpg"
                
                .build();
    }

    // =============================================================
    // LOGIQUE PHOTO (Votre logique était bonne, je l'ai gardée)
    // =============================================================
    private void choisirPhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Sélectionner une photo");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Images (JPG, PNG)", "jpg", "png", "jpeg"));
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File sourceFile = chooser.getSelectedFile();
                
                // Création du dossier si inexistant
                File photosDir = new File("photos");
                if (!photosDir.exists()) {
                    photosDir.mkdir();
                }

                // Nom unique
                String extension = "";
                int i = sourceFile.getName().lastIndexOf('.');
                if (i > 0) extension = sourceFile.getName().substring(i);
                
                String newFileName = "CL-" + System.currentTimeMillis() + extension;
                File destFile = new File(photosDir, newFileName);

                // Copie physique
                Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Mise à jour variable
                photoPath = destFile.getPath().replace("\\", "/"); // Normalisation des slashs

                // Affichage Aperçu
                ImageIcon icon = new ImageIcon(photoPath);
                Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                lblPhotoPreview.setText("");
                lblPhotoPreview.setIcon(new ImageIcon(img));

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erreur image : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                photoPath = null;
            }
        }
    }

    // =============================================================
    // RESET
    // =============================================================
    public static void clearClientForm() {
        txtNom.setText("");
        txtPrenom.setText("");
        txtTel.setText("");
        txtEmail.setText("");
        
        photoPath = null;
        if (lblPhotoPreview != null) {
            lblPhotoPreview.setIcon(null);
            lblPhotoPreview.setText("Pas d'image");
        }
    }

    // =============================================================
    // HELPERS UI (Pour garder le look moderne)
    // =============================================================
    private JTextField createModernField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(0, 35));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200)), 
            new EmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(100, 116, 139));
        return lbl;
    }
}