package metier;

import dao.Reparateur;
import java.util.List;

public interface IGestionReparateur {
    
    
    void ajouterReparateur(Reparateur r, int idBoutique);

    // Je vous suggère d'ajouter celle-ci pour votre Dashboard (optionnel pour l'instant)
    List<Reparateur> listerReparateursParProprietaire(int idProprietaire);
    void supprimerReparateur(int idReparateur);
    void modifierReparateur(Reparateur r);
    Reparateur obtenirReparateur(int idReparateur);
}