package utilitarios;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;



public class LeitorEnlacesConfig{
	final int INFINITY = 99;
	
	
	private List<String> conteudo; 
	private final String nome = "enlaces.config";
	private Map<List, String> mapa; 
	private static LeitorEnlacesConfig instancia;
	
	
	private LeitorEnlacesConfig(){
		mapa = new HashMap<List, String>();
		conteudo = LeitorArquivo.leiaArquivo(nome);
		this.mapeiaConteudo();
	}
	
	
	public static LeitorEnlacesConfig getInstance(){
		if (instancia == null){
			instancia = new LeitorEnlacesConfig();
		}
		return instancia;
		
	}
	
	
	private void mapeiaConteudo() {

		for (int i = 0; i < conteudo.size(); i++) {
			StringTokenizer st = new StringTokenizer (conteudo.get(i), " ");
			List<String> a = new ArrayList<String>();
			a.add(st.nextToken());
			a.add(st.nextToken());
			String id = st.nextToken();
			mapa.put(a, id);
		}
		
	}
	
	
	public List getVizinhos(String id) {
		List<String> vizinhos = new ArrayList<String>();
		Set <List> chaves = mapa.keySet();
		Iterator it = chaves.iterator();
		while(it.hasNext()) {
			List enlace = (List)it.next();
			
			if(enlace.get(0).equals(id))
				vizinhos.add((String)enlace.get(1));
			else if(enlace.get(1).equals(id))
				vizinhos.add((String)enlace.get(0));
			
		}
		return vizinhos;
	}
	
	
	public int getCusto(String id1, String id2){
		if (id1.equals(id2)) 
			return 0;

		
		List<String> enlace1 = new ArrayList<String>();
		enlace1.add(id1);
		enlace1.add(id2);
		if(mapa.containsKey(enlace1))
			return Integer.parseInt(mapa.get(enlace1));

		List<String> enlace2 = new ArrayList<String>();
		enlace2.add(id2);
		enlace2.add(id1);
		if(mapa.containsKey(enlace2))
			return Integer.parseInt(mapa.get(enlace2));

		return INFINITY;
	}
	
	
	public boolean ehVizinho(String id1, String id2){
		if (id1.equals(id2))
			return false;
		
		List<String> vizinhos = this.getVizinhos(id1);
		return vizinhos.contains(id2);
	}
}
