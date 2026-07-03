import java.io.*;
import java.util.*;
/* This program simulates FCFS and Non-Preemptive SJF CPU scheduling algorithms.
 * It reads process data from a text file and outputs Gantt charts and performance metrics.
 */

class Process {
    int pid, arrivalTime, burstTime, priority;
    int waitingTime, turnaroundTime, startTime, completionTime;

    public Process(int pid, int arrivalTime, int burstTime, int priority) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
    }
}

public class SchedulerSimulation {

    public static void main(String[] args) {
        List<Process> processes = readProcesses("processes.txt");
        if (processes.isEmpty()) return;
        
        System.out.println("--- FCFS Scheduling ---");
        simulateFCFS(new ArrayList<>(processes));
        
        System.out.println("\n--- SJF (Non-Preemptive) Scheduling ---");
        simulateSJF(new ArrayList<>(processes));
    }

    public static List<Process> readProcesses(String fileName) {
        List<Process> list = new ArrayList<>();
        try (Scanner sc = new Scanner(new File(fileName))) {
            while (sc.hasNextInt()) {
                list.add(new Process(sc.nextInt(), sc.nextInt(), sc.nextInt(), sc.nextInt()));
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        }
        return list;
    }
/*  Sort by arrival time first to ensure the first process in is the first served
proc.sort(Comparator.comparingInt(p -> p.arrivalTime))*/
    public static void simulateFCFS(List<Process> proc) {
        proc.sort(Comparator.comparingInt(p -> p.arrivalTime));
        int currentTime = 0;

        for (Process p : proc) {

          /*  Check if the CPU needs to stay idle because the next process hasn't arrived yet  */
            if (currentTime < p.arrivalTime) currentTime = p.arrivalTime;
            p.startTime = currentTime;
            currentTime += p.burstTime;
            p.completionTime = currentTime;
            /* Calculate TAT and WT */
            p.turnaroundTime = p.completionTime - p.arrivalTime;
            p.waitingTime = p.turnaroundTime - p.burstTime;
        }
        printResults(proc);
    }

    public static void simulateSJF(List<Process> proc) {
        int n = proc.size();
        List<Process> result = new ArrayList<>();
        boolean[] visited = new boolean[n];
        int currentTime = 0, completed = 0;
/*  Loop until every process from the file has been executed */
        while (completed < n) {
            int idx = -1;
            int minBurst = Integer.MAX_VALUE;
/*  Search for the shortest job among processes that have already arrived */
            for (int i = 0; i < n; i++) {
                if (!visited[i] && proc.get(i).arrivalTime <= currentTime) {
                    if (proc.get(i).burstTime < minBurst) {
                        minBurst = proc.get(i).burstTime;
                        idx = i;
                    }
                }
            }

            if (idx == -1) {
                currentTime++;
            } else {
                Process p = proc.get(idx);
                p.startTime = currentTime;
                currentTime += p.burstTime;
                p.completionTime = currentTime;
                p.turnaroundTime = p.completionTime - p.arrivalTime;
                p.waitingTime = p.turnaroundTime - p.burstTime;
                visited[idx] = true;
                result.add(p);
                completed++;
            }
        }
        printResults(result);
    }

  public static void printResults(List<Process> proc) {
        System.out.println("GANTT Chart:");
      
        System.out.println();
        for (Process p : proc) System.out.print("|  P" + p.pid + "  ");
        System.out.println("|");
        
        System.out.println();
        for (Process p : proc) {
            System.out.print(p.startTime + "      ");
        }
        
        int totalFinishTime = proc.get(proc.size() - 1).completionTime;
        System.out.println(totalFinishTime);

        System.out.println("\nMetrics:");
        System.out.println("PID\tWait\tTurnaround");
        double totalWT = 0, totalTAT = 0;
        double totalBurst = 0;

        for (Process p : proc) {
            System.out.println(p.pid + "\t" + p.waitingTime + "\t" + p.turnaroundTime);
            totalWT += p.waitingTime;
            totalTAT += p.turnaroundTime;
            totalBurst += p.burstTime;
        }

        double utilization = (totalBurst / totalFinishTime) * 100;

        System.out.printf("Average WT: %.2f\n", totalWT / proc.size());
        System.out.printf("Average TAT: %.2f\n", totalTAT / proc.size());
        System.out.printf("CPU Utilization: %.2f%%\n", utilization);
    } 
} 