package prog2.Map;


import java.awt.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class MapReader extends JPanel {
	
private ImageIcon mapImage;
	
	public MapReader(String givenMapFile) {
		mapImage = new ImageIcon(givenMapFile);
		setLayout(null);
		int w = mapImage.getIconWidth();
		int h = mapImage.getIconHeight();
		setPreferredSize(new Dimension (w, h));
		setMinimumSize(new Dimension (w, h));
	}

	protected void paintComponent(Graphics givenG){
		super.paintComponent(givenG);
		givenG.drawImage(mapImage.getImage(), 0, 0, this);	
	}

}
