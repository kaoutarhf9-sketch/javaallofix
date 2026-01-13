package metier;

import dao.Client;
import dao.Device;
import dao.Reparation;
import utils.JpaUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

// ✅ IMPORT IMPORTANT POUR L'ÉTAT
import metier.EtatReparation;

public class GestionReparation implements IGestionReparation {

    
    public GestionReparation() {}

    // =====================================================
    // SAUVEGARDE D'UNE NOUVELLE RÉPARATION
    // =====================================================
    @Override
    public void save(Reparation r) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();

            Device device = r.getDevice();
            Client client = device.getClient();

            // 1. Génération du code client si absent
            if (client.getCodeClient() == null || client.getCodeClient().isEmpty()) {
                client.setCodeClient("CL-" + System.currentTimeMillis());
            }

            // 2. Gestion client (Merge pour éviter "Detached entity passed to persist")
            if (client.getIdClient() == 0) {
                em.persist(client);
            } else {
                client = em.merge(client);
            }

            // 3. Gestion device (Lier au client géré par l'EM)
            device.setClient(client);
            // On utilise merge pour récupérer l'instance gérée par Hibernate
            Device managedDevice = em.merge(device);

            // 4. Gestion réparation
            r.setDevice(managedDevice);
            em.persist(r);

            tx.commit();
            System.out.println("✅ Réparaton enregistrée : " + r.getIdReparation());

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            // Important : On renvoie l'erreur à la vue
            throw new RuntimeException("Erreur sauvegarde : " + e.getMessage()); 
        } finally {
            em.close();
        }
    }

    // =====================================================
    // MISE À JOUR (CHANGEMENT D'ÉTAT)
    // =====================================================
    @Override
    public void update(Reparation r) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();

            // ✅ RÈGLE MÉTIER : SI LIVRÉE → RESTE = 0, AVANCE = TOTAL
            if (r.getEtat() == EtatReparation.LIVREE) {
                r.setReste(0.0);
                r.setAvance(r.getPrixTotal());
            }
            else if (r.getEtat() == EtatReparation.ANNULEE) {
                r.setAvance(0.0); // On rend l'avance au client -> 0 encaissé
                r.setReste(0.0);  // La dette est annulée -> 0 restant
            }

            em.merge(r);
            tx.commit();
            System.out.println("✅ Réparaton mise à jour : " + r.getEtat());

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            throw new RuntimeException("Erreur mise à jour : " + e.getMessage());
        } finally {
            em.close();
        }
    }

    // =====================================================
    // SUPPRESSION
    // =====================================================
    @Override
    public void delete(Reparation r) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();
            // On s'assure que l'objet est attaché avant de le supprimer
            Reparation toDelete = em.find(Reparation.class, r.getIdReparation());
            if (toDelete != null) {
                em.remove(toDelete);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            throw new RuntimeException("Erreur suppression : " + e.getMessage());
        } finally {
            em.close();
        }
    }

    // =====================================================
    // RÉCUPÉRATION DE TOUTES LES RÉPARATIONS
    // =====================================================
    @Override
    public List<Reparation> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("FROM Reparation", Reparation.class).getResultList();
        } finally {
            em.close();
        }
    }

    // =====================================================
    // 🔥 NOUVEAU : FILTRER PAR RÉPARATEUR
    // =====================================================
    public List<Reparation> findByReparateur(int idReparateur) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT r FROM Reparation r WHERE r.reparateur.idU = :id";
            TypedQuery<Reparation> query = em.createQuery(jpql, Reparation.class);
            query.setParameter("id", idReparateur);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }
    
}