package metier;

import java.util.List;

import dao.Boutique;

public interface IGestionBoutique {
	
	public void creerBoutique(Boutique b, int idProprietaire);
	public void modifierBoutique(Boutique b);
	public void supprimerBoutique(int idBoutique);
	public Boutique obtenirBoutique(int id);
	public List<Boutique> listerToutesLesBoutiques() ;


}
