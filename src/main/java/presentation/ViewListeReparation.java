package presentation;

import dao.Reparation;
import metier.GestionReparation;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class ViewListeReparation extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private GestionReparation gestionReparation;
    private TableRowSorter<DefaultTableModel> sorter;
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ViewListeReparation() {
        gestionReparation = new GestionReparation();

        setLayout(new BorderLayout(10, 10));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(createSearchPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);

        loadData();
    }

    // =========================================================================
    // 1. UI COMPONENTS
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

        // --- STYLING DESIGN ---
        table.setRowHeight(35);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(false); // On gère les lignes nous-mêmes
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(new Color(100, 116, 139));
        header.setPreferredSize(new Dimension(0, 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 200, 200)));

        // ✅ APPLICATION DU RENDERER INTELLIGENT
        table.setDefaultRenderer(Object.class, new ClientGroupRenderer());

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
            // ✅ TRI : On trie par ID Client (ou Date) pour que les groupes soient collés
            // On trie d'abord par Date (descendant) puis par Client
            liste.sort(Comparator.comparing(Reparation::getIdReparation).reversed());

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

                String dateStr = (r.getDateDepot() != null) ? r.getDateDepot().format(dateFormatter) : "-";
                Double prix = (r.getPrixTotal() != null) ? r.getPrixTotal() : 0.0;
                Double avance = (r.getAvance() != null) ? r.getAvance() : 0.0;
                Double reste = (r.getReste() != null) ? r.getReste() : 0.0;
                String cause = (r.getCause() != null) ? r.getCause() : "-";

                model.addRow(new Object[]{
                    "#" + r.getIdReparation(),
                    nomClient, // Col 1 : Clé de regroupement
                    typeDevice,
                    marqueDevice,
                    cause,
                    dateStr,
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

    // =========================================================================
    // 🔥 CLASSE INTERNE : LE RENDERER INTELLIGENT
    // =========================================================================
    // C'est cette classe qui gère l'affichage visuel des groupes
    private class ClientGroupRenderer extends DefaultTableCellRenderer {
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                                                       boolean isSelected, boolean hasFocus, 
                                                       int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            // Centrage du texte
            setHorizontalAlignment(JLabel.CENTER);

            // Récupération des infos pour comparer avec la ligne précédente et suivante
            String currentClient = (String) table.getValueAt(row, 1); // Col 1 = Nom Client
            
            String prevClient = "";
            if (row > 0) {
                prevClient = (String) table.getValueAt(row - 1, 1);
            }

            String nextClient = "";
            if (row < table.getRowCount() - 1) {
                nextClient = (String) table.getValueAt(row + 1, 1);
            }

            boolean isSameAsPrev = currentClient.equals(prevClient);
            boolean isSameAsNext = currentClient.equals(nextClient);

            // --- 1. GESTION DES COULEURS DE FOND ---
            if (!isSelected) {
                c.setBackground(Color.WHITE);
                c.setForeground(Color.BLACK);
            }

            // --- 2. GESTION DU TEXTE "FANTÔME" (Fusion visuelle) ---
            // Si c'est le même client que la ligne d'avant, on cache le nom et la date
            // pour alléger l'affichage et montrer que ça appartient au bloc du dessus.
            if (isSameAsPrev && !isSelected) {
                if (column == 1 || column == 5) { // 1 = Client, 5 = Date
                    setText(""); 
                }
            }

            // --- 3. GESTION DES BORDURES (Séparateurs) ---
            JComponent comp = (JComponent) c;
            
            if (!isSameAsNext) {
                // Si le client suivant est différent => GROSSE LIGNE DE SÉPARATION
                comp.setBorder(new MatteBorder(0, 0, 2, 0, new Color(200, 200, 200)));
            } else {
                // Si le client suivant est le même => Pas de bordure ou bordure très fine pointillée
                comp.setBorder(new MatteBorder(0, 0, 0, 0, Color.WHITE));
            }
            
            // Si c'est la toute dernière ligne du tableau
            if (row == table.getRowCount() - 1) {
                 comp.setBorder(new MatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));
            }

            return c;
        }
    }
}