package metier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import dao.Recette;
import utils.JpaUtil;

public class GestionRecette implements IGestionRecette {
    private EntityManager em;

    public GestionRecette() {
        this.em = JpaUtil.getEntityManager();
    }

    @Override
    public void ajouterTransaction(Recette r) {
        try {
            em.getTransaction().begin();
            
            // Correction : Utiliser LocalDateTime.now() au lieu de LocalDate
            if (r.getDateOperation() == null) {
                r.setDateOperation(LocalDateTime.now());
            }
            
            em.persist(r);
            em.getTransaction().commit();
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public List<Recette> obtenirHistorique(String periode) {
        // On définit le début à 00:00:00 de la période choisie
        LocalDateTime debut;
        // On définit la fin à 23:59:59 d'aujourd'hui
        LocalDateTime fin = LocalDateTime.now().with(LocalTime.MAX);

        if ("SEMAINE".equalsIgnoreCase(periode)) {
            debut = LocalDate.now().with(java.time.DayOfWeek.MONDAY).atStartOfDay();
        } else if ("MOIS".equalsIgnoreCase(periode)) {
            debut = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
        } else {
            // "JOUR" : Aujourd'hui à 00:00:00
            debut = LocalDate.now().atStartOfDay();
        }

        TypedQuery<Recette> query = em.createQuery(
            "SELECT r FROM Recette r WHERE r.dateOperation BETWEEN :debut AND :fin ORDER BY r.dateOperation DESC", 
            Recette.class
        );
        query.setParameter("debut", debut);
        query.setParameter("fin", fin);

        return query.getResultList();
    }

    @Override
    public double calculerTotalType(List<Recette> list, String type) {
        double total = 0;
        if (list != null) {
            for (Recette r : list) {
                if (r.getTypeOperation() != null && r.getTypeOperation().equalsIgnoreCase(type)) {
                    total += r.getMontant();
                }
            }
        }
        return total;
    }
}