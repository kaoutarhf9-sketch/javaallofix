package dao;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity

public class Boutique {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idb;
	@Column(length = 30)
	private String nomB;
	@Column(length = 100)
	private String adresse;
	@Column(length = 30, unique = true)
	private String patente;
	@Column(length = 10)
	private String numtel;
	@CreationTimestamp
	private LocalDate datecreation;
	
	@ManyToOne
	private Proprietaire proprietaire;
	
	@OneToMany(mappedBy = "boutique", cascade = CascadeType.ALL)
	@ToString.Exclude
	private List<Reparateur> reparateurs;

}
