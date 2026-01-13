package presentation;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import metier.EtatReparation;

// 1. L'INTERFACE DES ÉVÉNEMENTS
interface TableActionEvent {
    void onStart(int row);
    void onBlock(int row);
    void onFinish(int row);
    void onCancel(int row);
    void onDeliver(int row); // Pour livrer au client
}

// 2. LE PANNEAU DES BOUTONS (DESIGN PRO)
class PanelAction extends JPanel {
    
    private ActionButton cmdStart, cmdBlock, cmdFinish, cmdCancel, cmdDeliver;

    public PanelAction() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 8, 2)); // Espacement aéré
        setOpaque(true);
        setBackground(Color.WHITE);

        // Couleurs Modernes (Inspirées de Tailwind CSS)
        Color green = new Color(16, 185, 129);
        Color amber = new Color(245, 158, 11);
        Color blue  = new Color(59, 130, 246);
        Color red   = new Color(239, 68, 68);
        Color purple= new Color(139, 92, 246);

        cmdStart   = new ActionButton("Démarrer", green);
        cmdBlock   = new ActionButton("Bloquer", amber);
        cmdFinish  = new ActionButton("Terminer", blue);
        cmdCancel  = new ActionButton("Annuler", red);
        cmdDeliver = new ActionButton("Livrer & Encaisser", purple);

        add(cmdStart);
        add(cmdBlock);
        add(cmdFinish);
        add(cmdDeliver);
        add(cmdCancel);
    }

    public void updateButtons(EtatReparation etat) {
        // Reset
        cmdStart.setVisible(false);
        cmdBlock.setVisible(false);
        cmdFinish.setVisible(false);
        cmdCancel.setVisible(false);
        cmdDeliver.setVisible(false);

        if (etat == null) return;

        switch (etat) {
            case EN_ATTENTE:
                cmdStart.setText("Démarrer");
                cmdStart.setVisible(true);
                cmdCancel.setVisible(true);
                break;
                
            case EN_COURS:
                cmdBlock.setVisible(true);
                cmdFinish.setVisible(true);
                // On cache annuler ici pour simplifier, mais on peut le laisser
                break;
                
            case BLOQUEE:
                cmdStart.setText("Reprendre"); // Le texte change intelligemment
                cmdStart.setVisible(true);
                cmdCancel.setVisible(true);
                break;
                
            case TERMINEE:
                cmdDeliver.setVisible(true); // Seul bouton dispo : Livrer
                break;
                
            default:
                break;
        }
    }
    
    public void initEvent(TableActionEvent event, int row) {
        // Nettoyage radical des listeners pour éviter les doublons
        for(ActionListener al : cmdStart.getActionListeners()) cmdStart.removeActionListener(al);
        for(ActionListener al : cmdBlock.getActionListeners()) cmdBlock.removeActionListener(al);
        for(ActionListener al : cmdFinish.getActionListeners()) cmdFinish.removeActionListener(al);
        for(ActionListener al : cmdCancel.getActionListeners()) cmdCancel.removeActionListener(al);
        for(ActionListener al : cmdDeliver.getActionListeners()) cmdDeliver.removeActionListener(al);

        cmdStart.addActionListener(e -> event.onStart(row));
        cmdBlock.addActionListener(e -> event.onBlock(row));
        cmdFinish.addActionListener(e -> event.onFinish(row));
        cmdCancel.addActionListener(e -> event.onCancel(row));
        cmdDeliver.addActionListener(e -> event.onDeliver(row));
    }
    
    // --- BOUTON PERSONNALISÉ (STYLE MODERNE) ---
    private class ActionButton extends JButton {
        private Color baseColor;
        
        public ActionButton(String text, Color color) {
            super(text);
            this.baseColor = color;
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setForeground(color);
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                new javax.swing.border.LineBorder(color, 1, true), // Bordure arrondie
                new EmptyBorder(4, 12, 4, 12) // Padding interne
            ));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setOpaque(true);
            
            // Effet Hover simple
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    setBackground(baseColor);
                    setForeground(Color.WHITE);
                }
                public void mouseExited(MouseEvent e) {
                    setBackground(Color.WHITE);
                    setForeground(baseColor);
                }
            });
        }
    }
}

// 3. RENDERER
class TableActionCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        PanelAction action = new PanelAction();
        if(value instanceof EtatReparation) {
            action.updateButtons((EtatReparation) value);
        }
        action.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
        return action;
    }
}

// 4. EDITOR
class TableActionCellEditor extends DefaultCellEditor {
    private TableActionEvent event;

    public TableActionCellEditor(TableActionEvent event) {
        super(new JCheckBox());
        this.event = event;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        PanelAction action = new PanelAction();
        if(value instanceof EtatReparation) {
            action.updateButtons((EtatReparation) value);
        }
        action.initEvent(event, row);
        action.setBackground(table.getSelectionBackground());
        return action;
    }
}