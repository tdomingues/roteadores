package sistema;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;





import utilitarios.LeitorRoteadorConfig;



public class Tabela{
	final int INFINITY = 999;
	
	private Roteador roteador;

	private Map<String, Integer> mapaDistancias;
	private Map<String, String> mapaRotas;

	private LeitorRoteadorConfig leitorRoteadorConfig;
	
	public Tabela(Roteador roteador) {
		
		mapaDistancias = new HashMap<String, Integer>();
		mapaRotas = new HashMap<String, String>();
		this.roteador = roteador;
		this.leitorRoteadorConfig = LeitorRoteadorConfig.getInstance();
	}

	
	public void inicializar() {
		/*
		Iterator roteadoresIt = roteadores.keySet().iterator();
		while(roteadoresIt.hasNext()){
			No roteador = roteadores.get(roteadoresIt.next());
			if(roteador.getId().equals(idTab)){
				mapaDistancias.put(roteador.getId(), 0);
			}else
				mapaDistancias.put(roteador.getId(), INFINITY);
		}
		*/
		

		
		Iterator it = leitorRoteadorConfig.getIdRoteadores().iterator();
		while (it.hasNext()) {
			
			String id = (String) it.next();
			if (id.equals(this.roteador.getId())){
				mapaDistancias.put(id, 0);
			    mapaRotas.put(id, id);
			}else{
				mapaDistancias.put(id, INFINITY);
				mapaRotas.put(id, null);
			}
		}
	}

	
	
	public String toString() {
		String saida = "\n" + "Roteador " + this.roteador.getId() + "\n";
		int numRot = this.roteador.getRoteadores().size();
		for (int i = 1; i < numRot + 1; i++) {
			saida += "|  " + i + "  ";
		}
		saida += "|" + "\n" + "| ";
		for (int i = 1; i < numRot + 1; i++) {
			saida += mapaDistancias.get(String.valueOf(i)) + " | ";
		}

		return saida;
	}

	
	

	
	public String comp2actaTabela() {
		String saida = this.roteador.getId() + "#";
		int numRot = this.roteador.getRoteadores().size();
		for (int i = 1; i < numRot + 1; i++) {
			saida += mapaDistancias.get(String.valueOf(i)) + " ";
		}
		return saida;
	}
	
	
	public int getDistancia(String idDestino) {
		return mapaDistancias.get(idDestino);
	}

	
	public void setDistancia(String roteador, int valor) {
		mapaDistancias.put(roteador, valor);
	}
	
	
	public Map getMapaDistancia(){
		return this.mapaDistancias;
	}
	
	
	public void setRotasVizinhoNull(String idVizinho) {
		Iterator it = this.mapaRotas.keySet().iterator();
		while (it.hasNext()) {
		    String destino = (String) it.next();
			if (mapaRotas.get(destino) != null
					&& mapaRotas.get(destino).equals(idVizinho)) {
				mapaRotas.put(destino, null);
				this.setDistancia(destino, INFINITY);
			}
		}
	}


	public Map<String, String> getMapaRotas() {
		return mapaRotas;
	}


	public void setMapaRotas(Map<String, String> mapaRotas) {
		this.mapaRotas = mapaRotas;
	}
	
	
	
}
