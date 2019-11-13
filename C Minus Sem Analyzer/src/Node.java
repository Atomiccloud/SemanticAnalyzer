import java.util.ArrayList;
import java.util.Iterator;

public class Node implements Iterable<Node> {
	 /*******    Node Class   *******/
		private ArrayList<Node> children = new ArrayList<Node>();
		private String data;
		
		public Node(String d) {
			this.data = d;
		}
		
		public String getData() {
			return data;
		}
		
		public void addChild(Node c) {
			this.children.add(c);
		}
		
		public Node getChild(int i) {
			return this.children.get(i);
		}
		
		public int getSize() {
			return this.children.size();		
		}
		
		public boolean hasChildren() {
			return this.children.size() != 0;
		}
		
		public void printChildren(Node node) {
            for(Node get : node.children) { 
                System.out.println(get.data);
                printChildren(get);
            }
        }
		
		public Node next(Node node) {
			if(node.hasChildren()) {
				node = node.getChild(0);
			}
			return node;
		}

		@Override
		public Iterator<Node> iterator() {
			// TODO Auto-generated method stub
			return null;
		}
}



