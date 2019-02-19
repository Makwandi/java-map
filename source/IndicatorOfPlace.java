package prog2.Map;


import java.awt.*;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


@SuppressWarnings("serial")
public class IndicatorOfPlace extends JComponent {
	
	private static final int[] X_COORDINATES = new int[] {0, 14, 7};
	private static final int[] Y_COORDINATES = new int[] {0, 0, 20};		
	private Place place;

	
	public IndicatorOfPlace(Place givenPlace){
		place = givenPlace;
		Position position = givenPlace.getPosition();
		setPreferredSize(new Dimension(20, 20));
		setBounds(position.getPosX() - 7, position.getPosY() -20, 20, 20);
		addMouseListener(new PlaceListener());
	}
	
	
	@Override
	protected void paintComponent(Graphics givenG) {
		super.paintComponent(givenG);
		boolean isVisible = place.getIsVisible();
		if (isVisible == true) {
			String category = place.getCategory();
			if(category != null && category.equals("Bus")) {
				givenG.setColor(Color.RED);
			} 
			if(category != null && category.equals("Train")) {
				givenG.setColor(Color.GREEN);
			}
			if(category != null && category.equals("Underground")) {
				givenG.setColor(Color.BLUE);
			}
			if (category == null) {
				givenG.setColor(Color.BLACK);
			}
			givenG.fillPolygon(X_COORDINATES, Y_COORDINATES, 3);
			boolean isMarked = place.getIsMarked();
			if(isMarked == true) {
				Graphics2D anotherG = (Graphics2D) givenG;
				int thicknessOfLine = 3;
				Stroke oldLine = anotherG.getStroke();
				anotherG.setStroke(new BasicStroke(thicknessOfLine));
				anotherG.drawRect(0, 0, 14, 20);
				anotherG.setStroke(oldLine);
			}
		}
		else {
			return;
		}
	}
	
	
	protected class PlaceListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent givenMouseEv) {
			if (SwingUtilities.isLeftMouseButton(givenMouseEv)) {
				boolean isMarked = place.getIsMarked();
				Position position = place.getPosition();
				if (isMarked == true) {
					place.setIsMarked(false);
					MapOfPlaces.removeFromKeysForMarkedPlaces(position);
					repaint();	
				}
				else {
					place.setIsMarked(true);
					MapOfPlaces.addToKeysForMarkedPlaces(position);
					repaint();
				}
			}
			else if (SwingUtilities.isRightMouseButton(givenMouseEv)) {
				Component c = givenMouseEv.getComponent();
				String name = null;
				Position position = null;
				String nameAndPosition = null;
				if (place instanceof NamedPlace) {
					position = place.getPosition();
					name = place.getName();
					nameAndPosition = name + " {" + position + "}";
					JOptionPane.showMessageDialog(c, nameAndPosition, "Place info:", JOptionPane.INFORMATION_MESSAGE);	
				}
				else {
					FormForInfoAboutDescribedPlace formForInfo = new FormForInfoAboutDescribedPlace();
					position = place.getPosition();
					name = place.getName();
					nameAndPosition = name + " {" + position + "}";
					DescribedPlace dPlace = (DescribedPlace) place;
					String description = dPlace.getDescription();
					formForInfo.setName(nameAndPosition);
					formForInfo.setDescription(description);
					JOptionPane.showMessageDialog(c, formForInfo, "Place info:", JOptionPane.INFORMATION_MESSAGE);
				}
			}
			else {
				return;
			}
		}
	}
	
	
	protected class FormForInfoAboutDescribedPlace extends JPanel {
		private JTextField nameField = new JTextField(15);
		private JTextField descriptionField = new JTextField(30); 
		
		public FormForInfoAboutDescribedPlace () {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			JPanel firstRow = new JPanel();
			firstRow.add(new JLabel("Name:"));
			firstRow.add(nameField);
			add(firstRow);
			JPanel secondRow = new JPanel();
			secondRow.add(new JLabel("Description:"));
			secondRow.add(descriptionField);
			add(secondRow);
		}
			
		public void setName(String givenName) {
			nameField.setText(givenName);
		}
			
		public void setDescription(String givenDescription) {
			descriptionField.setText(givenDescription);
		}	
	}

}


