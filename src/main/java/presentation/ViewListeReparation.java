package presentation;

import dao.Reparation;
import metier.GestionReparation;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class ViewListeReparation extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private GestionReparation gestionReparation;
    private TableRowSorter<DefaultTableModel> sorter;

    public ViewListeReparation() {

        gestionReparation = new GestionReparation();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Historique des réparations"));

        // ===== MODEL =====
        model = new DefaultTableModel(
                new String[]{
                        "Code Client", "Nom", "Prénom",
                        "Type", "Marque", "Cause",
                        "Total", "Avance", "Reste"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // table non éditable
            }
        };

        // ===== TABLE =====
        table = new JTable(model);
        table.setRowHeight(28);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(220, 235, 250));
        table.setSelectionForeground(Color.BLACK);

        // ===== HEADER =====
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(45, 62, 80));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        // ===== ZEBRA EFFECT =====
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0
                            ? new Color(245, 247, 250)
                            : Color.WHITE);
                }
                return c;
            }
        });

        // ===== ALIGNEMENT (Code + montants) =====
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(center); // Code client
        table.getColumnModel().getColumn(6).setCellRenderer(center); // Total
        table.getColumnModel().getColumn(7).setCellRenderer(center); // Avance
        table.getColumnModel().getColumn(8).setCellRenderer(center); // Reste

        // ===== SCROLL =====
        JScrollPane scrollPane = new JScrollPane(table);

        // ===== RECHERCHE =====
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblSearch = new JLabel("🔍 Rechercher");
        JTextField txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(200, 28));

        topPanel.add(lblSearch);
        topPanel.add(txtSearch);

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { search(); }
            public void removeUpdate(DocumentEvent e) { search(); }
            public void changedUpdate(DocumentEvent e) { search(); }

            private void search() {
                String text = txtSearch.getText();
                sorter.setRowFilter(text.isEmpty()
                        ? null
                        : RowFilter.regexFilter("(?i)" + text));
            }
        });

        // ===== ADD TO PANEL =====
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        loadData();
    }

    // 🔄 Charger les réparations depuis la DB
    public void loadData() {

        model.setRowCount(0);

        for (Reparation r : gestionReparation.findAll()) {
            model.addRow(new Object[]{
                    r.getDevice().getClient().getCodeClient(),
                    r.getDevice().getClient().getNom(),
                    r.getDevice().getClient().getPrenom(),
                    r.getDevice().getType(),
                    r.getDevice().getMarque(),
                    r.getCause(),
                    r.getPrixTotal(),   // ✅ plus de getPrix()
                    r.getAvance(),
                    r.getReste()
            });
        }
    }
}
