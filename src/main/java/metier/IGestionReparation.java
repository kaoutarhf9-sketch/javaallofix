package metier;

import java.util.List;

import dao.Reparation;

public interface IGestionReparation {
    void save(Reparation r);
    void update(Reparation r);
    void delete(Reparation r);
    List<Reparation> findAll();
}
