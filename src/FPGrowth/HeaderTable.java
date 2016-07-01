/**
 * 
 */
package fpgrowth;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;


/**
* <p>Title: HeaderTable.java</p>
* <p>Description: </p>
* <p>Copyright: Copyright (c) 2007</p>
* <p>Company: Zhongwei</p>
* @author Zhongwei
* @date 2016��6��25��
* @version 1.0
*/
public class HeaderTable {
	
	private Vector<FPTree>       headerTable;
	private FPTree               header;
	private int                  threshold;
	private File                 file;
	private Map<String, Integer> itemsMaptoFrequencies;
	private List<String>         sortedItemsbyFrequencies;
	private Vector<String>       itemstoRemove;
	private Scanner              input;
	
	public HeaderTable() {
		this.headerTable = new Vector<FPTree>();
		this.header = new FPTree("null");
		this.header.item = null;
		this.header.root = true;
		
		this.itemsMaptoFrequencies = new HashMap<String, Integer>();
		this.sortedItemsbyFrequencies = new LinkedList<String>();
		this.itemstoRemove = new Vector<String>();
		
		this.threshold = 0;
	}
	
	public FPTree getFPTree(File file, int threshold) throws FileNotFoundException {
		this.file = file;
		this.threshold = threshold;
		FirstScanItems(this.file, this.threshold, this.itemsMaptoFrequencies, this.sortedItemsbyFrequencies, this.itemstoRemove);
		constructFPTree(this.file, this.itemsMaptoFrequencies, this.sortedItemsbyFrequencies);
		return this.header;
	}
	
	public Vector<FPTree> getHeaderTable() {
		return this.headerTable;
	}
	
	public int getThreshold() {
		return this.threshold;
	}
	
	/**
	 * @MethodName     : private void FirstScanItems()
	 * @Description    : ɨ���������ݿ⣬�õ�Ƶ��1���֧�ֶȼ�����֧�ֶ���ֵ��threshold��֧�ֶȼ���ͳ�ƽ���洢��Map<String, Integer> itemsMaptoFrequencies�У�Ƶ���List<String> sortedItemsbyFrequencies����֧�ֶȽ�������
	 * @Param          : File file, int threshold, Map<String, Integer> itemsMaptoFrequencies, List<String> sortedItemsbyFrequencies, Vector<String> itemstoRemove
	 * @throws         : FileNotFoundException 
	 * @Exmaple        : 
	 * input - threshold = 3
	 *         affairs.csv =  ��������������������������������������������������������������
	 *                        ��item_1,item_2,item_3         ��
	 *                        ��������������������������������������������������������������
	 *                        ��item_2                       ��
	 *                        ��������������������������������������������������������������
	 *                        ��item_1,item_2,item_4         ��
	 *                        ��������������������������������������������������������������
	 *                        ��item_1,item_2,item_3,item_4  ��
	 *                        ��������������������������������������������������������������
	 *        
	 * 
	 * output - sortedItemsbyFrequencies = {item_2, item_1}
	 * 
	 */
	private void FirstScanItems(File file, int threshold, Map<String, Integer> itemsMaptoFrequencies, List<String> sortedItemsbyFrequencies, Vector<String> itemstoRemove) throws FileNotFoundException {
		
		input = new Scanner(file);
		input.useDelimiter("[,\n\r]");
		
		//��������⣬ͳ��item֧�ֶ�
		while (input.hasNext()) {
            String temp = input.next();
            if (itemsMaptoFrequencies.containsKey(temp)) {
                int count = itemsMaptoFrequencies.get(temp);
                itemsMaptoFrequencies.put(temp, count + 1);
            } else {
                itemsMaptoFrequencies.put(temp, 1);
            }
        }
        //input.close();
        
        //����ͷ���<null, 0>
        sortedItemsbyFrequencies.add("null");
        itemsMaptoFrequencies.put("null", 0);
        
        //����item���в�������
        for (String item : itemsMaptoFrequencies.keySet()) {
            int count = itemsMaptoFrequencies.get(item);
            // System.out.println( count );
            int i = 0;
            for (String listItem : sortedItemsbyFrequencies) {
                if (itemsMaptoFrequencies.get(listItem) < count) {
                    sortedItemsbyFrequencies.add(i, item);
                    break;
                }
                i++;
            }
        }

        //��֧�ֶ���ֵ�ҳ���Ҫ��ȥ��item
        for (String listItem : sortedItemsbyFrequencies) {
            if (itemsMaptoFrequencies.get(listItem) < threshold) {
                itemstoRemove.add(listItem);
            }
        }
        //ɾ��sortedItemsbyFrequencies��δ�ﵽ֧�ֶ���ֵ��item
        for (String itemtoRemove : itemstoRemove) {
            sortedItemsbyFrequencies.remove(itemtoRemove);
        }
        sortedItemsbyFrequencies.remove(0);
        sortedItemsbyFrequencies.remove("null");
	}
	
