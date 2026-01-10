package dao;

import java.util.List;
import javax.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idDevice;

    private String type;
    private String marque;

    // 🔥 CORRECTION IMPORTANTE
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Client client;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
    private List<Reparation> reparations;
}
