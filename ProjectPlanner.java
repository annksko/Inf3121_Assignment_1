import java.util.Iterator;
import java.util.Scanner;
import java.io.IOException;
import java.io.*;
import java.util.*;



public class ProjectPlanner {
    static String fileName;
    static String manPower;
    Task start;
    Task[] task;//peker array med alle oppgavene
    boolean cycleFound = false;
    int completionTime; 
	
    public static void main(String[] args) {
    	if (args.length == 2) {
    		fileName = args[0]; 
    		manPower = args[1];
    		new ProjectPlanner();
    	}
    	else {
    		System.out.println("Mangler filnavn og/eller manpower. ");
    	}
    }
	
    ProjectPlanner() {
    	readFile();
    	testForCycle();
    	if (!cycleFound) {
    		makeEventNodeGraph();
    		computeEarliestStart();
    		computeLatestStart();
    		printTasks();
    		startProject();
    	}
    }
	
	
    
    /**
    * Metoden leser inn oppgave fra filen filName, og oppretter
    * Task objekter og tilhorende kanter i henhold til innlest 
    * informasjon. Resultatet er en event-node graf uten ekstra
    * dummy noder/kanter hvor oppgaver er avhengig av flere andre
    * oppgaver. 
    */
    public void readFile() {
    	int totalTasks;
    	int taskId; 
    	String name; 
    	int timeEst; 
    	int manReq; 
    	int depEdges;//array
    	
    	try {
    		Scanner scan = new Scanner(new File(fileName));
    		totalTasks = scan.nextInt();//totalt ant oppgaver
    		task = new Task[totalTasks];			
			
    		int taskIndex = 0;
    		while(scan.hasNext()) {
				
    			taskId = scan.nextInt();
    			name = scan.next();
    			timeEst = scan.nextInt();
    			manReq = scan.nextInt();
				
    			//tester om oppgaven allerede er opprettet
    			if (task[taskIndex] == null) {
    				task[taskIndex] = new Task(taskId, name, timeEst, manReq); 
    			} else {
    				//oppgaven er allerede opprettet
    				task[taskIndex].updateInfo(taskId, name, timeEst, manReq);
    			}
    			
    			//legger til kantene dette Task objektet er avhengig av:
    			int count = 0;
    			while((depEdges = scan.nextInt()) != 0) {
    				//sjekker om objektet task[depEdges-1] 
    				//eksisterer fra for:
    				if(task[depEdges-1] == null) {
    					task[depEdges-1] = new Task();
    				}
					
    				Edge edge = new Edge(task[taskIndex]);
    				//legger edge til i task[depEdges] sine kanter:
    				task[depEdges-1].addEdge(edge);
    				
    				//legger til en tilbakepeker i Task[taskIndex]
    				//til edge objektet som peker paa dette objektet
    				task[taskIndex].addInEdge(edge);
    				//legger til en tilbakepeker i objektet edge
    				edge.addBackPointer(task[depEdges-1]);
    				count++;
    			}
    			//all info for denne oppgaven er naa lest inn
    			task[taskIndex].dependencies(count);
    			taskIndex++;
    		}
    		//filen er ferdig lest inn
    	}
    	catch (IOException e) {
    		System.out.println("IOException : ");
    		System.out.println(e.getMessage()+".");
    	}
    }
	
   
    
    /**
	* Gaar igjennom alle objekter i task arrayen, og starter 
	* metoden cycelSearch for hver metode som ikke er testet
	* tidligere
	*
	*/
    public void testForCycle()  {
    	for (Task t: task) {
    		if (t.state != "tested" && !cycleFound) {
    			cycleSearch(t);
    		}
    	}
    	System.out.println("");
    }
    
    
    
