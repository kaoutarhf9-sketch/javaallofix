package metier;

import java.util.List;
import javax.persistence.*;

import dao.Boutique;
import dao.Reparateur;
import utils.EmailService;
import utils.JpaUtil; 
import utils.PasswordUtils;

public class GestionReparateur implements IGestionReparateur {

    public GestionReparateur() {}

    @Override
    public void ajouterReparateur(Reparateur r, int idBoutique) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // --- 1. VERIFICATION PRÉALABLE (Anti-Doublon) ---
            // On vérifie manuellement si le CIN ou l'Email existe déjà dans la table User
            // Cela évite l'erreur violente "GenericJDBCException"
            Long count = em.createQuery("SELECT count(u) FROM User u WHERE u.cin = :cin OR u.email = :email", Long.class)
                    .setParameter("cin", r.getCin())
                    .setParameter("email", r.getEmail())
                    .getSingleResult();

            if (count > 0) {
                throw new RuntimeException("Doublon détecté : Un utilisateur avec ce CIN ou cet Email existe déjà.");
            }

            // --- 2. RECUPERATION BOUTIQUE ---
            Boutique b = em.find(Boutique.class, idBoutique);
            if (b == null) {
                throw new RuntimeException("La boutique sélectionnée (ID " + idBoutique + ") n'existe pas.");
            }

            // --- 3. CONFIGURATION ---
            String password = PasswordUtils.generatePassword(8);
            r.setMdp(password);
            r.setBoutique(b);
            
            // Si vous avez un champ 'role' dans User, décommentez ceci :
            // r.setRole("REPARATEUR"); 

            // --- 4. SAUVEGARDE ---
            em.persist(r);
            tx.commit();
            
            System.out.println("✅ Réparateur créé : " + r.getNom() + " (ID: " + r.getIdU() + ")");

            // --- 5. EMAIL (Async) ---
            new Thread(() -> {
                try {
                    EmailService.envoyerEmailReparateur(r.getEmail(), r.getNom(), password);
                } catch (Exception e) {
                    System.err.println("Compte créé, mais erreur d'envoi email : " + e.getMessage());
                }
            }).start();

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            // On relance l'erreur pour que l'interface graphique puisse l'afficher
            throw new RuntimeException(e.getMessage());
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    @Override
    public void modifierReparateur(Reparateur r) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // Pour la modif, on devrait idéalement vérifier aussi les doublons si l'email change
            // mais on va faire simple ici :
            em.merge(r);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erreur modification : " + e.getMessage());
        } finally {
            em.close();
        }
    }

    @Override
    public List<Reparateur> listerReparateursParProprietaire(int idProprietaire) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("SELECT r FROM Reparateur r WHERE r.boutique.proprietaire.idU = :idProprio", Reparateur.class)
                     .setParameter("idProprio", idProprietaire)
                     .getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public Reparateur obtenirReparateur(int idReparateur) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.find(Reparateur.class, idReparateur);
        } finally {
            em.close();
        }
    }

    @Override
    public void supprimerReparateur(int idReparateur) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Reparateur r = em.find(Reparateur.class, idReparateur);
            if (r != null) em.remove(r);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}