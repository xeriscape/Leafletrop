package game.mechanics.character;

import misc.Identity;
import util2d.core.GLOBALS;

public class Effect extends Identity {
	public AbilityScores statDelta;
	public int timeToLive;
	public boolean usesTimeToLive;
	private Identity source;
	
	/**
	 * Create an Effect created by the specified Identity
	 * @param source Cause of effect
	 */
	public Effect(Identity source) {
		this(source, new AbilityScores());
	}
	
	/**
	 * Create an Effect created by the specified Identity,
	 * affecting stats as described by the AbilityScores block
	 * @param source Cause of effect
	 * @param statDelta Changes applied to stats
	 */
	public Effect(Identity source, AbilityScores statDelta) {
		this(source, statDelta, "");
	}
	
	/**
	 * Create an Effect created by the specified Identity,
	 * affecting stats as described by the AbilityScores block,
	 * with a name of friendlyName
	 * 
	 * @param source Cause of effect
	 * @param statDelta Changes applied to stats
	 * @param friendlyName What to call the Effect
	 */
	public Effect(Identity source, AbilityScores statDelta, String friendlyName) {
		this(source, statDelta, friendlyName, 0);
	}
	
	public Effect(Identity source, AbilityScores statDelta, String friendlyName, double secondsToLive) {
		this(source, statDelta, friendlyName, GLOBALS.secondsToFrames(secondsToLive));
	}
	
	public Effect(Identity source, AbilityScores statDelta, String friendlyName, int framesToLive) {
		super(friendlyName); //Identity: New ID, blank name
		this.source = source;
		this.statDelta = statDelta;
		this.timeToLive = framesToLive;
		this.usesTimeToLive = (this.timeToLive != 0);
	}

	public Identity getSource() {
		return source;
	}
}
