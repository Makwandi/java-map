package prog2.Map;


import javax.swing.JComponent;


@SuppressWarnings("serial")
public class Place extends JComponent{
	
	private String name;
	private String category;
	private Position position;
	private boolean isVisible = true;
	private boolean isMarked = false;
	
	public Place (Position givenPosition, String givenName) {
		position = givenPosition;
		name = givenName;
	}
	
	public Place (String givenCategory, Position givenPosition, String givenName) {
		this(givenPosition, givenName);
		category = givenCategory;
	}
	
	public String getName() {
		return name;
	}
	
	public String getCategory() {
		return category;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public boolean getIsMarked() {
		return isMarked;
	}
	
	public void setIsMarked(boolean b) {
		isMarked = b;
		repaint();
	}
	
	
	public boolean getIsVisible() {
		return isVisible;
	}
	
	public void setIsVisible(boolean b) {
		isVisible = b;
		repaint();
	}

	
	public String toString() {
		if (category == null) {
			return position + "  " + name;
		}
		else {
			return category + "  " + position + "  " + name;
		}
	}

}

