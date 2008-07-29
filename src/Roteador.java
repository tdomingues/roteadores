import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;




import utilitarios.LeitorEnlacesConfig;
import utilitarios.LeitorRoteadorConfig;




public class Roteador extends TimerTask {
	final int INFINITY = 999;
	private String id;
	private String porta;
	private String IP;
	private Tabela tabela; 
	private LeitorRoteadorConfig manipuladorRot;
	private LeitorEnlacesConfig manipuladorEn;
	private Servidor server;
	
	private Map<String,String> statusVizinhos;
	
	private Map<String,String> rotas;

	public Roteador(String id) {
		this.id = id;
		manipuladorRot = LeitorRoteadorConfig.getInstance(); 
		manipuladorEn = LeitorEnlacesConfig.getInstance(); 
		this.porta = manipuladorRot.getPorta(this.id); 
		this.IP = manipuladorRot.getIP(this.id); 
		this.tabela = new Tabela(this.id); 
		tabela.inicializar(); 
		statusVizinhos = new HashMap<String, String>();
		rotas = new HashMap<String, String>();
		inicializar();
	}
	
	
	public void inicializar() {
		inicializarRotas(); 
		Timer timer = new Timer();
		server = new Servidor(this);
		server.start(); 
		ligarVizinhos();
		timer.schedule(this, 300, 6000); 
		
	}
	
	
	private void inicializarRotas() {
		for ( String roteador : todosOsRoteadores() ) {
			rotas.put(roteador, null);
		}
	}
	
	
	public void run(){
		enviaParaVizinhos(); 
		System.out.println( horaSistema() + " tabela: " + getTabelaImprimir() );
	}
	
	
	private void ligarVizinhos() {
		List<String> vizinhos = getVizinhos(); 
		for ( String id : vizinhos ) {
			statusVizinhos.put(id, "ligado");
		}		
	}
	
	
	public void setVizinhoLigado(String idVizinho){
		statusVizinhos.put(idVizinho, "ligado");
	}
	
	
	public void setVizinhoDesligado(String idVizinho){
		statusVizinhos.put(idVizinho, "desligado");
	}
	
	
	public void enviaParaVizinhos() {
		List<String> vizinhos = getVizinhos();
		for ( String id : vizinhos ) {
			if ( statusVizinhos.get(id).equals("ligado") ) {
				
				Cliente c = new Cliente(this, id, manipuladorRot.getIP(id), manipuladorRot.getPorta(id));
				System.out.println("Enviando pacote para Vizinho ID: " + id );
			}
			
		}
	}
	
	
	public List<String> getVizinhos() {
		return manipuladorEn.getVizinhos(this.id);
	}
	
	
	public String getId() {
		return id;
	}

	
	public void setId(String id) {
		this.id = id;
	}

	
	public String getIP() {
		return IP;
	}

	
	public void setIP(String ip) {
		this.IP = ip;
	}

	
	public String getPorta() {
		return porta;
	}

	
	public void setPorta(String porta) {
		this.porta = porta;
	}
	
	
	public Tabela getTabela() {
		return tabela;
	}
	
	
	public String geraTabelaString() {
		String saida = this.getId() + "$";
		for ( String roteador : todosOsRoteadores() ) {
			saida += this.tabela.getMapaDistancia().get(roteador) +
			"-" + rotas.get(roteador)+ " ";
		}
		return saida;
	}
	
	
	public void atualizarTabela(String idVizinho, String dados) {
		
		StringTokenizer st = new StringTokenizer(dados);
		
		for ( String roteador : todosOsRoteadores() ) {
			StringTokenizer distEsalt = new StringTokenizer(st.nextToken(), "-");
			
			
			int distanciaTabelaRemota = Integer.parseInt( distEsalt.nextToken() );
			
			String proxSalto = distEsalt.nextToken(); 
			
			if( !roteador.equals(this.id) ) {
				
				
				if ( manipuladorEn.ehVizinho(this.id, roteador) ) {
					
					int distanciaFisica = manipuladorEn.getCusto(this.getId(), roteador);
					
					boolean ligado = statusVizinhos.get(roteador).equals("ligado");
					
					if ( distanciaFisica < getDistancia(roteador) ) { 
						if ( !ligado ) {
							setDistancia( roteador, INFINITY ); 
							this.adicionarRota(roteador, null); 
							continue; 
						}
						
						setDistancia( roteador,  distanciaFisica ); 
						if ( roteador.equals(idVizinho) ) {
							this.adicionarRota(roteador, idVizinho);
							
						} else {
							this.adicionarRota(roteador, roteador);
						}
					}
				
				}
				
				
				if ( (distanciaTabelaRemota == INFINITY && idVizinho.equals(rotas.get(roteador)) )
						|| (rotas.get(roteador) == null) ) {
					setDistancia( roteador, INFINITY );
					rotas.put( roteador, null);
				}

				
				int distanciaTabelaLocal = getDistancia(roteador);
				
				
				int distanciaVizinho = getDistancia(idVizinho);
				
				
				if( proxSalto.equals(this.id) ){
					continue;
				}
				
				
				if ( distanciaTabelaRemota + distanciaVizinho < distanciaTabelaLocal ) {
					
					setDistancia(roteador, distanciaTabelaRemota + distanciaVizinho);
					this.adicionarRota(roteador, idVizinho);
					
				}

			}
		}
		
	}
	
	
	public String horaSistema() {
		GregorianCalendar calendario = new GregorianCalendar();
		int hora = calendario.get(Calendar.HOUR_OF_DAY);
		int min = calendario.get(Calendar.MINUTE);
		int sec = calendario.get(Calendar.SECOND);
		return  hora + ":" +  min + ":" + sec;
		
	}
	
	
	public String getTabelaImprimir(){
		return this.tabela.toString();
	}
	
	
	public int getDistancia(String idDestino) {
		return tabela.getDistancia(idDestino);
	}
	
	
	public void setDistancia(String id, int valor) {
		tabela.setDistancia(id, valor);
	}
	
	
	private List<String> todosOsRoteadores() {
		return new ArrayList<String>(manipuladorRot.getIdRoteadores());
	}
	
	
	public void adicionarRota(String idDestino,String  proximoSalto){
		rotas.put(idDestino, proximoSalto);
	}
	
	
	public void setRotasVizinhoNull(String idVizinho) {
		Iterator it = this.rotas.keySet().iterator();
		while( it.hasNext()){
			String destino = (String)it.next();
			if( rotas.get(destino) != null && rotas.get(destino).equals(idVizinho)){
				rotas.put(destino, null);
				this.setDistancia(destino, INFINITY);
			}
		}
	}
}
