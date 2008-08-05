package sistema;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

	private Map<String, Vizinho> vizinhos;

	private Map<String, No> roteadores;

	// private LeitorRoteadorConfig manipuladorRot;
	// private LeitorEnlacesConfig manipuladorEn;

	private Servidor server;

	//private Map<String, String> statusVizinhos;

	private Map<No, No> rotas;

	public Roteador(String id) {
		this.id = id;

		LeitorRoteadorConfig leitorRoteadorConfig = LeitorRoteadorConfig
				.getInstance();

		this.porta = leitorRoteadorConfig.getPorta(this.id);
		this.IP = leitorRoteadorConfig.getIP(this.id);

		lerNos();
		lerVizinhos();

		this.tabela = new Tabela(this.id);
		tabela.inicializar();

		//statusVizinhos = new HashMap<String, String>();
		rotas = new HashMap<No,No>();
		inicializar();
		if (true) {
			System.out.println(horaSistema() + " tabela: "
					+ getTabelaImprimir());
		}
	}

	public void inicializar() {
		inicializarRotas();
		Timer timer = new Timer();
		server = new Servidor(this);
		server.start();
		ligarVizinhos();
		timer.schedule(this, 300, 6000);

	}

	private void lerNos() {
		LeitorRoteadorConfig leitorRoteadorConfig = LeitorRoteadorConfig
				.getInstance();
		this.roteadores = leitorRoteadorConfig.lerNos();
	}

	private void lerVizinhos() {
		this.vizinhos = new HashMap<String, Vizinho>();
		LeitorEnlacesConfig leitorEnlacesConfig = LeitorEnlacesConfig
				.getInstance();
		List vizinhosIds = leitorEnlacesConfig.getVizinhos(id);
		Iterator nosIt = roteadores.keySet().iterator();
		while (nosIt.hasNext()) {
			String noId = (String) nosIt.next();
			if (vizinhosIds.contains(noId)) {
				vizinhos.put(noId, new Vizinho(roteadores.get(noId),
						leitorEnlacesConfig.getCusto(id, noId), false));
			}
		}

	}

	private void inicializarRotas() {
		Iterator nosIt = roteadores.keySet().iterator();
		while(nosIt.hasNext()){
			rotas.put(roteadores.get(nosIt.next()),null);
		}
		//for (String roteador : todosOsRoteadores()) {
		//	rotas.put(roteador, null);
		//}
	}

	public void run() {
		enviaParaVizinhos();
		// System.out.println( horaSistema() + " tabela: " + getTabelaImprimir()
		// );
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
		Iterator vizinhosIt = vizinhos.keySet().iterator();
		while (vizinhosIt.hasNext()) {
			Vizinho vizinho = vizinhos.get(vizinhosIt.next());
			if (vizinho.isLigado()) {

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
		String saida = this.getId() + "$";
		Iterator nosIt = roteadores.keySet().iterator();
		while(nosIt.hasNext()){
			String noId = (String) nosIt.next();
			saida += this.tabela.getMapaDistancia().get(noId) + "-"
			+ rotas.get(noId) + " ";
		}
		//for (String roteador : todosOsRoteadores()) {
		//	saida += this.tabela.getMapaDistancia().get(roteador) + "-"
		//			+ rotas.get(roteador) + " ";
		//}
		return saida;
	}

	public void atualizarTabela(String idCliente, String dados) {
		
		Vizinho cliente = vizinhos.get(idCliente);

		StringTokenizer st = new StringTokenizer(dados);
		boolean atualizou = false;
        
		Iterator roteadoresIt = roteadores.keySet().iterator();
        
		while(roteadoresIt.hasNext()){
            No roteador = roteadores.get(roteadoresIt.next());           
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
							System.out.println("1");
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
						.equals(rotas.get(roteador.getId())))
						|| (rotas.get(roteador.getId()) == null)) {
					setDistancia(roteador.getId(), INFINITY);
					rotas.put(roteador, null);
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
			System.out.println(horaSistema() + " tabela: "
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
	}

	//private List<String> todosOsRoteadores() {
	//	return new ArrayList<String>(manipuladorRot.getIdRoteadores());
	//}

	public void adicionarRota(No destino, No proximoSalto) {
		rotas.put(destino, proximoSalto);
	}

	public void setRotasVizinhoNull(String idVizinho) {
		Iterator it = this.rotas.keySet().iterator();
		while (it.hasNext()) {
			No destino = (No) it.next();
			if (rotas.get(destino.getId()) != null
					&& rotas.get(destino).equals(idVizinho)) {
				rotas.put(roteadores.get(destino), null);
				this.setDistancia(destino.getId(), INFINITY);
			}
		}
	}
}
