package dao;

import java.time.LocalDate;
import javax.persistence.*;

import lombok.*;

import metier.EtatReparation; // 🔴 IMPORTANT

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reparation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int idReparation;

    private String cause;
    private Double prixTotal;
    private Double avance;
    private Double reste;
    private LocalDate dateDepot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtatReparation etat;

    @ManyToOne
    private Device device;

    @PrePersist
    public void initEtat() {
        if (etat == null) {
            etat = EtatReparation.EN_COURS;
        }
    }
    @ManyToOne
    private Reparateur reparateur;
}
