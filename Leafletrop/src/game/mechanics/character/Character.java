package game.mechanics.character;

import java.util.ArrayList;
import util2d.actor.Actor;

public class Character {
	public Actor characterActor;
	public AbilityScores statBlock;
	public ArrayList<Effect> activeEffects;
	
	/*public void processEffects(boolean update) {
		for (Effect e:this.activeEffects) {
			this.setStrength(this.getStrength()+e.statDelta.getStrength());
			this.setDexterity(this.getDexterity()+e.statDelta.getDexterity());
			this.setStrength(this.getStrength()+e.statDelta.getStrength());
			this.setStrength(this.getStrength()+e.statDelta.getStrength());
			this.setStrength(this.getStrength()+e.statDelta.getStrength());
			this.setStrength(this.getStrength()+e.statDelta.getStrength());
		}
	}*/

	/*public int getStrength() {
		return Strength;
	}

	public void setStrength(int strength) {
		Strength = strength;
	}

	public int getDexterity() {
		return Dexterity;
	}

	public void setDexterity(int dexterity) {
		Dexterity = dexterity;
	}

	public int getConstitution() {
		return Constitution;
	}

	public void setConstitution(int constitution) {
		Constitution = constitution;
	}

	public int getIntelligence() {
		return Intelligence;
	}

	public void setIntelligence(int intelligence) {
		Intelligence = intelligence;
	}

	public int getWisdom() {
		return Wisdom;
	}

	public void setWisdom(int wisdom) {
		Wisdom = wisdom;
	}

	public int getCharisma() {
		return Charisma;
	}

	public void setCharisma(int charisma) {
		Charisma = charisma;
	}*/
}
