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
        
        // 1. Métier
        try {
            this.metier = new GestionBoutique();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur système : " + e.getMessage());
        }

        // 2. Configuration Page
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND); // Gris clair (Slate 50)
        setBorder(new EmptyBorder(30, 50, 30, 50));

        // 3. Structure
        add(createHeader(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        
        // 4. Chargement
        refreshTable();
    }

    // =============================================================
    // 1. EN-TÊTE : Titre à gauche, Recherche + Bouton à droite
    // =============================================================
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 20));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 25, 0));

        // A. TITRES (Gauche)
        JPanel titles = new JPanel(new GridLayout(2, 1));
        titles.setOpaque(false);
        
        JLabel title = new JLabel("Mes Boutiques");
        title.setFont(Theme.FONT_HERO); 
        title.setForeground(Theme.TEXT_HEADLINE);
        
        JLabel subtitle = new JLabel("Gérez vos établissements et suivez leur activité.");
        subtitle.setFont(Theme.FONT_REGULAR);
        subtitle.setForeground(Theme.TEXT_BODY);
        
        titles.add(title);
        titles.add(subtitle);

        // B. ACTIONS (Droite : Recherche + Bouton)
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionsPanel.setOpaque(false);

        // -- Barre de Recherche Design --
        JPanel searchContainer = new JPanel(new BorderLayout());
        searchContainer.setBackground(Color.WHITE);
        searchContainer.setPreferredSize(new Dimension(250, 40));
        searchContainer.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(226, 232, 240), 1, true), // Bordure grise arrondie
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        JLabel iconSearch = new JLabel("🔍 ");
        iconSearch.setForeground(Color.GRAY);
        
        txtSearch = new JTextField();
        txtSearch.setBorder(null); // Pas de bordure moche interne
        txtSearch.setOpaque(false);
        txtSearch.setFont(Theme.FONT_REGULAR);
        
        // Logique de filtre
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
            void filter() {
                String text = txtSearch.getText();
                if (text.trim().length() == 0) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        searchContainer.add(iconSearch, BorderLayout.WEST);
        searchContainer.add(txtSearch, BorderLayout.CENTER);

        // -- Bouton Nouveau --
        JButton btnAdd = UIFactory.createGradientButton("+ Nouvelle Boutique");
        btnAdd.setPreferredSize(new Dimension(180, 40));
        btnAdd.addActionListener(e -> frame.changerVue(ModernMainFrame.VUE_FORM_BOUTIQUE));

        actionsPanel.add(searchContainer);
        actionsPanel.add(btnAdd);

        // Assemblage Header
        header.add(titles, BorderLayout.WEST);
        header.add(actionsPanel, BorderLayout.EAST); // Actions alignées à droite
        
        return header;
    }

    // =============================================================
    // 2. TABLEAU : Design "Clean" & "Flat"
    // =============================================================
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        // Bordure très subtile pour l'effet "Carte"
        panel.setBorder(BorderFactory.createLineBorder(new Color(230, 232, 235), 1));

        String[] columns = {"ID", "ENSEIGNE", "ADRESSE", "PATENTE", "ACTIONS"};
        
        // Modèle non éditable
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false; 
            }
        };

        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // --- STYLE PREMIUM ---
        table.setRowHeight(60); 
        table.setShowVerticalLines(false); // Pas de lignes verticales (Important pour le look)
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setFocusable(false);
        
        // Header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(248, 250, 252)); // Gris très clair
        header.setForeground(new Color(100, 116, 139)); // Gris texte
        header.setPreferredSize(new Dimension(0, 50));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        // Sélection
        table.setSelectionBackground(new Color(241, 245, 249)); // Bleu/Gris pâle
        table.setSelectionForeground(Theme.TEXT_HEADLINE);

        // Alignement Centré (ID et Patente)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        // Padding (Marge interne) pour le texte (Enseigne et Adresse)
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (c instanceof JComponent) ((JComponent) c).setBorder(new EmptyBorder(0, 20, 0, 20));
                if (!isSelected) c.setBackground(Color.WHITE);
                return c;
            }
        });

        // --- COLONNE ACTIONS ---
        table.getColumnModel().getColumn(4).setCellRenderer(new ActionsRenderer());
        table.getColumnModel().getColumn(4).setMinWidth(200);

        // Logique de Clic (Simple pour le prof)
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (row >= 0 && col == 4) {
                    int idBoutique = Integer.parseInt(table.getValueAt(row, 0).toString());
                    
                    // Calcul position clic (Gauche = Modifier, Droite = Supprimer)
                    Rectangle rect = table.getCellRect(row, col, true);
                    int x = e.getX() - rect.x;
                    
                    if (x < rect.width / 2) {
                        modifierBoutique(idBoutique);
                    } else {
                        supprimerBoutique(idBoutique);
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // =============================================================
    // 3. LOGIQUE MÉTIER
    // =============================================================
    public void refreshTable() {
        if (metier == null || frame.getCurrentUser() == null) return;

        tableModel.setRowCount(0);
        List<Boutique> liste = metier.listerBoutiquesDuProprietaire(frame.getCurrentUser().getIdU());
        
        if (liste != null) {
            for (Boutique b : liste) {
                tableModel.addRow(new Object[]{
                    b.getIdb(), 
                    b.getNomB(), 
                    b.getAdresse(), 
                    b.getPatente(), 
                    "" // Vide (Géré par Renderer)
                });
            }
        }
    }

    private void modifierBoutique(int id) {
        Boutique b = metier.obtenirBoutique(id);
        if (b != null) frame.ouvrirModificationBoutique(b);
    }

    private void supprimerBoutique(int id) {
        int choix = JOptionPane.showConfirmDialog(this, "Supprimer cette boutique ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (choix == JOptionPane.YES_OPTION) {
            metier.supprimerBoutique(id);
            refreshTable();
        }
    }

    // =============================================================
    // 4. RENDERER ACTIONS (Juste pour le visuel)
    // =============================================================
    class ActionsRenderer extends JPanel implements TableCellRenderer {
        public ActionsRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 8, 12));
            setBackground(Color.WHITE);
            add(createBadge("Modifier", new Color(238, 242, 255), Theme.PRIMARY));
            add(createBadge("Supprimer", new Color(254, 242, 242), Color.RED));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return this;
        }

        private JLabel createBadge(String text, Color bg, Color fg) {
            JLabel lbl = new JLabel(text, SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setOpaque(true);
            lbl.setBackground(bg);
            lbl.setForeground(fg);
            lbl.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12)); // Effet "Pill"
            return lbl;
        }
    }
}