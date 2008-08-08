package excecoes;

/**
 * 
 * Classe que representa uma excecao lancada pelo leitor de arquivos
 * 
 * @author Wilson
 * @author Pablo
 * 
 */
public class LeitorArquivoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public LeitorArquivoException(String msg) {
		super(msg);
	}

}
