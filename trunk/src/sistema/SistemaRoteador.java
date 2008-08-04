package sistema;

import java.util.Scanner;

import excecoes.LeitorArquivoException;
import excecoes.ServidorException;


public class SistemaRoteador {
	
	
	
	public static void main(String[] args) {

		System.out.print("Informe o ID do roteador: ");
		Scanner scan = new Scanner(System.in);
		try {
			
			
			@SuppressWarnings("unused")
			Roteador roteador;
			
			roteador = new Roteador(scan.next());
		
		} catch (LeitorArquivoException e1) {
			System.err.println(e1);
			System.exit(1);
		} catch (ServidorException e2) {
			System.err.println(e2);
			System.exit(1);
		} 
		
	}
}
