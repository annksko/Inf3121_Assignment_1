
/**
* FIFO lenkeliste. 
*
*/
public class List {
	Node first = null; 
	Node last = null; 
	
	class Node {
		Node next;
		Task task;
		
		Node(Task t) {
			task = t; 
		}
	}
	
	/**
	* Et Task objekt settes inn bakers i lenkelisten:
	* @param t objektet som settes inn. 
	*/
	public void push(Task t) {
		Node n = new Node(t);
		if (first == null) {
			first = n;
			last = n; 
		} else {
			last.next = n;
			last = n; 
		}
	}
	
	/**
	* Legger Task t inn i lenkelisten, sortert etter
	* naar task tidligs kan vaere ferdig med sin oppgave
	*
	*/
	public void pushSorted(Task t) {
		Node n = new Node(t);
		Node current = first; 
		Task c; 
		Node prev = null;
		int cEndTime;
		int tEndTime = t.getStartTime() + t.getTime();
		while (current != null) {
			c = current.task;
			cEndTime = c.getStartTime() + c.getTime();
			if (cEndTime < tEndTime) {
				prev = current;
				current = current.next;
			} else break;
		}
		if (current == first) {
			first = n; 
			first.next = current;
		} else if (current == null)  {
			prev.next = n;
		} else {
			prev.next = n;
			n.next = current; 
		}
	}
	
	
	
	/**
	* Forste element i lenkelisten returneres, og denne
	* sin neste settes til aa vaere forste. 
	* @return task objekt, eller null om listen er tom. 
	*/
	public Task pop() {
		Node n = first;
		if (first != null) {
			first = first.next; 
			return n.task; 
		} 
		return null; 
	}
}
