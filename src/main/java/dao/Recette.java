package dao;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Recette {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private double montant;
	private String typeOperation;
	private LocalDateTime dateOperation;
	private String statut;
	private String partenaire;
	
	@ManyToOne
    private Reparateur reparateur;
	
	

}
