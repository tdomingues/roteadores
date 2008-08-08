package excecoes;

/**
 * 
 * Classe que representa uma excecao lancada pelo Servidor
 * 
 * @author Wilson
 * @author Pablo
 * 
 */
public class ServidorException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ServidorException(String msg) {
		super(msg);
	}

}
