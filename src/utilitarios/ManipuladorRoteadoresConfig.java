package utilitarios;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;



public class ManipuladorRoteadoresConfig {
	
	private List<String> conteudo; 
	private String nome = "roteador.config";
	private static Map<String, List> mapa;
	private static ManipuladorRoteadoresConfig instancia;
	
	
	private ManipuladorRoteadoresConfig() {
		mapa = new TreeMap<String, List>();
		conteudo = LeitorArquivos.leiaArquivo(nome);
		mapeiaConteudo();
	}
	
	
	public static ManipuladorRoteadoresConfig getInstance(){
		if (instancia == null){
			instancia = new ManipuladorRoteadoresConfig();
		}
		return instancia;
	}

	
	private void mapeiaConteudo() {
		
		for (int i = 0; i < conteudo.size(); i++) {
			StringTokenizer st = new StringTokenizer (conteudo.get(i), " ");
			String id = st.nextToken();
			List<String> a = new ArrayList<String>();
			a.add(st.nextToken());
			a.add(st.nextToken());
			mapa.put(id, a);
		}
	}
	
	
	public static int numeroRoteadores(){
		return mapa.size();
	}
	
	
	public String getPorta(String id){
		return (String)mapa.get(id).get(0);
	}
	
	
	public String getIP(String id){
		return (String)mapa.get(id).get(1);
	}
	
	
	public boolean roteadorExiste(String id){
		return mapa.containsKey(id);
	}
	
	
	public Set getIdRoteadores(){
		return mapa.keySet();
	}
	
}
