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
import dao.User;
import dao.Reparateur;
import dao.Proprietaire;
import metier.GestionReparation;
import metier.EtatReparation;

public class ViewListeReparation extends JPanel {

    private ModernMainFrame frame;
    private GestionReparation metier;
    
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    
    private List<Reparation> cacheReparations;
    private boolean modeHistorique; // true = Archives (Livrées + Annulées), false = En cours

    // Couleurs du Thème
    private final Color HEADER_BG = new Color(30, 41, 59); 
    private final Color HEADER_TXT = Color.WHITE;
    private final Color ROW_SELECTED = new Color(224, 231, 255); 
    private final Color GRID_COLOR = new Color(241, 245, 249); 

    public ViewListeReparation(ModernMainFrame frame, boolean modeHistorique) {
        this.frame = frame;
        this.modeHistorique = modeHistorique;
        this.cacheReparations = new ArrayList<>();
        
        try { this.metier = new GestionReparation(); } catch (Exception e) {}

        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        setBorder(new EmptyBorder(30, 40, 30, 40)); 

        add(createTopBar(), BorderLayout.NORTH);
        add(createTableCard(), BorderLayout.CENTER);

        refreshTable();
    }

    // --- 1. BARRE SUPÉRIEURE ---
    private JPanel createTopBar() {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Titre dynamique mis à jour
        String titreTxt = modeHistorique ? "Historique (Livrés & Annulés)" : "Atelier (En Cours)";
        JLabel title = new JLabel(titreTxt);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Theme.TEXT_HEADLINE);
        
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setPreferredSize(new Dimension(300, 40));
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(203, 213, 225), 1, true), new EmptyBorder(5, 10, 5, 10)));

        JTextField txtSearch = new JTextField("Rechercher un client, une panne...");
        txtSearch.setBorder(null); txtSearch.setOpaque(false); 
        txtSearch.setForeground(Color.GRAY); 
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
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

    // --- 2. TABLEAU CENTRAL ---
    private JPanel createTableCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createMatteBorder(1, 1, 2, 1, new Color(226, 232, 240)));

        String[] columns = {"CODE", "CLIENT", "APPAREIL", "PANNE", "DATE", "PRIX", "AVANCE", "RESTE", "ÉTAT", "ID_REP", "RÉPARATEUR", "ACTIONS"};
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { 
                return col == 11; // Seule la colonne Actions est cliquable
            }
        };

        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        table.setRowHeight(55);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(GRID_COLOR);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(ROW_SELECTED);
        table.setSelectionForeground(Color.BLACK);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(HEADER_BG);
        header.setForeground(HEADER_TXT);
        header.setPreferredSize(new Dimension(0, 50));
        header.setReorderingAllowed(false);
        
        table.getColumnModel().getColumn(0).setCellRenderer(new GroupedCellRenderer());
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int i=4; i<=7; i++) table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        
        table.getColumnModel().getColumn(8).setCellRenderer(new RoundedStatusRenderer());
        
        table.getColumnModel().getColumn(9).setMinWidth(0); table.getColumnModel().getColumn(9).setMaxWidth(0); table.getColumnModel().getColumn(9).setWidth(0);

        // --- CONFIGURATION ACTIONS ---
        if (!modeHistorique) {
            TableActionEvent event = new TableActionEvent() {
                @Override public void onStart(int row) { updateStatusFromButton(row, EtatReparation.EN_COURS); }
                @Override public void onBlock(int row) { updateStatusFromButton(row, EtatReparation.BLOQUEE); }
                @Override public void onFinish(int row) { updateStatusFromButton(row, EtatReparation.TERMINEE); }
                
                // Si on annule, ça part dans l'historique aussi
                @Override public void onCancel(int row) { 
                    int confirm = JOptionPane.showConfirmDialog(frame, "Voulez-vous vraiment annuler cette réparation ?", "Annulation", JOptionPane.YES_NO_OPTION);
                    if(confirm == JOptionPane.YES_OPTION) updateStatusFromButton(row, EtatReparation.ANNULEE);
                }
                
                @Override public void onDeliver(int row) { 
                    int confirm = JOptionPane.showConfirmDialog(frame, "Confirmer la livraison et le paiement ?", "Validation", JOptionPane.YES_NO_OPTION);
                    if(confirm == JOptionPane.YES_OPTION) updateStatusFromButton(row, EtatReparation.LIVREE);
                }
            };

            table.getColumnModel().getColumn(11).setCellRenderer(new TableActionCellRenderer());
            table.getColumnModel().getColumn(11).setCellEditor(new TableActionCellEditor(event));
            table.getColumnModel().getColumn(11).setMinWidth(290);
            table.getColumnModel().getColumn(11).setMaxWidth(350);
        } else {
            table.getColumnModel().getColumn(11).setMinWidth(0);
            table.getColumnModel().getColumn(11).setMaxWidth(0);
        }

        // Clic Ligne pour Détails
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (table.getSelectedColumn() != 11) { 
                    if (e.getClickCount() == 2) { 
                        int row = table.getSelectedRow();
                        if (row != -1) {
                            int modelRow = table.convertRowIndexToModel(row);
                            int idRep = (int) tableModel.getValueAt(modelRow, 9);
                            showClientDetails(idRep);
                        }
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private void showClientDetails(int idReparation) {
        Reparation target = null;
        for(Reparation r : cacheReparations) {
            if(r.getIdReparation() == idReparation) { target = r; break; }
        }

        if(target != null && target.getDevice() != null && target.getDevice().getClient() != null) {
            new ClientInfoDialog(frame, target.getDevice().getClient()).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Impossible de charger les infos client.", "Erreur", JOptionPane.WARNING_MESSAGE);
        }
    }

    // --- 4. CHARGEMENT DES DONNÉES (FILTRAGE MODIFIÉ) ---
    public void refreshTable() {
        if (metier == null) return;
        User currentUser = frame.getCurrentUser();
        
        if (currentUser instanceof Reparateur && !(currentUser instanceof Proprietaire)) {
            table.getColumnModel().getColumn(10).setMinWidth(0); table.getColumnModel().getColumn(10).setMaxWidth(0);
        } else {
            table.getColumnModel().getColumn(10).setMinWidth(100); table.getColumnModel().getColumn(10).setMaxWidth(200);
        }

        new SwingWorker<List<Reparation>, Void>() {
            @Override
            protected List<Reparation> doInBackground() throws Exception {
                List<Reparation> rawList = (currentUser instanceof Reparateur) ? 
                    metier.findByReparateur(currentUser.getIdU()) : metier.findAll();
                
                List<Reparation> filteredList = new ArrayList<>();
                for (Reparation r : rawList) {
                    
                    // 🔥 MODIFICATION ICI : Archivé = LIVREE ou ANNULEE
                    boolean isArchive = (r.getEtat() == EtatReparation.LIVREE || r.getEtat() == EtatReparation.ANNULEE);
                    
                    // Logique booléenne simple :
                    // Si modeHistorique est TRUE, on veut isArchive TRUE.
                    // Si modeHistorique est FALSE, on veut isArchive FALSE.
                    if (modeHistorique == isArchive) {
                        filteredList.add(r);
                    }
                }
                return filteredList;
            }

            @Override
            protected void done() {
                try {
                    cacheReparations = get();
                    if (table.isEditing()) table.getCellEditor().stopCellEditing();
                    
                    tableModel.setRowCount(0);
                    if (cacheReparations != null) {
                        for (Reparation r : cacheReparations) {
                            String clientNom = "N/A", appareil = "N/A", codeClient = "N/A";
                            if(r.getDevice() != null) {
                                appareil = r.getDevice().getMarque() + " " + r.getDevice().getType();
                                if(r.getDevice().getClient() != null) {
                                    codeClient = r.getDevice().getClient().getCodeClient();
                                    clientNom = r.getDevice().getClient().getNom() + " " + r.getDevice().getClient().getPrenom();
                                }
                            }
                            String nomReparateur = (r.getReparateur() != null) ? r.getReparateur().getNom() : "-";

                            tableModel.addRow(new Object[]{
                                codeClient, 
                                clientNom, 
                                appareil, 
                                r.getCause(), 
                                r.getDateDepot(),
                                String.format("%.2f Dh", r.getPrixTotal()), 
                                String.format("%.2f Dh", r.getAvance()), 
                                String.format("%.2f Dh", r.getReste()),
                                r.getEtat(), 
                                r.getIdReparation(), 
                                nomReparateur, 
                                r.getEtat()
                            });
                        }
                    }
                } catch (Exception e) { e.printStackTrace(); }
            }
        }.execute();
    }

    private void updateStatusFromButton(int row, EtatReparation newStatus) {
        if (table.isEditing()) table.getCellEditor().stopCellEditing();
        
        int modelRow = table.convertRowIndexToModel(row);
        int idRep = (int) tableModel.getValueAt(modelRow, 9);
        
        Reparation targetTemp = null;
        for(Reparation r : cacheReparations) { if(r.getIdReparation() == idRep) { targetTemp = r; break; } }
        final Reparation target = targetTemp;
        
        if(target != null) {
            try { 
                target.setEtat(newStatus);
                metier.update(target);
                refreshTable(); // La ligne changera de tableau automatiquement si elle devient LIVREE ou ANNULEE

                if (newStatus == EtatReparation.TERMINEE) {
                    Client client = target.getDevice().getClient();
                    if (client.getEmail() != null && !client.getEmail().isEmpty() && client.getEmail().contains("@")) {
                        new Thread(() -> {
                            String nomComplet = client.getNom() + " " + client.getPrenom();
                            String appareil = target.getDevice().getMarque() + " " + target.getDevice().getType();
                            
                            utils.EmailService.envoyerNotificationFinReparation(
                                client.getEmail(),
                                nomComplet,
                                client.getCodeClient(),
                                appareil,
                                target.getAvance(),
                                target.getReste()
                            );
                        }).start();
                        JOptionPane.showMessageDialog(this, "Notification envoyée au client.", "Email", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                
            } catch (Exception ex) { 
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage()); 
            }
        }
    }

    // =========================================================
    // CLASSES INTERNES
    // =========================================================

    class ClientInfoDialog extends JDialog {
        public ClientInfoDialog(JFrame parent, Client client) {
            super(parent, "Fiche Client : " + client.getNom().toUpperCase(), true);
            setSize(450, 250);
            setLocationRelativeTo(parent);
            setResizable(false);
            
            JPanel content = new JPanel(new BorderLayout());
            content.setBackground(Color.WHITE);
            content.setBorder(new EmptyBorder(20, 20, 20, 20));

            JLabel lblPhoto = new JLabel();
            lblPhoto.setPreferredSize(new Dimension(140, 140));
            lblPhoto.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
            lblPhoto.setHorizontalAlignment(SwingConstants.CENTER);
            lblPhoto.setOpaque(true);
            lblPhoto.setBackground(new Color(248, 250, 252));

            if (client.getPhotoPath() != null) {
                File f = new File(client.getPhotoPath());
                if(f.exists()) {
                    ImageIcon icon = new ImageIcon(client.getPhotoPath());
                    Image img = icon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
                    lblPhoto.setIcon(new ImageIcon(img));
                } else { lblPhoto.setText("Img introuvable"); }
            } else { lblPhoto.setText("Pas de photo"); }

            JPanel infoPanel = new JPanel(new GridLayout(5, 1, 0, 5));
            infoPanel.setOpaque(false);
            infoPanel.setBorder(new EmptyBorder(0, 20, 0, 0));

            infoPanel.add(createDetailLabel("CLIENT", client.getNom().toUpperCase() + " " + client.getPrenom()));
            infoPanel.add(createDetailLabel("CODE", client.getCodeClient()));
            infoPanel.add(createDetailLabel("TÉLÉPHONE", client.getTelephone()));
            infoPanel.add(createDetailLabel("EMAIL", (client.getEmail() != null && !client.getEmail().isEmpty()) ? client.getEmail() : "Non renseigné"));

            JButton btnClose = new JButton("Fermer");
            btnClose.setFocusPainted(false);
            btnClose.addActionListener(e -> dispose());
            
            content.add(lblPhoto, BorderLayout.WEST);
            content.add(infoPanel, BorderLayout.CENTER);
            content.add(btnClose, BorderLayout.SOUTH);

            add(content);
        }
        private JLabel createDetailLabel(String title, String value) {
            return new JLabel("<html><font color='#64748b' size='3'><b>" + title + ":</b></font> <font color='#0f172a'>" + value + "</font></html>");
        }
    }

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

    class RoundedStatusRenderer extends DefaultTableCellRenderer {
        public RoundedStatusRenderer() { super(); setOpaque(false); setHorizontalAlignment(JLabel.CENTER); }
        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            EtatReparation etat = null;
            if (value instanceof EtatReparation) {
                etat = (EtatReparation) value;
            } else if (value != null) {
                try { etat = EtatReparation.valueOf(value.toString()); } catch(Exception e){}
            }
            
            if (etat != null) {
                setText(etat.name().replace("_", " "));
                Color bg = new Color(241, 245, 249); Color fg = new Color(71, 85, 105);
                switch (etat) {
                    case TERMINEE: bg = new Color(220, 252, 231); fg = new Color(22, 163, 74); break;
                    case EN_COURS: bg = new Color(219, 234, 254); fg = new Color(37, 99, 235); break;
                    case EN_ATTENTE: bg = new Color(254, 249, 195); fg = new Color(202, 138, 4); break;
                    case BLOQUEE: bg = new Color(254, 226, 226); fg = new Color(220, 38, 38); break;
                    case LIVREE: bg = new Color(243, 232, 255); fg = new Color(126, 34, 206); break;
                    case ANNULEE: bg = new Color(241, 245, 249); fg = new Color(100, 116, 139); break;
                    default: break;
                }
                setBackground(bg); setForeground(fg);
            } else { setText(""); setBackground(Color.WHITE); }
            
            if(isSelected) super.setBackground(table.getSelectionBackground());
            else super.setBackground(Color.WHITE);
            
            return this;
        }
        @Override protected void paintComponent(Graphics g) {
            if (getText().isEmpty()) { super.paintComponent(g); return; }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(super.getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(getBackground());
            int w = 110; int h = 24; int x = (getWidth() - w) / 2; int y = (getHeight() - h) / 2;
            g2.fillRoundRect(x, y, w, h, 15, 15);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}