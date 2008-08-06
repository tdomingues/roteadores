package sistema;

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

public class Roteador extends TimerTask{
	final int INFINITY = 999;

	private String id;

	private String porta;

	private String IP;

	private Tabela tabela;

	private Map<String, Vizinho> vizinhos;

	private Map<String, No> roteadores;

	private LeitorRoteadorConfig manipuladorRot;
	private LeitorEnlacesConfig manipuladorEn;

	private Servidor server;

	

	

	public Roteador(String id) {
		this.id = id;

		LeitorRoteadorConfig leitorRoteadorConfig = LeitorRoteadorConfig
				.getInstance();
		this.roteadores = leitorRoteadorConfig.getRoteadores();
		this.porta = leitorRoteadorConfig.getPorta(this.id);
		this.IP = leitorRoteadorConfig.getIP(this.id);
		
		
		LeitorEnlacesConfig leitorEnlacesConfig = LeitorEnlacesConfig
		.getInstance(this);
		this.vizinhos = leitorEnlacesConfig.getVizinhos();
		

		

		

		this.tabela = new Tabela(this);
		tabela.inicializar();

		
		
		inicializar();
		if (true) {
			System.out.println(horaSistema() + " tabela: "
					+ getTabelaImprimir());
		}
	}

	public void inicializar() {
		
		Timer timer = new Timer();
		server = new Servidor(this);
		server.start();
		ligarVizinhos();
		
		timer.schedule(this, 300, 6000);

	}

	

	

	

	public void run() {
		enviaParaVizinhos();
		
	}

	private void ligarVizinhos() {
		Iterator vizinhosIt = vizinhos.keySet().iterator();
		while(vizinhosIt.hasNext()){
			Vizinho vizinho = vizinhos.get(vizinhosIt.next());
			vizinho.setLigado(true);
		}
	
	}

	public void setVizinhoLigado(String idVizinho) {
		Vizinho vizinho = vizinhos.get(idVizinho);
		vizinho.setLigado(true);
	}

	public void setVizinhoDesligado(String idVizinho) {
		Vizinho vizinho = vizinhos.get(idVizinho);
		vizinho.setLigado(false);
	}

	public void enviaParaVizinhos() {
		System.out.println(horaSistema() + ": Enviando tabela para vizinhos ...");
		Iterator vizinhosIt = vizinhos.keySet().iterator();
		while (vizinhosIt.hasNext()) {
			Vizinho vizinho = vizinhos.get(vizinhosIt.next());
			if (vizinho.isLigado()) {
                System.out.println("vizinho " + vizinho.getId()); 
				Cliente cliente = new Cliente(this, vizinho.getId(), vizinho
						.getIp(), vizinho.getPorta());
			}
		}

		
	}

	public Map<String, Vizinho> getVizinhos() {
		return this.vizinhos;
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
		String saida = this.getId() + "#";
		int numeroRoteadores = this.roteadores.size();
		for(int i=1; i <= numeroRoteadores; i ++){
			String noId = String.valueOf(i);
			saida += this.tabela.getMapaDistancia().get(noId) + "-"
			+ this.tabela.getMapaRotas().get(noId) + " ";
		}
		return saida;
	}

	public void atualizarTabela(String idCliente, String dados) {
		
		Vizinho cliente = vizinhos.get(idCliente);

		StringTokenizer st = new StringTokenizer(dados);
		boolean atualizou = false;
        
		//Iterator roteadoresIt = roteadores.keySet().iterator();
		int numeroRoteadores = roteadores.size();
		
		for(int i = 1; i <= numeroRoteadores; i++){
			
		
        
		
            No roteador = roteadores.get(String.valueOf(i));           
        	StringTokenizer distEsalt = new StringTokenizer(st.nextToken(), "-");

			int distanciaTabelaRemota = Integer.parseInt(distEsalt.nextToken());

			String proxSalto = distEsalt.nextToken();

			if (!roteador.getId().equals(this.id)) {
                
				if(vizinhos.containsKey(roteador.getId())){
				    Vizinho vizinho = vizinhos.get(roteador.getId());
          
					int distanciaFisica = vizinho.getCusto();

					boolean ligado = vizinho.isLigado();

					if (distanciaFisica < getDistancia(roteador.getId())) {
						if (!ligado) {
							setDistancia(roteador.getId(), INFINITY);
							this.adicionarRota(roteador, null);
							// atualizou = true;
							
							continue;
						}

						setDistancia(roteador.getId(), distanciaFisica);
						if (vizinho.equals(cliente)) {
							this.adicionarRota(roteador, cliente.getNo());

						} else {
							this.adicionarRota(roteador,roteador);
						}
					}

				}

				if ((distanciaTabelaRemota == INFINITY && idCliente
						.equals(this.tabela.getMapaRotas().get(roteador.getId())))
						|| (this.tabela.getMapaRotas().get(roteador.getId()) == null)) {
					setDistancia(roteador.getId(), INFINITY);
					this.tabela.getMapaRotas().put(roteador.getId(), null);
					// atualizou = true;
				}

				int distanciaTabelaLocal = getDistancia(roteador.getId());

				int distanciaVizinho = getDistancia(idCliente);

				if (proxSalto.equals(this.id)) {
					continue;
				}

				if (distanciaTabelaRemota + distanciaVizinho < distanciaTabelaLocal) {

					setDistancia(roteador.getId(), distanciaTabelaRemota
							+ distanciaVizinho);
					this.adicionarRota(roteador, cliente.getNo());
					atualizou = true;
					System.out.println("2");

				}

			}
		}
		if (true) {
			System.out.println(horaSistema() + " Tabela: "
					+ getTabelaImprimir());
		}

     
	

	}

	public String horaSistema() {
		GregorianCalendar calendario = new GregorianCalendar();
		int hora = calendario.get(Calendar.HOUR_OF_DAY);
		int min = calendario.get(Calendar.MINUTE);
		int sec = calendario.get(Calendar.SECOND);
		return hora + ":" + min + ":" + sec;

	}

	public String getTabelaImprimir() {
		return this.tabela.toString();
	}

	public int getDistancia(String idDestino) {
		return tabela.getDistancia(idDestino);
	}

	public void setDistancia(String id, int valor) {
		tabela.setDistancia(id, valor);
		System.out.println(horaSistema() + " Tabela: "
				+ getTabelaImprimir());
	}

	

	public void adicionarRota(No destino, No proximoSalto) {
		String destinoId; 
		String proximoSaltoId;
		if(destino != null){
			destinoId = destino.getId();
		}else{
			destinoId = null;
		}
		if(proximoSalto != null){
			proximoSaltoId = proximoSalto.getId();
		}else{
			proximoSaltoId = null;
		}
		this.tabela.getMapaRotas().put(destinoId, proximoSaltoId);
	}

	public Map<String, No> getRoteadores() {
		return roteadores;
	}

	public void setRoteadores(Map<String, No> roteadores) {
		this.roteadores = roteadores;
	}

	public void setRotasVizinhoNull(String idVizinho) {
		this.tabela.setRotasVizinhoNull(idVizinho);
		
	}

	
}
