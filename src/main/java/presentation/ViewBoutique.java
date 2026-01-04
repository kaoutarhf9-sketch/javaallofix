package presentation;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import dao.Boutique;
import metier.GestionBoutique;

public class ViewBoutique extends JPanel {

    private ModernMainFrame frame;
    private GestionBoutique metier;
    private JTable table;
    private DefaultTableModel tableModel;

    public ViewBoutique(ModernMainFrame frame) {
        this.frame = frame;
        this.metier = new GestionBoutique();
        
        setLayout(new BorderLayout(0, 25));
        setBackground(Theme.BACKGROUND);
        setBorder(new EmptyBorder(40, 50, 40, 50));

        // --- EN-TÊTE ---
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        // Texte d'en-tête
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        
        JLabel title = new JLabel("Mes Boutiques");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Theme.TEXT_DARK);
        
        JLabel subtitle = new JLabel("Gérez et visualisez l'ensemble de vos points de vente");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(Theme.TEXT_GRAY);
        
        titlePanel.add(title);
        titlePanel.add(subtitle);
        
        // Bouton Ajouter stylisé
        JButton btnAdd = createStyledButton("+ AJOUTER UNE BOUTIQUE", Theme.PRIMARY);
        btnAdd.addActionListener(e -> frame.changerVue(ModernMainFrame.VUE_FORM_BOUTIQUE));

        header.add(titlePanel, BorderLayout.WEST);
        header.add(btnAdd, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // --- CONTENEUR DE LA TABLE (Design "Card") ---
        JPanel tableCard = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Ombre
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 20, 20);
                // Fond Blanc
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth()-5, getHeight()-5, 20, 20);
                g2.dispose();
            }
        };
        tableCard.setOpaque(false);
        tableCard.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Initialisation Table
        String[] columns = {"ID", "NOM DE LA BOUTIQUE", "ADRESSE", "PATENTE", "ACTIONS"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        table = new JTable(tableModel);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        tableCard.add(scrollPane, BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);

        // Chargement des données
        refreshTable();
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        // On récupère toutes les boutiques (Idéalement, filtrez par frame.getCurrentUser().getId())
        List<Boutique> liste = metier.listerToutesLesBoutiques();
        for (Boutique b : liste) {
            tableModel.addRow(new Object[]{
                "#" + b.getIdb(), 
                b.getNomB(), 
                b.getAdresse(), 
                b.getPatente(), 
                "🗑 Supprimer"
            });
        }
    }

    private void styleTable(JTable table) {
        table.setRowHeight(50);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setShowGrid(false);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Header de la table
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 45));
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(Color.WHITE);
        header.setForeground(Theme.TEXT_GRAY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)));

        // Sélection
        table.setSelectionBackground(new Color(79, 70, 229, 20)); // Indigo très clair
        table.setSelectionForeground(Theme.PRIMARY);
        
        // Centrage du texte
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.LEFT);
        centerRenderer.setBorder(new EmptyBorder(0, 15, 0, 0));
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(12, 25, 12, 25));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Effet Arrondi pour le bouton (Optionnel : utiliser le même paintComponent que les cartes si nécessaire)
        return btn;
    }
}