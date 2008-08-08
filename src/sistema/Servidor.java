package sistema;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.StringTokenizer;

/**
 * 
 * Classe que representa um hospedeiro do tipo servidor.
 * A finalidade dessa classe eh prover uma forma do roteador
 * receber os pacotes enviados pelos clientes.
 * 
 * @author Wilson
 * @author Pablo
 * 
 */
public class Servidor extends Thread {

	/**
	 * O roteador ao qual o servidor pertence
	 */
	private Roteador roteador;

	/**
	 * A porta que o servidor ira receber os pacotes UDP 
	 */
	private String porta;

	/**
	 * 
	 * @param roteador O roteador ao qual o servidor pertence
	 */
	public Servidor(Roteador roteador) {
		this.porta = roteador.getPorta();
		this.roteador = roteador;

	}

	/**
	 * Retorna a porta que o servidor esta escutando
	 * @return Uma String que representa a porta que o servidor esta escutando.
	 */
	public String getPorta() {
		return porta;
	}

	/**
	 * Esse metodo serve para a partir dos dados recebidos dentro do pacote udp
	 * ler as informacoes contidas nele, como o id do roteador que enviou, e a parte
	 * contendo o vetor de distancia enviado por esse roteador.
	 * @param dadosRecebidos
	 */
	private void lerTabela(String dadosRecebidos) {
		StringTokenizer st = new StringTokenizer(dadosRecebidos, "#");
		String vizinhoId = st.nextToken();
		//sempre que ele recebe um pacote de um roteador, ele avisa ao seu roteador
		//de que o roteadorVizinho que enviou esse pacote esta ligado, e caso o seu roteador
		//ainda nao saiba que esse vizinho esta ligado, ficara sabendo a partir desse momento.
		roteador.setVizinhoLigado(vizinhoId);
		//Aqui eh chamado o procedimento para atualizar a tabela de roteamento do roteador
		//com base no vetor de distancia que foi recebido agora.
		roteador.atualizarTabela(vizinhoId, st.nextToken());
	}

	//Esse eh o metodo da thread do servidor, que fica sempre em execucao esperando por um novo pacote UDP
	public void run() {
		while (true) {
			try {
                
				//Esse eh o socket que o servidor ira utilizar para receber os pacotes UDPs enviados pelos clientes
				//para a porta dele.
				DatagramSocket servidorSocket = new DatagramSocket(Integer
						.parseInt(getPorta()));

				
				//Esses sao os bufs utilizados para receber pacotes UDPs e enviar pacotes de confirmacao
				byte[] bufSaida = new byte[1024];
				byte[] bufEntrada = new byte[1024];

				
				//Esse eh o loop que fica sempre em execucao, esperando um novo pacote UDP
				//e enviando a confirmacao de recebimento
				while (true) {
                    
					//Datagrama utilizando para receber o pacote UDP
					DatagramPacket pacoteEntrada = new DatagramPacket(
							bufEntrada, bufEntrada.length);

					// Aqui o servidor ficara esperando por algum pacote enviado
					// para ele
					// A thread do servidor fica bloqueada ate que algo seja
					// recebido

					servidorSocket.receive(pacoteEntrada);

					// Quando chega algum pacote ele sera tratado

					// eh obtido o ip e a porta do cliente que enviou o pacote
					InetAddress address = pacoteEntrada.getAddress();
					int porta = pacoteEntrada.getPort();

					// a tabela em forma de string eh lida do campo de dados do pacote UDP
					String dadosRecebidos = new String(pacoteEntrada.getData(),
							pacoteEntrada.getOffset(), pacoteEntrada
									.getLength());


                    //Aqui eh executado o metodo que analisa a tabela recebida
					//e envia os dados colhidos como id do roteador que enviou 
					//e vetor de distancia, para o seu roteador, que ira fazer
					//a atualizacao do seu vetor de distancia.
					this.lerTabela(dadosRecebidos);

					// Nessa parte sera enviado um aviso de recebimento para o
					// cliente
					DatagramPacket pacoteSaida = new DatagramPacket(bufSaida,
							bufSaida.length, address, porta);
					try {
						servidorSocket.send(pacoteSaida);
					} catch (Exception e) {
						System.out
								.println("Nao foi possivel enviar o aviso de recebimento para o cliente "
										+ address.getHostAddress()
										+ ":"
										+ porta);
					}

				}

			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.exit(1);
			} catch (SocketException e) {
				System.out
						.println("Nao foi possivel inicializar: O roteador com o ID informado ja esta em uso.");
				System.exit(1);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	/**
	 * Seta a porta do servidor
	 * @param porta O novo valor da porta do servidor
	 */
	public void setPorta(String porta) {
		this.porta = porta;
	}

}
