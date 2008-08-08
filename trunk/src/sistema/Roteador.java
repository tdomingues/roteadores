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

/**
 * Classe que representa um Roteador.
 * A finalidade dessa classe e manter um servidor para receber dados dos roteadores vizinhos
 * e criar instancias de Clientes para enviar sua tabela para seus vizinhos ligados.
 * E a partir das informacoes recebidas, atualizar suas estimativas de distancia.
 * 
 * @author Wilson
 * @author Pablo
 * 
 */

public class Roteador extends TimerTask {
	/**
	 * Representacao de infinito
	 */
	final int INFINITY = 999;

	/**
	 * O identificador do roteador
	 */
	private String id;

	/**
	 * A porta do roteador
	 */
	private String porta;

	/**
	 * O endereco ip do roteador
	 */
	private String IP;

	/**
	 * A tabela de roteamento do roteador
	 */
	private Tabela tabela;

	/**
	 * Um mapa contendo todos os vizinhos do roteador
	 */
	private Map<String, Vizinho> vizinhos;

	/**
	 * Um no contendo todos os roteadores da sua tabela de roteamento
	 */
	private Map<String, No> roteadores;

	/**
	 * O servidor utilizado para receber pacotes dos roteadores vizinhos.
	 */
	private Servidor server;

	/**
	 * @param id O id do roteador
	 */
	public Roteador(String id) {
		//Seta o id do roteador
		this.id = id;

		//cria uma nova instancia de um leitro de arquivo
		LeitorRoteadorConfig leitorRoteadorConfig = LeitorRoteadorConfig
				.getInstance();
		
		//Caso o id informado seja inexistente no arquivo 
		//de configuracao, o sistema sera finalizado.
		if (!leitorRoteadorConfig.roteadorExiste(id)) {
			System.out.println("Roteador inexistente");
			System.exit(1);
		}
		
		//Seta os dados do roteador.
		this.roteadores = leitorRoteadorConfig.getRoteadores();
		this.porta = leitorRoteadorConfig.getPorta(this.id);
		this.IP = leitorRoteadorConfig.getIP(this.id);
    
		//Cria um novo leitro de arquivo de enlace
		LeitorEnlacesConfig leitorEnlacesConfig = LeitorEnlacesConfig
				.getInstance(this);
		
		//Ler os vizinhos deste roteador.
		this.vizinhos = leitorEnlacesConfig.getVizinhos();
        
		//Cria uma nova tabela de roteamento e a inicializa
		this.tabela = new Tabela(this);
		tabela.inicializar();

		//Inicializa o servidor.
		inicializar();
		
		//Imprime a tabela inicial de roteamento.
		imprimirTabela();

	}

	/**
	 * Adiciona uma nova rota a tabela de roteamento
	 * @param destino O destino da rota
	 * @param proximoSalto O proximo salto para esse destino
	 */
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

	/**
	 * Atualiza a estimativa de distancia para um destino
	 * @param vizinho O vizinho que informou a nova estimativa de distancia
	 * @param destino O destino para o qual ele esta informando a nova estimativa de distancia
	 * @param custo O custo dessa estimativa
	 * @return Retorna true se a estimativa foi atualizada, false caso contrario.
	 */
	public boolean atualizaEstimativa(Vizinho vizinho, No destino, int custo) {
		boolean atualizou = false;
		int novoCusto = vizinho.getCusto() + custo;
		int custoAtual = this.tabela.getDistancia(destino.getId());
		// Se o proximo salto para o destino eh o meu vizinho, entao sempre
		// atualizo, para o caso de um aumento de custo no enlace
		// Senao eu so atualizo se o custo for menor
		String proximoSalto = tabela.getMapaRotas().get(destino.getId());
		if (proximoSalto != null && proximoSalto.equals(vizinho.getId())) {
			atualizou = tabela.setDistancia(destino.getId(), novoCusto);
			adicionaRota(destino, vizinho.getNo());
		} else if (novoCusto < custoAtual) {
			atualizou = tabela.setDistancia(destino.getId(), novoCusto);
			adicionaRota(destino, vizinho.getNo());

		}

		return atualizou;

	}

