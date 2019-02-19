package prog2.Map

@SuppressWarnings("serial")
public class DescribedPlace extends Place {
	
	private String description;
	
	public DescribedPlace (Position givenPosition, String givenName, String givenDescription) {
		super(givenPosition, givenName);
		description = givenDescription;
	}
	
	public DescribedPlace (String givenCategory, Position givenPosition, String givenName, String givenDescription) {
		super(givenCategory, givenPosition, givenName);
		description = givenDescription;
	
	}

	public String getDescription() {
		return description;
	}
	
	@Override
	public String toString() {
		if (getCategory() == null) {
			return getPosition() + "  " + getName()  + "  " + description;
		}
		else {
			return getCategory() + "  " + getPosition() + "  " +  getName() + "  " + description;
		}
	}

}
