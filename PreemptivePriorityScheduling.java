package schedule;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

class Process2 {
	int pid;
	int arrivalTime;
	int cpuBurst;
	int priority;
	int quantum;
	boolean roundRobinDone;
	boolean roundRobinSwitch;

	public Process2(int pid, int arrivalTime, int cpuBurst, int priority, int quantum) {
		this.pid = pid;
		this.arrivalTime = arrivalTime;
		this.cpuBurst = cpuBurst;
		this.priority = priority;
		this.quantum = quantum;
		this.roundRobinDone = false;
		this.roundRobinSwitch =false;
	}
}

public class PreemptivePriorityScheduling {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		List<Process2> processes = new ArrayList<>();
		System.out.println("Enter processes properties. ");
		int id=0, priority=0, aTime=0, bTime=0;
		int processCount=0;
		System.out.println("enter number of quantum");
		int quantum = sc.nextInt();
		try {
			File myObj = new File("C:\\Users\\nikit\\eclipse-workspace\\OS\\src\\schedule\\in1.txt");
			Scanner myReader = new Scanner(myObj);
			processCount = Integer.parseInt(myReader.nextLine());
			for(int i=0;i<processCount;i++) {
				if(myReader.hasNextLine()) {
					String data = myReader.nextLine();
					String splitData[] = data.split(" ");
					id = i+1;
					aTime= Integer.parseInt(splitData[0]);
					priority= Integer.parseInt(splitData[1]);
					bTime= Integer.parseInt(splitData[2]);
					System.out.println(data);
					processes.add(new Process2(id, aTime, bTime,priority,quantum));
				}
			}

			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

		sc.close();

		// sort processes by arrival time
		processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
		Map<Process2,Integer> processTurnAround= new HashMap();
		int currentTime = 0;
		int completed = 0;
		List<Integer> ganttChart = new ArrayList<>();
		PriorityQueue<Process2> queue = new PriorityQueue<>(Comparator.comparingInt(p -> p.priority));
		boolean roundRobin = false;
		while (completed < processCount) {
			// add new processes to the queue
			while (!processes.isEmpty() && processes.get(0).arrivalTime <= currentTime) {
				processTurnAround.put(processes.get(0), currentTime);
				queue.add(processes.remove(0));

			}

			if (queue.isEmpty()) {
				// no processes in the queue, CPU idle
				ganttChart.add(-1);
				currentTime++;
				continue;
			}


			// select process with highest priority
			//Process2 prevProcess = null;

			// if(!roundRobin) {
			Process2 currentProcess  = queue.poll();
			/*}else {
            	currentProcess=prevProcess;
            }*/
			if(PreemptivePriorityScheduling.checkForProcessesWithSamePriority(currentProcess,queue)) {
				List<Process2> processesWithSamePriority = PreemptivePriorityScheduling.processesWithSamePriority(currentProcess,queue);
				processesWithSamePriority.sort(Comparator.comparingInt(p -> p.arrivalTime));
				for(int i=0;i<processesWithSamePriority.size();i++) {
					Process2 tempProcess=processesWithSamePriority.get(i);
					if(tempProcess.quantum>0 && !tempProcess.roundRobinDone && !tempProcess.roundRobinSwitch) {

						if(!queue.contains(currentProcess)) {
							queue.add(currentProcess);
						}
						currentProcess=tempProcess;
						if(queue.contains(currentProcess)) {
							queue.remove(currentProcess);
						}

						break;
					}

				}
				if(ganttChart!= null &&ganttChart.size()>=1 && ((ganttChart.get(ganttChart.size()-1) != currentProcess.pid))) {
					System.out.println("Process " + ganttChart.get(ganttChart.size()-1)+ " is preampted  at time " +currentTime);
				}
				ganttChart.add(currentProcess.pid);
				currentProcess.cpuBurst--;
				currentProcess.quantum--;
				if (currentProcess.cpuBurst == 0) {
					// process completed
					int turnAroundTime =(currentTime+1)- processTurnAround.get(currentProcess);
					processTurnAround.put(currentProcess,turnAroundTime);
					System.out.println("*************Process " + currentProcess.pid + "is completed...with turnAroundTime =" + turnAroundTime );
					completed++;
					currentProcess.roundRobinDone = true;
					currentProcess = null;

				} else if(currentProcess.quantum==0) {
					currentProcess.roundRobinDone = false;
					currentProcess.roundRobinSwitch = true;
					currentProcess.quantum=quantum;

					queue.add(currentProcess);
				}else {
					// process preempted by a higher priority process
					currentProcess.roundRobinSwitch = false;
					currentProcess.roundRobinDone = false;
					queue.add(currentProcess);
				}
				int count =0;
				for(int i=0;i<processesWithSamePriority.size();i++) {
					Process2 tempProcess=processesWithSamePriority.get(i);
					if(tempProcess.roundRobinSwitch) {
						count++;
					}
				}
				if(count == processesWithSamePriority.size()) {
					for(int i=0;i<processesWithSamePriority.size();i++) {
						Process2 tempProcess=processesWithSamePriority.get(i);
						tempProcess.roundRobinSwitch=false;
					}
				}
				currentTime++;
			}else {
				if(ganttChart!= null &&ganttChart.size()>=1 && ((ganttChart.get(ganttChart.size()-1) != currentProcess.pid))) {
					System.out.println("Process " + ganttChart.get(ganttChart.size()-1)+ " is preampted  at time " +currentTime);
				}
				ganttChart.add(currentProcess.pid);
				currentProcess.cpuBurst--;

				if (currentProcess.cpuBurst == 0) {
					// process completed
					int turnAroundTime =(currentTime+1)- processTurnAround.get(currentProcess);
					processTurnAround.put(currentProcess,turnAroundTime);
					System.out.println("******************Process " + currentProcess.pid + "is completed...with turnAroundTime =" + turnAroundTime );
					completed++;
					currentProcess = null;
				} else {
					// process preempted by a higher priority process

					// System.out.println("Process " + currentProcess.pid+ " is preampted by higher priorty at time " +currentTime);

					queue.add(currentProcess);
				}
				currentTime++;
			}

			// prevProcess= currentProcess;
		}
		float totalTurnAroundTime=0;
		for(Integer val: processTurnAround.values()) {
			totalTurnAroundTime+=val;
		}
		System.out.println("Average Turn Around Time = " + totalTurnAroundTime/processCount);
		System.out.println("##################################################################");
		System.out.println("Gantt Chart: " + ganttChart);
		System.out.println("##################################################################");
	}

	/**
	 * @param currentProcess
	 * @param queue
	 * @return
	 */
	public static List<Process2> processesWithSamePriority(Process2 currentProcess, PriorityQueue<Process2> queue) {
		Iterator<Process2> itr = queue.iterator();
		List<Process2> processesWithSamePriority = new ArrayList<>();

		while(itr.hasNext()){
			Process2 processInQueue = itr.next();
			// do some processing with str
			if(processInQueue.priority == currentProcess.priority) {
				processesWithSamePriority.add(processInQueue);
			}
		}
		if(!(processesWithSamePriority.size()==0) && !processesWithSamePriority.contains(currentProcess)) {
			processesWithSamePriority.add(currentProcess);
		}
		return processesWithSamePriority;
	}

	/**
	 * @param currentProcess
	 * @param queue
	 * @return
	 */
	public static boolean checkForProcessesWithSamePriority(Process2 currentProcess, PriorityQueue<Process2> queue) {
		Iterator<Process2> itr = queue.iterator();
		List<Process2> processesWithSamePriority = new ArrayList<>();

		while(itr.hasNext()){
			Process2 processInQueue = itr.next();
			// do some processing with str
			if(processInQueue.priority == currentProcess.priority) {
				return true;

			}
		}
		return false;

	}
}
