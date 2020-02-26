package net.susa.cfs.psl;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * A PslNode stores a name and a set of children for each node in the PSL
 * tree. If a node has an empty set if there are no children.
 * 
 * @author Kevin Sangeelee
 *
 */
public class PslNode implements Comparable<PslNode> {

	private String name;
	private TreeSet<PslNode> children;
	
	
	public PslNode(String name) {

		this.name = name;
		children = new TreeSet<PslNode>();
	}
	
	public PslNode(String name, PslNode child) {

		this(name);
		
		if(child != null) {
			
			children.add(child);
		}
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * Returns any immediate child of this node that has the given name. If any
	 * of the children are the wildcard, then return a new PslNode with the
	 * given name.
	 */
	public PslNode findChild(String name) {
		
		Iterator<PslNode> i = children.iterator();
		
		while(i.hasNext()) {
			PslNode child = i.next();
			
			if(child.name.equals(name))
				return child;
			else if(child.name.equals("*"))
				return new PslNode(name);
		}
		
		return null; // no node found with the given name
	}
	
	public void addChild(PslNode n) {
		children.add(n);
	}
		
	public boolean hasChildren() {
		return ! this.children.isEmpty();
	}

	public boolean hasNoChildren() {
		return this.children.isEmpty();
	}
	
	public int countChildren() {
		return children.size();
	}
	
	public String toString() {
		
		StringBuilder strb = new StringBuilder();
		
		strb.append("<node>\n<name>").append(name).append("</name>");
		strb.append("\n");
		
		if(children.isEmpty() == false) {
			strb.append("<children>\n");
			children.forEach((child) -> {
				strb.append(child.toString());
			});
			strb.append("</children>\n");
		}
		strb.append("</node>\n");
		
		return strb.toString();
	}

	@Override
	public int compareTo(PslNode n) {
		return (this.name).compareTo(n.name);
	}
}
