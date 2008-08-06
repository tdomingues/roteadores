package utilitarios;




import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import sistema.No;



public class LeitorRoteadorConfig {
	
	private List<String> conteudo; 
	private String nome = "roteador.config";
	private Map<String, No> roteadores;
	private static LeitorRoteadorConfig instancia;
	
	
	private LeitorRoteadorConfig() {
		
		roteadores = new HashMap<String, No>();
		conteudo = LeitorArquivo.leiaArquivo(nome);
		mapeiaConteudo();
	}
	
	
	public static LeitorRoteadorConfig getInstance(){
		if (instancia == null){
			instancia = new LeitorRoteadorConfig();
		}
		return instancia;
	}

	
	private void mapeiaConteudo() {
		
		for (int i = 0; i < conteudo.size(); i++) {
			StringTokenizer st = new StringTokenizer (conteudo.get(i), " ");
			String id = st.nextToken();
			String porta = st.nextToken();
			String ip = st.nextToken();
			roteadores.put(id, new No(id, porta, ip));
		}
	}
	
	
	public int numeroRoteadores(){
		return roteadores.size();
	}
	
	
	public String getPorta(String id){
		
		return roteadores.get(id).getPorta();
	}
	
	
	public String getIP(String id){
		return roteadores.get(id).getIp();
	}
	
	
	public boolean roteadorExiste(String id){
		return roteadores.containsKey(id);
	}
	
	
	public Set getIdRoteadores(){
		return roteadores.keySet();
	}
	
	public Map<String, No> getRoteadores(){
		return this.roteadores;
	}
	
	
}
