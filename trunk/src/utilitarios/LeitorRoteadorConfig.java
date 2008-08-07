package utilitarios;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import sistema.No;

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

	private LeitorRoteadorConfig() {

		roteadores = new HashMap<String, No>();
		conteudo = LeitorArquivo.leiaArquivo(nome);
		mapeiaConteudo();
	}

	public Set getIdRoteadores() {
		return roteadores.keySet();
	}

	public String getIP(String id) {
		return roteadores.get(id).getIp();
	}

	public String getPorta(String id) {

		return roteadores.get(id).getPorta();
	}

	public Map<String, No> getRoteadores() {
		return this.roteadores;
	}

	private void mapeiaConteudo() {

		for (int i = 0; i < conteudo.size(); i++) {
			StringTokenizer st = new StringTokenizer(conteudo.get(i), " ");
			String id = st.nextToken();
			String porta = st.nextToken();
			String ip = st.nextToken();
			roteadores.put(id, new No(id, porta, ip));
		}
	}

	public int numeroRoteadores() {
		return roteadores.size();
	}

	public boolean roteadorExiste(String id) {
		return roteadores.containsKey(id);
	}

}
