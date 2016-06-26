/**
 * 
 */
package FPGrowth;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

/**
* <p>Title: PatternSeacher.java</p>
* <p>Description: </p>
* <p>Copyright: Copyright (c) 2007</p>
* <p>Company: Zhongwei</p>
* @author Zhongwei
* @date 2016年6月25日
* @version 1.0
*/
public class PatternSeacher {
	
	private int threshold;
	private HashMap<String, Integer> frequentPatterns;
	
	public PatternSeacher() {
		this.threshold = 0;
		this.frequentPatterns = new HashMap<String, Integer>();;
	}
	
	public void updateFrequentPatterns(FPTree header, int threshold, Vector<FPTree> headerTable) {
		this.threshold = threshold;
		fpGrowth(header, null, this.threshold, headerTable, this.frequentPatterns);
	}
	
	public void print(File file) throws FileNotFoundException {
        
        FileOutputStream out=null;
        OutputStreamWriter osw=null;
        BufferedWriter bw=null;
        try {
            out = new FileOutputStream(file);
            osw = new OutputStreamWriter(out);
            bw =new BufferedWriter(osw);
            for (String frequentPattern : this.frequentPatterns.keySet()) {
            	String output = frequentPattern + "," + this.frequentPatterns.get(frequentPattern);
            	System.out.println("Result:" + output);
            	bw.append(output + "\n");
            }
        } catch (Exception e) {

        }finally{
            if(bw!=null){
                try {
                    bw.close();
                    bw=null;
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
            if(osw!=null){
                try {
                    osw.close();
                    osw=null;
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
            if(out!=null){
                try {
                    out.close();
                    out=null;
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
        }
        
    }
	
	private void fpGrowth(FPTree fptree, String base, int threshold, Vector<FPTree> headerTable, Map<String, Integer> frequentPatterns) {
		for (FPTree iteminTree : headerTable) {
            String currentPattern = (base != null ? base : "") + (base != null ? "," : "") + iteminTree.item;
            int supportofCurrentPattern = 0;
            Map<String, Integer> conditionalPatternBase = new HashMap<String, Integer>();
            while (iteminTree.next != null) {
                iteminTree = iteminTree.next;
                supportofCurrentPattern += iteminTree.count;
                String conditionalPattern = null;
                FPTree conditionalItem = iteminTree.parent;

                while (!conditionalItem.isRoot()) {
                    conditionalPattern = conditionalItem.item + " " + (conditionalPattern != null ? conditionalPattern : "");
                    conditionalItem = conditionalItem.parent;
                }
                if (conditionalPattern != null) {
                    conditionalPatternBase.put(conditionalPattern, iteminTree.count);
                }
            }
            frequentPatterns.put(currentPattern, supportofCurrentPattern);
            //counting frequencies of single items in conditional pattern-base
            Map<String, Integer> conditionalItemsMaptoFrequencies = new HashMap<String, Integer>();
            for (String conditionalPattern : conditionalPatternBase.keySet()) {
                StringTokenizer tokenizer = new StringTokenizer(conditionalPattern);
                while (tokenizer.hasMoreTokens()) {
                    String item = tokenizer.nextToken();
                    if (conditionalItemsMaptoFrequencies.containsKey(item)) {
                        int count = conditionalItemsMaptoFrequencies.get(item);
                        count += conditionalPatternBase.get(conditionalPattern);
                        conditionalItemsMaptoFrequencies.put(item, count);
                    } else {
                        conditionalItemsMaptoFrequencies.put(item, conditionalPatternBase.get(conditionalPattern));
                    }
                }
            }
            //conditional fptree
            //HeaderTable Creation
            // first elements are being used just as pointers
            // non conditional frequents also will be removed
            Vector<FPTree> conditional_headerTable = new Vector<FPTree>();
            for (String itemsforTable : conditionalItemsMaptoFrequencies.keySet()) {
                int count = conditionalItemsMaptoFrequencies.get(itemsforTable);
                if (count < threshold) {
                    continue;
                }
                FPTree f = new FPTree(itemsforTable);
                f.count = count;
                conditional_headerTable.add(f);
            }
            FPTree conditional_fptree = constructConditionalFPTree(conditionalPatternBase, conditionalItemsMaptoFrequencies, threshold, conditional_headerTable);
            //headertable reverse ordering
            Collections.sort(conditional_headerTable, new FrequencyComparitorinHeaderTable());
            //
            if (!conditional_fptree.children.isEmpty()) {
                fpGrowth(conditional_fptree, currentPattern, threshold, conditional_headerTable, frequentPatterns);
            }
        }
	}
	
	private FPTree constructConditionalFPTree(Map<String, Integer> conditionalPatternBase, Map<String, Integer> conditionalItemsMaptoFrequencies, int threshold, Vector<FPTree> conditional_headerTable) {
		
		FPTree conditional_fptree = new FPTree("null");
        conditional_fptree.item = null;
        conditional_fptree.root = true;
		
        for (String pattern : conditionalPatternBase.keySet()) {
            
            Vector<String> pattern_vector = new Vector<String>();
            StringTokenizer tokenizer = new StringTokenizer(pattern);
            while (tokenizer.hasMoreTokens()) {
                String item = tokenizer.nextToken();
                if (conditionalItemsMaptoFrequencies.get(item) >= threshold) {
                    pattern_vector.addElement(item);
                }
            }      
            insertToConditionalTree(pattern_vector, conditionalPatternBase.get(pattern), conditional_fptree, conditional_headerTable);
        }
        return conditional_fptree;
    }
	
	private void insertToTree(Vector<String> affairSortedbyFrequencies, FPTree header, Vector<FPTree> headerTable) {
		if (affairSortedbyFrequencies.isEmpty()) {
            return;
        }
        String itemtoAddtotree = affairSortedbyFrequencies.firstElement();
        FPTree newNode = null;
        boolean ifisdone = false;
        for (FPTree child : header.children) {
            if (child.item.equals(itemtoAddtotree)) {
                newNode = child;
                child.count++;
                ifisdone = true;
                break;
            }
        }
        if (!ifisdone) {
            newNode = new FPTree(itemtoAddtotree);
            newNode.count = 1;
            newNode.parent = header;
            header.children.add(newNode);
            //将事务item序列的首节点插入到项头表headerTable中对应item的链表尾端。
            for (FPTree headerPointer : headerTable) {
                if (headerPointer.item.equals(itemtoAddtotree)) {
                    while (headerPointer.next != null) {
                        headerPointer = headerPointer.next;
                    }
                    headerPointer.next = newNode;
                }
            }
        }
        affairSortedbyFrequencies.remove(0);
        insertToTree(affairSortedbyFrequencies, newNode, headerTable);
	}
	
	
	private void insertToConditionalTree(Vector<String> pattern_vector, int count_of_pattern, FPTree conditional_fptree, Vector<FPTree> conditional_headerTable) {
		if (pattern_vector.isEmpty()) {
            return;
        }
        String itemtoAddtotree = pattern_vector.firstElement();
        FPTree newNode = null;
        boolean ifisdone = false;
        for (FPTree child : conditional_fptree.children) {
            if (child.item.equals(itemtoAddtotree)) {
                newNode = child;
                child.count += count_of_pattern;
                ifisdone = true;
                break;
            }
        }
        if (!ifisdone) {
            for (FPTree headerPointer : conditional_headerTable) {
                //this if also gurantees removing og non frequets
                if (headerPointer.item.equals(itemtoAddtotree)) {
                    newNode = new FPTree(itemtoAddtotree);
                    newNode.count = count_of_pattern;
                    newNode.parent = conditional_fptree;
                    conditional_fptree.children.add(newNode);
                    while (headerPointer.next != null) {
                        headerPointer = headerPointer.next;
                    }
                    headerPointer.next = newNode;
                }
            }
        }
        pattern_vector.remove(0);
        insertToConditionalTree(pattern_vector, count_of_pattern, newNode, conditional_headerTable);
	}

}