    /**
	* Metoden gaar igjennom alle etterfolgere til t rekursivt,
	* og ser om noen av disse danner en lokke, om det finnes
	* en lokke settes cycleFound = true og rekursonen stopper.
	* Lokken som er funnet skrives til slutt ut.
	* @param t Task objektet som undersokes
	*/
    public void cycleSearch(Task t) {
    	if (t.state == "testing") {
    		cycleFound = true; 
    		System.out.println("Prosjektet kan ikke gjennomfores. ");
    		System.out.print("Funnet lokke: ");
    		System.out.print(t.getId());
    	}
    	else if (t.state == "untested" && !cycleFound) {
    		t.state = "testing";
    		Edge edge = t.getEdges();
    		
    		//gaar igjennom alle Task objekter som er avhengig
    		//av oppgave t
    		while (edge != null && !cycleFound) {
    			cycleSearch(edge.getTask());
    			edge = edge.getNext();
    		}
    		t.state = "tested";
    		//skriver ut lokken som evt er funnet:
    		if (cycleFound) {
    			System.out.print(" <-- " + t.getId());
    		}
    	}
    }
    
    
    /**
    * Gjor om grafen fra activity node graf til 
    * event node graf. 
    *
    */
    public void makeEventNodeGraph() {
    	start = new Task();//peker til forste Task i grafen
    	for (Task t :task) {
    		if (t.cntPredecessors == 0)  {
    			start.addEdge(new Edge(t));
    		}
    		else if (t.cntPredecessors > 1 ) {
    			Task dummyTask = new Task(t.getId());
    			Edge dummyEdge = new Edge(t);
    			dummyTask.addEdge(dummyEdge);
    			Edge inEdge = t.getInEdges();
    			//endrer alle inEdge (kanter som peker paa t)
    			//til aa peke paa dummyTask isteden
    			//og endrer inedgenes timevariabel til 0:
    			while (inEdge != null) {
    				//inEdge.task = dummyTask; rettet
    				inEdge.addTask(dummyTask);
    				//inEdge.changeTime(0);
    				inEdge = inEdge.getNextInEdge();
    			}	
    			dummyTask.changeInEdges(t.getInEdges());
    			t.changeInEdges(dummyEdge);
    			dummyEdge.addBackPointer(dummyTask);
    			dummyTask.cntPredecessors = t.cntPredecessors;
    			t.cntPredecessors = 1; 
    			
    		}
    	}
    }
    
    
    /**
    * I topologisk rekkefolge regnes det ut earliestStart verdi
    * for alle Task objekter.
    *
    */
    public void computeEarliestStart() {
    	List list = new List();
    	Task previous;//Task objekt med inngrad 0, og med naboene current
    	Task current;//nabo til previous
    	Task lastTask = null;//siste Task objekt i grafen. 
    	Edge edge = start.getEdges();
    	
    	//legger inn alle Task objekter som start peker paa
    	//inn i en liste: 
    	while (edge != null) {
    		current = edge.getTask();
    		current.updateStart(0);
    		list.push(current);
    		edge = edge.getNext(); 
    	}
    	
    	int startTime;//earlistStart tid til current
    	previous = list.pop();
    	while (previous != null) {
    		edge = previous.getEdges();
    		//gaar igjennom alle naboer til current: 
    		while (edge != null) {
    			current = edge.getTask();
    			startTime = previous.getTime() + previous.getStartTime();
    			current.updateStart(startTime);
    			if (--current.cntPredecessors == 0) {
    				list.push(current);
    			}
    			edge = edge.getNext();
    		}
    		lastTask = previous; 
    		previous = list.pop();//tar ut en ny 'task' fra list
    		
    	}
    	completionTime = lastTask.getTime() + lastTask.getStartTime();
    }
    
    
    
    
    /**
    * Gaar igjennom grafen topologisk, men baklengs for 
    * aa finne latestTime en task kan begynnes paa
    * uten aa forsinke prosjektet. 
    *
    */
    public void computeLatestStart() {
    	List list = new List();
    	Task prev;
    	Task current;
    	Edge edge;
    	
    	//finner alle task obj som ikke har
    	//naboer:
    	for (Task t: task) {
    		edge = t.getEdges();
    		if (edge == null) {
    			t.updateLatestStart(completionTime-t.getTime());
    			t.updateSlack();//regner ut slack i t
    			list.push(t);
    		} 
    	}
    	int startTime;
    	prev = list.pop();
    	while (prev != null) {
    		edge = prev.getInEdges();
    		while (edge != null) {
    			current = edge.getPrecedingTask();
    			startTime = prev.getLatestStart() - current.getTime();
    			current.updateLatestStart(startTime);
    			current.updateSlack();
    			if (--current.cntOutEdges == 0) {
    				list.push(current);
    			}
    			edge = edge.getNextInEdge();
    		}
    		prev = list.pop();
    	}
    }
    