	/**
	 * @MethodName     : private void constructFPTree()
	 * @Description    : ɨ���������ݿ⣬�õ�Ƶ��1���֧�ֶȼ�����֧�ֶ���ֵ��threshold��֧�ֶȼ���ͳ�ƽ���洢��Map<String, Integer> itemsMaptoFrequencies�У�Ƶ���List<String> sortedItemsbyFrequencies����֧�ֶȽ�������
	 * @param          : File file, Map<String, Integer> itemsMaptoFrequencies, Scanner input, List<String> sortedItemsbyFrequencies
	 * @throws         : FileNotFoundException 
	 */
	private void constructFPTree(File file, Map<String, Integer> itemsMaptoFrequencies, List<String> sortedItemsbyFrequencies) throws FileNotFoundException {
		
		input = new Scanner(file);
		
		//��ͷ�����Ƶ��1�
		for (String itemsforTable : sortedItemsbyFrequencies) {
            this.headerTable.add(new FPTree(itemsforTable));
        }
		
		while(input.hasNextLine()) {
			String line = input.nextLine();
			StringTokenizer tokenizer = new StringTokenizer(line, ",");
			//�洢�����е�item���У���֧�ֶȴ�С�������У�ע��ֻ�洢����Ƶ���������
			Vector<String> affairSortedbyFrequencies = new Vector<String>();
			while(tokenizer.hasMoreTokens()) {
				String item = tokenizer.nextToken();
				
				if (this.itemstoRemove.contains(item)) {
                    continue;
                }
				int index = 0;
                for (String vectorString : affairSortedbyFrequencies) {
                	if (itemsMaptoFrequencies.get(vectorString) < itemsMaptoFrequencies.get(item) || ((itemsMaptoFrequencies.get(vectorString) == itemsMaptoFrequencies.get(item)) && (vectorString.compareToIgnoreCase(item) < 0 ? true : false))) {
                    	affairSortedbyFrequencies.add(index, item);
                        break;
                    }
                    index++;
                }
                if (!affairSortedbyFrequencies.contains(item)) {
                	affairSortedbyFrequencies.add(item);
                }
			}
                
            insertToTree(affairSortedbyFrequencies, this.header, this.headerTable);
            affairSortedbyFrequencies.clear();
		}
		//ͳ��֧�ֶȼ���
		//�������Ҫͳ�ƣ�ֱ�Ӵ�Mapȡ�����𣿣�
		for (FPTree item : this.headerTable) {
	        int count = 0;
	        FPTree itemtemp = item;
	        while (itemtemp.next != null) {
	        	itemtemp = itemtemp.next;
	            count += itemtemp.count;
	        }
	        item.count = count;
	    }
		//����Ҫ���������𣿣�
	    Comparator c = new FrequencyComparitorinHeaderTable();
	    Collections.sort(headerTable, c);
	    //input.close();
	}
	
	
	/**
	 * @MethodName     : private void insertToTree()
	 * @Description    : �������item���еݹ����FP���У�ͬʱ������ͷ���������
	 * @param          : Vector<String> affairSortedbyFrequencies, FPTree header, Vector<FPTree> headerTable
	 */
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
            //������item���е��׽ڵ���뵽��ͷ��headerTable�ж�Ӧitem������β�ˡ�
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
}
