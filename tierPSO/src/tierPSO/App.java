package tierPSO;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class App {
	private final static int NUM_OF_PARTICLES = 500;
	public final static int DIMENSIONS = 3;
	private final static int ITERATIONS = 10;

	public final static double DISTANCE_BETWEEN_PARTICLES_FOR_TIER= 0.2;
	public final static int MINIMUN_PARTICLES_THAT_ARE_CLOSE = 3;
	public final static double[] TIER_INERTIA = new double[] {0.6,0.6,0.6};
	public final static double GlOBAL_INERTIA = 0.1;
	public static final double MINIMUMSPACING = 0.25;
	
	
	
	
	public static void main(String[] args) {
		pso(new double[] {0.6,0.6,0.6},TIER_INERTIA, 0.1, 0.1, new double[][] {{0,1},{0,1},{0,1}});
		//System.out.println();
		//randomSearch();
	}
	private static void outputSolution(double[] gbest, double cost) {

		System.out.println("Solution: \n");
		for(double section: gbest) {
			System.out.print(section + "  ");
		}
		System.out.print("\nCost " + cost + "  ");
	}
	
	
	private static void randomSearch() {
		
		AntennaArray aa = new AntennaArray(3, 90);
		double[] gbest = randomSolution(aa);
	    double gbestCost = aa.evaluate(gbest);
		for(int i = 0; i < 50; i++) {
			double[] a = randomSolution(aa);
			if(aa.evaluate(a) < gbestCost) {
				gbest = a;
				gbestCost = aa.evaluate(a);
			}
		}
		System.out.println("\nRandom Search");
		outputSolution(gbest, gbestCost);
	}
	
	private static void pso(double[] inertia, double[] tierInertia, double cognitiveCo, double socialCo, double[][] bounds) {
		System.out.println("\nPSO solution");
		Tier tierZero = new Tier(0, null);
		AntennaArray aa = new AntennaArray(3, 90);
		tierZero.addParticle(new Particle(aa, randomVelocity(), randomSolution(aa), inertia,tierInertia, cognitiveCo, socialCo, bounds));
	    for(int i = 0; i < NUM_OF_PARTICLES - 1; i++){
	        tierZero.addParticle(new Particle(randomSolution(aa), randomVelocity()));
	    }

    	
    	
	    for(int i = 0; i < ITERATIONS; i++){
	    	double[] gbestNotInTier = new double[DIMENSIONS];
	    	double gbestCostNotInTier = 0;

	        for(Particle particle: tierZero.getParticles()){
	        	
                if(gbestCostNotInTier == 0){
                	gbestNotInTier = particle.getBestPosition();
                	gbestCostNotInTier = Particle.getCost(gbestNotInTier);
                } else {
                    if(gbestCostNotInTier < Particle.getCost(particle.getBestPosition())){
                    	gbestNotInTier = particle.getBestPosition();
                    	gbestCostNotInTier = Particle.getCost(gbestNotInTier);
                    }
                }
	            
	        }
	        if(gbestCostNotInTier == 0) {
	        	gbestNotInTier = randomSolution(aa);
	        }
	        tierZero.updateParticles(gbestNotInTier);

	        tierZero.updateTier();
	        System.out.println(tierZero.getParticles().size() + " This");
	    }
	    Particle gbest = tierZero.bestPosition();

    	
    	
	    outputSolution(gbest.getBestPosition(), gbest.getBestCost());
	}
		
	
	
//	private static double[] randomLocation(){
//		Random random = new Random();
//		double [] location = {};
//		while(inTier(location) > 0) {
//			location = random;
//		}
//	    return location;
//	    // location is not in a tier.
//	}
	
	
	private static double[] standardDeviation(Tier t) {
		ArrayList<double[]> values = new ArrayList<double[]>();
		ArrayList<Particle> particles = t.getParticles();
				//t.getParticles();
		for(Tier subTiers: t.getSubTiers()) {
			particles.addAll(subTiers.getParticles());
		}
		for(Particle p: particles) {
			values.add(p.getCurrentPosition());
		}
		double[] mean =  meanValue(values);
		double[] diffSquarSum = new double[values.get(0).length];
		for(double[] value: values) {
			diffSquarSum = add(diffSquarSum, squared(minus(value, mean)));
		}
		diffSquarSum = divide(diffSquarSum, (values.size() - 1));
		diffSquarSum = sqrtVector(diffSquarSum);
		return diffSquarSum;
	}
	
	private static void writeToCSV() throws IOException {
		FileWriter csvWriter = new FileWriter("new.xcsv");
		for (List<String> rowData : rows) {
		    csvWriter.append(String.join(",", rowData));
		    csvWriter.append("\n");
		}

		csvWriter.flush();
		csvWriter.close();
	}
	
	private static double[] meanValue(ArrayList<double[]> values) {
		double[] total = new double[values.get(0).length];
		for(double[] v: values) {
			total = add(v, total);
		}
		total = divide(total, values.size());
		return total;
	}
	
	private static double[] divide(double[] input1, double input2){
	    double[] a1 = input1.clone();
	    double[] output = new double[a1.length];
	    for(int i =0; i< a1.length; i++){
	        output[i] = a1[i]/input2; 
	    }
	    return output;
	}
	
	private static double[] add(double[] input1, double[] input2){
		double[] a1 = input1.clone();
		double[] a2 = input2.clone();
		double[] output = new double[a1.length];
		for(int i =0; i< a1.length; i++){
			output[i] = a1[i] + a2[i]; 
		}
		return output;
	}
	public static double[] randomSolution(AntennaArray antArr) {
		double[][] allBounds = antArr.bounds();
		double[] design = new double[allBounds.length];
		Random random = new Random();
		while(!(antArr.is_valid(design) && minSpacing(design, MINIMUMSPACING))  || (antArr.evaluate(design) < 0)) {
			int i = 0;
			design = new double[allBounds.length];
			for(double[] bounds : allBounds) {
				double lb = bounds[0];
				double ub = bounds[1];
				
				if(i == 0) {	
					design[i] = design.length/2.0;
					
				} else {
					double maxLength  = (design.length - sum(design))/2 - (design.length - i)*MINIMUMSPACING;
					if(MINIMUMSPACING >= lb) {
						if(ub <= maxLength) {
							design[i]  = (MINIMUMSPACING + sum(design)) + (ub - MINIMUMSPACING - sum(design))*(random.nextDouble());						
						} else {
							design[i]  = MINIMUMSPACING + sum(design) + (maxLength -  sum(design) - MINIMUMSPACING)*(random.nextDouble());	
						}
					} else {
						if(ub <= maxLength) {
							design[i]  = lb + sum(design) + (ub - lb -  sum(design) )*(random.nextDouble());						
						} else {
							design[i]  = lb +  sum(design) + (maxLength - lb -  sum(design) )*(random.nextDouble());	
						}						
					}
					
				}
				i++;
			}			
		}
		Arrays.sort(design);
		return design;
	}
	
	private static double sum(double[] values) {
		double total = 0;
		for(double value: values) {
			total += value;
		}
		return total;
	}
	private static boolean minSpacing(double[] design, double minSpace) {
		for(int i = 0; i < design.length; i++) {
			for(int j = 0; j < design.length; j++) {
				if(i == j) {
					break;
				}
				if(minSpace > Math.abs(design[i] - design[j])) {
					return false;
				}
			}
		}
		return true;
	}
	private static double[] randomVelocity() {
		Random random = new Random();
		double[] v = new double[DIMENSIONS];
		for(int i =0; i < DIMENSIONS; i ++) {
			v[i] = random.nextDouble();
		}
		return v;
	}

}	