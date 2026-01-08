package metier;

import dao.Proprietaire;
import dao.Reparateur;
import dao.User;

public interface IGestionUser {

	public User seConnecter(String cin, String mdp);

	public void inscriptionProprietaire(Proprietaire p);
	
	void modifierUtilisateur(User u);


}