package sistema;
/**
 * 
 * Classe que representa um no.
 * Cada roteador eh constituido de um no
 * contendo um id uma porta e um ip
 * 
 * @author Wilson
 * @author Pablo
 * 
 */
public class No {

	/**
	 * O identificador do no
	 */
	private String id;

	/**
	 * A porta do no
	 */
	private String porta;

	/**
	 * O ip do no
	 */
	private String ip;

	/**
	 * @param id O id do no
	 * @param porta A porta do no
	 * @param ip O ip do no
	 */
	public No(String id, String porta, String ip) {
		this.id = id;
		this.porta = porta;
		this.ip = ip;
	}

	/**
	 * Retorna o id do no
	 * @return A String que representa o id do no
	 */
	public String getId() {
		return id;
	}

	/**
	 * Retorna o ip do no
	 * @return A string que representa o ip do no
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * Retorna a porta do no
	 * @return A String que representa a porta do no
	 */
	public String getPorta() {
		return porta;
	}

	/**
	 * Seta o id o no
	 * @param id O novo valor id do no
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Seta o ip do no
	 * @param ip O novo valor do ip do no
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * Seta a porta do no
	 * @param porta O novo valor da porta do no
	 */
	public void setPorta(String porta) {
		this.porta = porta;
	}

	/**
	 * Imprime o id como sendo a representacao do no
	 * @return A string que representa o no
	 */
	public String toStrint() {
		return this.getId();
	}

}
