package presentation;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import dao.Recette;
import dao.Reparateur;
import metier.GestionRecette;
import metier.IGestionRecette;

public class ViewRecette extends JPanel {

    private ModernMainFrame frame;
    private IGestionRecette metier;
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> comboPeriode;
    private JLabel lblTotalCredit, lblTotalVerse;

    public ViewRecette(ModernMainFrame frame) {
        this.frame = frame;
        this.metier = new GestionRecette();
        
        setLayout(new BorderLayout(0, 20));
        setBackground(new Color(245, 246, 250));
        setBorder(new EmptyBorder(30, 40, 30, 40));

        initComponents();
        setupLayout();
        refresh();
    }

    private void initComponents() {
        // Ajout de la colonne "PARTENAIRE" pour voir avec qui l'argent est échangé
        String[] columns = {"DATE", "TYPE", "PARTENAIRE", "MONTANT", "STATUT"};
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        styleTable(table);

        comboPeriode = new JComboBox<>(new String[]{"JOUR", "SEMAINE", "MOIS"});
        comboPeriode.setPreferredSize(new Dimension(150, 35));
        comboPeriode.addActionListener(e -> refresh());

        lblTotalCredit = createStatLabel(new Color(231, 76, 60)); 
        lblTotalVerse = createStatLabel(new Color(46, 204, 113));  
    }

    private void setupLayout() {
        // --- HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Recettes & Échanges");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        header.add(title, BorderLayout.WEST);

        JPanel statsContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        statsContainer.setOpaque(false);
        statsContainer.add(createStatGroup("À RECEVOIR", lblTotalCredit));
        statsContainer.add(createStatGroup("DÉJÀ VERSÉ", lblTotalVerse));
        header.add(statsContainer, BorderLayout.EAST);

        // --- TOOLBAR ---
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        
        JPanel leftTool = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftTool.setOpaque(false);
        leftTool.add(new JLabel("Période :"));
        leftTool.add(comboPeriode);

        JButton btnAdd = new JButton("+ Nouvelle Opération");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAdd.setBackground(new Color(52, 152, 219));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setPreferredSize(new Dimension(180, 35));
        btnAdd.addActionListener(e -> showAddForm());

        toolbar.add(leftTool, BorderLayout.WEST);
        toolbar.add(btnAdd, BorderLayout.EAST);

        // --- TABLE ---
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(new EmptyBorder(15, 15, 15, 15));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);
        centerPanel.add(toolbar, BorderLayout.NORTH);
        centerPanel.add(tableCard, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
    }

    private void showAddForm() {
        JDialog dialog = new JDialog(frame, "Ajouter une opération", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtMontant = new JTextField();
        JTextField txtPartenaire = new JTextField(); // Nouveau champ
        JComboBox<String> cbType = new JComboBox<>(new String[]{"CREDIT", "VERSEMENT"});

        gbc.gridx = 0; gbc.gridy = 0; dialog.add(new JLabel("Type :"), gbc);
        gbc.gridx = 1; dialog.add(cbType, gbc);

        gbc.gridx = 0; gbc.gridy = 1; dialog.add(new JLabel("Montant (DH) :"), gbc);
        gbc.gridx = 1; dialog.add(txtMontant, gbc);

        gbc.gridx = 0; gbc.gridy = 2; dialog.add(new JLabel("Autre Réparateur :"), gbc);
        gbc.gridx = 1; dialog.add(txtPartenaire, gbc);

        JButton btnSave = new JButton("Enregistrer");
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        btnSave.addActionListener(e -> {
            String montantRaw = txtMontant.getText().trim().replace(",", ".");
            String partenaire = txtPartenaire.getText().trim();

            if (montantRaw.isEmpty() || partenaire.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Veuillez remplir tous les champs.");
                return;
            }

            try {
                double montant = Double.parseDouble(montantRaw);
                String type = (String) cbType.getSelectedItem();
                
                Recette r = Recette.builder()
                        .montant(montant)
                        .typeOperation(type)
                        .dateOperation(LocalDateTime.now())
                        .partenaire(partenaire) // On utilise le champ ajouté dans l'entité
                        .statut(type.equals("CREDIT") ? "NON_RENDU" : "RENDU")
                        .reparateur((Reparateur) frame.getCurrentUser())
                        .build();

                metier.ajouterTransaction(r);
                dialog.dispose();
                refresh();
                JOptionPane.showMessageDialog(this, "Opération enregistrée !");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Erreur : Le montant est invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        dialog.add(btnSave, gbc);

        dialog.setVisible(true);
    }

    public void refresh() {
        model.setRowCount(0);
        String periode = (String) comboPeriode.getSelectedItem();
        List<Recette> recettes = metier.obtenirHistorique(periode);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Recette r : recettes) {
            model.addRow(new Object[]{
                r.getDateOperation().format(formatter),
                r.getTypeOperation(),
                r.getPartenaire() != null ? r.getPartenaire() : "-",
                String.format("%.2f DH", r.getMontant()),
                r.getStatut()
            });
        }

        double totalCredit = metier.calculerTotalType(recettes, "CREDIT");
        double totalVerse = metier.calculerTotalType(recettes, "VERSEMENT");
        lblTotalCredit.setText(String.format("%.2f DH", totalCredit));
        lblTotalVerse.setText(String.format("%.2f DH", totalVerse));
    }

    private void styleTable(JTable t) {
        t.setRowHeight(45);
        t.setShowGrid(false);
        t.getTableHeader().setBackground(Color.WHITE);
        t.getTableHeader().setPreferredSize(new Dimension(0, 40));
        
        t.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if ("CREDIT".equals(value)) l.setForeground(new Color(231, 76, 60));
                else l.setForeground(new Color(46, 204, 113));
                return l;
            }
        });
    }

    private JLabel createStatLabel(Color c) {
        JLabel l = new JLabel("0.00 DH");
        l.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l.setForeground(c);
        return l;
    }

    private JPanel createStatGroup(String title, JLabel val) {
        JPanel p = new JPanel(new GridLayout(2, 1));
        p.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        t.setForeground(Color.GRAY);
        p.add(t); p.add(val);
        return p;
    }
}