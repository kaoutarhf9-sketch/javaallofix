package metier;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import dao.Boutique;
import dao.Proprietaire;
import utils.JpaUtil;

public class GestionBoutique implements IGestionBoutique {
	
	private EntityManager em;
	
	public GestionBoutique() {
		this.em = JpaUtil.getEntityManager();
	}

    
    @Override
    public List<Boutique> listerBoutiquesDuProprietaire(int idProprietaire) {
        try {
            // Requête pour trouver les boutiques liées à l'ID du propriétaire
            String jpql = "SELECT b FROM Boutique b WHERE b.proprietaire.idU = :idProp";
            
            TypedQuery<Boutique> query = em.createQuery(jpql, Boutique.class);
            query.setParameter("idProp", idProprietaire);
            
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

	@Override
	public void creerBoutique(Boutique b, int idProprietaire) {
		try {
			em.getTransaction().begin();
			Proprietaire prop = em.find(Proprietaire.class, idProprietaire);
            
            if (prop != null) {
				b.setProprietaire(prop);
				em.persist(b);
				em.getTransaction().commit();
                System.out.println("Boutique créée avec succès pour le proprio #" + idProprietaire);
			} else {
				System.err.println("Propriétaire introuvable avec ID: " + idProprietaire);
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
			em.merge(b);
			em.getTransaction().commit();
		} catch (Exception e) {
			if (em.getTransaction().isActive()) em.getTransaction().rollback();
			e.printStackTrace();
		}
	}

	@Override
	public void supprimerBoutique(int idBoutique) {
		try {
			em.getTransaction().begin();
			Boutique b = em.find(Boutique.class, idBoutique);
			
            if (b != null) {
				em.remove(b);
				em.getTransaction().commit();
				System.out.println("Boutique supprimée.");
			} else {
				System.err.println("Suppression impossible : Boutique introuvable.");
				em.getTransaction().rollback();
			}
		} catch (Exception e) {
			if (em.getTransaction().isActive()) em.getTransaction().rollback();
			e.printStackTrace();
		}
	}

	public Boutique obtenirBoutique(int id) {
		return em.find(Boutique.class, id);
	}

	public List<Boutique> listerToutesLesBoutiques() {
		return em.createQuery("SELECT b FROM Boutique b", Boutique.class).getResultList();
	}
}