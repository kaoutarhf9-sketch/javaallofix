package dao;

import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ReparateurStat {
	private Reparateur reparateur;
    private long nombreReparations; 
    private double chiffreAffaires; 
    private double partProprietaire;
    private double partReparateur;

}