	/**
	 * Atualiza toda a tabela de roteamento, baseado no vetor de distancia enviado 
	 * por um vizinho.
	 * @param idVizinho
	 * @param dados
	 */
	public void atualizarTabela(String idVizinho, String dados) {
		//Variavel utilizada para saber se foi atualizado alguma estimativa de custo.
		boolean atualizou = false;

		
		//Sera impresso na tela um aviso de recebimento de novo vetor de distancia de um vizinho.
		System.out.println(horaSistema() + ": Recebendo Tabela do Vizinho < "
				+ idVizinho + " >");

		//Eh obtido o vizinho que informou o vetor de distancia.
		Vizinho vizinho = vizinhos.get(idVizinho);

		// Esse token contem o vetro de distancia enviado pelo vizinho
		StringTokenizer st = new StringTokenizer(dados);

		//Aqui eh obtido o numero de roteadores existentes no aquivo de configuracao.
		int numeroRoteadores = roteadores.size();

		//Para cada um desses roteadores, sera analizada a possibilidade de atualizacao
		//de estimativa de custo, baseado no vetor de distancia enviado pelo vizinho.
		for (int i = 1; i <= numeroRoteadores; i++) {
			No roteador = roteadores.get(String.valueOf(i));

			// Esse token tem a informacao da estimativa de distancia para um destino
			//informada pelo vizinho.
			StringTokenizer distProxSaltoToken = new StringTokenizer(st
					.nextToken(), "-");

			// Aqui sao lidos os valores da distancia
			// informado pelo vizinho
			int custo = Integer.parseInt(distProxSaltoToken.nextToken());

			// Se a o roteador contido no vetor de distancia do vizinho for eu mesmo
			//entao nao faco nada, pois a estimativa de distancia para eu mesmo eh sempre zero.
			if (!roteador.getId().equals(this.id)) {
                 
				//aqui eh feita a chamada do metodo que verifica se vale a pena atualizar
				//a estimativa, e caso seja atualizado a variavel "atualizou" ser setada como
				//true e a tabela sera impressa na tela.
				if (atualizaEstimativa(vizinho, roteador, custo)) {
					atualizou = true;
				}

			}

		}
		//Se ocorreu atualizacao, a tabela sera impressa.
		if (atualizou) {
			imprimirTabela();
		}

	}

	/**
	 * Avisa a todos os vizinhos que o roteador foi ligado.
	 */
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

	/**
	 * Envia o vetor de distancia da tabela de roteamento
	 * para todos os vizinhos que estejam ligados.
	 */
	public void enviaParaVizinhos() {
		System.out.print(horaSistema()
				+ ": Enviando Tabela para Vizinhos Ligados: ");
		Iterator vizinhosIt = vizinhos.keySet().iterator();
		while (vizinhosIt.hasNext()) {
			Vizinho vizinho = vizinhos.get(vizinhosIt.next());
			//So envia se o vizinho estiver ligado.
			if (vizinho.isLigado()) {
				System.out.print(" < " + vizinho.getId() + " > ");
				Cliente cliente = new Cliente(this, vizinho.getId(), vizinho
						.getIp(), vizinho.getPorta());
			}
		}
		System.out.println();

	}

