package metier;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import dao.Boutique;
import dao.Reparateur;
import utils.EmailService;
import utils.PasswordUtils;

public class GestionReparateur implements IGestionReparateur {

    private EntityManager em;

    public GestionReparateur() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AlloFix");
        em = emf.createEntityManager();
    }

    @Override
    public void ajouterReparateur(Reparateur r, int idBoutique) {
        try {
            Boutique b = em.find(Boutique.class, idBoutique);

            if (b != null) {
                // Génération Mdp
                String motDePasseGenere = PasswordUtils.generatePassword(8);

                // Config
                r.setBoutique(b);
                r.setMdp(motDePasseGenere);

                // Sauvegarde
                em.getTransaction().begin();
                em.persist(r);
                em.getTransaction().commit();

                System.out.println("Réparateur créé ID: " + r.getIdU());

                
                final String emailDest = r.getEmail(); 
                final String nom = r.getNom();
                final String mdp = motDePasseGenere;

                // 2. Thread sécurisé avec try-catch pour voir les erreurs
                new Thread(() -> {
                    try {
                        if(emailDest != null && emailDest.contains("@")) {
                             EmailService.envoyerEmailReparateur(emailDest, nom, mdp);
                        } else {
                            System.err.println("❌ L'adresse email est invalide ou vide : " + emailDest);
                        }
                    } catch (Exception e) {
                        System.err.println("❌ ERREUR CRITIQUE ENVOI MAIL :");
                        e.printStackTrace();
                    }
                }).start();

            } else {
                System.err.println("Erreur : Boutique introuvable ID " + idBoutique);
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            System.err.println("Erreur Ajout Réparateur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Reparateur> listerReparateursParProprietaire(int idProprietaire) {
        String jpql = "SELECT r FROM Reparateur r WHERE r.boutique.proprietaire.idU = :idProprio";
        Query query = em.createQuery(jpql);
        query.setParameter("idProprio", idProprietaire);
        return query.getResultList();
    }
    
    @Override
    public Reparateur obtenirReparateur(int idReparateur) {
        return em.find(Reparateur.class, idReparateur);
    }

    @Override
    public void modifierReparateur(Reparateur r) {
        try {
            em.getTransaction().begin();
            em.merge(r); // "merge" sert à mettre à jour une entité existante
            em.getTransaction().commit();
            System.out.println("Réparateur modifié ID: " + r.getIdU());
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void supprimerReparateur(int idReparateur) {
        try {
            em.getTransaction().begin();
            Reparateur r = em.find(Reparateur.class, idReparateur);
            if (r != null) {
                em.remove(r);
                System.out.println("Réparateur supprimé ID: " + idReparateur);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        }
    }
}