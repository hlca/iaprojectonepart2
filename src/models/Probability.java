package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Probability {
	private String mainVariable = "";
    private ArrayList<String> conditionVariables = new ArrayList<String>();
    private ArrayList<String> allVariables = new ArrayList<String>();
    private Double value = 0.0;
	
	public Probability(ArrayList<Token> tokens) {
        for(Token token: tokens) {
            if(token.token == 2) {
            	mainVariable = token.sequence;
            }
            else if(token.token == 3) {
            	if(token.sequence.length() > 1) {
            		String tempString = token.sequence.replace("|", "").replace(" ", "");
            		conditionVariables.addAll(Arrays.asList(tempString.split(",")));
            	}
            }
            else if(token.token == 5) {
            	value = Double.parseDouble(token.sequence);
            }
        }
        allVariables.add(mainVariable);
        allVariables.addAll(conditionVariables);
    }
	

    public Probability(String variable, Double value) {
    	mainVariable = variable;
    	this.value = value;

        allVariables.add(mainVariable);
    }
    
    public Probability(String variable, ArrayList<String> conditional, Double value) {
    	mainVariable = variable;
    	conditionVariables = conditional;
        this.value = value;
        
        allVariables.add(mainVariable);
        allVariables.addAll(conditionVariables);
    }
    
    
    public void orderConditionalVariables() {
        ArrayList<String> temp = new ArrayList<String>();
        for(String s: conditionVariables) {
            temp.add(s.replace("!", ""));
        }
        // Se ordena temp
        Collections.sort(temp);
        ArrayList<String> helper = new ArrayList<String>();
        helper.addAll(conditionVariables);
        conditionVariables = new ArrayList<String>(); // Se limpia
        for(String string: temp) {
            for(String stringHelper: helper) {
                if(stringHelper.contains(string)) {
                    conditionVariables.add((stringHelper.contains("!")?"!":"") + string);
                }
            }
        }
    }

	public String getMainVariable() {
		return mainVariable;
	}

	public void setMainVariable(String mainVariable) {
		this.mainVariable = mainVariable;
	}


	public ArrayList<String> getConditionVariables() {
		return conditionVariables;
	}


	public void setConditionVariables(ArrayList<String> conditionVariables) {
		this.conditionVariables = conditionVariables;
	}


	public ArrayList<String> getAllVariables() {
		return allVariables;
	}


	public void setAllVariables(ArrayList<String> allVariables) {
		this.allVariables = allVariables;
	}


	public Double getValue() {
		return value;
	}


	public void setValue(Double value) {
		this.value = value;
	}
	
	 @Override
	    public String toString()
	    {
	        String r = "";
	        r += "P(";
            r += mainVariable;
	        if(this.conditionVariables.size() > 0)
	        {
	            r += "|";
	        }
	        for(String s:this.conditionVariables)
	        {
	            r += s+" ";
	        }
	        r+=")="+this.value;
	        return r;
	    }
}
