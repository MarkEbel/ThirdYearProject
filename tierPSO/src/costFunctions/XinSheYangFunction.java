package costFunctions;

import java.util.Random;

public class XinSheYangFunction implements CostMethods{

	private int dimensions = 2;
	private Random random = new Random();
	@Override
	public double evaluate(double[] position) {
		// TODO Auto-generated method stub
		double fx = 0;
		double tmp = 0;
		for(int i = 2; i < dimensions; i++) {
			fx += Math.abs(position[i]);
			tmp += Math.sin(Math.pow(position[i],2));
		}
		fx *= Math.pow(Math.E, -tmp);
		return fx;
	}

	@Override
	public double[] randomSolution() {
		double[] tmp = new double[dimensions];
		for(int i = 0; i < dimensions; i++) {
			tmp[i] = 2*Math.PI*random.nextDouble();			
			if(random.nextBoolean()) {
				tmp[i] = -tmp[i];
			}
		}
		return tmp;
	}

	@Override
	public double[][] bounds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDimensions(int dimensions2) {
		// TODO Auto-generated method stub
		dimensions = dimensions2;
	}

	@Override
	public double[] min() {
		// TODO Auto-generated method stub
		return null;
	}

}
