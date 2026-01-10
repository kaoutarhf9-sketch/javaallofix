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
import java.time.format.DateTimeFormatter; // ✅ Nouvel import pour LocalDate
import java.util.List;

public class ViewListeReparation extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private GestionReparation gestionReparation;
    private TableRowSorter<DefaultTableModel> sorter;
    
    // ✅ UTILISATION DE DATETIMEFORMATTER (Pour LocalDate)
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ViewListeReparation() {
        // Init métier
        gestionReparation = new GestionReparation();

        // Config Panel
        setLayout(new BorderLayout(10, 10));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. BARRE DE RECHERCHE
        add(createSearchPanel(), BorderLayout.NORTH);

        // 2. TABLEAU
        add(createTablePanel(), BorderLayout.CENTER);

        // 3. CHARGEMENT INITIAL
        loadData();
    }

    // =========================================================================
    // 1. COMPOSANTS UI
    // =========================================================================

    private JPanel createSearchPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        
        JLabel lblSearch = new JLabel("🔍 Rechercher :");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSearch.setForeground(Theme.TEXT_BODY);
        
        JTextField txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(250, 35));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { search(txtSearch.getText()); }
            public void removeUpdate(DocumentEvent e) { search(txtSearch.getText()); }
            public void changedUpdate(DocumentEvent e) { search(txtSearch.getText()); }
        });

        topPanel.add(lblSearch);
        topPanel.add(txtSearch);
        return topPanel;
    }

    private JScrollPane createTablePanel() {
        String[] columns = {
            "ID", "Client", "Appareil", "Marque", 
            "Cause", "Date Dépôt", "Total", "Avance", "Reste"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // --- STYLING ---
        table.setRowHeight(35);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(new Color(100, 116, 139));
        header.setPreferredSize(new Dimension(0, 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                }
                this.setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scroll.getViewport().setBackground(Color.WHITE);
        
        return scroll;
    }

    // =========================================================================
    // 2. LOGIQUE MÉTIER
    // =========================================================================

    public void refreshTable() {
        loadData();
        revalidate();
        repaint();
    }

    private void loadData() {
        model.setRowCount(0);

        List<Reparation> liste = gestionReparation.findAll();
        
        if (liste != null) {
            for (Reparation r : liste) {
                String nomClient = "Inconnu";
                String typeDevice = "-";
                String marqueDevice = "-";

                if (r.getDevice() != null) {
                    typeDevice = r.getDevice().getType();
                    marqueDevice = r.getDevice().getMarque();
                    if (r.getDevice().getClient() != null) {
                        nomClient = r.getDevice().getClient().getNom() + " " + r.getDevice().getClient().getPrenom();
                    }
                }

                // ✅ CHANGEMENT ICI : Formatage LocalDate
                String dateStr = "-";
                if (r.getDateDepot() != null) {
                    // La méthode .format() appartient maintenant à LocalDate
                    dateStr = r.getDateDepot().format(dateFormatter);
                }

                Double prix = (r.getPrixTotal() != null) ? r.getPrixTotal() : 0.0;
                Double avance = (r.getAvance() != null) ? r.getAvance() : 0.0;
                Double reste = (r.getReste() != null) ? r.getReste() : 0.0;
                String cause = (r.getCause() != null) ? r.getCause() : "-";

                model.addRow(new Object[]{
                    "#" + r.getIdReparation(),
                    nomClient,
                    typeDevice,
                    marqueDevice,
                    cause,
                    dateStr, // Affiche la date formatée
                    formatMoney(prix),
                    formatMoney(avance),
                    formatMoney(reste)
                });
            }
        }
    }

    private String formatMoney(Double amount) {
        if (amount == 0) return "-";
        return String.format("%.2f Dh", amount);
    }

    private void search(String text) {
        if (text.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }
}