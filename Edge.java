

public class Edge {
	
    private Edge next;//peker til neste outedge
    private Edge nextInEdge; 
    private Task task;
    private Task precedingTask;
    
	
    Edge(Task task) {
    	this.task = task; 
    	next = null; 
    }
	
    
    public Task getTask() {
    	return task; 
    }
    
    public void addTask(Task t) {
    	task = t; 
    }
    
    public void addBackPointer(Task t) {
    	precedingTask = t; 
    }
    
    public Task getPrecedingTask() {
    	return precedingTask; 
    }
    
    public Edge getNext() {
    	return next; 
    }
    
    public void addNext(Edge edge) {
    	next = edge;
    }
    
    public Edge getNextInEdge() {
    	return nextInEdge; 
    }
     
    public void addNextInEdge(Edge inEdge) {
    	nextInEdge = inEdge;
    }
    
}