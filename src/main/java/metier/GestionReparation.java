package metier;

import dao.Client;
import dao.Device;
import dao.Reparation;
import utils.JpaUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;

// ✅ IMPORT IMPORTANT POUR L'ÉTAT
import metier.EtatReparation;

public class GestionReparation implements IGestionReparation {

    private EntityManager em;

    public GestionReparation() {
        this.em = JpaUtil.getEntityManager();
    }

    // =====================================================
    // SAUVEGARDE D'UNE NOUVELLE RÉPARATION
    // =====================================================
    @Override
    public void save(Reparation r) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Device device = r.getDevice();
            Client client = device.getClient();

            // Génération du code client si absent
            if (client.getCodeClient() == null || client.getCodeClient().isEmpty()) {
                client.setCodeClient("CL-" + System.currentTimeMillis());
            }

            // Gestion client
            if (client.getIdClient() == 0) {
                em.persist(client);
            } else {
                client = em.merge(client);
            }

            // Gestion device
            device.setClient(client);
            device = em.merge(device);

            // Gestion réparation
            r.setDevice(device);
            em.persist(r);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    // =====================================================
    // MISE À JOUR (CHANGEMENT D'ÉTAT)
    // =====================================================
    @Override
    public void update(Reparation r) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            // ✅ RÈGLE MÉTIER :
            // SI LA RÉPARATION EST LIVRÉE → RESTE = 0
            if (r.getEtat() == EtatReparation.LIVREE) {
                r.setReste(0.0);
                r.setAvance(r.getPrixTotal());
            }

            em.merge(r);
            tx.commit();

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    // =====================================================
    // SUPPRESSION
    // =====================================================
    @Override
    public void delete(Reparation r) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.remove(em.contains(r) ? r : em.merge(r));
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    // =====================================================
    // RÉCUPÉRATION DE TOUTES LES RÉPARATIONS
    // =====================================================
    @Override
    public List<Reparation> findAll() {
        // 🔥 Important pour rafraîchir correctement
        em.clear();

        return em.createQuery(
                "FROM Reparation", Reparation.class
        ).getResultList();
    }
}
