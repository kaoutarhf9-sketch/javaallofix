package metier;

import dao.Client;
import dao.Device;
import dao.Reparation;

import javax.persistence.*;
import java.util.List;

public class GestionReparation implements IGestionReparation {

    // ✅ Une seule factory (bonne pratique)
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("reparationPU");

    private EntityManager em;

    public GestionReparation() {
        em = emf.createEntityManager();
    }

    @Override
    public void save(Reparation r) {

        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // 1️⃣ récupérer les entités liées
            Device device = r.getDevice();
            Client client = device.getClient();

            // 2️⃣ générer le code client UNE SEULE FOIS
            if (client.getCodeClient() == null || client.getCodeClient().isEmpty()) {
                client.setCodeClient("CL-" + System.currentTimeMillis());
            }

            // 3️⃣ CLIENT : persist si nouveau, merge sinon
            if (client.getIdClient() == 0) {
                em.persist(client);              // ✅ INSERT garanti
            } else {
                client = em.merge(client);       // ✅ UPDATE
            }

            // 4️⃣ DEVICE : toujours rattaché au client géré
            device.setClient(client);
            device = em.merge(device);

            // 5️⃣ REPARATION
            r.setDevice(device);
            em.persist(r);

            tx.commit();

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void update(Reparation r) {

        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.merge(r);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Reparation r) {

        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.remove(em.merge(r));
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public List<Reparation> findAll() {
        return em.createQuery("FROM Reparation", Reparation.class)
                 .getResultList();
    }
}
