package sistema;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import utilitarios.LeitorRoteadorConfig;

public class Tabela {
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

		Iterator it = leitorRoteadorConfig.getIdRoteadores().iterator();
		while (it.hasNext()) {

			String id = (String) it.next();
			if (id.equals(this.roteador.getId())) {
				mapaDistancias.put(id, 0);
				mapaRotas.put(id, id);
			} else {
				mapaDistancias.put(id, INFINITY);
				mapaRotas.put(id, null);
			}
		}
	}

	public String toString() {
		String saida = "\n" + "Roteador " + this.roteador.getId() + "\n";
		int numRot = this.roteador.getRoteadores().size();
		for (int i = 1; i < numRot + 1; i++) {
			saida += "|" + formata(String.valueOf(i));
		}
		saida += "|" + "\n" + "|";
		for (int i = 1; i < numRot + 1; i++) {
			saida += formata(String.valueOf(mapaDistancias.get(String
					.valueOf(i))))
					+ "|";
		}

		return saida;
	}

	private String formata(String entrada) {
		String saida = entrada;

		if (entrada.length() == 5) {
			saida = entrada;
		} else if (entrada.length() == 4) {
			saida = entrada + " ";
		} else if (entrada.length() == 3) {
			saida = " " + entrada + " ";
		} else if (entrada.length() == 2) {
			saida = " " + entrada + "  ";
		} else if (entrada.length() == 1) {
			saida = "  " + entrada + "  ";
		}

		return saida;

	}

	public int getDistancia(String idDestino) {
		return mapaDistancias.get(idDestino);
	}

	public boolean setDistancia(String roteador, int valor) {
		boolean atualizou = false;
		if (mapaDistancias.get(roteador) != valor) {
			atualizou = true;
		}
		mapaDistancias.put(roteador, valor);
		return atualizou;
	}

	public Map getMapaDistancia() {
		return this.mapaDistancias;
	}

	// Seta todas as rotas que tem como proximo salto o vizinho que foi
	// desligado como nulas
	public void setRotasVizinhoNull(String idVizinho) {
		Iterator it = this.mapaRotas.keySet().iterator();
		while (it.hasNext()) {
			String destino = (String) it.next();
			// Se o proximo salto para o destino for o vizinho desligado
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
