package metier;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import dao.Boutique;
import dao.Proprietaire;

public class GestionBoutique implements IGestionBoutique {
	
	private EntityManager em;
	
	public GestionBoutique() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("AlloFix");
		em = emf.createEntityManager();
	}

	@Override
	public void creerBoutique(Boutique b, int idProprietaire) {
		try {
			em.getTransaction().begin();
			// On cherche le propriétaire pour l'associer à la nouvelle boutique
			Proprietaire prop = em.find(Proprietaire.class, idProprietaire);
			if (prop != null) {
				b.setProprietaire(prop);
				em.persist(b);
				em.getTransaction().commit();
			} else {
				System.out.println("Propriétaire introuvable !");
				em.getTransaction().rollback();
			}
		} catch (Exception e) {
			if (em.getTransaction().isActive()) em.getTransaction().rollback();
			e.printStackTrace();
		}
	}

	@Override
	public void modifierBoutique(Boutique b) {
		try {
			em.getTransaction().begin();
			// merge permet de mettre à jour une entité détachée dans la base de données
			em.merge(b);
			em.getTransaction().commit();
			System.out.println("Boutique mise à jour avec succès.");
		} catch (Exception e) {
			if (em.getTransaction().isActive()) em.getTransaction().rollback();
			e.printStackTrace();
		}
	}

	@Override
	public void supprimerBoutique(int idBoutique) {
		try {
			em.getTransaction().begin();
			// En JPA, on doit d'abord trouver l'objet avant de le supprimer
			Boutique b = em.find(Boutique.class, idBoutique);
			if (b != null) {
				em.remove(b);
				em.getTransaction().commit();
				System.out.println("Boutique supprimée.");
			} else {
				System.out.println("Suppression impossible : Boutique inexistante.");
				em.getTransaction().rollback();
			}
		} catch (Exception e) {
			if (em.getTransaction().isActive()) em.getTransaction().rollback();
			e.printStackTrace();
		}
	}

	// Méthode utile pour récupérer une boutique ou lister
	public Boutique obtenirBoutique(int id) {
		return em.find(Boutique.class, id);
	}

	public List<Boutique> listerToutesLesBoutiques() {
		return em.createQuery("SELECT b FROM Boutique b", Boutique.class).getResultList();
	}
}