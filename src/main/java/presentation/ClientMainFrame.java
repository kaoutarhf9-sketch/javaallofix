package presentation;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

import dao.User;
import dao.Proprietaire;
import dao.Reparation;
import metier.GestionClient;
import metier.EtatReparation;

public class ClientMainFrame extends JFrame {

    private User currentUser;
    private GestionClient gestionClient = new GestionClient();
    
    // Conteneur pour la liste des résultats (qui sera rempli dynamiquement)
    private JPanel resultListContainer;

    public ClientMainFrame(User user) {
        this.currentUser = user;

        setTitle("AlloFix | Espace Client");
        setSize(1300, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Fond global légèrement bleuté/gris (Très moderne)
        getContentPane().setBackground(new Color(240, 244, 248));
        setLayout(new BorderLayout());

        // 1. Navbar (Haut)
        add(createNavbar(), BorderLayout.NORTH);

        // 2. Contenu principal avec Scroll (Centre)
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(new Color(240, 244, 248));
        mainContent.setBorder(new EmptyBorder(40, 0, 40, 0)); // Marges verticales

        // Ajout des sections
        addHeader(mainContent);
        addSearchBar(mainContent);
        
        // Espace vide qui sépare la recherche des résultats
        mainContent.add(Box.createVerticalStrut(30));
        
        // Zone invisible qui prendra les résultats
        resultListContainer = new JPanel();
        resultListContainer.setLayout(new BoxLayout(resultListContainer, BoxLayout.Y_AXIS));
        resultListContainer.setOpaque(false); // Transparent pour voir le fond
        mainContent.add(resultListContainer);

        JScrollPane scroll = new JScrollPane(mainContent);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16); // Scroll fluide
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);
    }

    // =================================================================================
    // 1. SECTION EN-TÊTE (TITRE)
    // =================================================================================
    private void addHeader(JPanel container) {
        JLabel title = new JLabel("Suivi de Réparation");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(30, 41, 59));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitle = new JLabel("Consultez l'avancement de vos appareils en temps réel.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(new Color(100, 116, 139));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        container.add(title);
        container.add(Box.createVerticalStrut(10));
        container.add(subtitle);
        container.add(Box.createVerticalStrut(40));
    }

    // =================================================================================
    // 2. BARRE DE RECHERCHE "GOOGLE STYLE" (AVEC OMBRE)
    // =================================================================================
    private void addSearchBar(JPanel container) {
        // Un panel qui dessine une ombre et des bords arrondis
        JPanel searchPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 50, 50);
                
                // Fond Blanc
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(2, 2, getWidth()-6, getHeight()-6, 50, 50);
                
                // Bordure fine
                g2.setColor(new Color(220, 220, 230));
                g2.drawRoundRect(2, 2, getWidth()-6, getHeight()-6, 50, 50);
            }
        };
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 12));
        searchPanel.setOpaque(false);
        searchPanel.setMaximumSize(new Dimension(700, 65));
        searchPanel.setPreferredSize(new Dimension(700, 65));

        // Champ de texte
        JTextField field = new JTextField("Entrez votre N° de Dossier (ex: CL-1789)");
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBorder(null);
        field.setOpaque(false);
        field.setForeground(Color.GRAY);
        field.setPreferredSize(new Dimension(500, 40));

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if(field.getText().startsWith("Entrez")) { field.setText(""); field.setForeground(new Color(30, 41, 59)); }
            }
        });

        // Bouton Loupe / Valider
        JButton btnSearch = new JButton("Rechercher");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setBackground(new Color(37, 99, 235)); // Bleu vif
        btnSearch.setFocusPainted(false);
        btnSearch.setBorder(new EmptyBorder(8, 20, 8, 20));
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Arrondir le bouton aussi
        btnSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(37, 99, 235), 0),
            new EmptyBorder(8, 20, 8, 20)
        ));

        // LOGIQUE DE RECHERCHE
        btnSearch.addActionListener(e -> {
            String code = field.getText().trim();
            if(code.length() < 3) return;

            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            btnSearch.setText("...");
            btnSearch.setEnabled(false);
            
            resultListContainer.removeAll(); // Reset visuel

            new SwingWorker<List<Reparation>, Void>() {
                @Override protected List<Reparation> doInBackground() { 
                    // Appel au métier
                    return gestionClient.findReparationsByCode(code); 
                }
                
                @Override protected void done() {
                    try {
                        List<Reparation> res = get();
                        if(res.isEmpty()) {
                            showNoResult();
                        } else {
                            // On ajoute chaque carte
                            res.forEach(r -> {
                                resultListContainer.add(createSophisticatedCard(r));
                                resultListContainer.add(Box.createVerticalStrut(15)); // Espace entre les cartes
                            });
                        }
                        
                        resultListContainer.revalidate();
                        resultListContainer.repaint();
                    } catch(Exception ex) { 
                        ex.printStackTrace(); 
                    } finally {
                        setCursor(Cursor.getDefaultCursor());
                        btnSearch.setText("Rechercher");
                        btnSearch.setEnabled(true);
                    }
                }
            }.execute();
        });

        searchPanel.add(field);
        searchPanel.add(btnSearch);
        container.add(searchPanel);
    }

    private void showNoResult() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        JLabel l = new JLabel("Aucun résultat trouvé. Vérifiez votre code sur le ticket.");
        l.setForeground(new Color(220, 38, 38));
        l.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        p.add(l);
        resultListContainer.add(p);
    }

    // =================================================================================
    // 3. CARTE DE RÉSULTAT "PRO" (OMBRÉE & ARRONDIE)
    // =================================================================================
    private JPanel createSophisticatedCard(Reparation r) {
        // Wrapper pour ajouter des marges autour de la carte (pour voir l'ombre)
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(720, 140));

        // La carte elle-même avec dessin personnalisé
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 1. Ombre portée (Soft Shadow)
                g2.setColor(new Color(200, 210, 220, 100)); // Ombre bleutée
                g2.fillRoundRect(4, 6, getWidth()-8, getHeight()-10, 15, 15);

                // 2. Fond de la carte (Blanc)
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth()-4, getHeight()-6, 15, 15);
                
                // 3. Petite barre de couleur à gauche (Status indicator)
                Color statusColor = getColorForStatus(r.getEtat());
                g2.setColor(statusColor);
                g2.fillRoundRect(0, 0, 8, getHeight()-6, 15, 15); // Coins arrondis à gauche
                g2.fillRect(5, 0, 5, getHeight()-6); // Pour rendre le côté droit de la barre droit
            }
        };
        card.setPreferredSize(new Dimension(700, 110));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(15, 25, 15, 20)); // Padding interne

        // --- CONTENU GAUCHE (Infos) ---
        JPanel left = new JPanel(new GridLayout(2, 1, 0, 5));
        left.setOpaque(false);
        
        String nomAppareil = (r.getDevice() != null) ? r.getDevice().getMarque() + " " + r.getDevice().getType() : "Appareil Inconnu";
        JLabel lblTitle = new JLabel(nomAppareil);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(30, 41, 59));

        JLabel lblPanne = new JLabel("Problème : " + r.getCause());
        lblPanne.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPanne.setForeground(new Color(100, 116, 139));

        left.add(lblTitle);
        left.add(lblPanne);

        // --- CONTENU DROITE (Badge & Prix) ---
        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);

        // Badge Status (Pill shape)
        JLabel statusBadge = new JLabel(r.getEtat().toString().replace("_", " "), SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // Pill shape
                super.paintComponent(g);
            }
        };
        Color c = getColorForStatus(r.getEtat());
        statusBadge.setBackground(new Color(c.getRed(), c.getGreen(), c.getBlue(), 30)); // Fond très clair transparent
        statusBadge.setForeground(c.darker());
        statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusBadge.setPreferredSize(new Dimension(120, 30));
        statusBadge.setBorder(new EmptyBorder(5, 10, 5, 10));

        // LOGIQUE D'AFFICHAGE DU PRIX (CORRIGÉE)
        JLabel lblPrix = new JLabel("", SwingConstants.RIGHT);
        lblPrix.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblPrix.setBorder(new EmptyBorder(10, 0, 0, 0));

        if(r.getEtat() == EtatReparation.ANNULEE) {
            // Cas annulé : On affiche un tiret gris
            lblPrix.setText("Annulée");
            lblPrix.setForeground(Color.GRAY);
        } else if(r.getReste() > 0) {
            // Cas reste à payer
            lblPrix.setText("Reste: " + r.getReste() + " Dh");
            lblPrix.setForeground(new Color(220, 38, 38)); // Rouge
        } else {
            // Cas payé
            lblPrix.setText("Payé ✔");
            lblPrix.setForeground(new Color(22, 163, 74)); // Vert
        }

        JPanel badgeWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        badgeWrapper.setOpaque(false);
        badgeWrapper.add(statusBadge);

        right.add(badgeWrapper, BorderLayout.NORTH);
        right.add(lblPrix, BorderLayout.SOUTH);

        card.add(left, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);

        // Effet Hover (La carte change de curseur)
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });

        wrapper.add(card);
        return wrapper;
    }

    // Helper pour les couleurs
    private Color getColorForStatus(EtatReparation etat) {
        if(etat == null) return Color.GRAY;
        switch (etat) {
            case TERMINEE: return new Color(22, 163, 74);   // Vert
            case EN_COURS: return new Color(37, 99, 235);   // Bleu
            case BLOQUEE:  return new Color(220, 38, 38);   // Rouge
            case ANNULEE:  return new Color(156, 163, 175); // Gris
            default:       return new Color(202, 138, 4);   // Jaune/Orange (En attente)
        }
    }

    // Navbar simple avec ombre
    private JPanel createNavbar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(Color.WHITE);
        nav.setPreferredSize(new Dimension(1000, 70));
        // Ombre sous la navbar
        nav.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0,0,1,0, new Color(230,230,235)),
            new EmptyBorder(0, 40, 0, 40)
        ));
        
        JLabel logo = new JLabel("AlloFix");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logo.setForeground(new Color(30, 41, 59));
        nav.add(logo, BorderLayout.WEST);

        JButton btnLogin = new JButton("Connexion Atelier");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setForeground(new Color(100, 116, 139));
        btnLogin.setContentAreaFilled(false);
        btnLogin.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
        btnLogin.setPreferredSize(new Dimension(140, 35));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> {
             // Ouvre le backend
             new SwingWorker<ModernMainFrame, Void>() {
                    @Override protected ModernMainFrame doInBackground() { 
                        return new ModernMainFrame();
                    }
                    @Override protected void done() { 
                        try { 
                            get().setVisible(true); 
                            get().changerVue(ModernMainFrame.VUE_LOGIN_PROPRIO); 
                            dispose(); 
                        } catch(Exception ex) { ex.printStackTrace(); } 
                    }
            }.execute();
        });
        
        // Wrapper pour centrer verticalement le bouton
        JPanel btnWrapper = new JPanel(new GridBagLayout());
        btnWrapper.setOpaque(false);
        btnWrapper.add(btnLogin);
        nav.add(btnWrapper, BorderLayout.EAST);
        
        return nav;
    }

    public static void main(String[] args) {
        // Anti-aliasing pour le texte (Crucial pour le look Pro)
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(() -> new ClientMainFrame(null).setVisible(true));
    }
}