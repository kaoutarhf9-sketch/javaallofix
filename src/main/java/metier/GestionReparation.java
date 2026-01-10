package metier;

import dao.Client;
import dao.Device;
import dao.Reparation;
import utils.JpaUtil;

import javax.persistence.*;
import java.util.List;

public class GestionReparation implements IGestionReparation {

    private EntityManager em;

    public GestionReparation() {
        this.em = JpaUtil.getEntityManager();
    }

    @Override
    public void save(Reparation r) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            // 1️⃣ Récupérer les entités liées
            Device device = r.getDevice();
            Client client = device.getClient();

            // 2️⃣ Générer le code client si absent
            if (client.getCodeClient() == null || client.getCodeClient().isEmpty()) {
                client.setCodeClient("CL-" + System.currentTimeMillis());
            }

            // 3️⃣ CLIENT : persist ou merge
            if (client.getIdClient() == 0) {
                em.persist(client);
            } else {
                client = em.merge(client);
            }

            // 4️⃣ DEVICE : Lier au client géré
            device.setClient(client);
            device = em.merge(device);

            // 5️⃣ REPARATION : Lier au device géré
            r.setDevice(device);
            em.persist(r);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
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
            // Petite astuce : on merge avant de remove pour être sûr que l'objet est attaché
            em.remove(em.contains(r) ? r : em.merge(r));
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public List<Reparation> findAll() {
        // 🔥 LA SOLUTION AU PROBLÈME DE RAFRAÎCHISSEMENT 🔥
        // On vide le cache de l'EntityManager pour être sûr de récupérer 
        // les données fraîchement insérées par les autres onglets.
        em.clear(); 
        
        return em.createQuery("FROM Reparation", Reparation.class)
                 .getResultList();
    }
}