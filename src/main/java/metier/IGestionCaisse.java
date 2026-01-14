package metier;

import java.util.List;

import dao.Recouvrement;
import dao.ReparateurStat;

public interface IGestionCaisse {
	
	public List<ReparateurStat> calculerCommissions();
	public List<Recouvrement> getHistoriquePaiements();

}
