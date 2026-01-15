package metier;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import dao.Boutique;
import dao.Proprietaire;
import dao.Reparateur; // Import nécessaire
import utils.JpaUtil;

public class GestionBoutique implements IGestionBoutique {
    
    private EntityManager em;
    
    public GestionBoutique() {
        this.em = JpaUtil.getEntityManager();
    }

    @Override
    public List<Boutique> listerBoutiquesDuProprietaire(int idProprietaire) {
        try {
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
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Proprietaire prop = em.find(Proprietaire.class, idProprietaire);
            
            if (prop != null) {
                b.setProprietaire(prop);
                em.persist(b);
                tx.commit();
                System.out.println("Boutique créée avec succès.");
            } else {
                System.err.println("Propriétaire introuvable avec ID: " + idProprietaire);
                tx.rollback();
            }
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void modifierBoutique(Boutique b) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(b);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void supprimerBoutique(int idBoutique) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Boutique b = em.find(Boutique.class, idBoutique);
            
            if (b != null) {
                // --- ÉTAPE CRUCIALE : Libérer les Réparateurs ---
                // On met leur champ 'boutique' à null pour ne pas qu'ils bloquent la suppression
                if (b.getReparateurs() != null && !b.getReparateurs().isEmpty()) {
                    for (Reparateur r : b.getReparateurs()) {
                        r.setBoutique(null); // On détache le réparateur de la boutique
                        em.merge(r);         // On sauvegarde le changement
                    }
                    // On vide la liste locale pour éviter que le CascadeType.ALL ne réagisse mal
                    b.getReparateurs().clear();
                }

                // Maintenant, on peut supprimer la boutique sans erreur SQL
                em.remove(b);
                tx.commit();
                System.out.println("Boutique supprimée avec succès (employés détachés).");
            } else {
                System.err.println("Suppression impossible : Boutique introuvable.");
                tx.rollback();
            }
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            // On relance l'erreur pour que l'interface graphique puisse afficher un message
            throw new RuntimeException("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    public Boutique obtenirBoutique(int id) {
        return em.find(Boutique.class, id);
    }

    public List<Boutique> listerToutesLesBoutiques() {
        return em.createQuery("SELECT b FROM Boutique b", Boutique.class).getResultList();
    }
}