package sistema;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * 
 * Classe que representa um hospedeiro do tipo cliente
 * A finalidade dessa classe eh enviar uma tabela para um
 * servidor e aguardar por uma confimacao de recebimento.
 * Caso ele receba a confirmacao, entao seu trabalho estara
 * cumprido e sua thread terminara, caso contrario ele identificara
 * que o link com o servidor caiu e avisara isso ao seu roteador
 * e em seguida seu trabalho estara completo.
 * 
 * @author Wilson
 * @author Pablo
 * 
 */
public class Cliente extends Thread {

	/**
	 * Nossa representacao para infinito
	 */
	final int INFINITY = 999;

	/**
	 *O ip do servidor que o cliente ira se conectar 
	 */
	private String ipServidor;

	/**
	 *A porta do serividor que o cliente ira se conectar 
	 */
	private String portaServidor;

	/**
	 * O id do servidor que o cliente ira se contectar
	 */
	private String idServidor;

	/**
	 * O roteador ao qual o cliente pertence
	 */
	private Roteador roteador;

	/**
	 * @param rot O roteador que criou esse cliente
	 * @param id  O id do roteador que o cliente ira se conectar
	 * @param ip O ip do servidor que o cliente ira se contectar
	 * @param porta A porta do servidor que o cliente ira se conectar
	 */
	public Cliente(Roteador rot, String id, String ip, String porta) {
		this.ipServidor = ip;
		this.portaServidor = porta;
		this.idServidor = id;

		this.roteador = rot;

		start();

	}

	/**
	 * Retorna o id do servidor
	 * @return Uma String que representa o id do servidor
	 */
	public String getIdServidor() {
		return idServidor;
	}

	
	/**
	 * Retorna o ip do servidor
	 * @return Uma String que representa o id do servidor
	 */
	public String getIpServidor() {
		return ipServidor;
	}

	/**
	 * Retorna a porta do servidor
	 * @return Uma String que representa a porta do servidor
	 */
	public String getPortaServidor() {
		return portaServidor;
	}

    //Funcao executada pela thread do cliente 
	public void run() {
		try {
			//Aqui eh criado um address com base no ip do servidor
			InetAddress address = InetAddress.getByName(this.getIpServidor());

			//sera criado um socket UDP
			DatagramSocket clienteSocket = new DatagramSocket();
			
			//eh setado o timeout do socket para detectar que esta desligado
			clienteSocket.setSoTimeout(4000);

			//Sao os bufs usados para receber e enviar pacotes udp
			byte[] bufEntrada = new byte[1024];
			byte[] bufSaida = new byte[1024];

			//Aqui sera gerada a tabela para o Servidor, e essa tabela
			//eh do tipo envenenada para evitar problemas com contagem 
			//ao infinito
			String tabela = roteador.geraTabelaStringEnvenenada(idServidor);
			bufSaida = tabela.getBytes();

			//Aqui eh criado um datagrama UDP, com destino setado para o 
			//Ip e Porta do servidor, e com a tabela envenenada no campo de dados.
			DatagramPacket pacoteSaida = new DatagramPacket(bufSaida,
					bufSaida.length, address, Integer
							.parseInt(getPortaServidor()));
             
			//Eh enviado o pacote para o servidor
			clienteSocket.send(pacoteSaida);

			//Aqui eh criado um datagrama para receber a confirmacao do servidor
			DatagramPacket pacoteEntrada = new DatagramPacket(bufEntrada,
					bufEntrada.length);

			//Nesse ponto a thread do cliente eh bloqueada, e ela fica esperando
			//por uma confirmacao de recebimento do servidor, caso ele nao receba
			//antes de dar timeout, ele assumira que o link com o cliente foi perdido
			//e entao avisara ao seu roteador.
			clienteSocket.receive(pacoteEntrada);
			clienteSocket.close();

		} catch (SocketTimeoutException e) {
			//Aqui ele avisa ao seu roteador que nao recebeu uma confirmacao de envio por
			//parte do servidor, assumindo assim que o link foi perdido.
			roteador.setVizinhoDesligado(idServidor);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Seta o id do servidor
	 * @param idServidor O novo valor do idServidor
	 */
	public void setIdServidor(String idServidor) {
		this.idServidor = idServidor;
	}

	/**
	 * Seta o ip do servidor
	 * @param ipServidor O novo valor do ipServidor
	 */
	public void setIpServidor(String ipServidor) {
		this.ipServidor = ipServidor;
	}

	/**
	 * Seta a porat do servidor
	 * @param portaServidor O novo valor da porta do servidor
	 */
	public void setPortaServidor(String portaServidor) {
		this.portaServidor = portaServidor;
	}

}
