/**
 * 
 */
package FPGrowth;

import java.util.Vector;

/**
* <p>Title: FPTree.java</p>
* <p>Description: </p>
* <p>Copyright: Copyright (c) 2007</p>
* <p>Company: Zhongwei</p>
* @author Zhongwei
* @date 2016Äê6ÔÂ24ÈÕ
* @version 1.0
*/
public class FPTree {
	
	public  String               item;
	public  FPTree               next;
	public  FPTree               parent;
	public  Vector<FPTree>       children;
	public  int                  count;
   	public  boolean              root;
   	
	public FPTree(String item) {
		this.item = item;
		this.next = null;
		this.children = new Vector<FPTree>();
		this.root = false;
	}
	
	public boolean isRoot() {
		return this.root;
	}
}
