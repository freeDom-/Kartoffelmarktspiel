package jKMS.cards;

public class BuyerCard extends Card {
	public BuyerCard(int idNumber, int value, char pack) {
		super(idNumber, value, pack);
	}

	@Override
	public String toString() {
		return "BuyerCard " + this.pack + " [ID = " + idNumber + ", wtp = " + value + "]";
	}
	
}
