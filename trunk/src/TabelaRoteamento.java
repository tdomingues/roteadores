

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import utilitarios.LeitorEnlacesConfig;
import utilitarios.LeitorRoteadorConfig;



public class TabelaRoteamento{
	final int INFINITY = 999;
	
	private String id;

	private LeitorRoteadorConfig manipuladorRot;

	private LeitorEnlacesConfig manipuladorEn;

	private Map<String, Integer> mapaDistancias;

	
	public TabelaRoteamento(String id) {
		manipuladorRot = LeitorRoteadorConfig.getInstance();
		manipuladorEn = LeitorEnlacesConfig.getInstance();
		mapaDistancias = new HashMap<String, Integer>();
		this.id = id;
	}

	
	public void inicializar() {
		Iterator it = manipuladorRot.getIdRoteadores().iterator();
		while (it.hasNext()) {
			
			String id = (String) it.next();
			if (id.equals(id))
				mapaDistancias.put(id, 0);
			else
				mapaDistancias.put(id, INFINITY);
			
		}
	}

	
	public String toString() {
		String saida = "\n" + "Rot" + this.getId() + "\n";
		int numRot = LeitorRoteadorConfig.numeroRoteadores();
		for (int i = 1; i < numRot + 1; i++) {
			saida += "|  " + i + " ";
		}
		saida += "|" + "\n" + "| ";
		for (int i = 1; i < numRot + 1; i++) {
			saida += mapaDistancias.get(String.valueOf(i)) + " | ";
		}

		return saida;
	}

	
	public String getId() {
		return id;
	}

	
	public void setId(String id) {
		this.id = id;
	}

	
	public String compactaTabela() {
		String saida = this.getId() + "$";
		int numRot = LeitorRoteadorConfig.numeroRoteadores();
		for (int i = 1; i < numRot + 1; i++) {
			saida += mapaDistancias.get(String.valueOf(i)) + " ";
		}
		return saida;
	}
	
	
	public int getDistancia(String idDestino) {
		return mapaDistancias.get(idDestino);
	}

	
	public void setDistancia(String id, int valor) {
		mapaDistancias.put(id, valor);
	}
	
	
	public Map getMapaDistancia(){
		return this.mapaDistancias;
	}
	
}
