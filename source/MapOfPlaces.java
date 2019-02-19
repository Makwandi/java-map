package prog2.Map;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Iterator;
import java.io.*;


@SuppressWarnings("serial")
public class MapOfPlaces extends JFrame {
	
	//To keep track of marked places (via their positions):
	private static List<Position> keysForMarkedPlaces = new ArrayList<>();;
	//To keep track of places by position:
	private Map<Position, Place> places = new HashMap<>();
	//To keep track of places by category or name:
	private Map<String, ArrayList<Place>> placesGroupedByCategoryOrName = new TreeMap<>();

	//For other functions
	private JRadioButton namedPlaceButton, describedPlaceButton;
	private JList<String> categoryList;
	private JScrollPane scrollPane;
	private JFileChooser mapChooser = new JFileChooser(".");
	private JFileChooser fileChooser = new JFileChooser(".");
	private MapReader givenMap = null;
	private File mapFile;
	private Position position;
	private Cursor cursor;
	private JTextField searchField;
	private boolean changed = false;		//Part of check for unsaved changes before exiting program
	
	
	MapOfPlaces(){

		super("Inlupp 2");
		setLayout(new BorderLayout());
		FileNameExtensionFilter imgFilter = new FileNameExtensionFilter("Image Only", "png", "gif", "jpg");
		mapChooser.setFileFilter(imgFilter);

		// ARCHIVE MENU
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		JMenu archiveMenu = new JMenu("Archive");
		menuBar.add(archiveMenu);
				
		JMenuItem newMapSelection = new JMenuItem("New Map");
		newMapSelection.addActionListener(new NewMapListener());
		archiveMenu.add(newMapSelection);

		JMenuItem loadPlacesSelection = new JMenuItem("Load Places");
		loadPlacesSelection.addActionListener(new LoadPlacesListener());
		archiveMenu.add(loadPlacesSelection);

		JMenuItem saveSelection = new JMenuItem("Save");
		saveSelection.addActionListener(new SavePlaceListener());
		archiveMenu.add(saveSelection);

		JMenuItem exitSelection = new JMenuItem("Exit");
		exitSelection.addActionListener(new ExitListener());
		archiveMenu.add(exitSelection);
		addWindowListener(new ExitListener());

		// TOP PANEL
		JPanel topPanel = new JPanel();
		add(topPanel, BorderLayout.NORTH);

		// Type of place function
		JButton newButton = new JButton("New");
		newButton.addActionListener(new NewPlaceListener());
		topPanel.add(newButton);
		JPanel panelForPlaceButtons = new JPanel();
		panelForPlaceButtons.setLayout(new BoxLayout(panelForPlaceButtons, BoxLayout.Y_AXIS));
		namedPlaceButton = new JRadioButton("Named", true);
		panelForPlaceButtons.add(namedPlaceButton);
		describedPlaceButton = new JRadioButton("Described");
		panelForPlaceButtons.add(describedPlaceButton);
		ButtonGroup bg = new ButtonGroup();
		bg.add(namedPlaceButton);
		bg.add(describedPlaceButton);
		topPanel.add(panelForPlaceButtons);

		// Search function
		searchField = new JTextField("Search", 10);
		topPanel.add(searchField);
		JButton searchButton = new JButton("Search");
		searchButton.addActionListener(new SearchButtonListener());
		topPanel.add(searchButton);

		// Hide function
		JButton hideButton = new JButton("Hide");
		hideButton.addActionListener(new HideButtonListener());
		topPanel.add(hideButton);

		// Remove function
		JButton removeButton = new JButton("Remove");
		removeButton.addActionListener(new RemoveButtonListener());
		topPanel.add(removeButton);

		// Coordinate function
		JButton coordinatesButton = new JButton("Coordinates");
		coordinatesButton.addActionListener(new CoordinatesButtonListener());
		topPanel.add(coordinatesButton);
		

		// RIGHT PANEL
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		
		// Show categories function
		rightPanel.add(new JLabel("Categories"));
		String[] categories = {"Bus", "Train", "Underground"};
		categoryList = new JList<>(categories);
		categoryList.setVisibleRowCount(3);
		categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		categoryList.setFixedCellWidth(120);
		rightPanel.add(categoryList);
		categoryList.addListSelectionListener(new SelectedCategoryListener());
		
		// Hide category function
		JButton hideCategoryButton = new JButton("Hide category");
		hideCategoryButton.addActionListener(new HideCategoryButtonListener());
		hideCategoryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		rightPanel.add(hideCategoryButton);

		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridBagLayout());
		centerPanel.add(rightPanel);
		add(centerPanel, BorderLayout.EAST);
	

