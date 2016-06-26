/**
 * 
 */
package FPGrowth;

import java.io.File;
import java.io.FileNotFoundException;

/**
* <p>Title: main.java</p>
* <p>Description: </p>
* <p>Copyright: Copyright (c) 2007</p>
* <p>Company: Zhongwei</p>
* @author Zhongwei
* @date 2016Äê6ÔÂ25ÈÕ
* @version 1.0
*/
public class main {
	
	static int threshold = 2;
    static String file = "C:\\Users\\zhwei\\workspace\\FP-Growth\\input.csv";
    static String output = "output.csv";

    public static void main(String[] args) throws FileNotFoundException {
        long start = System.currentTimeMillis();
        
        HeaderTable headerTable = new HeaderTable();
        FPTree header = headerTable.getFPTree(new File(file), threshold);
        
        PatternSeacher seacher = new PatternSeacher();
        seacher.updateFrequentPatterns(header, threshold, headerTable.getHeaderTable());
        seacher.print(new File(output));
        
        System.out.println((System.currentTimeMillis() - start));
    }

}
