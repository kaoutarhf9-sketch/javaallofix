package metier;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query; // Import simple (pas TypedQuery)

import org.springframework.stereotype.Service;

import dao.Boutique;
import dao.Proprietaire;
import dao.Reparateur;
import dao.User;

public class GestionUser implements IGestionUser {
	
	private EntityManager em;
	
	public GestionUser() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("AlloFix");
		em = emf.createEntityManager();
	}

	@Override
	public User seConnecter(String cin, String mdp) {
		try {
			
			Query query = em.createQuery("select u from User u where u.cin=:x and u.mdp=:y");
			
			
			query.setParameter("x", cin);
			query.setParameter("y", mdp);
			
		
			return (User) query.getSingleResult();
			
		} catch (Exception e) {
			return null; 
		}
	}

	@Override
	public void inscriptionProprietaire(Proprietaire p) {
		em.getTransaction().begin(); 
		em.persist(p);              
		em.getTransaction().commit();
	}

	@Override
	public void ajouterReparateur(Reparateur r, int idBoutique) {

		Boutique b = em.find(Boutique.class, idBoutique);
		
		if(b != null) {
			r.setBoutique(b); 
			
			em.getTransaction().begin();
			em.persist(r);
			em.getTransaction().commit();
		}
	}

}