package ameer.WorkStealing.sim;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedDeque;

import ameer.WorkStealing.Deferred;
import ameer.WorkStealing.Task;
import ameer.WorkStealing.conf.ManufactoringPlan;
import ameer.WorkStealing.sim.tools.GcdScrewDriver;
import ameer.WorkStealing.sim.tools.Tool;

/**
 * A class representing the warehouse in your simulation
 * 
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!!!
 *
 */
public class Warehouse {
	ConcurrentLinkedDeque<Tool> gSDriver;
	ConcurrentLinkedDeque<Tool> nPHammer;
	ConcurrentLinkedDeque<Tool> rSPliers;
	ConcurrentLinkedDeque<Deferred<Tool>> gSDriverWaitingList;
	ConcurrentLinkedDeque<Deferred<Tool>> nPHammerWaitingList;
	ConcurrentLinkedDeque<Deferred<Tool>> rSPliersWaitingList;
	public HashMap<String, ManufactoringPlan> plans;

	/**
	 * Constructor
	 */
	public Warehouse() {
		gSDriver = new ConcurrentLinkedDeque<>();
		nPHammer = new ConcurrentLinkedDeque<>();
		rSPliers = new ConcurrentLinkedDeque<>();
		gSDriverWaitingList = new ConcurrentLinkedDeque<>();
		nPHammerWaitingList = new ConcurrentLinkedDeque<>();
		rSPliersWaitingList = new ConcurrentLinkedDeque<>();
		plans = new HashMap<>();

	}

	/**
	 * Tool acquisition procedure Note that this procedure is non-blocking and
	 * should return immediatly
	 * 
	 * @param type
	 *            - string describing the required tool
	 * @return a deferred promise for the requested tool
	 */
	public synchronized  Deferred<Tool> acquireTool(String type) {
		Deferred<Tool> ans = new Deferred();
		Tool tool;
		if (type.equals("gs-driver")) {
			tool = gSDriver.pollLast();
			if (tool == null)
				gSDriverWaitingList.addFirst(ans);
		} else if (type.equals("np-hammer")) {
			tool = nPHammer.pollLast();
			if (tool == null)
				nPHammerWaitingList.addFirst(ans);
		} else {
			tool = rSPliers.pollLast();
			if (tool == null)
				rSPliersWaitingList.addFirst(ans);
		}
		if (tool != null)
			ans.resolve(tool);
		return ans;
	}

	/**
	 * Tool return procedure - releases a tool which becomes available in the
	 * warehouse upon completion.
	 * 
	 * @param tool
	 *            - The tool to be returned
	 */
	public synchronized void releaseTool(Tool tool) {
		if (tool.getType().equals("gs-driver")) {
			if (gSDriverWaitingList.isEmpty())
				gSDriver.addFirst(tool);
			else
				gSDriverWaitingList.removeLast().resolve(tool);
		} else if (tool.getType().equals("np-hammer")) {
			if (nPHammerWaitingList.isEmpty())
				nPHammer.addFirst(tool);
			else
				nPHammerWaitingList.removeLast().resolve(tool);
		} else {
			if (rSPliersWaitingList.isEmpty())
				rSPliers.addFirst(tool);
			else
				rSPliersWaitingList.removeLast().resolve(tool);
		}
	}

	/**
	 * Getter for ManufactoringPlans
	 * 
	 * @param product
	 *            - a string with the product name for which a ManufactoringPlan
	 *            is desired
	 * @return A ManufactoringPlan for product
	 */
	public ManufactoringPlan getPlan(String product) {
		return plans.get(product);
	}

	/**
	 * Store a ManufactoringPlan in the warehouse for later retrieval
	 * 
	 * @param plan
	 *            - a ManufactoringPlan to be stored
	 */
	public void addPlan(ManufactoringPlan plan) {
		plans.put(plan.getProductName(), plan);
	}

	/**
	 * Store a qty Amount of tools of type tool in the warehouse for later
	 * retrieval
	 * 
	 * @param tool
	 *            - type of tool to be stored
	 * @param qty
	 *            - amount of tools of type tool to be stored
	 */
	public void addTool(Tool tool, int qty) {
		if (tool.getType().equals("gs-driver")) {
			for (int i = 0; i < qty; i++)
				gSDriver.addFirst(tool);
		} else if (tool.getType().equals("np-hammer")) {
			for (int i = 0; i < qty; i++)
				nPHammer.addFirst(tool);
		} else {
			for (int i = 0; i < qty; i++)
				rSPliers.addFirst(tool);
		}
	}

}
