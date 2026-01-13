package presentation;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

import dao.Boutique;
import dao.Proprietaire;
import dao.Reparateur;
import metier.GestionBoutique;
import metier.GestionReparateur;
import metier.IGestionReparateur;

public class ViewDashboardProprio extends JPanel {

    private ModernMainFrame frame;
    private GestionBoutique metierBoutique;
    private IGestionReparateur metierReparateur;

    // UI Components
    private JLabel lblWelcome;
    private JLabel lblDate;
    private JLabel lblCountBoutiques;
    private JLabel lblCountReparateurs;

    public ViewDashboardProprio(ModernMainFrame frame) {
        this.frame = frame;
        
        try {
            this.metierBoutique = new GestionBoutique();
            this.metierReparateur = new GestionReparateur();
        } catch (Exception e) { e.printStackTrace(); }

        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND); 
        setBorder(new EmptyBorder(30, 40, 30, 40)); 

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // 1. EN-TÊTE
        content.add(createHeaderSection());
        content.add(Box.createVerticalStrut(40));

        // 2. STATISTIQUES (Seulement 2 cartes maintenant)
        content.add(createStatsSection());
        
        // Pousser le reste vers le haut
        content.add(Box.createVerticalGlue());

        add(content, BorderLayout.CENTER);
        updateStats();
    }

    private JPanel createHeaderSection() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(2000, 80));

        JPanel texts = new JPanel(new GridLayout(2, 1));
        texts.setOpaque(false);

        lblWelcome = new JLabel("Bonjour, Propriétaire");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblWelcome.setForeground(Theme.TEXT_HEADLINE);

        lblDate = new JLabel("Aperçu de votre activité aujourd'hui.");
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDate.setForeground(Theme.TEXT_BODY);

        texts.add(lblWelcome);
        texts.add(lblDate);

        header.add(texts, BorderLayout.WEST);
        return header;
    }

    private JPanel createStatsSection() {
        // ✅ CHANGEMENT : 2 colonnes seulement, plus de CA
        JPanel cardsPanel = new JPanel(new GridLayout(1, 2, 30, 0)); 
        cardsPanel.setOpaque(false);
        cardsPanel.setMaximumSize(new Dimension(2000, 160)); 

        lblCountBoutiques = new JLabel("-");
        lblCountReparateurs = new JLabel("-");

        // Carte 1 : Boutiques
        cardsPanel.add(createKpiCard("🏪", "Boutiques Actives", lblCountBoutiques, new Color(59, 130, 246)));
        
        // Carte 2 : Réparateurs
        cardsPanel.add(createKpiCard("👨‍🔧", "Techniciens", lblCountReparateurs, new Color(16, 185, 129)));
        
        return cardsPanel;
    }

    private JPanel createKpiCard(String icon, String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(226, 232, 240), 1, true),
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, accentColor),
                new EmptyBorder(20, 25, 20, 25)
            )
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        
        JLabel lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(new Color(148, 163, 184));

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        top.add(lblTitle, BorderLayout.WEST);
        top.add(lblIcon, BorderLayout.EAST);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(Theme.TEXT_HEADLINE);
        
        card.add(top, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    public void updateStats() {
        if (frame.getCurrentUser() == null || !(frame.getCurrentUser() instanceof Proprietaire)) return;
        
        Proprietaire p = (Proprietaire) frame.getCurrentUser();
        lblWelcome.setText("Bonjour, " + p.getPrenom());

        new SwingWorker<int[], Void>() {
            @Override
            protected int[] doInBackground() throws Exception {
                int nbBoutiques = 0;
                int nbReparateurs = 0;
                if (metierBoutique != null) {
                    List<Boutique> listB = metierBoutique.listerBoutiquesDuProprietaire(p.getIdU());
                    if (listB != null) nbBoutiques = listB.size();
                }
                if (metierReparateur != null) {
                    List<Reparateur> listR = metierReparateur.listerReparateursParProprietaire(p.getIdU());
                    if (listR != null) nbReparateurs = listR.size();
                }
                return new int[]{nbBoutiques, nbReparateurs};
            }

            @Override
            protected void done() {
                try {
                    int[] results = get();
                    lblCountBoutiques.setText(String.valueOf(results[0]));
                    lblCountReparateurs.setText(String.valueOf(results[1]));
                } catch (Exception e) { e.printStackTrace(); }
            }
        }.execute();
    }
}