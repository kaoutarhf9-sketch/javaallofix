package dao;

import java.util.List;
import javax.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idClient;

    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private String photo;
    private String photoPath;

    
    @Column(unique = true)
    private String codeClient; 
    

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Device> devices;
}
