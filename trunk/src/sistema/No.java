package sistema;

public class No {

	private String id;

	private String porta;

	private String ip;

	public No(String id, String porta, String ip) {
		this.id = id;
		this.porta = porta;
		this.ip = ip;
	}

	public String getId() {
		return id;
	}

	public String getIp() {
		return ip;
	}

	public String getPorta() {
		return porta;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setPorta(String porta) {
		this.porta = porta;
	}

	public String toStrint() {
		return this.getId();
	}

}
