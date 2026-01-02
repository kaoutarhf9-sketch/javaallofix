package dao;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity

public class Proprietaire extends User{
	
	@Column(nullable = false)
	@Builder.Default
    private boolean estReparateur = false;
	@CreationTimestamp
	private LocalDate dateinscription;
	
	@OneToMany(mappedBy = "proprietaire", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Boutique> boutiques;
	

}
