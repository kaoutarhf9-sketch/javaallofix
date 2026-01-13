package metier;

import java.util.List;
import dao.Reparation;

public interface IGestionClient {

   
    public List<Reparation> findReparationsByCode(String codeClient);

}