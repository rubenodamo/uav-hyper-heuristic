package com.aim.project.uzf.runners;


import AbstractClasses.HyperHeuristic;
//import com.aim.project.uzf.hyperheuristics.APCF_NW_HH;
import com.aim.project.uzf.hyperheuristics.APCF_NW_HH;
import com.aim.project.uzf.hyperheuristics.SR_IE_HH;

/**
 * @author Warren G Jackson
 * @since 1.0.0 (22/03/2024)
 *
 * Runs a simple random IE hyper-heuristic then displays the best solution found
 */
public class SR_IE_VisualRunner extends HH_Runner_Visual {

	public SR_IE_VisualRunner(int instanceId) {
		super(instanceId);
	}
	@Override
	protected HyperHeuristic getHyperHeuristic(long seed) {

		return new APCF_NW_HH(seed, 0.5, 0.5);
		//return new SR_IE_HH(seed);
	}

	public static void main(String [] args) {

		/* { 0: square.uzf, 1: libraries-15.uzf, 2: carparks-40.uzf, 3: tramstops-85.uzf, 4: grid.uzf,
		 		5: clustered-enclosures.uzf, 6: chatgpt-instance-100-enclosures.uzf } */
		HH_Runner_Visual runner = new SR_IE_VisualRunner(4);
		runner.run();
	}

}
