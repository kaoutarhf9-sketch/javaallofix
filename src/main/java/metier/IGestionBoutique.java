package metier;

import java.util.List;
import dao.Boutique;

public interface IGestionBoutique {
	
    // Changement de int -> Long pour correspondre aux ID en base de données
	public void creerBoutique(Boutique b, int idProprietaire);
	
	public void modifierBoutique(Boutique b);
	
    // Changement de int -> Long
	public void supprimerBoutique(int idBoutique);
	
    // Changement de int -> Long
	public Boutique obtenirBoutique(int id);
	
	public List<Boutique> listerToutesLesBoutiques();

    // AJOUT : Cette méthode est requise par votre ViewBoutique
    public List<Boutique> listerBoutiquesDuProprietaire(int idProprietaire);

}