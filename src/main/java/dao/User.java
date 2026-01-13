package dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.validation.constraints.Size; 

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int idU;

    @Column(length = 20, nullable = false) 
    protected String nom;

    @Column(length =20, nullable = false)
    protected String prenom;

    @Column(length = 10, nullable = false) 
    protected String numtel;

    @Column(unique = true, nullable = false) 
    protected String cin;

    @Column(unique = true, nullable = false)
    protected String email;
    
    private String photoPath;

    
    @Size(min = 8) 
    @Column(nullable = false)
    protected String mdp;
}