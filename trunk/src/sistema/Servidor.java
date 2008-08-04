package sistema;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.StringTokenizer;


import excecoes.ServidorException;

public class Servidor extends Thread {

	private Roteador roteador;

	private String porta;

	public Servidor(Roteador roteador) {
		this.porta = roteador.getPorta();
		this.roteador = roteador;

	}

	public void run() {
		while (true) {
			try {

				sleep(5); // Tivemos que fazer isso para evitar sobrecarga de
				// processamento desnecessario
				// Mas ainda acho que isso nao eh necessario

				DatagramSocket servidorSocket = new DatagramSocket(Integer
						.parseInt(getPorta()));

				byte[] bufSaida = new byte[1024];
				byte[] bufEntrada = new byte[1024];

				while (true) {

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

					// sera impresso na tela um aviso de recebimento de novo
					// pacote
					avisaChegadaDePacote(address.getHostAddress(), porta);

					// a tabela em forma de string eh lida do pacote
					String dadosRecebidos = new String(pacoteEntrada.getData(),
							pacoteEntrada.getOffset(), pacoteEntrada
									.getLength());

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
			} catch (InterruptedException e) {

				System.out.println("Interrupcao nao esperada no servidor");
				System.exit(1);
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

	private void avisaChegadaDePacote(String ip, int porta) {
		System.out.println("Recebendo dados do vizinho " + ip + ":" + porta);

	}

	private void lerTabela(String dadosRecebidos) {
		StringTokenizer st = new StringTokenizer(dadosRecebidos, "$");
		String idTabela = st.nextToken();
		roteador.setVizinhoLigado(idTabela);
		Tabela tabela = new Tabela(idTabela);
		roteador.atualizarTabela(idTabela, st.nextToken());
	}

	public String getPorta() {
		return porta;
	}

	public void setPorta(String porta) {
		this.porta = porta;
	}

}
