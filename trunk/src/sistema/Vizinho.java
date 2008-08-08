package sistema;

/**
 * Classe que representa um objeto do tipo Vizinho.
 * A finalidade dessa classe eh simbolizar que um vizinho eh 
 * constituido por um No, um custo fisico, e o seu status.
 * Cada roteador te uma lista contendo todos os seus vizinhos, que
 * eh utilizada, por exemplo, na hora que ocorre uma mudanca na tabela de
 * roteamento, e essa tem que ser enviada para todos os seus vizinhos que 
 * estejam ligados. 
 * 
 * @author Wilson
 * @author Pablo
 * 
 */
public class Vizinho {

	/**
	 * O no que o vizinho tem
	 */
	private No no;

	/**
	 * O custo fisico ate esse vizinho 
	 */
	private int custo;

	/**
	 * O status do vizinho
	 */
	private boolean ligado;

	/**
	 * @param no O no do vizinho
	 * @param custo O custo do enlace para esse vizinho
	 * @param ligado O status do vizinho.
	 */
	public Vizinho(No no, int custo, boolean ligado) {
		this.no = no;
		this.custo = custo;
		this.ligado = ligado;
	}

	/**
	 * Retorna o custo do enlace para esse vizinho
	 * @return
	 */
	public int getCusto() {
		return custo;
	}

	/**
	 * Retorna o id do vizinho, que na realidade eh o id o No que o vizinho contem
	 * @return O id do vizinho.
	 */
	public String getId() {
		return no.getId();
	}

	/**
	 * Retorna o ip do vizinho, que na realidade eh o ip do No que o vizinho contem
	 * @return O ip do vizinho
	 */
	public String getIp() {
		return no.getIp();
	}

	/**
	 * Retorna o no do vizinho 
	 * @return O no do vizinho
	 */
	public No getNo() {
		return no;
	}

	/**
	 * Retorna a porta do vizinho
	 * @return A porta do vizinho
	 */
	public String getPorta() {
		return no.getPorta();
	}

	/**
	 * Retorna o status do vizinho, se esta ligado ao nao
	 * @return O status do vizinho
	 */
	public boolean isLigado() {
		return ligado;
	}

	/**
	 * Seta o custo do enlace para o vizinho
	 * @param custo O novo custo do enlace para o vizinho
	 */
	public void setCusto(int custo) {
		this.custo = custo;
	}

	/**
	 * Seta o status do vizinho
	 * @param ligado O novo valor do estatus do vizinho
	 */
	public void setLigado(boolean ligado) {
		this.ligado = ligado;
	}

	/**
	 * Seta o no do vizinho
	 * @param no O novo no do vizinho
	 */
	public void setNo(No no) {
		this.no = no;
	}

}
