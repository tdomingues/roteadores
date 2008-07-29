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
								
				sleep(5); //Tivemos que fazer isso para evitar sobrecarga de processamento desnecessario
				
				
				
				DatagramSocket servidorSocket = new DatagramSocket(Integer.parseInt(getPorta()));
				
				
				byte[] bufSaida = new byte[1024];
				byte[] bufEntrada = new byte[1024];
				

				while ( true ) {
					
					
					DatagramPacket pacoteEntrada = new DatagramPacket( bufEntrada, bufEntrada.length );
					
					servidorSocket.receive( pacoteEntrada );
					
					InetAddress address = pacoteEntrada.getAddress();
					int porta = pacoteEntrada.getPort();
			
					
					String dadosRecebidos = new String( pacoteEntrada.getData() , pacoteEntrada.getOffset() , pacoteEntrada.getLength() );
					
					this.lerTabela(dadosRecebidos);
					
					
					DatagramPacket pacoteSaida = new DatagramPacket(bufSaida, bufSaida.length, address, porta);
					
					
					servidorSocket.send(pacoteSaida);
										
				}
			} catch (InterruptedException e) {
				
				
				System.out.println("Interrupcao nao esperada no servidor");
				System.exit(1);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.exit(1);
			} catch (SocketException e) {
				e.printStackTrace();
				System.out.println("Nao foi possivel inicializar: O roteador com o ID informado ja esta em uso.");
				System.exit(1);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	
	private void lerTabela(String dadosRecebidos){
		StringTokenizer st = new StringTokenizer(dadosRecebidos, "$"); 
		String idTabela = st.nextToken();
		roteador.setVizinhoLigado(idTabela);
		TabelaRoteamento tabela = new TabelaRoteamento(idTabela);
		roteador.atualizarTabela(idTabela, st.nextToken());
	}

	
	public String getPorta() {
		return porta;
	}
	
	
	public void setPorta(String porta) {
		this.porta = porta;
	}

}
