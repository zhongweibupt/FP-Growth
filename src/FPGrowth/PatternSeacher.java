/**
 * 
 */
package FPGrowth;

import java.io.FileNotFoundException;
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
* @date 2016Äê6ÔÂ25ÈÕ
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
	
	public void print() throws FileNotFoundException {
        Formatter output = new Formatter("out.csv");
        for (String frequentPattern : this.frequentPatterns.keySet()) {
            output.format("%s\t%d\n", frequentPattern, this.frequentPatterns.get(frequentPattern));
        }
    }
	
	private void fpGrowth(FPTree fptree, String base, int threshold, Vector<FPTree> headerTable, Map<String, Integer> frequentPatterns) {
		for (FPTree iteminTree : headerTable) {
            String currentPattern = (base != null ? base : "") + (base != null ? " " : "") + iteminTree.item;
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
            HeaderTable hd = new HeaderTable(); 
            FPTree conditional_fptree = hd.constructConditionalFPTree(conditionalPatternBase, conditionalItemsMaptoFrequencies, threshold, conditional_headerTable);
            conditional_headerTable = hd.getHeaderTable();

            Collections.sort(conditional_headerTable, new FrequencyComparitorinHeaderTable());
            
            if (!conditional_fptree.children.isEmpty()) {
                fpGrowth(conditional_fptree, currentPattern, threshold, conditional_headerTable, frequentPatterns);
            }
        }
	}
	
	

}
