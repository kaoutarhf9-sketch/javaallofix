package dao;


import java.time.LocalDate;
import java.util.Date;

import javax.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reparation  {

    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int idReparation;

    private String cause;
    private Double prixTotal;
    private Double avance;
    private Double reste;
    private LocalDate dateDepot;

    @ManyToOne
    private Device device;
}
