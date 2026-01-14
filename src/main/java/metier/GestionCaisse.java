package metier;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import dao.Recouvrement;
import dao.Reparateur;
import dao.ReparateurStat;
import dao.Reparation;
import utils.JpaUtil;

public class GestionCaisse implements IGestionCaisse {

    @Override
    public List<ReparateurStat> calculerCommissions() {
        EntityManager em = JpaUtil.getEntityManager();
        List<ReparateurStat> stats = new ArrayList<>();

        try {
            List<Reparateur> reparateurs = em.createQuery("FROM Reparateur", Reparateur.class).getResultList();

            for (Reparateur rep : reparateurs) {
                
                // 1. Calcul du CA (LIVRÉE + NON ENCAISSÉE)
                // On ajoute : AND r.estEncaissee = false
                String jpql = "SELECT SUM(r.prixTotal) FROM Reparation r " +
                              "WHERE r.reparateur.idU = :id " +
                              "AND r.etat = :etat " +
                              "AND r.estEncaissee = false";
                
                TypedQuery<Double> query = em.createQuery(jpql, Double.class);
                query.setParameter("id", rep.getIdU());
                query.setParameter("etat", EtatReparation.LIVREE);

                Double totalCA = query.getSingleResult();
                if (totalCA == null) totalCA = 0.0;

                // 2. Nombre de dossiers en attente de paiement
                String countJpql = "SELECT COUNT(r) FROM Reparation r " +
                                   "WHERE r.reparateur.idU = :id " +
                                   "AND r.etat = :etat " +
                                   "AND r.estEncaissee = false";
                
                Long nombreDossiers = em.createQuery(countJpql, Long.class)
                        .setParameter("id", rep.getIdU())
                        .setParameter("etat", EtatReparation.LIVREE)
                        .getSingleResult();

                // 3. Calculs parts
                double tauxProprio = (rep.getPourcentage() != null) ? rep.getPourcentage() : 0.0;
                if (tauxProprio > 1.0) tauxProprio = tauxProprio / 100.0;

                double partProprio = totalCA * tauxProprio;
                double partReparateur = totalCA - partProprio;

                ReparateurStat stat = ReparateurStat.builder()
                        .reparateur(rep)
                        .nombreReparations(nombreDossiers)
                        .chiffreAffaires(totalCA)
                        .partProprietaire(partProprio)
                        .partReparateur(partReparateur)
                        .build();

                stats.add(stat);
            }
            return stats;
        } finally {
            em.close();
        }
    }

    // =================================================================
    // 🔥 NOUVELLE MÉTHODE : ENCAISSER L'ARGENT (RESET COMPTEUR)
    // =================================================================
    public void validerEncaissement(Reparateur reparateur, double montantRecupere) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();
            
            // 1. Créer l'historique (Traçabilité)
            Recouvrement rec = Recouvrement.builder()
                .reparateur(reparateur)
                .montantRecupere(montantRecupere)
                .datePaiement(new Date())
                .build();
            em.persist(rec);

            // 2. Mettre à jour les réparations (Remise à zéro du compteur)
            // On cherche toutes les réparations LIVRÉES et NON PAYÉES de ce réparateur
            String jpql = "UPDATE Reparation r SET r.estEncaissee = true " +
                          "WHERE r.reparateur.idU = :id " +
                          "AND r.etat = :etat " +
                          "AND r.estEncaissee = false";
            
            int updatedCount = em.createQuery(jpql)
                    .setParameter("id", reparateur.getIdU())
                    .setParameter("etat", EtatReparation.LIVREE)
                    .executeUpdate();
            
            tx.commit();
            System.out.println("✅ Encaissement validé ! " + updatedCount + " dossiers archivés.");

        } catch (Exception e) {
            if(tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
    
    public List<Recouvrement> getHistoriquePaiements() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // On récupère tout, trié du plus récent au plus ancien
            return em.createQuery("FROM Recouvrement r ORDER BY r.datePaiement DESC", Recouvrement.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }
}