package presentation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

import metier.GestionCaisse;
import dao.ReparateurStat;
import dao.Recouvrement;

public class CaissePanel extends JPanel {

    // Instance du métier pour les calculs et la BDD
    private GestionCaisse gestionCaisse = new GestionCaisse();
    
    // Composants graphiques
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblTotalGlobal;
    private JLabel lblTotalProprio;
    
    // Stockage temporaire des données affichées (pour savoir sur qui on clique)
    private List<ReparateurStat> currentStats;

    public CaissePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 244, 248)); // Gris très clair moderne
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // 1. En-tête (Titre + Totaux globaux)
        add(createHeader(), BorderLayout.NORTH);

        // 2. Tableau central
        add(createTableSection(), BorderLayout.CENTER);

        // 3. Pied de page (Boutons d'action)
        add(createFooter(), BorderLayout.SOUTH);

        // 4. Chargement initial
        refreshData();
    }

    // ========================================================================================
    // 1. CRÉATION DE L'EN-TÊTE
    // ========================================================================================
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Partie Gauche : Titres
        JPanel titles = new JPanel(new GridLayout(2, 1));
        titles.setOpaque(false);
        JLabel title = new JLabel("Gestion de Caisse & Recouvrement");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(30, 41, 59));
        
        JLabel subtitle = new JLabel("Calcul basé sur les réparations LIVRÉES (Argent encaissé par le réparateur).");
        subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        subtitle.setForeground(Color.GRAY);
        
        titles.add(title);
        titles.add(subtitle);

        // Partie Droite : Les gros chiffres
        JPanel statsInfo = new JPanel(new GridLayout(2, 1, 0, 5));
        statsInfo.setOpaque(false);

        lblTotalGlobal = new JLabel("CA En Attente: 0.00 Dh", SwingConstants.RIGHT);
        lblTotalGlobal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotalGlobal.setForeground(new Color(30, 41, 59));

        lblTotalProprio = new JLabel("Net Boutique: 0.00 Dh", SwingConstants.RIGHT);
        lblTotalProprio.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalProprio.setForeground(new Color(37, 99, 235)); // Bleu

        statsInfo.add(lblTotalGlobal);
        statsInfo.add(lblTotalProprio);

        header.add(titles, BorderLayout.WEST);
        header.add(statsInfo, BorderLayout.EAST);
        return header;
    }

    // ========================================================================================
    // 2. CRÉATION DU TABLEAU
    // ========================================================================================
    private JPanel createTableSection() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        // Bordure fine grise
        container.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            new EmptyBorder(0, 0, 0, 0)
        ));

        // Définition des colonnes
        String[] cols = {
            "Réparateur", 
            "Dossiers Livrés", 
            "Chiffre d'Affaires", 
            "Part Boutique (À Encaisser)", 
            "Gain Réparateur"
        };
        
        // Modèle non éditable
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(45); // Lignes confortables
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(240, 240, 240));
        table.setSelectionBackground(new Color(237, 242, 255));
        table.setSelectionForeground(Color.BLACK);

        // Style de l'en-tête du tableau
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(new Color(100, 116, 139));
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(0, 45));

        // --- RENDERERS (Couleurs et Alignement) ---
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        rightRenderer.setBorder(new EmptyBorder(0, 0, 0, 10));

        // Bleu pour la part boutique
        DefaultTableCellRenderer blueRenderer = new DefaultTableCellRenderer();
        blueRenderer.setHorizontalAlignment(JLabel.RIGHT);
        blueRenderer.setForeground(new Color(37, 99, 235)); 
        blueRenderer.setFont(new Font("Segoe UI", Font.BOLD, 14));
        blueRenderer.setBorder(new EmptyBorder(0, 0, 0, 10));

        // Vert pour le gain réparateur
        DefaultTableCellRenderer greenRenderer = new DefaultTableCellRenderer();
        greenRenderer.setHorizontalAlignment(JLabel.RIGHT);
        greenRenderer.setForeground(new Color(22, 163, 74)); 
        greenRenderer.setFont(new Font("Segoe UI", Font.BOLD, 14));
        greenRenderer.setBorder(new EmptyBorder(0, 0, 0, 10));

        // Application des styles aux colonnes
        table.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(blueRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(greenRenderer);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        
        container.add(scroll, BorderLayout.CENTER);
        return container;
    }

    // ========================================================================================
    // 3. PIED DE PAGE (BOUTONS)
    // ========================================================================================
    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(15, 0, 0, 0));

        // Bouton : Voir Historique
        JButton btnHistory = new JButton("📜 Voir Historique");
        btnHistory.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnHistory.setForeground(new Color(30, 41, 59));
        btnHistory.setBackground(new Color(226, 232, 240)); // Gris clair
        btnHistory.setFocusPainted(false);
        btnHistory.addActionListener(e -> showHistoriqueDialog());

        // Bouton : Encaisser
        JButton btnPay = new JButton("💰 Encaisser la Sélection");
        btnPay.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPay.setForeground(Color.WHITE);
        btnPay.setBackground(new Color(234, 179, 8)); // Or / Jaune Foncé
        btnPay.setFocusPainted(false);
        btnPay.addActionListener(e -> actionEncaisser());

        // Bouton : Actualiser
        JButton btnRefresh = new JButton("Actualiser");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setBackground(new Color(37, 99, 235)); // Bleu
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> refreshData());

        footer.add(btnHistory);
        footer.add(Box.createHorizontalStrut(10));
        footer.add(btnPay);
        footer.add(Box.createHorizontalStrut(10));
        footer.add(btnRefresh);

        return footer;
    }

    // ========================================================================================
    // LOGIQUE MÉTIER & ACTIONS
    // ========================================================================================

    /**
     * Charge les données depuis la base de données de manière asynchrone.
     */
    public void refreshData() {
        model.setRowCount(0);
        lblTotalGlobal.setText("Chargement...");
        
        new SwingWorker<List<ReparateurStat>, Void>() {
            @Override
            protected List<ReparateurStat> doInBackground() {
                // Calculer les commissions en temps réel
                return gestionCaisse.calculerCommissions();
            }

            @Override
            protected void done() {
                try {
                    List<ReparateurStat> stats = get();
                    currentStats = stats; // Sauvegarde pour utilisation lors du clic
                    
                    double totalCA = 0;
                    double totalProprio = 0;

                    for (ReparateurStat s : stats) {
                        model.addRow(new Object[]{
                            s.getReparateur().getNom().toUpperCase() + " " + s.getReparateur().getPrenom(),
                            s.getNombreReparations(),
                            String.format("%.2f Dh", s.getChiffreAffaires()),
                            String.format("%.2f Dh", s.getPartProprietaire()),
                            String.format("%.2f Dh", s.getPartReparateur())
                        });

                        totalCA += s.getChiffreAffaires();
                        totalProprio += s.getPartProprietaire();
                    }
                    
                    lblTotalGlobal.setText("CA En Attente: " + String.format("%.2f Dh", totalCA));
                    lblTotalProprio.setText("Net Boutique: " + String.format("%.2f Dh", totalProprio));

                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(CaissePanel.this, "Erreur : " + e.getMessage());
                }
            }
        }.execute();
    }

    /**
     * Action déclenchée par le bouton "Encaisser".
     */
    private void actionEncaisser() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une ligne à encaisser.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Vérification de sécurité
        if (currentStats == null || selectedRow >= currentStats.size()) return;
        
        ReparateurStat stat = currentStats.get(selectedRow);
        double montant = stat.getPartProprietaire();

        if (montant <= 0) {
            JOptionPane.showMessageDialog(this, "Aucun montant à encaisser pour ce réparateur.", "Info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Demande de confirmation
        int rep = JOptionPane.showConfirmDialog(this, 
            "Confirmez-vous l'encaissement de " + String.format("%.2f Dh", montant) + " ?\n\n" +
            "Cela va :\n" +
            "1. Archiver ce montant dans l'historique.\n" +
            "2. Remettre le compteur de ce réparateur à zéro.",
            "Validation Encaissement",
            JOptionPane.YES_NO_OPTION);

        if (rep == JOptionPane.YES_OPTION) {
            // Appel au métier pour sauvegarder et reset
            gestionCaisse.validerEncaissement(stat.getReparateur(), montant);
            
            JOptionPane.showMessageDialog(this, "Encaissement réussi !");
            refreshData(); // Mise à jour de l'affichage
        }
    }

    // ========================================================================================
    // POPUP HISTORIQUE
    // ========================================================================================
    private void showHistoriqueDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Historique des Encaissements", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Titre Popup
        JLabel title = new JLabel("Archives des paiements Propriétaire", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setBorder(new EmptyBorder(15, 0, 5, 0));
        dialog.add(title, BorderLayout.NORTH);

        // Tableau Historique
        String[] cols = {"Date", "Réparateur", "Montant Récupéré"};
        DefaultTableModel histModel = new DefaultTableModel(cols, 0) {
             public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable histTable = new JTable(histModel);
        histTable.setRowHeight(35);
        histTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        histTable.getTableHeader().setBackground(new Color(240, 240, 240));
        histTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Remplissage (Simple, sans thread pour l'exemple, car souvent peu de données au début)
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        List<Recouvrement> historique = gestionCaisse.getHistoriquePaiements();
        
        if (historique.isEmpty()) {
             histModel.addRow(new Object[]{"-", "Aucun historique", "-"});
        } else {
            for (Recouvrement r : historique) {
                histModel.addRow(new Object[]{
                    sdf.format(r.getDatePaiement()),
                    r.getReparateur().getNom() + " " + r.getReparateur().getPrenom(),
                    String.format("%.2f Dh", r.getMontantRecupere())
                });
            }
        }
        
        JScrollPane scroll = new JScrollPane(histTable);
        scroll.setBorder(new EmptyBorder(10, 10, 10, 10));
        scroll.getViewport().setBackground(Color.WHITE);

        dialog.add(scroll, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
}