		setSize(250, 100);		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocation(450, 100);
		pack();
		setVisible(true);

	} //End of Constructor
	
	
	//INNER CLASSES (in order roughly corresponding to the layout of functions from left to right on the main frame)
	
	private class NewMapListener implements ActionListener{	
		public void actionPerformed(ActionEvent givenAction){
			if(changed){
				int result = JOptionPane.showConfirmDialog(MapOfPlaces.this, "There are unsaved changes in current document. Open a new document anyway?", "Warning:", JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION) {
					keysForMarkedPlaces.clear();
					placesGroupedByCategoryOrName.clear();
					places.clear();
					repaint();
					givenMap.setEnabled(false);
					loadMap();
					changed = false;
				}
				else if (result == JOptionPane.CANCEL_OPTION) {
					JOptionPane.getFrameForComponent(getParent());
				}
			}
			else {
				loadMap();
			}
		}	
	}
				
	
	private class LoadPlacesListener implements ActionListener{
		public void actionPerformed(ActionEvent givenAction){
			FileNameExtensionFilter fnef = new FileNameExtensionFilter("txt", "places.txt");
			fileChooser.addChoosableFileFilter(fnef);
			if (changed) {
				int result = JOptionPane.showConfirmDialog(MapOfPlaces.this, "There are unsaved changes in current document. Open a new document anyway?", "Warning:", JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION) {
					keysForMarkedPlaces.clear();
					placesGroupedByCategoryOrName.clear();
					places.clear();
					givenMap.removeAll();
					givenMap.repaint();
					changed = false;
				}
				else if (result == JOptionPane.CANCEL_OPTION) {
					return;
				}
			}	
			int result = fileChooser.showOpenDialog(MapOfPlaces.this);
			if (result != JFileChooser.APPROVE_OPTION) {
				return;
			}
			File fileToBeLoaded = fileChooser.getSelectedFile();
			loadPlaces(fileToBeLoaded);		
		}	
	}
	
	
	private class SavePlaceListener implements ActionListener {
		public void actionPerformed(ActionEvent givenAction)  {
			FileNameExtensionFilter fnef = new FileNameExtensionFilter("txt", "places.txt");
			fileChooser.addChoosableFileFilter(fnef);
			int result = fileChooser.showOpenDialog(MapOfPlaces.this);
			if (result != JFileChooser.APPROVE_OPTION) {
				return;
			}
			File saveFile = fileChooser.getSelectedFile();
			savePlaces(saveFile);
		}	
	}
	

	private class ExitListener extends WindowAdapter implements ActionListener{
		private void windowClosing(){
			if (changed){
				int answer = JOptionPane.showConfirmDialog(MapOfPlaces.this, "There are unsaved changes. Exit anyway?", "Warning:", JOptionPane.OK_CANCEL_OPTION);
				if (answer == JOptionPane.OK_OPTION) {
					System.exit(0);
				}
				if (answer == JOptionPane.CANCEL_OPTION) {
					JOptionPane.getFrameForComponent(getParent());
				}
			}
			else {
				System.exit(0);	
			}
		}
		@Override
		public void windowClosing(WindowEvent givenWindowEv){
			windowClosing();
		}
		public void actionPerformed(ActionEvent givenWindowEv){
			windowClosing();
		}
	}
	

	private class SearchButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent givenAction){
			searchPlaceByName();
		}	
	}
	
	
	private class HideButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent givenAction){
			hideMarkedPlaces();
		}	
	}
	
	
	private class RemoveButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent givenAction){
			removeMarkedPlaces();
		}	
	}

	
	//Listener for a click on the Coordinates button
	private class CoordinatesButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent givenAction){
			searchPlaceByCoordinates();
		}	
	}
	
	//Fill-in form for user to give coordinates of a place to look for
	private class FormForCoordinates extends JPanel {
		private JTextField xField = new JTextField(4);
		private JTextField yField = new JTextField(4); 
		
		public FormForCoordinates() {
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			JPanel firstPanel = new JPanel();
			firstPanel.add(new JLabel("X:"));
			firstPanel.add(xField);
			add(firstPanel);
			JPanel secondPanel = new JPanel();
			secondPanel.add(new JLabel("Y:"));
			secondPanel.add(yField);
			add(secondPanel);
		}
		
		public int getCoordinateX() {
			return Integer.parseInt(xField.getText());
		}
		
		public int getCoordinateY() {
			return Integer.parseInt(yField.getText());
		}
	}
	
	
	private class SelectedCategoryListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent givenListEv) {
			if (givenListEv.getValueIsAdjusting()) {
				showCategory();
			}	
		}
	}
	
	//To prepare for the creation of a new place:
	private class NewPlaceListener implements ActionListener {
		public void actionPerformed(ActionEvent givenAction) {
			Cursor cursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
			givenMap.setCursor(cursor);
			givenMap.addMouseListener(newPlaceOnMapListener);	
		}
	}
	
	//To detect the position of a new place on the map:
	MouseListener newPlaceOnMapListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent givenMouseEv) {
			if (SwingUtilities.isRightMouseButton(givenMouseEv)) {
				position = new Position(givenMouseEv.getX(), givenMouseEv.getY());
				createPlace(position);
			}
		}
	};
	

	//Fill-in form for new described places:
	private class FormForDescribedPlace extends JPanel {
		private JTextField nameField = new JTextField(10);
		private JTextField descriptionField = new JTextField(30); 
		
		public FormForDescribedPlace () {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			JPanel firstRow = new JPanel();
			firstRow.add(new JLabel("Name of place:"));
			firstRow.add(nameField);
			add(firstRow);
			JPanel secondRow = new JPanel();
			secondRow.add(new JLabel("Description of place:"));
			secondRow.add(descriptionField);
			add(secondRow);
		}
		
		public String getName() {
			return nameField.getText();
		}
		
		public String getDescription() {
			return descriptionField.getText();
		}
	}
	
	
	private class HideCategoryButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent givenAction){
			hideCategory();	
		}	
	}
	
	
	//METHODS (in alphabetical order)
	
	//Method called from IndicatorOfPlace when such an object is marked:
	public static void addToKeysForMarkedPlaces(Position givenPosition) {
		keysForMarkedPlaces.add(givenPosition);
	}
	
	private void addToPlacesGroupedByCategoryOrName(String givenCategoryOrName, Place givenPlace) {
		ArrayList<Place> temp = placesGroupedByCategoryOrName.get(givenCategoryOrName);
		if (temp == null) {
			temp = new ArrayList<>();
			placesGroupedByCategoryOrName.put(givenCategoryOrName, temp);
		}
		temp.add(givenPlace);
	}
	
	
	private void createIndicatorOfPlace(Place givenPlace) {
		IndicatorOfPlace triangle = new IndicatorOfPlace(givenPlace);
		givenMap.add(triangle);
		givenMap.validate(); 									
		givenMap.repaint();
	}
	

	private void createPlace(Position givenPosition) {
		if (places.containsKey(givenPosition)) {
			JOptionPane.showMessageDialog(null, "A place exists already on this position! Cannot have two in the same location.");
			return;
		}
		else {
			String givenCategory = (String)categoryList.getSelectedValue();
			@SuppressWarnings("unused")
			IndicatorOfPlace triangle;
			//What to do if category is selected:
			if (!categoryList.isSelectionEmpty()) {
				if ((namedPlaceButton.isSelected() && givenCategory.equals("Bus")) || (namedPlaceButton.isSelected() && givenCategory.equals("Train")) || (namedPlaceButton.isSelected() && givenCategory.equals("Underground"))) {
					String givenNameOfPlace = JOptionPane.showInputDialog(null, "Name:", "Enter information about place:", JOptionPane.QUESTION_MESSAGE);
					if (givenNameOfPlace == null) {
						return;																			
					} 
					else if (givenNameOfPlace.equals("")){
						JOptionPane.showMessageDialog(MapOfPlaces.this, "No name of place given!");
						return;		
					}
					NamedPlace nPlace = new NamedPlace(givenCategory, givenPosition, givenNameOfPlace);
					//Putting a triangle indicating the position of the place on the map:
					createIndicatorOfPlace(nPlace);
					
					places.put(givenPosition, nPlace);
					//For the show, hide or remove category functions:
					if (givenCategory.equals("Bus")) {
						addToPlacesGroupedByCategoryOrName("Bus", nPlace);
					}
					if (givenCategory.equals("Train")) {
						addToPlacesGroupedByCategoryOrName("Train", nPlace);
					}
					if (givenCategory.equals("Underground")) {
						addToPlacesGroupedByCategoryOrName("Underground", nPlace);
					}
					//For the search place by name function:
					addToPlacesGroupedByCategoryOrName(givenNameOfPlace, nPlace);
				} 
				else if ((describedPlaceButton.isSelected() && givenCategory.equals("Bus")) || (describedPlaceButton.isSelected() && givenCategory.equals("Train")) || (describedPlaceButton.isSelected() && givenCategory.equals("Underground"))) {
					FormForDescribedPlace bForm = new FormForDescribedPlace();
					int answer = JOptionPane.showConfirmDialog(null, bForm, "Enter information about place:", JOptionPane.OK_CANCEL_OPTION);
					if (answer != JOptionPane.OK_OPTION) {
						return;
					}
					String givenNameOfPlace = bForm.getName();
					if (givenNameOfPlace.equals("")) {
						JOptionPane.showMessageDialog(MapOfPlaces.this, "No name of place given!");
						return;
					}
					String givenDescription = bForm.getDescription();
					if (givenDescription.equals("")) {
						JOptionPane.showMessageDialog(MapOfPlaces.this, "No description of place given!");
						return;
					}
					DescribedPlace dPlace = new DescribedPlace(givenCategory, givenPosition, givenNameOfPlace, givenDescription);
					createIndicatorOfPlace(dPlace);
					
					places.put(givenPosition, dPlace);
					if (givenCategory.equals("Bus")) {
						addToPlacesGroupedByCategoryOrName("Bus", dPlace);
					}
					if (givenCategory.equals("Train")) {
						addToPlacesGroupedByCategoryOrName("Train", dPlace);
					}
					if (givenCategory.equals("Underground")) {
						addToPlacesGroupedByCategoryOrName("Underground", dPlace);
					}
					addToPlacesGroupedByCategoryOrName(givenNameOfPlace, dPlace);
				}
			}  //End of alternatives with category selected
			//What to do if category is not selected:
			else {
				if (namedPlaceButton.isSelected()) {
					String givenNameOfPlace = JOptionPane.showInputDialog(null, "Name:", "Enter information about place", JOptionPane.QUESTION_MESSAGE);
					if (givenNameOfPlace == null) {
						return;																			
					} 
					else if (givenNameOfPlace.equals("")){
						JOptionPane.showMessageDialog(MapOfPlaces.this, "No name of place given!");
						return;		
					}
					NamedPlace nPlace = new NamedPlace(givenPosition, givenNameOfPlace);
					createIndicatorOfPlace(nPlace);
					
					places.put(givenPosition, nPlace);	
					addToPlacesGroupedByCategoryOrName("Uncategorized", nPlace);
					addToPlacesGroupedByCategoryOrName(givenNameOfPlace, nPlace);
				}
				//If namedPlaceButton is not selected, then describedPlaceButton must be selected:
				else {
					FormForDescribedPlace form = new FormForDescribedPlace();
					int answer = JOptionPane.showConfirmDialog(null, form, "Enter information about place", JOptionPane.OK_CANCEL_OPTION);
					if (answer != JOptionPane.OK_OPTION) {
						return;
					}
					String givenNameOfPlace = form.getName();
					if (givenNameOfPlace.equals("")) {
						JOptionPane.showMessageDialog(MapOfPlaces.this, "No name of place given!");
						return;
					}
					String givenDescription = form.getDescription();
					if (givenDescription.equals("")) {
						JOptionPane.showMessageDialog(MapOfPlaces.this, "No description of place given!");
						return;
					}
					DescribedPlace dPlace = new DescribedPlace(givenPosition, givenNameOfPlace, givenDescription);
					createIndicatorOfPlace(dPlace);
					
					places.put(givenPosition, dPlace);
					addToPlacesGroupedByCategoryOrName("Uncategorized", dPlace);
					addToPlacesGroupedByCategoryOrName(givenNameOfPlace, dPlace);
				}
			}
		}
		categoryList.clearSelection(); 								    //To enable the creation of category-less places
		givenMap.removeMouseListener(newPlaceOnMapListener);			//To stop the mouse listener from listening after one click
		cursor = Cursor.getDefaultCursor();								//To remove the cross hair cursor after the creation of a new place
		givenMap.setCursor(cursor);
		changed = true;	
	} //End of createPlace() method
	
	
	private void hideCategory() {
		String givenCategory = categoryList.getSelectedValue();
		List<Place> busPlaces = new ArrayList<>();
		List<Place> trainPlaces = new ArrayList<>();
		List<Place> undergroundPlaces = new ArrayList<>();
		List<Place> uncategorizedPlaces = new ArrayList<>();
		
		if (givenCategory.equals("Bus")) {
			placesGroupedByCategoryOrName.get("Bus").forEach(b -> busPlaces.add(b));
			for(Place b : busPlaces){
				b.setIsVisible(false);
			}
			placesGroupedByCategoryOrName.get("Train").forEach(t -> trainPlaces.add(t));
			for(Place t : trainPlaces){
				t.setIsVisible(true);
			}
			placesGroupedByCategoryOrName.get("Underground").forEach(u -> undergroundPlaces.add(u));
			for(Place u : undergroundPlaces){
				u.setIsVisible(true);
			}
			placesGroupedByCategoryOrName.get("Uncategorized").forEach(noC -> uncategorizedPlaces.add(noC));
			for(Place noC :	uncategorizedPlaces){
				noC.setIsVisible(true);
			}
		}
		if (givenCategory.equals("Train")) {
			placesGroupedByCategoryOrName.get("Train").forEach(t -> trainPlaces.add(t));
			for(Place t : trainPlaces){
				t.setIsVisible(false);
			}
			placesGroupedByCategoryOrName.get("Bus").forEach(b -> busPlaces.add(b));
			for(Place b : busPlaces){
				b.setIsVisible(true);
			}
			placesGroupedByCategoryOrName.get("Underground").forEach(u -> undergroundPlaces.add(u));
			for(Place u : undergroundPlaces){
				u.setIsVisible(true);
			}
			placesGroupedByCategoryOrName.get("Uncategorized").forEach(noC -> uncategorizedPlaces.add(noC));
			for(Place noC : uncategorizedPlaces){
				noC.setIsVisible(true);
			}
		}
		if (givenCategory.equals("Underground")) {
			placesGroupedByCategoryOrName.get("Underground").forEach(u -> undergroundPlaces.add(u));
			for(Place u : undergroundPlaces){
				u.setIsVisible(false);
			}
			placesGroupedByCategoryOrName.get("Bus").forEach(b -> busPlaces.add(b));
			for(Place b : busPlaces){
				b.setIsVisible(true);
			}
			placesGroupedByCategoryOrName.get("Train").forEach(t -> trainPlaces.add(t));
			for(Place t : trainPlaces){
				t.setIsVisible(true);
			}
			placesGroupedByCategoryOrName.get("Uncategorized").forEach(noC -> uncategorizedPlaces.add(noC));
			for(Place noC : uncategorizedPlaces){
				noC.setIsVisible(true);
			}
		}
		givenMap.repaint();	
	} //End of hideCategory() method
	
	
	private void hideMarkedPlaces() {
		for (Position p : keysForMarkedPlaces) {
			Place placeToHide = places.get(p);
			placeToHide.setIsMarked(false);
			placeToHide.setIsVisible(false);
		}
		givenMap.repaint();
		keysForMarkedPlaces.clear();
	}
	
	
	private void loadMap() {
		int result = mapChooser.showOpenDialog(MapOfPlaces.this);
		if(result != JFileChooser.APPROVE_OPTION)
			return;
		mapFile = mapChooser.getSelectedFile();
		String givenImage = mapFile.getAbsolutePath();
		
		if (scrollPane != null)
			remove(scrollPane);
		
		givenMap = new MapReader(givenImage);
		scrollPane = new JScrollPane(givenMap);
		add(scrollPane, BorderLayout.CENTER);
		pack();
		validate();
		repaint();
	}
	
	
	private void loadPlaces(File givenFile) {
		try {
			FileReader in = new FileReader(givenFile);
			BufferedReader bReader = new BufferedReader(in);
			String line;
			while ((line = bReader.readLine()) != null) {
				String[] partsOfString = line.split(",");
				int posX = Integer.valueOf(partsOfString[2]);
				int posY = Integer.valueOf(partsOfString[3]);
				Position p = new Position(posX, posY);
				String name = partsOfString[4];
				if (line.startsWith("Named,None")) {
					NamedPlace nPlace = new NamedPlace(p, name);
					//Putting a triangle indicating the position of the place on map:
					createIndicatorOfPlace(nPlace);
					//Storing new place in collections:
					places.put(p, nPlace);
					addToPlacesGroupedByCategoryOrName("Uncategorized", nPlace);
					addToPlacesGroupedByCategoryOrName(name, nPlace);
				}
				else if (line.startsWith("Named,Bus") || line.startsWith("Named,Train") || line.startsWith("Named,Underground") ) {
					String category = partsOfString[1];
					NamedPlace nPlace = new NamedPlace(category, p, name);
					createIndicatorOfPlace(nPlace);
					places.put(p, nPlace);
					if (line.contains("Bus")) {
						addToPlacesGroupedByCategoryOrName("Bus", nPlace);
					}
					if (line.contains("Train")) {
						addToPlacesGroupedByCategoryOrName("Train", nPlace);
					}
					if (line.contains("Underground")) {
						addToPlacesGroupedByCategoryOrName("Underground", nPlace);
					}
					addToPlacesGroupedByCategoryOrName(name, nPlace);
				}
				else if (line.startsWith("Described,None")) {
					String description = partsOfString[5];
					DescribedPlace dPlace = new DescribedPlace(p, name, description);
					createIndicatorOfPlace(dPlace);
					places.put(p, dPlace);
					addToPlacesGroupedByCategoryOrName("Uncategorized", dPlace);
					addToPlacesGroupedByCategoryOrName(name, dPlace);
				}
				else if (line.startsWith("Described,Bus") || line.startsWith("Described,Train") || line.startsWith("Described,Underground")) {
					String category = partsOfString[1];
					String description = partsOfString[5];
					DescribedPlace dPlace = new DescribedPlace(category, p, name, description);
					createIndicatorOfPlace(dPlace);
					places.put(p, dPlace);
					if (line.contains("Bus")) {
						addToPlacesGroupedByCategoryOrName("Bus", dPlace);
					}
					if (line.contains("Train")) {
						addToPlacesGroupedByCategoryOrName("Train", dPlace);
					}
					if (line.contains("Underground")) {
						addToPlacesGroupedByCategoryOrName("Underground", dPlace);
					}
					addToPlacesGroupedByCategoryOrName(name, dPlace);
				}
			}
			bReader.close();
			in.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(MapOfPlaces.this, "Error");
		}
	} //End of loadPlaces() method

	
	//Method called from IndicatorOfPlace when such an object is unmarked:
	public static void removeFromKeysForMarkedPlaces(Position givenPosition) {
		int location = keysForMarkedPlaces.indexOf(givenPosition);
		keysForMarkedPlaces.remove(location);
	}
		
		
	private void removeMarkedPlaces() {
		for (Position p : keysForMarkedPlaces) {
			Place placeToErase = places.get(p);
			placeToErase.setIsMarked(false);
			placeToErase.setIsVisible(false);
			places.remove(p);
			
			String categoryOfPlace = placeToErase.getCategory();
			if (categoryOfPlace == null) {
				categoryOfPlace = "Uncategorized";
			}
			List<Place> placesInGivenCategory = new ArrayList<>();
			placesInGivenCategory = placesGroupedByCategoryOrName.get(categoryOfPlace);
			placesInGivenCategory.removeIf((Place place) -> place.getPosition().equals(p));
		
			String nameOfPlace = placeToErase.getName();
			List<Place> placesWithGivenName = new ArrayList<>();
			placesWithGivenName = placesGroupedByCategoryOrName.get(nameOfPlace);
			placesWithGivenName.removeIf((Place place) -> place.getPosition().equals(p));
		}
		givenMap.repaint();
		keysForMarkedPlaces.clear();
		changed = true;	
	}
	
	
	private void savePlaces(File givenFile) {
		DescribedPlace d;
		try {
			FileWriter fWriter = new FileWriter(givenFile);
			PrintWriter out = new PrintWriter(fWriter);
			for (Map.Entry<Position, Place> entry : places.entrySet()) {
				Place p = entry.getValue();
				int x = p.getPosition().getPosX();
				int y = p.getPosition().getPosY();
				if (p.getCategory() == null && p.getClass().equals(NamedPlace.class)) {
					out.println("Named," + "None," + x + "," + y + "," + p.getName());
					out.flush();
				}
				else if (p.getClass().equals(NamedPlace.class)) {
					out.println("Named," + p.getCategory() + "," + x + "," + y + "," + p.getName());
					out.flush();
				}
				else if (p.getCategory() == null && p.getClass().equals(DescribedPlace.class)) {
					d = (DescribedPlace) p;
					out.println("Described," + "None," + x + "," + y + "," + d.getName() + "," + d.getDescription());
					out.flush();
				}
				else {
					d = (DescribedPlace) p;
					out.println("Described," + p.getCategory() + "," + x + "," + y + "," + d.getName() + "," + d.getDescription());
					out.flush();
				}
			}
			out.close();
			changed = false;
		}  catch (IOException e) {
				JOptionPane.showMessageDialog(MapOfPlaces.this, "Error");
		}
	} //End of savePlaces() method
	
	
	private void searchPlaceByCoordinates() {
		FormForCoordinates cForm = new FormForCoordinates();
		int answer = JOptionPane.showConfirmDialog(null, cForm, "Enter coordinates:", JOptionPane.OK_CANCEL_OPTION);
		if (answer != JOptionPane.OK_OPTION) {
			return;
		}
		int givenX = cForm.getCoordinateX();
		int givenY = cForm.getCoordinateY();
		Position p = new Position(givenX, givenY);
		if (places.containsKey(p)) {
			List<Place> listOfPlaces = new ArrayList<>();
			places.values().forEach(v -> listOfPlaces.add(v));
			listOfPlaces.forEach(e -> e.setIsVisible(false));
			Place givenPlace = places.get(p);
			givenPlace.setIsVisible(true);
			givenPlace.setIsMarked(true);
			addToKeysForMarkedPlaces(p);
			givenMap.repaint();
		}
		else {
			JOptionPane.showMessageDialog(MapOfPlaces.this, "There is no place at these coordinates");
			return;
		}
	}


	private void searchPlaceByName() {
		String searchTokens = searchField.getText();
		List<Place> placesWithGivenName = placesGroupedByCategoryOrName.get(searchTokens);
		if (placesWithGivenName == null) {
			JOptionPane.showMessageDialog(MapOfPlaces.this, "There is no place with that name registered");
			return;
		}
		else {
			List<Place> listOfPlaces = new ArrayList<>();
			places.values().forEach(v -> listOfPlaces.add(v));
			listOfPlaces.forEach(e -> e.setIsVisible(false));
			List<Place> listOfPlacesWithGivenName = new ArrayList<>();
			placesGroupedByCategoryOrName.get(searchTokens).forEach(n -> listOfPlacesWithGivenName.add(n));
			for(Place n : listOfPlacesWithGivenName){
				n.setIsVisible(true);
				n.setIsMarked(true);
				Position p = n.getPosition();
				addToKeysForMarkedPlaces(p);
			}
			givenMap.repaint();
		}
	}
	
	
	private void showCategory() {
		String givenCategory = categoryList.getSelectedValue();
		List<Place> busPlaces = new ArrayList<>();
		List<Place> trainPlaces = new ArrayList<>();
		List<Place> undergroundPlaces = new ArrayList<>();
		List<Place> uncategorizedPlaces = new ArrayList<>();
		
		if (givenCategory.equals("Bus")) {
			placesGroupedByCategoryOrName.get("Bus").forEach(b -> busPlaces.add(b));
			for(Place b : busPlaces){
				b.setIsVisible(true);
			}
			placesGroupedByCategoryOrName.get("Train").forEach(t -> trainPlaces.add(t));
			for(Place t : trainPlaces){
				t.setIsVisible(false);
			}
			placesGroupedByCategoryOrName.get("Underground").forEach(u -> undergroundPlaces.add(u));
			for(Place u : undergroundPlaces){
				u.setIsVisible(false);
			}
			placesGroupedByCategoryOrName.get("Uncategorized").forEach(noC -> uncategorizedPlaces.add(noC));
			for(Place noC : uncategorizedPlaces){
				noC.setIsVisible(false);
			}
		}
		if (givenCategory.equals("Train")) {
			placesGroupedByCategoryOrName.get("Train").forEach(t -> trainPlaces.add(t));
			for(Place t : trainPlaces){
				t.setIsVisible(true);
			}
			placesGroupedByCategoryOrName.get("Bus").forEach(b -> busPlaces.add(b));
			for(Place b : busPlaces){
				b.setIsVisible(false);
			}
			placesGroupedByCategoryOrName.get("Underground").forEach(u -> undergroundPlaces.add(u));
			for(Place u : undergroundPlaces){
				u.setIsVisible(false);
			}
			placesGroupedByCategoryOrName.get("Uncategorized").forEach(noC -> uncategorizedPlaces.add(noC));
			for(Place noC : uncategorizedPlaces){
				noC.setIsVisible(false);
			}
		}
		if (givenCategory.equals("Underground")) {
			placesGroupedByCategoryOrName.get("Underground").forEach(u -> undergroundPlaces.add(u));
			for(Place u : undergroundPlaces){
				u.setIsVisible(true);
			}
			placesGroupedByCategoryOrName.get("Bus").forEach(b -> busPlaces.add(b));
			for(Place b : busPlaces){
				b.setIsVisible(false);
			}
			placesGroupedByCategoryOrName.get("Train").forEach(t -> trainPlaces.add(t));
			for(Place t : trainPlaces){
				t.setIsVisible(false);
			}
			placesGroupedByCategoryOrName.get("Uncategorized").forEach(noC -> uncategorizedPlaces.add(noC));
			for(Place noC : uncategorizedPlaces){
				noC.setIsVisible(false);
			}
		}
		givenMap.repaint();
	} //End of showCategory() method
	
	
	public static void main(String[] args) {
		
		new MapOfPlaces();	

	}
	
}
