public class LinkNode {
	 /*******    Node Class   *******/
		private String ID;
		private String type;
		boolean arr;
		
		public LinkNode(String ID, String type, boolean arr) {
			this.ID = ID;
			this.type = type;
			this.arr = arr;
		}

		public String getID() {
			return ID;
		}

		public void setID(String iD) {
			ID = iD;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public boolean isArr() {
			return arr;
		}

		public void setArr(boolean arr) {
			this.arr = arr;
		}
}



