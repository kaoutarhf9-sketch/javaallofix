package dao;

import java.io.Serializable;
import javax.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reparation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idReparation;

    private String cause;
    private Double prixTotal;
    private Double avance;
    private Double reste;

    @ManyToOne
    private Device device;
}
