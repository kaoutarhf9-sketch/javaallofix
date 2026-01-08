package metier;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.NoResultException;
import javax.persistence.RollbackException;

import dao.Proprietaire;
import dao.User;

public class GestionUser implements IGestionUser {
    
    private EntityManager em;
    
    public GestionUser() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AlloFix");
        em = emf.createEntityManager();
    }

    @Override
    public User seConnecter(String cin, String mdp) {
        System.out.println("--- DIAGNOSTIC CONNEXION ---");
        System.out.println("Login reçu : " + cin);
        
        try {
            Query checkQuery = em.createQuery("SELECT count(u) FROM User u WHERE u.cin = :cin");
            checkQuery.setParameter("cin", cin);
            Long count = (Long) checkQuery.getSingleResult();
            
            if (count == 0) {
                System.err.println("ERREUR : Aucun compte trouvé avec le CIN '" + cin + "'.");
                return null;
            }

            Query query = em.createQuery("SELECT u FROM User u WHERE u.cin = :cin AND u.mdp = :mdp");
            query.setParameter("cin", cin);
            query.setParameter("mdp", mdp);
        
            User u = (User) query.getSingleResult();
            System.out.println("SUCCÈS : Connecté en tant que " + u.getNom());
            return u;
            
        } catch (NoResultException e) {
            System.err.println("ECHEC : Le mot de passe est incorrect.");
            return null;
        } catch (Exception e) {
            System.err.println("ECHEC TECHNIQUE GRAVE :");
            e.printStackTrace(); 
            return null;
        }
    }

    @Override
    public void inscriptionProprietaire(Proprietaire p) {
        try {
            em.getTransaction().begin();
            
    
            if (p.getEmail() == null || p.getEmail().isEmpty()) {
                p.setEmail(p.getCin());
            }

            em.persist(p);
            em.getTransaction().commit();
            
            System.out.println("Inscription réussie pour : " + p.getNom());

        } catch (RollbackException e) {
            
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            
            
            Throwable cause = e.getCause();
            while (cause != null) {
                if (cause.getMessage() != null && cause.getMessage().contains("Duplicate entry")) {
                    System.err.println("ERREUR : Le CIN ou l'Email existe déjà !");
                    throw new RuntimeException("Ce CIN ou cet Email est déjà utilisé par un autre compte.");
                }
                cause = cause.getCause();
            }
            
            e.printStackTrace();
            throw new RuntimeException("Erreur technique lors de l'inscription.");
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            System.err.println("Erreur Inscription : " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur inconnue : " + e.getMessage());
        }
    }
    
    @Override
    public void modifierUtilisateur(User u) {
        try {
            em.getTransaction().begin();
            em.merge(u); // "Merge" met à jour l'enregistrement existant
            em.getTransaction().commit();
            System.out.println("Utilisateur modifié : " + u.getNom());
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la modification du profil.");
        }
    }
}