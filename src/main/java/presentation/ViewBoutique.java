package presentation;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import dao.Boutique;
import dao.Proprietaire; 
import metier.GestionBoutique;

public class ViewBoutique extends JPanel {

    private ModernMainFrame frame;
    private GestionBoutique metier;
    private JTable table;
    private DefaultTableModel tableModel;

    public ViewBoutique(ModernMainFrame frame) {
        this.frame = frame;
        
        // --- 1. INIT MÉTIER ---
        try {
            this.metier = new GestionBoutique();
        } catch (Exception e) {
            System.err.println("Erreur init métier : " + e.getMessage());
        }
        
        // --- 2. CONFIGURATION GLOBALE ---
        setLayout(new BorderLayout(0, 30)); // Plus d'espace vertical
        setBackground(Theme.BACKGROUND); // Gris très clair (ex: #F3F4F6)
        setBorder(new EmptyBorder(40, 60, 40, 60)); // Marges généreuses

        // --- 3. EN-TÊTE (Header) ---
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        // Titres
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setOpaque(false);
        
        JLabel title = new JLabel("Mes Boutiques");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Theme.TEXT_DARK);
        
        JLabel subtitle = new JLabel("Gérez vos points de vente et suivez leur activité.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(Theme.TEXT_GRAY);
        
        titlePanel.add(title);
        titlePanel.add(subtitle);
        
        // Bouton Ajouter (Style "Pill")
        JButton btnAdd = new JButton("   + Nouvelle Boutique   ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Arrondi complet
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAdd.setBackground(Theme.PRIMARY);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setBorderPainted(false);
        btnAdd.setContentAreaFilled(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.setPreferredSize(new Dimension(180, 40));
        btnAdd.addActionListener(e -> frame.changerVue(ModernMainFrame.VUE_FORM_BOUTIQUE));

        header.add(titlePanel, BorderLayout.WEST);
        header.add(btnAdd, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // --- 4. CARTE BLANCHE (Table Container) ---
        JPanel cardPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre portée douce (Drop Shadow)
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(3, 5, getWidth()-6, getHeight()-6, 15, 15);
                
                // Fond Blanc
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth()-3, getHeight()-3, 15, 15);
                g2.dispose();
            }
        };
        cardPanel.setOpaque(false);
        cardPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Marge interne à la carte

        // --- 5. CONFIGURATION TABLEAU ---
        String[] columns = {"ID", "ENSEIGNE", "ADRESSE", "PATENTE", "ACTIONS"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        table = new JTable(tableModel);
        styleTable(table); // Application du design "Web"

        // ScrollPane propre (sans bordures laides)
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBackground(Color.WHITE);
        
        cardPanel.add(scrollPane, BorderLayout.CENTER);
        add(cardPanel, BorderLayout.CENTER);

        // --- 6. INTERACTION (Suppression) ---
        initTableActions();
    }

    /**
     * Logique de chargement des données (Version corrigée int)
     */
    public void refreshTable() {
        if (metier == null) return; 

        tableModel.setRowCount(0); 
        
        Proprietaire currentUser = frame.getProprietaireConnecte();

        if (currentUser != null) {
            // CORRECTION : getIdU() est int, la méthode lister attend int. Parfait.
            int idProprio = currentUser.getIdU(); 
            
            List<Boutique> liste = metier.listerBoutiquesDuProprietaire(idProprio);
            
            if (liste != null) {
                for (Boutique b : liste) {
                    tableModel.addRow(new Object[]{
                        "#" + b.getIdb(), 
                        b.getNomB(), 
                        b.getAdresse(), 
                        b.getPatente(), 
                        "SUPPRIMER" // Le rendu visuel est géré par le CellRenderer
                    });
                }
            }
        }
    }

    /**
     * Gestion du clic sur la colonne Actions
     */
    private void initTableActions() {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                // Si clic sur la colonne ACTIONS (index 4)
                if (row >= 0 && col == 4) {
                    String idString = (String) tableModel.getValueAt(row, 0); // "#123"
                    // Parsing propre en INT
                    int idBoutique = Integer.parseInt(idString.replace("#", ""));

                    int confirm = JOptionPane.showConfirmDialog(
                        frame, 
                        "Supprimer définitivement la boutique #" + idBoutique + " ?", 
                        "Confirmation", 
                        JOptionPane.YES_NO_OPTION
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            metier.supprimerBoutique(idBoutique); // Attend un int
                            refreshTable();
                            JOptionPane.showMessageDialog(frame, "Boutique supprimée.");
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(frame, "Erreur : " + ex.getMessage());
                        }
                    }
                }
            }
        });
    }

    /**
     * Design avancé du tableau
     */
    private void styleTable(JTable table) {
        // Dimensions
        table.setRowHeight(60); // Lignes hautes "aérées"
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setShowGrid(false); // Pas de grille verticale
        table.setShowHorizontalLines(true); // Lignes horizontales seulement
        table.setGridColor(new Color(240, 240, 240)); // Lignes très claires
        
        // Police
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setForeground(Theme.TEXT_DARK);
        
        // Sélection
        table.setSelectionBackground(new Color(245, 247, 255)); // Bleu très pâle au clic
        table.setSelectionForeground(Theme.TEXT_DARK);
        
        // --- HEADER PERSONNALISÉ ---
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 50));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setBackground(Color.WHITE);
                l.setForeground(Theme.TEXT_GRAY);
                l.setFont(new Font("Segoe UI", Font.BOLD, 12));
                l.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(240, 240, 240)));
                l.setBorder(new EmptyBorder(0, 20, 0, 0)); // Padding gauche
                return l;
            }
        });

        // --- RENDU DES CELLULES DE DONNÉES ---
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 20, 0, 0)); // Padding gauche pour le texte
                return c;
            }
        };
        
        // Appliquer le rendu standard aux 4 premières colonnes
        for (int i = 0; i < 4; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // --- RENDU SPÉCIAL COLONNE ACTIONS (Bouton Rouge) ---
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = new JLabel("🗑  " + value.toString());
                l.setFont(new Font("Segoe UI", Font.BOLD, 12));
                l.setForeground(new Color(220, 38, 38)); // Rouge moderne
                l.setHorizontalAlignment(JLabel.CENTER);
                l.setCursor(new Cursor(Cursor.HAND_CURSOR));
                
                // Effet "Badge"
                l.setBackground(new Color(254, 242, 242)); // Fond rouge très pâle
                l.setOpaque(true);
                l.setBorder(new EmptyBorder(5, 10, 5, 10)); // Padding interne
                
                // Conteneur pour centrer le "badge"
                JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
                p.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                p.add(l);
                return p;
            }
        });
    }
}