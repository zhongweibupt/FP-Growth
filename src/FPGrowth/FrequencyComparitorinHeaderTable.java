/**
 * 
 */
package FPGrowth;

import java.util.Comparator;

/**
* <p>Title: FrequencyComparitorinHeaderTable.java</p>
* <p>Description: </p>
* <p>Copyright: Copyright (c) 2007</p>
* <p>Company: Zhongwei</p>
* @author Zhongwei
* @date 2016Äê6ÔÂ25ÈÕ
* @version 1.0
*/
public class FrequencyComparitorinHeaderTable implements Comparator<FPTree> {
	
	public FrequencyComparitorinHeaderTable() {
    }

    public int compare(FPTree o1, FPTree o2) {
        if(o1.count>o2.count){
            return 1;
        }
        else if(o1.count < o2.count)
            return -1;
        else
            return 0;
    }
}
