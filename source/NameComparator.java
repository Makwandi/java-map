package prog2.Map;



import java.util.Comparator;

public class NameComparator implements Comparator<Place>{
	
	@Override
	public int compare(Place p1, Place p2) {
		return p1.getName().compareTo(p2.getName());
	}

}
