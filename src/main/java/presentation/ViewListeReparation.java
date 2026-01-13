package presentation;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import dao.Reparation;
import dao.Client;
import metier.GestionReparation;
import metier.EtatReparation;

public class ViewListeReparation extends JPanel {

    private ModernMainFrame frame;
    private GestionReparation metier;
    
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    
    private List<Reparation> cacheReparations;

    // Couleurs Design
    private final Color HEADER_BG = new Color(30, 41, 59); 
    private final Color HEADER_TXT = Color.WHITE;
    private final Color ROW_SELECTED = new Color(224, 231, 255); 
    private final Color GRID_COLOR = new Color(241, 245, 249); 

    public ViewListeReparation(ModernMainFrame frame) {
        this.frame = frame;
        this.cacheReparations = new ArrayList<>();
        
        try { this.metier = new GestionReparation(); } catch (Exception e) {}

        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        setBorder(new EmptyBorder(30, 40, 30, 40)); 

        add(createTopBar(), BorderLayout.NORTH);
        add(createTableCard(), BorderLayout.CENTER);

        refreshTable();
    }

    // ... [createTopBar() reste identique au code précédent] ...
    private JPanel createTopBar() {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(0, 0, 20, 0));
        JLabel title = new JLabel("Suivi des Réparations");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Theme.TEXT_HEADLINE);
        
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setPreferredSize(new Dimension(300, 40));
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(203, 213, 225), 1, true), new EmptyBorder(5, 10, 5, 10)));

        JTextField txtSearch = new JTextField("Rechercher un client, une panne...");
        txtSearch.setBorder(null); txtSearch.setOpaque(false); txtSearch.setForeground(Color.GRAY); txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        txtSearch.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { if(txtSearch.getText().startsWith("Rechercher")) { txtSearch.setText(""); txtSearch.setForeground(Theme.TEXT_HEADLINE); } searchPanel.setBorder(new LineBorder(Theme.PRIMARY, 1, true)); }
            public void focusLost(FocusEvent e) { if(txtSearch.getText().isEmpty()) { txtSearch.setText("Rechercher un client, une panne..."); txtSearch.setForeground(Color.GRAY); } searchPanel.setBorder(new LineBorder(new Color(203, 213, 225), 1, true)); }
        });
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(txtSearch.getText()); }
            public void removeUpdate(DocumentEvent e) { filter(txtSearch.getText()); }
            public void changedUpdate(DocumentEvent e) { filter(txtSearch.getText()); }
        });
        searchPanel.add(new JLabel("🔍  "), BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        container.add(title, BorderLayout.WEST);
        container.add(searchPanel, BorderLayout.EAST);
        return container;
    }
    
    private void filter(String text) {
        if (text.startsWith("Rechercher") || text.trim().isEmpty()) sorter.setRowFilter(null);
        else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
    }

    // =============================================================
    // 2. TABLEAU AVEC INTERACTION "DOUBLE-CLIC"
    // =============================================================
    private JPanel createTableCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createMatteBorder(1, 1, 2, 1, new Color(226, 232, 240)));

        String[] columns = {"CODE CLIENT", "CLIENT", "APPAREIL", "PANNE", "DATE", "PRIX", "AVANCE", "RESTE", "ÉTAT", "ID_REP"};
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // Style Global
        table.setRowHeight(55);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(GRID_COLOR);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(ROW_SELECTED);
        table.setSelectionForeground(Color.BLACK);
        
        // Header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(HEADER_BG);
        header.setForeground(HEADER_TXT);
        header.setPreferredSize(new Dimension(0, 50));
        header.setReorderingAllowed(false);
        
        // Renderers
        table.getColumnModel().getColumn(0).setCellRenderer(new GroupedCellRenderer());
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int i=4; i<=7; i++) table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(8).setCellRenderer(new RoundedStatusRenderer());
        
        // Masquer ID
        table.getColumnModel().getColumn(9).setMinWidth(0); table.getColumnModel().getColumn(9).setMaxWidth(0); table.getColumnModel().getColumn(9).setWidth(0);

        // 🔥 INTERACTION : OUVRIR LA FICHE CLIENT (DOUBLE CLIC)
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Double clic
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        int modelRow = table.convertRowIndexToModel(row);
                        int idRep = (int) tableModel.getValueAt(modelRow, 9);
                        showClientDetails(idRep);
                    }
                }
            }
        });

        // Menu Clic Droit (inchangé)
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem itemTermine = new JMenuItem("Marquer comme TERMINÉ"); itemTermine.addActionListener(e -> updateStatusFromMenu(EtatReparation.TERMINEE));
        JMenuItem itemEnCours = new JMenuItem("Marquer comme EN COURS"); itemEnCours.addActionListener(e -> updateStatusFromMenu(EtatReparation.EN_COURS));
        JMenuItem itemEnAttente = new JMenuItem("Marquer comme EN ATTENTE"); itemEnAttente.addActionListener(e -> updateStatusFromMenu(EtatReparation.EN_ATTENTE));
        popupMenu.add(itemEnCours); popupMenu.add(itemTermine); popupMenu.addSeparator(); popupMenu.add(itemEnAttente);
        table.setComponentPopupMenu(popupMenu);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    // =============================================================
    // 🔥 LOGIQUE D'AFFICHAGE DE LA FICHE CLIENT
    // =============================================================
    private void showClientDetails(int idReparation) {
        // 1. Trouver la réparation et le client associé
        Reparation target = null;
        for(Reparation r : cacheReparations) {
            if(r.getIdReparation() == idReparation) { target = r; break; }
        }

        if(target != null && target.getDevice() != null && target.getDevice().getClient() != null) {
            Client c = target.getDevice().getClient();
            // 2. Ouvrir la fenêtre modale
            new ClientInfoDialog(frame, c).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Aucune information client disponible.");
        }
    }

    public void refreshTable() {
        if (metier == null) return;
        new SwingWorker<List<Reparation>, Void>() {
            @Override protected List<Reparation> doInBackground() throws Exception { return metier.findAll(); }
            @Override protected void done() {
                try {
                    cacheReparations = get(); tableModel.setRowCount(0);
                    if (cacheReparations != null) {
                        for (Reparation r : cacheReparations) {
                            String clientNom = "N/A"; String appareil = "N/A"; String codeClient = "N/A";
                            if(r.getDevice() != null) {
                                appareil = r.getDevice().getMarque() + " " + r.getDevice().getType();
                                if(r.getDevice().getClient() != null) {
                                    codeClient = r.getDevice().getClient().getCodeClient();
                                    clientNom = r.getDevice().getClient().getNom() + " " + r.getDevice().getClient().getPrenom();
                                }
                            }
                            tableModel.addRow(new Object[]{
                                codeClient, clientNom, appareil, r.getCause(), r.getDateDepot(),
                                String.format("%.2f Dh", r.getPrixTotal()), String.format("%.2f Dh", r.getAvance()), String.format("%.2f Dh", r.getReste()),
                                r.getEtat(), r.getIdReparation()
                            });
                        }
                        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
                        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
                        sorter.setSortKeys(sortKeys);
                    }
                } catch (Exception e) { e.printStackTrace(); }
            }
        }.execute();
    }

    private void updateStatusFromMenu(EtatReparation newStatus) {
        int row = table.getSelectedRow();
        if (row == -1) return;
        int modelRow = table.convertRowIndexToModel(row);
        int idRep = (int) tableModel.getValueAt(modelRow, 9);
        Reparation target = null;
        for(Reparation r : cacheReparations) { if(r.getIdReparation() == idRep) { target = r; break; } }
        if(target != null) {
            try { target.setEtat(newStatus); metier.update(target); tableModel.setValueAt(newStatus, modelRow, 8); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage()); }
        }
    }

    // =============================================================
    // 🔥 CLASSE INTERNE : POPUP FICHE CLIENT
    // =============================================================
    class ClientInfoDialog extends JDialog {
        public ClientInfoDialog(JFrame parent, Client client) {
            super(parent, "Fiche Client : " + client.getNom().toUpperCase(), true);
            setSize(450, 250);
            setLocationRelativeTo(parent);
            setResizable(false);
            
            JPanel content = new JPanel(new BorderLayout());
            content.setBackground(Color.WHITE);
            content.setBorder(new EmptyBorder(20, 20, 20, 20));

            // --- GAUCHE : PHOTO ---
            JLabel lblPhoto = new JLabel();
            lblPhoto.setPreferredSize(new Dimension(140, 140));
            lblPhoto.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
            lblPhoto.setHorizontalAlignment(SwingConstants.CENTER);
            lblPhoto.setOpaque(true);
            lblPhoto.setBackground(new Color(248, 250, 252));

            // Chargement Image
            if (client.getPhotoPath() != null) {
                File f = new File(client.getPhotoPath());
                if(f.exists()) {
                    ImageIcon icon = new ImageIcon(client.getPhotoPath());
                    Image img = icon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
                    lblPhoto.setIcon(new ImageIcon(img));
                } else {
                    lblPhoto.setText("Img introuvable");
                }
            } else {
                lblPhoto.setText("Pas de photo");
            }

            // --- DROITE : INFOS ---
            JPanel infoPanel = new JPanel(new GridLayout(5, 1, 0, 5));
            infoPanel.setOpaque(false);
            infoPanel.setBorder(new EmptyBorder(0, 20, 0, 0)); // Marge gauche

            infoPanel.add(createDetailLabel("CLIENT", client.getNom().toUpperCase() + " " + client.getPrenom()));
            infoPanel.add(createDetailLabel("CODE", client.getCodeClient()));
            infoPanel.add(createDetailLabel("TÉLÉPHONE", client.getTelephone()));
            infoPanel.add(createDetailLabel("EMAIL", (client.getEmail() != null && !client.getEmail().isEmpty()) ? client.getEmail() : "Non renseigné"));

            // Bouton Fermer
            JButton btnClose = new JButton("Fermer");
            btnClose.setFocusPainted(false);
            btnClose.addActionListener(e -> dispose());
            
            // Assemblage
            content.add(lblPhoto, BorderLayout.WEST);
            content.add(infoPanel, BorderLayout.CENTER);
            content.add(btnClose, BorderLayout.SOUTH);

            add(content);
        }

        private JLabel createDetailLabel(String title, String value) {
            return new JLabel("<html><font color='#64748b' size='3'><b>" + title + ":</b></font> <font color='#0f172a'>" + value + "</font></html>");
        }
    }

    // --- RENDERERS (Identiques) ---
    class GroupedCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSel, boolean hasFocus, int row, int col) {
            super.getTableCellRendererComponent(table, value, isSel, hasFocus, row, col);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(new Color(15, 23, 42));
            setBorder(new EmptyBorder(0, 15, 0, 0));
            if (row > 0) {
                Object prev = table.getValueAt(row - 1, col);
                if (value != null && value.equals(prev)) setText("");
            }
            return this;
        }
    }

    class RoundedStatusRenderer extends JPanel implements TableCellRenderer {
        private JLabel lblText;
        public RoundedStatusRenderer() { setOpaque(false); setLayout(new GridBagLayout()); lblText = new JLabel(); lblText.setFont(new Font("Segoe UI", Font.BOLD, 11)); add(lblText); }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            if (value instanceof EtatReparation) {
                EtatReparation etat = (EtatReparation) value;
                lblText.setText(etat.name().replace("_", " "));
                Color bg, fg;
                switch (etat) {
                    case TERMINEE: bg = new Color(220, 252, 231); fg = new Color(22, 163, 74); break;
                    case EN_COURS: bg = new Color(219, 234, 254); fg = new Color(37, 99, 235); break;
                    case EN_ATTENTE: bg = new Color(254, 249, 195); fg = new Color(202, 138, 4); break;
                    default: bg = new Color(241, 245, 249); fg = new Color(71, 85, 105); break;
                }
                this.putClientProperty("pill.bg", bg); lblText.setForeground(fg);
            }
            return this;
        }
        @Override protected void paintComponent(Graphics g) { g.setColor(getBackground()); g.fillRect(0, 0, getWidth(), getHeight()); Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); Color pillColor = (Color) getClientProperty("pill.bg"); if (pillColor != null) { g2.setColor(pillColor); int badgeW = 90; int badgeH = 26; int x = (getWidth() - badgeW) / 2; int y = (getHeight() - badgeH) / 2; g2.fillRoundRect(x, y, badgeW, badgeH, 20, 20); } g2.dispose(); super.paintComponent(g); }
    }
}