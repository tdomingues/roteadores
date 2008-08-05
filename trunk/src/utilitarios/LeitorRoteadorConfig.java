package utilitarios;




import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import sistema.No;



public class LeitorRoteadorConfig {
	
	private List<String> conteudo; 
	private String nome = "roteador.config";
	private static Map<String, List> mapa;
	private static LeitorRoteadorConfig instancia;
	
	
	private LeitorRoteadorConfig() {
		mapa = new TreeMap<String, List>();
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
	
	
	public Map<String,No> lerNos(){
		Map<String,No> nos = new HashMap<String,No>();
		
		Set idRoteadores = this.getIdRoteadores();
		Iterator roteadoresIt = idRoteadores.iterator();
		while(roteadoresIt.hasNext()){
			String id = (String) roteadoresIt.next();
			String porta = this.getPorta(id);
			String ip = this.getIP(id);
			nos.put(id,new No(id, porta, ip));
		}
		return nos;
	}
	
}