	/**
	 * Gera um string contendo o vetor de distancia da tabela de roteamento.
	 * Esse vetor de distancia eh gerado utilizando o envenenamento reverso
	 * para evitar o problema da contagem ao infinito.
	 * @param destinoId O destino para o qual o vetor sera enviado.
	 * @return A string contendo o vetor de distancia
	 */
	public String geraTabelaStringEnvenenada(String destinoId) {
		String saida = this.getId() + "#";
		int numeroRoteadores = this.roteadores.size();
		for (int i = 1; i <= numeroRoteadores; i++) {
			String roteadorId = String.valueOf(i);
			String custo = this.tabela.getMapaDistancia().get(roteadorId)
					.toString();
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

	/**
	 * Retorna o id do roteador
	 * @return O id do roteador.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Retorna o ip do roteador
	 * @return O ip do roteador.
	 */
	public String getIP() {
		return IP;
	}

	/**
	 * Retorna a porta do roteador.
	 * @return O valor da porta do roteador.
	 */
	public String getPorta() {
		return porta;
	}

	/**
	 * Retorna um mapa contendo todos os roteadores da sua tabela de roteamento.
	 * @return Um mapa contendo todos os roteadores da sua tabela de roteamento.
	 */
	public Map<String, No> getRoteadores() {
		return roteadores;
	}

	/**
	 * Retorna a tabela de roteamento.
	 * @return A tabela de roteamento.
	 */
	public Tabela getTabela() {
		return tabela;
	}

	/**
	 * Retorna um mapa contendo todos os seus vizinhos.
	 * @return Um mapa contendo todos os seus vizinhos.
	 */
	public Map<String, Vizinho> getVizinhos() {
		return this.vizinhos;
	}

	/**
	 * Retorna uma string formatada, representando a hora do sistema.
	 * @return A hora do sistema.
	 */
	public String horaSistema() {
		GregorianCalendar calendario = new GregorianCalendar();
		int hora = calendario.get(Calendar.HOUR_OF_DAY);
		int min = calendario.get(Calendar.MINUTE);
		int sec = calendario.get(Calendar.SECOND);
		return hora + ":" + min + ":" + sec;

	}

	/**
	 * Imprime a table de roteamento com suas estimativas de distancia.
	 */
	public void imprimirTabela() {
		System.out.println(horaSistema() + " Imprimindo Tabela: "
				+ tabela.toString());
		System.out.println();
	}

	/**
	 * Inicializa o roteador.
	 */
	public void inicializar() {

		Timer timer = new Timer();
		//aqui eh criado o servidor que ficara aguardando por pacotes udp
		server = new Servidor(this);
		server.start();
		
		//Aqui todos os vizinhos serao avisados de que fui ligado.
		avisaVizinhos();

		//Aqui sera escalonada uma tarefa que executara a cada 8 segundos
		//mandando o meu vetor de distancia par todos os meus vizinhos ligados.
		timer.schedule(this, 400, 8000);

	}

	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	public void run() {
		enviaParaVizinhos();

	}

	/**
	 * Seta o id do roteador.
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Seta o ip do roteador.
	 * @param ip
	 */
	public void setIP(String ip) {
		this.IP = ip;
	}

	/**
	 * Seta a porta do roteador.
	 * @param porta
	 */
	public void setPorta(String porta) {
		this.porta = porta;
	}

	/**
	 * Seta todas as rotas que tem como proximo salto o vizinho que foi desligado como nulas.
	 * e as estimativas de distancias serao setadas para infinito.
	 * @param idVizinho
	 */
	public void setRotasVizinhoNull(String idVizinho) {
		this.tabela.setRotasVizinhoNull(idVizinho);

	}

	/**
	 * Seta os roteadores contidos na sua tabela de roteamento.
	 * @param roteadores Os novos roteadores da sua tabela de roteamento.
	 */
	public void setRoteadores(Map<String, No> roteadores) {
		this.roteadores = roteadores;
	}

	/**
	 * Seta um vizinho como desligado, atualizando assim a sua estimativa 
	 * de distancia para esse vizinho como infinito e removendo todas as 
	 * rotas que tem como proximo salto esse vizinho.
	 * @param idVizinho O id do vizinho que foi desligado
	 */
	public void setVizinhoDesligado(String idVizinho) {
		boolean atualizou = false;

		Vizinho vizinho = this.vizinhos.get(idVizinho);
		vizinho.setLigado(false);

		atualizou = tabela.setDistancia(idVizinho, INFINITY);
       
		//Todas as rotas que passam por esse viziho desligado sao setadas como nulas.
		// e as estimativas de distancia como infinito.
		setRotasVizinhoNull(idVizinho);

		//Envia meu novo vetor de distancia para os meus vizinhos.
		this.enviaParaVizinhos();

		//aqui sera impressa minha nova tabela de roteamento.
		if (atualizou) {
			imprimirTabela();
		}
	}

	/**
	 * Seta um vizinho como ligado
	 * @param idVizinho O id do vizinho que foi ligado.
	 */
	public void setVizinhoLigado(String idVizinho) {
		Vizinho vizinho = vizinhos.get(idVizinho);
		vizinho.setLigado(true);
	}

}
