package dao;

import java.util.Date;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Recouvrement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne
    private Reparateur reparateur; // Qui a payé ?
    
    private double montantRecupere; // Combien le proprio a pris ?
    
    
    private Date datePaiement; // Quand ?
}