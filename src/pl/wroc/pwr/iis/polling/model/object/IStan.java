package pl.wroc.pwr.iis.polling.model.object;

import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;

public interface IStan {
	public enum Porownanie {
		Lepszy, Rowny, Gorszy, NieMoznaPorownac
	};
	
	/**
	 * Zwraca stan serwera. Stan serwera jest częściowo 
	 * obserwowalny. To znaczy, że rzeczywiste dwa stany
	 * serwera będą rozpoznawane przez agenta jako 
	 * jeden i ten sam stan.
	 * 
	 * @param serwer Serwer którego stan chcemy uzyskać
	 * 
	 * @return stan reprezentowany przez tablicę wartości int
	 */
	int[] getStan(Serwer serwer);
	
	/**
	 * Metoda służąca do porównania relacji pomiędzy dwoma stanami.
	 * 
	 * @param serwer Serwer ktorego stan chcemy porównac
	 * @param stan Stan który chcemy porownać z aktualnym stanem serwera
	 * @return Wartosć porównania
	 */
	Porownanie compare(Serwer serwer, int[] stan);
	
	/**
	 * Porównóje dwa stany i podaje wspolczynnik różnicy między nimi.
	 * Neleży pamiętać aby przed tą metodą wywołać compara i sprawdzić
	 * czy stany można wogóle porównywać. W przypadku kiedy stany są
	 * nieporównywalne lub różnica między nimi jest niemieżalna
	 * zwracana jest wartość 0
	 * 
	 * @param serwer
	 * @param stan
	 * @return
	 */
	float compareWspolczynnik(Serwer serwer, int[] stan);

	int[] getMaxStanow(Serwer serwer);
}
