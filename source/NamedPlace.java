package prog2.Map;


@SuppressWarnings("serial")
public class NamedPlace extends Place {
	
	public NamedPlace (Position givenPosition, String givenName) {
		super(givenPosition, givenName);
	}
	
	public NamedPlace (String givenCategory, Position givenPosition, String givenName) {
		super(givenCategory, givenPosition, givenName);
	}

}
