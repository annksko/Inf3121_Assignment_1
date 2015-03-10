

public class Task {
    private int id;
    private int time;
    private int staff; 
    private String name;
    String state;//untestet, tested, eller testing
	static int max = Integer.MAX_VALUE;
    private int earliestStart = 0;
    private int latestStart;
    private int slack;
    private Edge outEdges;
    int cntOutEdges;
    int cntPredecessors; 
    private Edge inEdges; 
	
    Task(int taskId, String name, int timeEst, int manReq) {
    	id = taskId;
    	this.name = name; 
    	time = timeEst;
    	staff = manReq;
    	outEdges = null;
    	inEdges = null; 
    	state = "untested";
    	latestStart = max;
    }
	
    Task() {
    	outEdges = null; 
    	inEdges = null; 
    	state = "untested";
    	latestStart = max;
    }
    
    Task(int taskId) {
    	id = taskId; 
    	name = "dummy";
    	time = 0;
    	latestStart = max;
    }
	
    public void updateInfo(int taskId, String name, int timeEst, int manReq) {
    	id = taskId;
    	this.name = name; 
    	time = timeEst;
    	staff = manReq;
    }
	
   
    
    /**
    * Legger til en ny outedge
    *
    */
    public void addEdge(Edge edge) {
    	if (outEdges == null) {
    		cntOutEdges++;
    		outEdges = edge;
    	} else {
    		edge.addNext(outEdges);
    		outEdges = edge; 
    		cntOutEdges++;
    	}
    }
    
    /**
    * Legger til en ny inedge
    *
    */
    public void addInEdge(Edge inEdge) {
    	if (inEdges == null) {
    		inEdges = inEdge;
    	} else {
    		inEdge.addNextInEdge(inEdges);
    		inEdges = inEdge; 
    	}
    }
    
    
    public void updateSlack() {
    	slack = latestStart-earliestStart;
    }
    
    public void updateStart(int t) {
    	if (earliestStart < t) {
    		earliestStart = t; 
    	} 
    }
    
    public void updateLatestStart(int t) {
    	if (latestStart > t) {
    		latestStart = t;
    	} 
    }
    
    public int getStartTime() {
    	return earliestStart; 
    }
    
    public int getLatestStart() {
    	return latestStart; 
    }
	
    public void dependencies(int dep) {
    	cntPredecessors = dep; 
    }
	
    
    public String getName() {
    	return name; 
    }
	
    public Edge getEdges() {
    	return outEdges; 
    }
    
    public Edge getInEdges() {
    	return inEdges; 
    }
    
    public void changeInEdges(Edge in) {
    	inEdges = in;
    }
    
    public String getState() {
    	return state;  
    }
	
    public int getTime() {
    	return time; 
    }
    
    public int getId() {
    	return id;
    }
    
    public int getStaff() {
    	return staff; 
    }
	
    public int getSlack() {
    	return slack;
    }
	
}

