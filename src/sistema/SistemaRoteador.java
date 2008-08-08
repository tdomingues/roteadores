package sistema;

import java.util.Scanner;

import excecoes.LeitorArquivoException;
import excecoes.ServidorException;
/**
 * 
 * Classe que representa o sistema de um roteador.
 * Ela eh a responsavel por ler o id do roteador que se deseja iniciar
 * e entao instancia um novo roteador com esse id.
 * 
 * @author Wilson
 * @author Pablo
 * 
 */
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
