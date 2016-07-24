package misc;

import java.util.UUID;

public class Identity {
	public String friendlyName ="";
	private String ID="";
	
	public String getID() {
		return this.ID;
	}
	
	/** 
	 * Create an Identity without a name
	 */
	public Identity() {
		this.ID = UUID.randomUUID().toString();
		this.friendlyName = this.ID;
	}
	
	/** 
	 * Create a named Identity
	 * @param friendlyName What to call the Identity
	 */
	public Identity(String friendlyName) {
		this.ID = UUID.randomUUID().toString();
		this.friendlyName = friendlyName;
	}
	
}
