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
 * @author Wilson
 * @author Pablo
 *
 */
public class Cliente extends Thread {
	
	final int INFINITY = 999;
	
	private String ipServidor;
	private String portaServidor;
	private String idServidor;
	
	private Roteador roteador;
	
	public Cliente( Roteador rot, String id, String ip, String porta){
		this.ipServidor = ip;
		this.portaServidor = porta;
		this.idServidor = id;
		
		this.roteador = rot;
		
		start();
		
		
	}
	
	public void run(){
		try {
			InetAddress address = InetAddress.getByName(this.getIpServidor());
			
			DatagramSocket clienteSocket = new DatagramSocket();
			clienteSocket.setSoTimeout(4000);
			
			
			byte[] bufEntrada = new byte[1024];
			byte[] bufSaida = new byte[1024];
			
			
			String tabela = roteador.geraTabelaStringEnvenenada(idServidor);
			bufSaida= tabela.getBytes();
			
			
						
			
			DatagramPacket pacoteSaida = new DatagramPacket(bufSaida, bufSaida.length, address, Integer
				.parseInt(getPortaServidor()));
			
			
			clienteSocket.send(pacoteSaida);
					
			
			DatagramPacket pacoteEntrada = new DatagramPacket(bufEntrada, bufEntrada.length);
			
			
			clienteSocket.receive(pacoteEntrada);
			clienteSocket.close();
			
			
		} catch (SocketTimeoutException e) {
			roteador.setVizinhoDesligado(idServidor);
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	
	public String getIdServidor() {
		return idServidor;
	}

	
	public void setIdServidor(String idServidor) {
		this.idServidor = idServidor;
	}

	
	public String getIpServidor() {
		return ipServidor;
	}

	
	public void setIpServidor(String ipServidor) {
		this.ipServidor = ipServidor;
	}

	
	public String getPortaServidor() {
		return portaServidor;
	}

	
	public void setPortaServidor(String portaServidor) {
		this.portaServidor = portaServidor;
	}

	
	
	
	
	

}
