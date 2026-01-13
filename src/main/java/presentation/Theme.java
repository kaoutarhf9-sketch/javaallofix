package presentation;

import java.awt.Color;
import java.awt.Font;

public class Theme {
    public static final Color SIDEBAR_BG = new Color(30, 41, 59);    // Bleu nuit très sombre
    public static final Color BACKGROUND = new Color(241, 245, 249); // Gris très clair (presque blanc)
    public static final Color PANEL_BG = Color.WHITE;
    
    public static final Color PRIMARY = new Color(59, 130, 246);     // Bleu vif
    public static final Color PRIMARY_HOVER = new Color(37, 99, 235);
    
    public static final Color TEXT_SIDEBAR = new Color(148, 163, 184); // Gris clair pour sidebar
    public static final Color TEXT_SIDEBAR_ACTIVE = Color.WHITE;
    
    public static final Color TEXT_HEADLINE = new Color(15, 23, 42); // Presque noir
    public static final Color TEXT_BODY = new Color(51, 65, 85);     // Gris foncé
    
    public static final Color DANGER = new Color(239, 68, 68);       // Rouge moderne

    // Polices
    public static final Font FONT_HERO = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
}