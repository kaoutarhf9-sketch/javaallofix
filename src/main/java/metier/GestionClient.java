package metier;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import dao.Reparation;
import utils.JpaUtil;

public class GestionClient implements IGestionClient {

    @Override
    public List<Reparation> findReparationsByCode(String codeClient) {
        EntityManager em = JpaUtil.getEntityManager();
        
        try {
            
            String jpql = "SELECT r FROM Reparation r WHERE r.device.client.codeClient = :code";
            
            TypedQuery<Reparation> query = em.createQuery(jpql, Reparation.class);
            query.setParameter("code", codeClient);
            
            return query.getResultList();
            
        } catch (Exception e) {
            e.printStackTrace();
            // En cas d'erreur, on retourne une liste vide pour ne pas faire planter l'interface
            return new ArrayList<>(); 
        } finally {
            em.close();
        }
    }
}