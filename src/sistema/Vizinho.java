package sistema;

public class Vizinho {

	private No no;

	private int custo;

	private boolean ligado;

	public Vizinho(No no, int custo, boolean ligado) {
		this.no = no;
		this.custo = custo;
		this.ligado = ligado;
	}

	public int getCusto() {
		return custo;
	}

	public String getId() {
		return no.getId();
	}

	public String getIp() {
		return no.getIp();
	}

	public No getNo() {
		return no;
	}

	public String getPorta() {
		return no.getPorta();
	}

	public boolean isLigado() {
		return ligado;
	}

	public void setCusto(int custo) {
		this.custo = custo;
	}

	public void setLigado(boolean ligado) {
		this.ligado = ligado;
	}

	public void setNo(No no) {
		this.no = no;
	}

}
