
import utils.BayesianNetwork;
import utils.Parser;


public class Proyecto1Task2 {
	public static void main(String[] args) {
        Parser p = new Parser();
        if(!p.parse("test.txt")) {
        	System.out.println("Revise la red bayesiana de ingreso.");
        }else {
        	System.out.println("Su red bayesiana es válida.");
        	
        	BayesianNetwork network = new BayesianNetwork(p.getProbabilities());
            boolean estaCompleta = network.isComplete();
            if(estaCompleta) {
            	System.out.println("La red bayesiana está completa");
            	System.out.println("La forma compacta es: " + network.createCompact());
            	
            	System.out.println("Los factores son: " + network.showFactors());
            	
            }else {
            	System.out.println("La red bayesiana NO está completa");
            }
        }
	}
}
