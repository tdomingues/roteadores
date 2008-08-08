package sistema;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import utilitarios.LeitorRoteadorConfig;

/**
 * 
 * Classe que representa a tabela utilizada pelo roteador para guardar
 * informacoes relativas ao seu vetor de distancia e suas rotas.
 * 
 * @author Wilson
 * @author Pablo
 * 
 */
public class Tabela {
	
	/**
	 * Representacao de infinito
	 */
	final int INFINITY = 999;

	/**
	 * O roteador ao qual a tabela pertence
	 */
	private Roteador roteador;

	/**
	 * Um mapa contendo a estimativa de distancia para cada roteador existente 
	 * <Roteador, Estimativa de distancia>
	 */
	private Map<String, Integer> mapaDistancias;

	/**
	 * Um mapa contendo o proximo salto para cada roteador existente.
	 * <Roteador, Proximo salto para esse roteador> 
	 */
	private Map<String, String> mapaRotas;

	/**
	 * Um leitor utilizado para colher informacoes do arquivo roteador.config
	 * que contem informacoes sobre os roteadores 
	 */
	private LeitorRoteadorConfig leitorRoteadorConfig;

	/**
	 * @param roteador O roteador ao qual a tabela pertence
	 */
	public Tabela(Roteador roteador) {

		mapaDistancias = new HashMap<String, Integer>();
		mapaRotas = new HashMap<String, String>();
		this.roteador = roteador;
		this.leitorRoteadorConfig = LeitorRoteadorConfig.getInstance();
	}

	
	
	/**
	 * Esse metodo eh utilizado para formatar os valores da tabela
	 * de tal forma que eles sejam impressos alinhados.
	 * @param entrada O valor que se deseja formatar
	 * @return O novo valor formatado 
	 */
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

	/**
	 * Retorna a distancia contida na tabela para um destino especificado
	 * @param idDestino O destino ao qual se deseja obter a sua distancia
	 * @return A distancia para esse destino.
	 */
	public int getDistancia(String idDestino) {
		return mapaDistancias.get(idDestino);
	}

	/**
	 * Retorna o mapa das estimativas de distancia da tabela
	 * @return O mapa das estimativas de distancia da tabela 
	 */
	public Map getMapaDistancia() {
		return this.mapaDistancias;
	}

	/**
	 * Retorna o mapa contendo as rotas
	 * @return O mapa contendo as rotas
	 */
	public Map<String, String> getMapaRotas() {
		return mapaRotas;
	}

	/**
	 * Esse metodo inicializa a tabela que eh feito
	 * lendo quais sao os roteadores existentes no arquivo de 
	 * configuracao e em seguida estando a estimativa de distancia para 
	 * eles como sendo infinito e o proximo salto para chegar a eles como
	 * nulo. Isso eh feito para todos os roteadore, menos o roteador ao qual
	 * a tabela pertence, pois para esse a estimativa de distancia eh zero, e
	 * o proximo salto eh o proprio roteador.
	 */
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

	/**
	 * Seta a nova estimativa de distancia para um roteador.
	 * Ela avisa se esse novo valor eh diferente do ja existente
	 * e portanto se ele foi atualizado ou nao.
	 * @param roteador O roteado ao qual se deseja setar a estimativa de distancia
	 * @param valor O novo valor da estimativa de distancia para esse roteador
	 * @return Retorna true se o novo valor eh diferente existente, e portanto se foi atualizado
	 */
	public boolean setDistancia(String roteador, int valor) {
		boolean atualizou = false;
		if(valor > this.INFINITY){
			valor = this.INFINITY;
		}
		
		int distanciaOrig = mapaDistancias.get(roteador).intValue();
		
		//se o valor atual da estimativa de distancia eh diferente do que 
		//ira ser setado, entao ocorrera uma atualizacao
		if (distanciaOrig != valor) {
			atualizou = true;
		}
		
		mapaDistancias.put(roteador, valor);
		return atualizou;
	}

	/**
	 * Seta o mapa de rotas da tabela
	 * @param mapaRotas O novo mapa de rotas da tabela
	 */
	public void setMapaRotas(Map<String, String> mapaRotas) {
		this.mapaRotas = mapaRotas;
	}

	
	/**
	 * Seta todas as rotas que tem como proximo salto o vizinho que foi 
	 * desligado como nulas, e coloca a estimativa de distancia como infinito.
	 * @param idVizinho O vizinho que foi desligado
	 */
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	//Metodo utilizado para imprimir a tabela
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

}
