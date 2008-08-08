package utilitarios;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import sistema.No;

/**
 * Classe utilizada para ler informacoes do arquivo roteador.config
 * 
 * @author Wilson
 * @author Pablo
 * 
 */
public class LeitorRoteadorConfig {

	private static LeitorRoteadorConfig instancia;

	public static LeitorRoteadorConfig getInstance() {
		if (instancia == null) {
			instancia = new LeitorRoteadorConfig();
		}
		return instancia;
	}

	private List<String> conteudo;

	private String nome = "roteador.config";

	private Map<String, No> roteadores;

	/**
	 * Construtor
	 */
	private LeitorRoteadorConfig() {

		roteadores = new HashMap<String, No>();
		conteudo = LeitorArquivo.leiaArquivo(nome);
		mapeiaConteudo();
	}

	/**
	 * Retorna todos os ids dos roteadores contidos no arquivo
	 * @return Os ids de todos os roteadores contidos no arquivo.
	 */
	public Set getIdRoteadores() {
		return roteadores.keySet();
	}

	/**
	 * Retorna o ip de um roteador especifico.
	 * @param id O id do roteador que se deseja obter seu ip
	 * @return O ip do roteador.
	 */
	public String getIP(String id) {
		return roteadores.get(id).getIp();
	}

	/**
	 * Retorna a porta de um roteador espefico.
	 * @param id O id do roteador que se deseja obter o valor de sua porta
	 * @return O valor da porta desse roteador especificado.
	 */
	public String getPorta(String id) {

		return roteadores.get(id).getPorta();
	}

	/**
	 * Retorna um mapa contendo todos os roteadore do arquivo.
	 * @return O mapa que contem todos os roteadores do arquivo.
	 */
	public Map<String, No> getRoteadores() {
		return this.roteadores;
	}

	/**
	 * Mapeia o conteudo enviado pelo leitor de arquivo 
	 * agrupando as informacoes de cada roteador.
	 */
	private void mapeiaConteudo() {

		for (int i = 0; i < conteudo.size(); i++) {
			StringTokenizer st = new StringTokenizer(conteudo.get(i), " ");
			String id = st.nextToken();
			String porta = st.nextToken();
			String ip = st.nextToken();
			roteadores.put(id, new No(id, porta, ip));
		}
	}

	/**
	 * Retorna o numero de roteadores especificados no arquivo.
	 * @return O numero de roteadores especificados no arquivo.
	 */
	public int numeroRoteadores() {
		return roteadores.size();
	}

	/**
	 * Metodo utilizado para saber se existe um determinado roteador no arquivo de configuracao.
	 * @param id O id do roteador que se deseja verifcar a existencia.
	 * @return True se o roteador existe, false se nao existe.
	 */
	public boolean roteadorExiste(String id) {
		return roteadores.containsKey(id);
	}

}
