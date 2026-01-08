package presentation;

import java.awt.Color;
import java.awt.Font;

public class Theme {
    // ========================================================================================
    //                                      PALETTE DE COULEURS
    // ========================================================================================
    
    // --- FOND & SURFACES ---
    // Un blanc cassé très subtil (Slate 50), plus doux pour les yeux que le blanc pur #FFFFFF
    public static final Color BACKGROUND = new Color(248, 250, 252); 
    public static final Color SURFACE = new Color(255, 255, 255);
    
    // --- TEXTE ---
    // Noir bleuté profond pour les titres (Slate 900)
    public static final Color TEXT_HEADLINE = new Color(15, 23, 42); 
    // Gris moyen pour le texte courant (Slate 500)
    public static final Color TEXT_BODY = new Color(100, 116, 139);  
    public static final Color TEXT_LIGHT = new Color(255, 255, 255);
    
    // --- DÉGRADÉ DE MARQUE (BRANDING) ---
    // C'est ce qui donne le look "Tech/Startup"
    public static final Color GRADIENT_START = new Color(79, 70, 229); // Indigo vibrant
    public static final Color GRADIENT_END = new Color(124, 58, 237);   // Violet profond
    
    // Pour les éléments unis (si pas de dégradé)
    public static final Color PRIMARY = GRADIENT_START;
    public static final Color PRIMARY_HOVER = new Color(67, 56, 202); // Indigo plus sombre
    public static final Color NAVY = new Color(30, 41, 59); // Bleu nuit corporate

    // --- ACCENTS & ÉTATS ---
    public static final Color SUCCESS = new Color(16, 185, 129); // Vert Émeraude
    public static final Color DANGER = new Color(239, 68, 68);   // Rouge Vif
    public static final Color WARNING = new Color(245, 158, 11); // Orange Ambre
    
    // ========================================================================================
    //                                      TYPOGRAPHIE
    // ========================================================================================
    
    // Titre géant (Page d'accueil)
    public static final Font FONT_HERO = new Font("Segoe UI", Font.BOLD, 48);
    
    // Titres de sections / Cartes
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    
    // Texte standard (Corps de texte)
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 15);
    
    // Texte en gras (Labels, Boutons)
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
}