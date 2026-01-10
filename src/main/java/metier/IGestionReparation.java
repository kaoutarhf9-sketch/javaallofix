package metier;

import dao.Reparation;
import java.util.List;

public interface IGestionReparation {
    void save(Reparation r);
    void update(Reparation r);
    void delete(Reparation r);
    List<Reparation> findAll();
}
