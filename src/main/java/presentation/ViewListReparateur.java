package presentation;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import dao.Proprietaire;
import dao.Reparateur;
import metier.GestionReparateur;
import metier.IGestionReparateur;

public class ViewListReparateur extends JPanel {

    private ModernMainFrame frame;
    private IGestionReparateur metier;
    
    // Composants
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtSearch;

    public ViewListReparateur(ModernMainFrame frame) {
        this.frame = frame;
        
        // 1. Init Métier
        try { 
            this.metier = new GestionReparateur(); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur système : " + e.getMessage());
        }

        // 2. Config Page
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND); // Gris clair
        setBorder(new EmptyBorder(30, 50, 30, 50)); // Marges externes

        // 3. Structure
        add(createHeader(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        
        // 4. Chargement
        refreshTable();
    }

    // =============================================================
    // 1. EN-TÊTE (Titre + Recherche + Bouton)
    // =============================================================
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 20));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        // --- HAUT : Titre & Bouton ---
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        
        JLabel title = new JLabel("Gestion des Réparateurs");
        title.setFont(Theme.FONT_HERO); 
        title.setForeground(Theme.TEXT_HEADLINE);
        
        // Bouton Ajout
        JButton btnAdd = UIFactory.createGradientButton("+ Nouveau Réparateur");
        btnAdd.addActionListener(e -> frame.changerVue(ModernMainFrame.VUE_FORM_REPARATEUR));

        top.add(title, BorderLayout.WEST);
        top.add(btnAdd, BorderLayout.EAST);

        // --- BAS : Recherche ---
        txtSearch = new JTextField(20);
        txtSearch.setPreferredSize(new Dimension(300, 40));
        txtSearch.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        // Logique de filtre (Copier-coller de ViewBoutique)
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

        header.add(top, BorderLayout.NORTH);
        header.add(txtSearch, BorderLayout.SOUTH);
        return header;
    }

    // =============================================================
    // 2. TABLEAU (Style Simple & Propre)
    // =============================================================
    private JPanel createTablePanel() {
        // Fond blanc simple (Pas de paintComponent compliqué)
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));

        // Colonnes (Ajout de ACTIONS)
        String[] columns = {"ID", "NOM COMPLET", "EMAIL", "BOUTIQUE", "COM %", "TEL", "ACTIONS"};
        
        // Modèle lecture seule
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false; 
            }
        };

        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // --- STYLE ---
        table.setRowHeight(60); // Même hauteur que Boutique
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Header
        JTableHeader h = table.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 13));
        h.setBackground(new Color(248, 250, 252));
        h.setForeground(new Color(100, 116, 139));
        h.setPreferredSize(new Dimension(0, 50));

        // Centrage ID, Commission et Tel
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Commission
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Tel

        // --- ACTIONS (Logique Simple MouseListener) ---
        table.getColumnModel().getColumn(6).setCellRenderer(new ActionsRenderer());
        table.getColumnModel().getColumn(6).setMinWidth(180);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                // Si clic colonne ACTIONS (Index 6)
                if (row >= 0 && col == 6) {
                    // On récupère l'ID (colonne 0)
                    // Note: Il faut parser car c'est un String "#12"
                    String idStr = table.getValueAt(row, 0).toString().replace("#", "");
                    int idReparateur = Integer.parseInt(idStr);
                    
                    // Calcul zone de clic
                    int clickX = e.getX() - table.getCellRect(row, col, true).x;
                    int width = table.getColumnModel().getColumn(col).getWidth();
                    
                    if (clickX < width / 2) {
                        modifierReparateur(idReparateur);
                    } else {
                        supprimerReparateur(idReparateur);
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
        
        if (frame.getCurrentUser() instanceof Proprietaire) {
            Proprietaire p = (Proprietaire) frame.getCurrentUser();
            tableModel.setRowCount(0); 
            
            List<Reparateur> list = metier.listerReparateursParProprietaire(p.getIdU());
            if (list != null) {
                for (Reparateur r : list) {
                    String nomBoutique = (r.getBoutique() != null) ? r.getBoutique().getNomB() : "-";
                    
                    tableModel.addRow(new Object[]{
                        "#" + r.getIdU(),
                        r.getNom().toUpperCase() + " " + r.getPrenom(),
                        r.getEmail(),
                        nomBoutique,
                        r.getPourcentage() + "%",
                        r.getNumtel(),
                        "" // Vide pour ActionsRenderer
                    });
                }
            }
        }
    }
    
    // Méthodes pour le prof (Actions)
    private void supprimerReparateur(int id) {
        int choix = JOptionPane.showConfirmDialog(this, 
            "Êtes-vous sûr de vouloir supprimer ce réparateur ?\nCette action est irréversible.", 
            "Confirmation", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);

        if (choix == JOptionPane.YES_OPTION) {
            try {
                metier.supprimerReparateur(id);
                refreshTable(); // On rafraîchit le tableau immédiatement
                JOptionPane.showMessageDialog(this, "Réparateur supprimé avec succès.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression : " + e.getMessage());
            }
        }
    }

    private void modifierReparateur(int id) {
        // 1. On récupère l'objet complet depuis la base
        Reparateur r = metier.obtenirReparateur(id);
        
        if (r != null) {
            // 2. On demande à la Frame d'ouvrir le formulaire en mode "Edition"
            // Assurez-vous d'avoir ajouté cette méthode dans ModernMainFrame !
            frame.ouvrirModificationReparateur(r); 
        } else {
            JOptionPane.showMessageDialog(this, "Erreur : Impossible de récupérer les données du réparateur.");
        }
    }

    // =============================================================
    // 4. VISUEL DES BOUTONS (Renderer)
    // =============================================================
    class ActionsRenderer extends JPanel implements TableCellRenderer {
        public ActionsRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
            setBackground(Color.WHITE);
            add(createLabelBtn("Modifier", new Color(235, 240, 255), Theme.PRIMARY));
            add(createLabelBtn("Supprimer", new Color(255, 240, 240), Color.RED));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return this;
        }
        
        private JLabel createLabelBtn(String text, Color bg, Color fg) {
            JLabel lbl = new JLabel(text, SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setOpaque(true);
            lbl.setBackground(bg);
            lbl.setForeground(fg);
            lbl.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            return lbl;
        }
    }
}