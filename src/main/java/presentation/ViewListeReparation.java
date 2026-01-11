package presentation;

import dao.Reparation;
import metier.GestionReparation;
import metier.EtatReparation;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ViewListeReparation extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private GestionReparation gestionReparation;
    private TableRowSorter<DefaultTableModel> sorter;

    private JComboBox<EtatReparation> cbEtat;
    private JButton btnChangerEtat;

    private List<Reparation> cacheReparations;

    public ViewListeReparation() {

        gestionReparation = new GestionReparation();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Historique des réparations"));

        // ================= TOP PANEL =================
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel lblSearch = new JLabel("🔍 Rechercher");
        JTextField txtSearch = new JTextField(15);

        cbEtat = new JComboBox<>(EtatReparation.values());
        btnChangerEtat = new JButton("Changer état");

        topPanel.add(lblSearch);
        topPanel.add(txtSearch);
        topPanel.add(new JLabel("État :"));
        topPanel.add(cbEtat);
        topPanel.add(btnChangerEtat);

        add(topPanel, BorderLayout.NORTH);

        // ================= MODEL =================
        model = new DefaultTableModel(
                new String[]{
                        "Code Client", "Nom", "Prénom",
                        "Type", "Marque", "Cause",
                        "Total", "Avance", "Reste", "État"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(220, 235, 250));

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // ================= SEARCH =================
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

        // ================= ACTION =================
        btnChangerEtat.addActionListener(e -> changerEtat());

        loadData();
    }

    // ================= LOAD =================
    public void loadData() {
        model.setRowCount(0);
        cacheReparations = gestionReparation.findAll();

        for (Reparation r : cacheReparations) {
            model.addRow(new Object[]{
                    r.getDevice().getClient().getCodeClient(),
                    r.getDevice().getClient().getNom(),
                    r.getDevice().getClient().getPrenom(),
                    r.getDevice().getType(),
                    r.getDevice().getMarque(),
                    r.getCause(),
                    r.getPrixTotal(),
                    r.getAvance(),
                    r.getReste(),
                    r.getEtat()
            });
        }
    }

    // ================= CHANGE STATE =================
    private void changerEtat() {

        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner une réparation");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        Reparation r = cacheReparations.get(modelRow);

        EtatReparation nouvelEtat =
                (EtatReparation) cbEtat.getSelectedItem();

        r.setEtat(nouvelEtat);
        gestionReparation.update(r);

        refreshTable();

        JOptionPane.showMessageDialog(this,
                "État mis à jour : " + nouvelEtat);
    }

    // ================= REFRESH =================
    public void refreshTable() {
        loadData();
        revalidate();
        repaint();
    }
}
