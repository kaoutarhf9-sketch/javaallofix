package utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JpaUtil {

    // L'unique usine pour toute l'application (Singleton)
    private static final EntityManagerFactory emf;

    // Bloc statique : exécuté 1 seule fois au lancement de l'app
    static {
        try {
            // "AlloFix" doit être le nom exact dans votre persistence.xml
            emf = Persistence.createEntityManagerFactory("AlloFix");
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    
    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    
    public static void close() {
        if (emf != null && emf.isOpen()) emf.close();
    }
}