package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import models.Probability;
import models.Token;



public class Parser {
	private Tokenizer tokenizer;
	private ArrayList<String> bayesianNetwork = new ArrayList<String>();
	private ArrayList<Probability> probabilities = new ArrayList<Probability>();
	private ArrayList<ArrayList<Token>> tokenList = new ArrayList<ArrayList<Token>>();
	
	public Parser() {
		tokenizer = new Tokenizer();
		tokenizer.add("(P|p)\\(",1);
        tokenizer.add("(!|)[a-zA-Z](,(!|)[a-zA-Z])*(\\| |)",2);
        tokenizer.add("\\|(!|)[a-zA-Z](,(!|)[a-zA-Z])*",3);
        tokenizer.add("\\)\\=", 4); 
        tokenizer.add("0.[0-9]*",5);
	}
	
	public boolean parse(String filePath) {
		fileReader(filePath);
		createTokens();
		createProbabilities();
		System.out.println(probabilities);
		if(!checkProbabilities()) {
			return false;
		}
		return true;
	}
	
	public boolean createTokens() {
		boolean success = true;
        for(String expr: bayesianNetwork) {
            ArrayList<Token> tokens = new ArrayList<Token>();
            success = success && tokenizer.tokenize(expr);
            for(Token t: tokenizer.getTokens()) {
            	tokens.add(t);
            }
            tokenList.add(tokens);
        }
        return success;
    }
    
	public void createProbabilities() {
        for(ArrayList<Token> allTokens : tokenList) {
            probabilities.add(new Probability(allTokens));
        }
    }
	
	public void fileReader(String filePath) {
		File file = new File(filePath);
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
            while ((line = br.readLine()) != null) {
            	line = line.replace(" ", "");
                if (line.equals("")) continue;
                bayesianNetwork.add(line.replace(" ",""));
            }
        }
        catch(Exception e) {
            System.out.println("Error reading the file.");
        }
	}
	
	public boolean checkProbabilities() {
		int index = 0;
        for(Probability p: probabilities) {
        	ArrayList<String> conditionVariables = p.getConditionVariables();
        	String mainVariable = p.getMainVariable();
        	
            if(conditionVariables.contains(mainVariable)) return false;

            int parents = conditionVariables.size();
            for(int i = index + 1; i < probabilities.size(); i++) {
            	//Cuando es la misma variable, validamos
                if(probabilities.get(i).getMainVariable().equals(mainVariable)) {
                    int parentsHelper = probabilities.get(i).getConditionVariables().size();
                    if(parents  != parentsHelper) return false;
                    //Las condicionales no pueden ser la misma
                    if(probabilities.get(i).getConditionVariables().containsAll(p.getConditionVariables())) return false;
                    
                    ArrayList<String> evaluatingProbabilityVariables = new ArrayList<String>();
                    ArrayList<String> currentProbabilityVariables = new ArrayList<String>();
                    
                    for(String s: probabilities.get(i).getConditionVariables()) {
                        evaluatingProbabilityVariables.add(s.replace("!", ""));
                    }
                    for(String s: p.getConditionVariables()) {
                        currentProbabilityVariables.add(s.replace("!",""));
                    }
                    if(!currentProbabilityVariables.containsAll(evaluatingProbabilityVariables)) return false;
                }

                if(probabilities.get(i).getConditionVariables().containsAll(p.getConditionVariables())) {
                	//Chequeamos la primera parte en caso que la segunda sea igual
                    if(probabilities.get(i).getMainVariable().equals(p.getMainVariable())) return false;
                    if(p.getMainVariable().contains("!")) {
                        if(probabilities.get(i).getMainVariable().equals(p.getMainVariable().replace("!", ""))) return false;
                    }
                    else {
                        if(probabilities.get(i).getMainVariable().equals("!"+p.getMainVariable())) return false;
                    }
                }
            }
            index++;
        }
        return true;
	}

	public Tokenizer getTokenizer() {
		return tokenizer;
	}

	public void setTokenizer(Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}

	public ArrayList<String> getBayesianNetwork() {
		return bayesianNetwork;
	}

	public void setBayesianNetwork(ArrayList<String> bayesianNetwork) {
		this.bayesianNetwork = bayesianNetwork;
	}

	public ArrayList<Probability> getProbabilities() {
		return probabilities;
	}

	public void setProbabilities(ArrayList<Probability> probabilities) {
		this.probabilities = probabilities;
	}

	public ArrayList<ArrayList<Token>> getTokenList() {
		return tokenList;
	}

	public void setTokenList(ArrayList<ArrayList<Token>> tokenList) {
		this.tokenList = tokenList;
	}
	
}
