package presentation;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import dao.Recette;
import dao.Reparateur;
import metier.GestionRecette;
import metier.IGestionRecette;

public class ViewRecette extends JPanel {

    private ModernMainFrame frame;
    private IGestionRecette metier;
    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private JComboBox<String> comboPeriode;
    private JLabel lblTotalCredit, lblTotalVerse;
    private JTextField txtSearch;

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
        String[] columns = {"ID", "DATE", "TYPE", "PARTENAIRE", "MONTANT", "STATUT", "ACTION"};
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Masquer ID
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);

        styleTable(table);

        comboPeriode = new JComboBox<>(new String[]{"JOUR", "SEMAINE", "MOIS"});
        comboPeriode.setPreferredSize(new Dimension(120, 35));
        comboPeriode.addActionListener(e -> refresh());

        // Initialisation de la barre de recherche
        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(200, 35));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(0, 10, 0, 10)
        ));
        
        // Filtrage en temps réel
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String text = txtSearch.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    // Filtre sur la colonne PARTENAIRE (index 3)
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 3));
                }
            }
        });

        lblTotalCredit = createStatLabel(new Color(231, 76, 60)); 
        lblTotalVerse = createStatLabel(new Color(46, 204, 113));  
    }

    private void setupLayout() {
        // --- HEADER (Titre + Stats) ---
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

        // --- TOOLBAR (Recherche + Période + Bouton) ---
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        
        JPanel leftTool = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftTool.setOpaque(false);
        leftTool.add(new JLabel("Rechercher :"));
        leftTool.add(txtSearch);
        leftTool.add(new JLabel("Période :"));
        leftTool.add(comboPeriode);

        JButton btnAdd = new JButton("+ Nouvelle Opération");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAdd.setBackground(new Color(52, 152, 219));
        btnAdd.setForeground(Color.WHITE);
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

    private void styleTable(JTable t) {
        t.setRowHeight(45);
        t.getTableHeader().setBackground(Color.WHITE);
        t.getTableHeader().setPreferredSize(new Dimension(0, 40));
        
        t.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if ("CREDIT".equals(value)) l.setForeground(new Color(231, 76, 60));
                else l.setForeground(new Color(46, 204, 113));
                return l;
            }
        });

        t.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                if ("RENDRE".equals(value)) {
                    JButton btn = new JButton("Rendre");
                    btn.setBackground(new Color(46, 204, 113));
                    btn.setForeground(Color.WHITE);
                    btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    btn.setBorderPainted(false);
                    return btn;
                }
                return new JLabel(value != null ? value.toString() : "");
            }
        });

        t.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = t.columnAtPoint(e.getPoint());
                int row = t.rowAtPoint(e.getPoint());
                if (col == 6 && row != -1) {
                    // Attention : avec le sorter, il faut convertir l'index de la vue vers le modèle
                    int modelRow = t.convertRowIndexToModel(row);
                    Object val = model.getValueAt(modelRow, col);
                    if ("RENDRE".equals(val)) {
                        int id = (int) model.getValueAt(modelRow, 0);
                        if (JOptionPane.showConfirmDialog(null, "Marquer comme rendu ?") == JOptionPane.YES_OPTION) {
                            metier.marquerCommeRendu(id);
                            refresh();
                        }
                    }
                }
            }
        });
    }

    public void refresh() {
        model.setRowCount(0);
        String periode = (String) comboPeriode.getSelectedItem();
        List<Recette> recettes = metier.obtenirHistorique(periode);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Recette r : recettes) {
            String action = "NON_RENDU".equals(r.getStatut()) ? "RENDRE" : "-";
            model.addRow(new Object[]{
                r.getId(),
                r.getDateOperation().format(formatter),
                r.getTypeOperation(),
                r.getPartenaire() != null ? r.getPartenaire() : "-",
                String.format("%.2f DH", r.getMontant()),
                r.getStatut(),
                action
            });
        }

        lblTotalCredit.setText(String.format("%.2f DH", metier.calculerTotalType(recettes, "CREDIT")));
        lblTotalVerse.setText(String.format("%.2f DH", metier.calculerTotalType(recettes, "VERSEMENT")));
    }

    private void showAddForm() {
        JDialog dialog = new JDialog(frame, "Nouvelle Opération", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtMnt = new JTextField();
        JTextField txtPart = new JTextField();
        JComboBox<String> cbType = new JComboBox<>(new String[]{"CREDIT", "VERSEMENT"});

        gbc.gridy = 0; gbc.gridx = 0; dialog.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1; dialog.add(cbType, gbc);
        gbc.gridy = 1; gbc.gridx = 0; dialog.add(new JLabel("Montant:"), gbc);
        gbc.gridx = 1; dialog.add(txtMnt, gbc);
        gbc.gridy = 2; gbc.gridx = 0; dialog.add(new JLabel("Partenaire:"), gbc);
        gbc.gridx = 1; dialog.add(txtPart, gbc);

        JButton btn = new JButton("Enregistrer");
        btn.setBackground(new Color(46, 204, 113));
        btn.setForeground(Color.WHITE);
        btn.addActionListener(e -> {
            try {
                Recette r = Recette.builder()
                    .montant(Double.parseDouble(txtMnt.getText().replace(",",".")))
                    .typeOperation((String)cbType.getSelectedItem())
                    .dateOperation(LocalDateTime.now())
                    .partenaire(txtPart.getText())
                    .statut("NON_RENDU")
                    .reparateur((Reparateur)frame.getCurrentUser())
                    .build();
                metier.ajouterTransaction(r);
                dialog.dispose();
                refresh();
            } catch(Exception ex) { JOptionPane.showMessageDialog(null, "Données invalides"); }
        });
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        dialog.add(btn, gbc);
        dialog.setVisible(true);
    }

    private JLabel createStatLabel(Color c) {
        JLabel l = new JLabel("0.00 DH");
        l.setFont(new Font("Segoe UI", Font.BOLD, 16)); l.setForeground(c);
        return l;
    }

    private JPanel createStatGroup(String title, JLabel val) {
        JPanel p = new JPanel(new GridLayout(2, 1)); p.setOpaque(false);
        JLabel t = new JLabel(title); t.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        p.add(t); p.add(val);
        return p;
    }
}