    /**
    * Skriver ut informasjon om alle Task objektene.
    */
    public void printTasks() {
    	Task depTask;
    	Edge e;
    	for (Task t : task) {
    		System.out.println("---------------------------------");
    		System.out.println("Task: Identity number: "+ t.getId());
    		System.out.println("---------------------------------");
    		System.out.println("Name: "+ t.getName());
    		System.out.println("Time to finish this task: "+t.getTime());
    		System.out.println("Manpower required: "+t.getStaff());
    		System.out.println("Slack: "+ t.getSlack());
    		System.out.println("Latest start time: "+ t.getLatestStart());
    		e = t.getEdges();
    		System.out.println("Tasks depending on this task: ");
    		while (e != null) {
    			depTask = e.getTask();
    			System.out.println("Task: "+depTask.getId());
    			e = e.getNext();
    		}
    		System.out.println("");
    	}
    	System.out.println("---------------------------------");
    	System.out.println("");
    }
    
    
    
    /**
    * Metoden gaar igjennom grafen topologisk, hvor alle paabegynte
    * task legges i listen list, sortert etter naar de er ferdige.
    * Tasks som begynner, legges inn i startingTasks listen, og 
    * ferdige task legges inn i finishedTask.
    * Disse skrives deretter ut i printTasks.
    *
    */
    public void startProject() {
    	List list = new List();
    	List startingTasks = new List();
    	List finishedTasks = new List();
    	int time = 0;
    	int prevTime;
    	Task prev = start;//avsluttet task
    	Task current;//naboene til prev
    	Edge edge; 
    	int manpower = 0;
    	
    	while (prev != null) {
    		edge = prev.getEdges();
    		prevTime = time;
    		time = prev.getTime()+prev.getStartTime();
    		
    		if (prevTime != time ) {
    			printLists(prevTime, startingTasks, finishedTasks, manpower);
    		}
    		manpower -= prev.getStaff();
    		if (prev != start) {
    			finishedTasks.push(prev);
    		}
    		//gaar igjennom naboene til prev og ser om
    		//noen er klare til aa starte:
    		while (edge != null) {
    			current = edge.getTask();
    			if (time == current.getStartTime()) {
    				//hopper over eventuelle dummy noder: 
    				if (current.getName() == "dummy") {
    					Edge e = current.getEdges();
    					current = e.getTask();
    				}
    				manpower += current.getStaff();
    				list.pushSorted(current);
    				startingTasks.push(current);
    			}
    			edge = edge.getNext();
    		}
    		prev = list.pop();
    		
    	}
    	printLists(time, startingTasks, finishedTasks, manpower);
    	System.out.print("**** Shortest possible project execution is ");
    	System.out.println(completionTime+" ****");
    	System.out.println("");
    }
    
      
    /**
    * Ferdige og startede task objekter ved tiden time skrives 
    * ut.
    * @param time 
    * @param s liste av task objektene som er startet 
    * @param f liste av task objekter som er ferdige
    * @param mpow antall manpower i arbeid ved tid time
    */
    public  void printLists(int time, List s, List f, int mpow ) {
    	Task starting = s.pop();
    	Task finished = f.pop();
    	
    	if (starting != null || finished != null) {
    		System.out.println("Time: "+time);
    		//skriver ut task som er ferdige:
    		while (finished != null) {
    			System.out.println("        Finished: " + finished.getId());
    			finished = f.pop();
    		}
    		//skriver ut tasks som begynner:
    		while (starting != null) {
    			System.out.println("        Starting: " + starting.getId());
    			starting = s.pop();
    		}
    		System.out.println("   Current staff: "+ mpow);
    		System.out.println("");
    	} 
    }
}




