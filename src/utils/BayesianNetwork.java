package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import models.Probability;
import models.Variable;




public class BayesianNetwork {
	private ArrayList<Probability> probabilities = new ArrayList<Probability>(); // Probabiidades
	private ArrayList<Probability> calculatedProbabilities = new ArrayList<Probability>();
	
	private ArrayList<Variable> network = new ArrayList<Variable>();
	private ArrayList<Variable> conditionalVariables = new ArrayList<Variable>();
	private ArrayList<Probability> factors = new ArrayList<Probability>();
	private ArrayList<String> networkVariables = new ArrayList<String>();
	private Map<String, ArrayList<Probability>> networkMap = new HashMap<String, ArrayList<Probability>>();
//	private HashMap<String, Variable> networkMap2 = new HashMap<String, Variable>();

	public BayesianNetwork(ArrayList<Probability> probabilities) {
		this.probabilities = probabilities;
		
        for(Probability p: probabilities) {
        	String mainVariable = p.getMainVariable();
            Variable variable = new Variable(mainVariable.replace("!",""));
            
            int variableIndex = indexOfVariable(variable);
            if(variableIndex >= 0) variable = network.get(variableIndex);
            
            for(String conditionalVariable : p.getConditionVariables()) {
                conditionalVariable = conditionalVariable.replace("!", "");
                // Se crea una nueva variable
                Variable var = new Variable(conditionalVariable);
                int conditionalVariableIndex = indexOfVariable(var);
                
                if(conditionalVariableIndex >= 0) var = network.get(conditionalVariableIndex);
                else network.add(var);
                variable.addParent(var);
            }
            
            if(variableIndex == -1) {
                network.add(variable);
                networkVariables.add(variable.getValue());
            }
            
        }
        this.calculatedProbabilities.addAll(probabilities);
//        // Se generan los complementos
//        for(Variable var: network)
//        {
//            if(var.getParents().isEmpty()) {
//                calculatedProbabilities.add(new Probability("!" + var.getValue(), 1 - getValueOfSingleVariable(var.getValue())));
//            }
//            networkMap2.put(var.getValue(), var);
//        }
    }
    
    public int indexOfVariable(Variable v) {
        for(int i = 0; i < network.size(); i ++) {
            if(v.getValue().equals(network.get(i).getValue())) {
                return i;
            }
        }
        return -1;
    }
    
    public Double getValueOfSingleVariable(String s) {
        for(Probability probability: probabilities) {
            if(probability.getMainVariable().equals(s)) {
                return probability.getValue();
            }
        }
        return -1.0;
    }
    
    public boolean isComplete() {
    	conditionalVariables = new ArrayList<Variable>();
    	
    	//Vemos cuáles son las variables condicionadas
        for(Variable variable: network) {
            boolean agregar = true;
            for(Variable conditionalVariable: conditionalVariables) {
                if(conditionalVariable.getValue().equals(variable.getValue())) {
                    agregar = false;
                    break;
                }
            }
            if(agregar) {
                if(!variable.getParents().isEmpty()) conditionalVariables.add(variable); 
            }
        }
        
       for(Variable variable: conditionalVariables) {
           String target = variable.getValue();
           // Se genera la tabla de verdad
           int size = variable.getParents().size();
           int necesarios = (int) Math.pow(2,size);
           boolean[][] tabla = new boolean[necesarios][size];
           for(int i = 0; i <tabla.length; i ++)
           {
               for(int j = 0; j<tabla[i].length; j++)
               {
                   int val = tabla.length * j + i;
                   int ret = (1 & (val >>> j));
                   tabla[i][j] = ret != 0;
               }
           }
           ArrayList<Integer> revisado = new ArrayList<Integer>();
           for(int k = 0; k < tabla.length;k++){
               revisado.add(0); 
           }
           // Se busca en las probabilidads aquelles que tengan entre sus querys
           // al target
           for(Probability probability : probabilities) {
               // Se ve si p tiene como variable query a target
        	   
               if(probability.getMainVariable().contains(target)) {
                   // Se chequea que las variables condicionadas sean las mismas
                   if(probability.getConditionVariables().size()!= variable.getParents().size()) {
                       // Se regresa falso dado que esta mal dada la definicion
                       System.out.println("Al parecer esta mal dada la definición porque tienen diferente tamaño");
                       return false;
                   }
                   // Verificamos que sean las mismas
                   if(variable.checkVariables(probability.getConditionVariables())) {
                       System.out.println("Al parecer no tienen las mismas variables");
                       return false;
                   }
                   // Se ordenan
                   probability.orderConditionalVariables();
                   
                   // Se traducen las variables condicionales a bool
                   boolean [] fila_check = new boolean[variable.getParents().size()];
                   for(int i = 0; i < variable.getParents().size(); i++) {
                	   fila_check[i] = !probability.getConditionVariables().get(i).contains("!");
                   }
                   // Ahora se recorre la tabla y si ve si alguna fila es igual
                   // a fila_check y se pone 1 en la parte donde la haya encontrado
                   for(int i = 0; i<tabla.length; i ++) {
                       if(Arrays.equals(fila_check,tabla[i])) {
                           if(revisado.get(i) == 0) {
                                revisado.set(i, 1);
                           }
                           else {
                               System.out.println("Hay una combinación repetida");
                               return false;
                           }
                       }
                   }
               }
           }
           
           // Se revisa que el revi tenga todos 1 de lo contrario hubo alguna que falto
           for(int i = 0; i<revisado.size(); i ++) {
               if(revisado.get(i) == 0) {
                   System.out.println("REGRESO FALSE PORQUE: Al parecer falta algo por definir");
                   System.out.println(Arrays.toString(tabla[i]));
                   return false;
               }
           }
       }
        return true;
    }
    
    public String createCompact() {
        String s = "";
        for(Variable variable: network) {
            s += "P(" + variable.getValue();
            if(!variable.getParents().isEmpty()) {
                s += "|";
                for(Variable variableParent : variable.getParents()) {
                    s += variableParent.getValue() + ",";
                }
                s =s.substring(0,s.length()-1);
                s+=")";
            }
            else {
                s+=")";
            }
        }
        return s;
    }
    
    public void calculateFactors() {
        // Se agregan todas las probabilidades de probs a factores
        factors.addAll(calculatedProbabilities);
        // Se deben recorrer las probabilidades e ir negando si necesario, es decir se debe revisar si ya esta el negado de la variable
        for(Probability probability: calculatedProbabilities) {
            // Se niega cada variable query en las probs
            String mainVariable = probability.getMainVariable();

            if(mainVariable.contains("!")) mainVariable = mainVariable.replace("!", ""); 
            else  mainVariable = "!" + mainVariable;
            
            // Se crea una probabilidad y se veririca si ya esta en factores
            Probability nueva = new Probability(mainVariable, probability.getConditionVariables(), 1 - probability.getValue());
            // Ahora se verifica si esta en el arraylist de factores
            boolean newFactor = false;
            for(Probability factor: factors) {
            	newFactor = nueva.equals(factor);
            }
            if(!newFactor) factors.add(nueva);
        }
        
        // Creando el hashmap que mapea de variables String a probibilidades Probabilidad
        for(Variable variable: network) {
            ArrayList<Probability> probabilities = new ArrayList<Probability>();
            for(Probability factor: factors) {
                if(factor.getMainVariable().contains(variable.getValue())) probabilities.add(factor);
            }
            this.networkMap.put(variable.getValue(), probabilities);
        }
    }
    public String showFactors() {
    	calculateFactors();
        String s = "\n";
        for(Probability p : factors) {
            s += p.toString() + "\n";
        }
        return s;
    }
}
