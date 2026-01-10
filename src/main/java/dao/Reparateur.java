package dao;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class Reparateur extends User{
	
	private Double pourcentage;
	
	@ManyToOne
	private Boutique boutique;
	
	@OneToMany
	private List<Recette> recette;
		

}
