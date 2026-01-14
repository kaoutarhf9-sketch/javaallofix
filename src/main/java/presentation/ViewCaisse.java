package presentation;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

import dao.Reparation;
import dao.Recette;
import dao.Reparateur;
import dao.User;
import dao.Proprietaire;
import metier.GestionReparation;
import metier.GestionRecette;
import metier.EtatReparation;

public class ViewCaisse extends JPanel {

    private ModernMainFrame frame;
    private GestionReparation metierReparation;
    // On retire metierRecette d'ici pour l'instancier à la demande (fraîcheur des données)
    
    // --- LES 4 CARTES D'INFORMATIONS ---
    private JLabel lblTiroirCaisse;
    private JLabel lblDettesExternes;
    private JLabel lblResteProprio;
    private JLabel lblMesGains;

    private JTable table;
    private DefaultTableModel model;

    public ViewCaisse(ModernMainFrame frame) {
        this.frame = frame;
        this.metierReparation = new GestionReparation();
        
        setLayout(new BorderLayout());
        setBackground(new Color(245, 246, 250)); 
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(createHeader(), BorderLayout.NORTH);
        add(createTableSection(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);

        refreshCalculations();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("Ma Trésorerie (Temps Réel)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(30, 41, 59));
        
        JPanel cards = new JPanel(new GridLayout(1, 4, 15, 0));
        cards.setOpaque(false);
        
        lblTiroirCaisse = new JLabel("0.00 Dh");
        cards.add(createCard("TIROIR (Physique)", lblTiroirCaisse, new Color(59, 130, 246)));

        lblDettesExternes = new JLabel("0.00 Dh");
        cards.add(createCard("DETTES EXT. (À Rendre)", lblDettesExternes, new Color(239, 68, 68)));

        lblResteProprio = new JLabel("0.00 Dh");
        cards.add(createCard("PART PATRON (Fixe)", lblResteProprio, new Color(245, 158, 11)));

        lblMesGains = new JLabel("0.00 Dh");
        cards.add(createCard("MES GAINS NETS", lblMesGains, new Color(16, 185, 129)));

        header.add(title, BorderLayout.NORTH);
        header.add(cards, BorderLayout.CENTER);
        return header;
    }

    private JPanel createCard(String title, JLabel valueLabel, Color barColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createMatteBorder(0, 5, 0, 0, barColor)
        ));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTitle.setForeground(Color.GRAY);
        lblTitle.setBorder(new EmptyBorder(10, 10, 0, 10));
        
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(new Color(30, 41, 59));
        valueLabel.setBorder(new EmptyBorder(5, 10, 10, 10));
        
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createTableSection() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));

        String[] cols = {"Type", "Partenaire / Source", "Montant", "Impact Caisse"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(240, 240, 240));
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(248, 250, 252));
        header.setPreferredSize(new Dimension(0, 40));

        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                String val = value.toString();
                if (val.contains("+")) setForeground(new Color(22, 163, 74));
                else if (val.contains("-")) setForeground(new Color(220, 38, 38));
                else setForeground(Color.BLACK);
                setHorizontalAlignment(JLabel.RIGHT);
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        container.add(scroll, BorderLayout.CENTER);
        return container;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        JButton btnRefresh = new JButton("🔄 Forcer l'actualisation");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefresh.setBackground(new Color(37, 99, 235));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.addActionListener(e -> refreshCalculations());
        footer.add(btnRefresh);
        return footer;
    }

    // =================================================================
    // LOGIQUE DE CALCUL ROBUSTE
    // =================================================================
    public void refreshCalculations() {
        User currentUser = frame.getCurrentUser();
        if (currentUser == null) return;

        new SwingWorker<Void, Void>() {
            List<Reparation> reparations = new ArrayList<>();
            List<Recette> recettesPerso = new ArrayList<>();

            @Override
            protected Void doInBackground() {
                try {
                    // 1. On recrée les services pour forcer la lecture DB fraîche
                    GestionReparation serviceRep = new GestionReparation();
                    GestionRecette serviceRec = new GestionRecette();

                    // 2. Charger Réparations
                    if (currentUser instanceof Reparateur) 
                        reparations = serviceRep.findByReparateur(currentUser.getIdU());
                    else 
                        reparations = serviceRep.findAll(); 

                    // 3. Charger Recettes
                    List<Recette> allRecettes = serviceRec.obtenirHistorique("TOUT");
                    
                    for(Recette r : allRecettes) {
                        boolean isMine = false;
                        if(r.getReparateur() != null && r.getReparateur().getIdU() == currentUser.getIdU()) isMine = true;
                        
                        if(isMine) {
                            // On ne garde QUE les "NON_RENDU"
                            if ("NON_RENDU".equals(r.getStatut())) {
                                if ("CREDIT".equals(r.getTypeOperation()) || "VERSEMENT".equals(r.getTypeOperation())) {
                                    recettesPerso.add(r);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    model.setRowCount(0);

                    double totalTiroir = 0;      
                    double totalDettesExt = 0;   
                    double partPatronFixe = 0;   
                    
                    // A. RECETTES (Seulement celles qui restent à traiter)
                    if (recettesPerso != null) {
                        for (Recette rec : recettesPerso) {
                            if ("CREDIT".equals(rec.getTypeOperation())) {
                                model.addRow(new Object[]{
                                    "📥 CRÉDIT", rec.getPartenaire(), 
                                    String.format("+ %.2f Dh", rec.getMontant()), "Dette en cours"
                                });
                                totalTiroir += rec.getMontant();
                                totalDettesExt += rec.getMontant(); 
                            } 
                            else if ("VERSEMENT".equals(rec.getTypeOperation())) {
                                model.addRow(new Object[]{
                                    "📤 VERSEMENT", rec.getPartenaire(), 
                                    String.format("- %.2f Dh", rec.getMontant()), "Argent dehors"
                                });
                                totalTiroir -= rec.getMontant(); 
                            }
                        }
                    }

                    // B. RÉPARATIONS
                    double pourcentPatron = 0.0;
                    if (currentUser instanceof Reparateur) {
                        Double p = ((Reparateur)currentUser).getPourcentage();
                        pourcentPatron = (p != null ? p : 0.0) / 100.0;
                    }

                    if (reparations != null) {
                        for (Reparation r : reparations) {
                            boolean isMine = false;
                            if (currentUser instanceof Reparateur && r.getReparateur() != null && r.getReparateur().getIdU() == currentUser.getIdU()) isMine = true;
                            else if (currentUser instanceof Proprietaire && r.getProprietaire() != null && r.getProprietaire().getIdU() == currentUser.getIdU()) isMine = true;
                            if (!isMine) continue;

                            double cashRep = r.getAvance();
                            if (r.getEtat() == EtatReparation.LIVREE) cashRep += r.getReste();

                            double partPatronLigne = 0.0;
                            if (r.getEtat() == EtatReparation.LIVREE) {
                                partPatronLigne = r.getPrixTotal() * pourcentPatron;
                            }

                            if (cashRep > 0) {
                                String client = (r.getDevice() != null) ? r.getDevice().getMarque() : "Client";
                                model.addRow(new Object[]{
                                    "🔧 RÉPARATION", client,
                                    String.format("+ %.2f Dh", cashRep),
                                    "Gain Business"
                                });
                                totalTiroir += cashRep;
                                partPatronFixe += partPatronLigne;
                            }
                        }
                    }

                    // C. CALCULS FINAUX
                    double mesGainsReels = (totalTiroir - totalDettesExt) - partPatronFixe;

                    lblTiroirCaisse.setText(String.format("%.2f Dh", totalTiroir));
                    lblDettesExternes.setText(String.format("%.2f Dh", totalDettesExt));
                    lblResteProprio.setText(String.format("%.2f Dh", partPatronFixe));
                    
                    lblMesGains.setText(String.format("%.2f Dh", mesGainsReels)); 
                    if (mesGainsReels < 0) {
                        lblMesGains.setForeground(new Color(239, 68, 68));
                        lblMesGains.setText(String.format("%.2f Dh (Déficit)", mesGainsReels));
                    } else {
                        lblMesGains.setForeground(new Color(16, 185, 129));
                    }
                    
                    // Force l'affichage à se rafraîchir
                    revalidate();
                    repaint();

                } catch (Exception e) { e.printStackTrace(); }
            }
        }.execute();
    }
}