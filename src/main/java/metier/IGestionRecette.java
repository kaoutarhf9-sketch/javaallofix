package metier;

import dao.Recette;
import java.util.List;

public interface IGestionRecette {
    void ajouterTransaction(Recette r);
    List<Recette> obtenirHistorique(String periode);
    double calculerTotalType(List<Recette> list, String type);
    void marquerCommeRendu(int idRecette); // Nouvelle méthode
}