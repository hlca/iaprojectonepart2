package models;

import java.util.ArrayList;

public class Variable {
	private String value ="";
    private ArrayList<Variable> parents = new ArrayList<Variable>();
    private boolean checked = false;

    public Variable(String value)
    {
        this.value = value;
    }
    
    public void addParent(Variable node) {
    	node.value = node.value.replace("!", "");
        for(Variable parent: parents) {
            if(parent.value.equals(node.value)) return;
        }
        parents.add(node);
    }
    
    public boolean checkVariables(ArrayList<String> check){
        for(String s: check) {
            if(!parents.contains(s)) return false; 
        }
        return true;
    }

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ArrayList<Variable> getParents() {
		return parents;
	}

	public void setParents(ArrayList<Variable> parents) {
		this.parents = parents;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
    
    
}
