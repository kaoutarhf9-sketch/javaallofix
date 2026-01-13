package presentation;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import dao.Boutique;
import metier.GestionBoutique;

public class ViewBoutique extends JPanel {

    private ModernMainFrame frame;
    private GestionBoutique metier;
    
    // Composants
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtSearch;

    public ViewBoutique(ModernMainFrame frame) {
        this.frame = frame;
        
        try {
            this.metier = new GestionBoutique();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur système : " + e.getMessage());
        }

        // 1. CONFIGURATION GLOBALE
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND); 
        // Marge externe pour que la vue ne colle pas aux bords
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // 2. EN-TÊTE (Titre + Actions)
        add(createHeader(), BorderLayout.NORTH);
        
        // 3. TABLEAU (Dans une carte blanche)
        add(createTableCard(), BorderLayout.CENTER);
        
        // 4. Chargement des données
        refreshTable();
    }

    // =============================================================
    // 1. EN-TÊTE : Titre à gauche, Recherche + Bouton à droite
    // =============================================================
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0)); // Espace sous le header

        // --- GAUCHE : TITRES ---
        JPanel titles = new JPanel(new GridLayout(2, 1));
        titles.setOpaque(false);
        
        JLabel title = new JLabel("Mes Boutiques");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Theme.TEXT_HEADLINE);
        
        JLabel subtitle = new JLabel("Gérez vos points de vente et leurs informations.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(Theme.TEXT_BODY);
        
        titles.add(title);
        titles.add(subtitle);

        // --- DROITE : ACTIONS ---
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionsPanel.setOpaque(false);

        // 1. Barre de Recherche (Style Input Moderne)
        JPanel searchBox = new JPanel(new BorderLayout());
        searchBox.setBackground(Color.WHITE);
        searchBox.setPreferredSize(new Dimension(280, 45));
        searchBox.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(226, 232, 240), 1, true),
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        JLabel iconSearch = new JLabel("🔍 ");
        iconSearch.setForeground(Color.GRAY);
        
        txtSearch = new JTextField();
        txtSearch.setBorder(null);
        txtSearch.setOpaque(false);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setForeground(Theme.TEXT_HEADLINE);
        // Placeholder simulation
        txtSearch.setText("Rechercher une boutique...");
        txtSearch.setForeground(Color.GRAY);
        
        // Gestion Placeholder + Filtre
        txtSearch.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if(txtSearch.getText().equals("Rechercher une boutique...")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(Theme.TEXT_HEADLINE);
                }
                searchBox.setBorder(new LineBorder(Theme.PRIMARY, 1, true)); // Highlight bordure
            }
            public void focusLost(FocusEvent e) {
                if(txtSearch.getText().isEmpty()) {
                    txtSearch.setText("Rechercher une boutique...");
                    txtSearch.setForeground(Color.GRAY);
                }
                searchBox.setBorder(new LineBorder(new Color(226, 232, 240), 1, true));
            }
        });

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
            void filter() {
                String text = txtSearch.getText();
                if (text.equals("Rechercher une boutique...") || text.trim().isEmpty()) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        searchBox.add(iconSearch, BorderLayout.WEST);
        searchBox.add(txtSearch, BorderLayout.CENTER);

        // 2. Bouton Ajouter (Bleu)
        JButton btnAdd = UIFactory.createGradientButton("Ajouter");
        btnAdd.setPreferredSize(new Dimension(140, 45));
        btnAdd.addActionListener(e -> frame.changerVue(ModernMainFrame.VUE_FORM_BOUTIQUE));

        actionsPanel.add(searchBox);
        actionsPanel.add(btnAdd);

        header.add(titles, BorderLayout.WEST);
        header.add(actionsPanel, BorderLayout.EAST);
        
        return header;
    }

    // =============================================================
    // 2. TABLEAU DANS UNE CARTE BLANCHE
    // =============================================================
    private JPanel createTableCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        // Bordure fine grise pour l'effet "Card"
        card.setBorder(new LineBorder(new Color(226, 232, 240), 1, true)); 

        String[] columns = {"ID", "ENSEIGNE", "ADRESSE", "PATENTE", "ACTIONS"};
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // --- STYLE TABLEAU ---
        table.setRowHeight(60); // Lignes hautes
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(241, 245, 249)); // Lignes horizontales très claires
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(239, 246, 255)); // Bleu très pâle au survol/selection
        table.setSelectionForeground(Theme.TEXT_HEADLINE);
        table.setFocusable(false);
        
        // Header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(248, 250, 252)); // Gris fond header
        header.setForeground(new Color(100, 116, 139)); // Gris texte header
        header.setPreferredSize(new Dimension(0, 50));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        // Alignement et Padding des cellules
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (c instanceof JComponent) {
                    ((JComponent) c).setBorder(new EmptyBorder(0, 20, 0, 20)); // Padding latéral
                }
                return c;
            }
        };
        
        // ID et Patente centrés, le reste à gauche
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        for(int i=0; i<table.getColumnCount(); i++) {
            if(i==0 || i==3) table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            else if(i!=4) table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        // --- ACTIONS RENDERER (Boutons) ---
        table.getColumnModel().getColumn(4).setCellRenderer(new ActionsRenderer());
        table.getColumnModel().getColumn(4).setMinWidth(180);

        // Gestion du clic sur les boutons d'action
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (row >= 0 && col == 4) {
                    int modelRow = table.convertRowIndexToModel(row);
                    int idBoutique = Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString());
                    
                    Rectangle rect = table.getCellRect(row, col, true);
                    int x = e.getX() - rect.x;
                    
                    // Zone Gauche = Modifier (Bleu), Droite = Supprimer (Rouge)
                    if (x < rect.width / 2) modifierBoutique(idBoutique);
                    else supprimerBoutique(idBoutique);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder()); // Supprimer double bordure
        scroll.getViewport().setBackground(Color.WHITE);
        
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    // =============================================================
    // 3. LOGIQUE MÉTIER
    // =============================================================
    public void refreshTable() {
        if (metier == null || frame.getCurrentUser() == null) return;

        // SwingWorker pour ne pas figer l'interface si beaucoup de données
        new SwingWorker<List<Boutique>, Void>() {
            @Override
            protected List<Boutique> doInBackground() throws Exception {
                return metier.listerBoutiquesDuProprietaire(frame.getCurrentUser().getIdU());
            }

            @Override
            protected void done() {
                try {
                    List<Boutique> liste = get();
                    tableModel.setRowCount(0);
                    if (liste != null) {
                        for (Boutique b : liste) {
                            tableModel.addRow(new Object[]{
                                b.getIdb(), 
                                b.getNomB(), 
                                b.getAdresse(), 
                                b.getPatente(), 
                                "" // Placeholder Actions
                            });
                        }
                    }
                } catch (Exception e) { e.printStackTrace(); }
            }
        }.execute();
    }

    private void modifierBoutique(int id) {
        Boutique b = metier.obtenirBoutique(id);
        if (b != null) frame.ouvrirModificationBoutique(b);
    }

    private void supprimerBoutique(int id) {
        int choix = JOptionPane.showConfirmDialog(this, 
            "Confirmer la suppression de cette boutique ?", 
            "Attention", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (choix == JOptionPane.YES_OPTION) {
            try {
                metier.supprimerBoutique(id);
                refreshTable();
                JOptionPane.showMessageDialog(this, "Boutique supprimée.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
            }
        }
    }

    // =============================================================
    // 4. RENDERER ACTIONS (BOUTONS PLATS "PILL")
    // =============================================================
    class ActionsRenderer extends JPanel implements TableCellRenderer {
        public ActionsRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 12));
            setBackground(Color.WHITE);
            // Badge Modifier (Bleu clair texte bleu)
            add(createBadge("Modifier", new Color(239, 246, 255), Theme.PRIMARY));
            // Badge Supprimer (Rouge clair texte rouge)
            add(createBadge("Supprimer", new Color(254, 242, 242), new Color(220, 38, 38)));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            // Garder le fond blanc ou sélectionné
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return this;
        }

        private JLabel createBadge(String text, Color bg, Color fg) {
            JLabel lbl = new JLabel(text);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setOpaque(true);
            lbl.setBackground(bg);
            lbl.setForeground(fg);
            lbl.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Padding interne
            return lbl;
        }
    }
}