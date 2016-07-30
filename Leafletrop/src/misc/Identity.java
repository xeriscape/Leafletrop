package misc;

import java.util.UUID;

public class Identity {
	public String friendlyName ="";
	private UUID ID=null;
	
	public UUID getID() {
		return this.ID;
	}
	
	/** 
	 * Create an Identity without a name
	 */
	public Identity() {
		this.ID = UUID.randomUUID();
		this.friendlyName = this.ID.toString();
	}
	
	/** 
	 * Create a named Identity
	 * @param friendlyName What to call the Identity
	 */
	public Identity(String friendlyName) {
		this.ID = UUID.randomUUID();
		this.friendlyName = friendlyName;
	}
	
}
