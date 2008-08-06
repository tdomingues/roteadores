package sistema;

public class No {
	
	private String id;
    private String porta;
    private String ip;
    
    public No(String id, String porta, String ip){
    	this.id = id;
    	this.porta = porta;
    	this.ip = ip;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPorta() {
		return porta;
	}

	public void setPorta(String porta) {
		this.porta = porta;
	}
    
    public String toStrint(){
    	return this.getId();
    }
	
	

}
