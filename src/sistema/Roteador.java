package sistema;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
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

	private Map<String, Vizinho> vizinhos;

	private Map<String, No> roteadores;

	private Servidor server;

	public Roteador(String id) {
		this.id = id;

		LeitorRoteadorConfig leitorRoteadorConfig = LeitorRoteadorConfig
				.getInstance();
		if (!leitorRoteadorConfig.roteadorExiste(id)) {
			System.out.println("Roteador inexistente");
			System.exit(1);
		}
		this.roteadores = leitorRoteadorConfig.getRoteadores();
		this.porta = leitorRoteadorConfig.getPorta(this.id);
		this.IP = leitorRoteadorConfig.getIP(this.id);

		LeitorEnlacesConfig leitorEnlacesConfig = LeitorEnlacesConfig
				.getInstance(this);
		this.vizinhos = leitorEnlacesConfig.getVizinhos();

		this.tabela = new Tabela(this);
		tabela.inicializar();

		inicializar();
		imprimirTabela();

	}

	public void inicializar() {

		Timer timer = new Timer();
		server = new Servidor(this);
		server.start();
		avisaVizinhos();

		timer.schedule(this, 400, 8000);

	}

	public void run() {
		enviaParaVizinhos();

	}

	public void imprimirTabela() {
		System.out.println(horaSistema() + " Imprimindo Tabela: "
				+ tabela.toString());
		System.out.println();
	}

	private void avisaVizinhos() {
		System.out.print(horaSistema()
				+ ": Avisando aos vizinhos que fui ligado: ");
		Iterator vizinhosIt = vizinhos.keySet().iterator();
		while (vizinhosIt.hasNext()) {
			Vizinho vizinho = vizinhos.get(vizinhosIt.next());

			System.out.print(" < " + vizinho.getId() + " > ");
			Cliente cliente = new Cliente(this, vizinho.getId(), vizinho
					.getIp(), vizinho.getPorta());

		}
		System.out.println();

	}

	public void setVizinhoLigado(String idVizinho) {
		Vizinho vizinho = vizinhos.get(idVizinho);
		vizinho.setLigado(true);
	}

	public void enviaParaVizinhos() {
		System.out.print(horaSistema()
				+ ": Enviando Tabela para Vizinhos Ligados: ");
		Iterator vizinhosIt = vizinhos.keySet().iterator();
		while (vizinhosIt.hasNext()) {
			Vizinho vizinho = vizinhos.get(vizinhosIt.next());
			if (vizinho.isLigado()) {
				System.out.print(" < " + vizinho.getId() + " > ");
				Cliente cliente = new Cliente(this, vizinho.getId(), vizinho
						.getIp(), vizinho.getPorta());
			}
		}
		System.out.println();

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

	public String geraTabelaStringEnvenenada(String destinoId) {
		String saida = this.getId() + "#";
		int numeroRoteadores = this.roteadores.size();
		for (int i = 1; i <= numeroRoteadores; i++) {
			String roteadorId = String.valueOf(i);
			String custo = this.tabela.getMapaDistancia().get(roteadorId).toString();
			String proximoSaltoId = this.tabela.getMapaRotas().get(roteadorId);
			// Se o proximo salto para um destino for o proprio roteador que
			// estou enviando, entao envenena
			if (proximoSaltoId != null && proximoSaltoId.equals(destinoId)) {
				custo = String.valueOf(this.INFINITY);
			}
			saida += custo + " ";
		}
		return saida;
	}

	public void atualizarTabela(String idVizinho, String dados) {
		boolean atualizou = false;

		System.out.println(horaSistema() + ": Recebendo Tabela do Vizinho < "
				+ idVizinho + " >");

		Vizinho vizinho = vizinhos.get(idVizinho);

		// Esse token eh constituido de varios fragmentos no formato
		// Distancia-ProximoSalto
		StringTokenizer st = new StringTokenizer(dados);

		int numeroRoteadores = roteadores.size();

		for (int i = 1; i <= numeroRoteadores; i++) {
			No roteador = roteadores.get(String.valueOf(i));

			// Esse token tem o formato Distancia-ProximoSalto
			StringTokenizer distProxSaltoToken = new StringTokenizer(st
					.nextToken(), "-");

			// Aqui sao lidos os valores da distancia e do proximo salto
			// informado pelo vizinho
			int custo = Integer.parseInt(distProxSaltoToken.nextToken());

			// Se o roteador nao for eu mesmo
			if (!roteador.getId().equals(this.id)) {

				if (atualizaEstimativa(vizinho, roteador, custo)) {
					atualizou = true;
				}

			}

		}
		if (atualizou) {
			imprimirTabela();
		}

	}

	public boolean atualizaEstimativa(Vizinho vizinho, No destino, int custo) {
		boolean atualizou = false;
		int novoCusto = vizinho.getCusto() + custo;
		int custoAtual = this.tabela.getDistancia(destino.getId());
		if (novoCusto < custoAtual) {
			atualizou = tabela.setDistancia(destino.getId(), novoCusto);
			adicionaRota(destino, vizinho.getNo());

		}

		return atualizou;

	}

	public void setVizinhoDesligado(String idVizinho) {
		boolean atualizou = false;

		Vizinho vizinho = this.vizinhos.get(idVizinho);
		vizinho.setLigado(false);

		atualizou = tabela.setDistancia(idVizinho, INFINITY);

		setRotasVizinhoNull(idVizinho);

		this.enviaParaVizinhos();

		if (atualizou) {
			imprimirTabela();
		}
	}

	public String horaSistema() {
		GregorianCalendar calendario = new GregorianCalendar();
		int hora = calendario.get(Calendar.HOUR_OF_DAY);
		int min = calendario.get(Calendar.MINUTE);
		int sec = calendario.get(Calendar.SECOND);
		return hora + ":" + min + ":" + sec;

	}

	
	

	

	public void adicionaRota(No destino, No proximoSalto) {
		String destinoId;
		String proximoSaltoId;
		if (destino != null) {
			destinoId = destino.getId();
		} else {
			destinoId = null;
		}
		if (proximoSalto != null) {
			proximoSaltoId = proximoSalto.getId();
		} else {
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
