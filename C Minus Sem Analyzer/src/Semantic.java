import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Semantic {
	//Global Variables
	static boolean enteredReturn = false;
	static boolean mainVoid2 = false;
	static boolean mainVoid = false;
	static boolean foundInParams = false;
	static boolean returnInt = false;
	static Scanner sc = null;
	static String currToken;
	static String funString; 
	static String voidCheck;
	static String lastToken;
	static int currScope = 0;
	static ArrayList<String> params;
	static List<String> funArray = new ArrayList<String>(); 
	static Map<String, String[]> varMap = new HashMap<String, String[]>();
	static Map<String, String> returnCheck = new HashMap<String, String>();
	static Map<String, ArrayList<String>> paramMap = new HashMap<String, ArrayList<String>>();
	static List<Map<String, String[]>> ScopeArray = new ArrayList<Map<String, String[]>>(); 

	public static void main(String[] args) {
		File textFile = new File(args[0]);
		Lexer lexer = new Lexer(textFile);
		textFile = new File("tokens.txt");
		
		try {
			sc = new Scanner(textFile);
			currToken = sc.nextLine();
			
			decList();
			if(currToken.equals("$")) {
				checkFunctions();
				System.out.println("ACCEPT");
			} else {
				rej();
			}
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found!");
		}
		sc.close();
	}
	
	private static void checkFunctions() {
		//check that main is last
		if(!funArray.get(funArray.size()-1).equals("ID: main")) {
			rej();
		}
		//check for void main(void)
		if(mainVoid == false || mainVoid2 == false) {
			rej();
		}
		// Check for duplicate functions
		Set<String> set = new HashSet<String>(funArray);
		if(set.size() < funArray.size()){
		    rej();
		}
	}

	private static void decList() {	
		declaration();
		decListP();
		return;
	}
	
	private static void decListP() {
		if (currToken.contains("K: ")) {
			declaration();
			decListP();
		}
		return;
	}

	private static void declaration() {
		voidCheck = currToken;
		typeSpecifier();
		funString = currToken;
		checkID();
		declarationP();
		return;
	}

	private static void checkID() {
		if(currToken.contains("ID: ")) {
			currToken = sc.nextLine();
		} else {
			rej();
		}		
		return;
	}

	private static void declarationP() {
		if (currToken.equals(";") || currToken.equals("[")) {
			if(voidCheck.equals("K: void")) {
				rej();
			}
			varDecP();
		} else if (currToken.equals("(")) {
			if(funString.equals("ID: main")) {
				if(voidCheck.equals("K: void")) {
					mainVoid2 = true;					
				}
			}
			funArray.add(funString);
			returnCheck.put(funString, voidCheck);
			funDecP();
		} else {
			rej();
		}
		return;
	}
	
	private static void funDecP() {
		if (currToken.equals("(")) {
			currToken = sc.nextLine();
			params();
			if (currToken.equals(")")) {
				currToken = sc.nextLine();
				compoundStmt();
			} else {
				rej();
			}
		} else {
			rej();
		}
		return;
	}

	private static void compoundStmt() {
		if(currToken.equals("{")) {
			currScope++;
			if(currScope == 1) {
				if(returnCheck.get(funString).equals("K: int")) {
					returnInt = true;
					enteredReturn = false;
				}
			}
			if(ScopeArray.size() != currScope) {
				ScopeArray.add(null);
			}
			varMap = new HashMap<String, String[]>();
			currToken = sc.nextLine();
			localDec();
			stmtList();
			if(currToken.equals("}")) {
				if(ScopeArray.size() != currScope) {
					ScopeArray.remove(currScope);					
				}
				currScope--;
				if(currScope == 0) {
					if(returnInt) {
						if(!enteredReturn) {
							System.out.println("Rejected in cmpdstmt");
							rej();
						}
					}
					returnInt = false;
				}
				varMap = ScopeArray.get(currScope);
				currToken = sc.nextLine();
				if((funArray.get(funArray.size()-1).equals("ID: main")) && currScope == 0) {
					if(!currToken.equals("$")) {
						rej();
					}
				}
			} else {
				rej();
			}
		}
		return;
	}

	private static void stmtList() {
		if (currToken.equals("(") || currToken.equals("{") || currToken.contains("ID: ") || currToken.contains("K: ") || currToken.contains("INT: ") 
				|| currToken.equals(";") ) {
			statement();
			stmtList();
		}
		return;
	}

	private static void statement() {
		if (currToken.equals("{")) {
			compoundStmt();
		} else if (currToken.equals("K: if")) {
			selectionStmt();
		} else if (currToken.equals("K: while")) {
			iterationStmt();
		} else if (currToken.equals("K: return")) {
			enteredReturn = true;
			returnStmt();
		} else {
			expressionStmt();
		}
		return;
	}

	private static void expressionStmt() {
		if (currToken.equals(";")) {
			currToken = sc.nextLine();			
		} else {
			expression();
			if(currToken.equals(";")) {	
				currToken = sc.nextLine();				
			} else {
				rej();
			}
		}
		return;
	}

	private static void expression() {
		addExpression();
		if(currToken.equals("<=") || currToken.equals("<") || currToken.equals(">") || currToken.equals(">=") || currToken.equals("==") || currToken.equals("!=")) {
			relop();
			addExpression();
		}
		return;
	}

	private static void relop() {
		currToken = sc.nextLine();
		return;
	}

	private static void addExpression() {
		term();
		addExpressionP();
		return;
	}

	private static void addExpressionP() {
		if (currToken.equals("+") || currToken.equals("-")) {
			addop();
			term();
			addExpressionP();
		}
		return;
	}

	private static void termP() {
		if (currToken.equals("*") || currToken.equals("/")) {
			mulop();
			factor();
			termP();
		}
		return;
	}

	private static void mulop() {
		currToken = sc.nextLine();
		return;
	}

	private static void addop() {
		currToken = sc.nextLine();
		if(returnCheck.containsKey(currToken) && !returnCheck.get(currToken).equals("K: int")) {
			System.out.println("Rejected in addopP");
			rej();
		}
		return;
	}

	private static void factor() {
		if (currToken.equals("(")) {
			currToken = sc.nextLine();
			expression();
			if (currToken.equals(")")) {
				currToken = sc.nextLine();
			}
		} else if (currToken.contains("ID: ")) {
			String func = currToken;
			checkID();
			if(enteredReturn && returnCheck.get(funString).equals("K: int")) {
				if(params.size()>1) {
					for(int i=1;i<params.size();i=i+2) {
						if(params.get(i).equals(func)) {
							if(params.get(i-1).equals("array")) {
								System.out.println("Rej in factor");
								rej();
							}
						}
					}
				}
			}
			factorP(func);
		} else if (currToken.contains("INT: ") ) {
			checkNUM();
		} else {
			rej();
		}
		return;
	}

	private static void factorP(String func) {
		if (currToken.equals("(")) {
			callP(func);
		} else {
			varP(func);
		}
		return;
	}

	private static void varP(String func) {
		int found = scopeCheck(func);
		if (currToken.equals("[")) {
			arrayCheck(found, func);
			currToken = sc.nextLine();
			expression();
			if (!currToken.equals("]")) {
				rej();
			} else {
				currToken = sc.nextLine();
				if (currToken.equals("=")) {
					currToken = sc.nextLine();
					if(currToken.equals("(") || currToken.contains("ID: ") || currToken.contains("INT: ") ) {
						expression();
					} else {
						checkID();
						if (currToken.equals("[")) {
							
							currToken = sc.nextLine();
							expression();
							if (currToken.equals("]")) {
								
								currToken = sc.nextLine();
							} else {
								rej();
							}
						} else {						
					}
						rej();
					}
				}
			}
		} else if (currToken.equals("=")) {
			arrayCheck(found,func);
			currToken = sc.nextLine();
			expression();
		} 
		return;
	}

	private static void callP(String func) {
		currToken = sc.nextLine();
		args(func);
		if (currToken.equals(")")) {
			currToken = sc.nextLine();
		} else {
			rej();
		}
		if(currToken.equals("+")) {
			if(!returnCheck.get(func).equals("K: int")) {
				System.out.println("Rejected in callP");
				rej();
			}
		}
		return;
	}

	private static void args(String func) {
		if(currToken.equals(")")) {
			if(!paramMap.get(func).get(0).equals("void")) {
				System.out.println("Rejected in args");
				rej();
			}
		}
		if (currToken.contains("ID: ") || currToken.contains("INT: ") ) {
			argList(func);
		}
		return;
	}

	private static void argList(String func) {
		int counter = 2;
		if(!varMap.containsKey(currToken)) {
			rej();
		} else {
			if(paramMap.get(func) == null) {
				System.out.println("rejected in arglist");
				rej();
			}
			if(!(varMap.get(currToken)[1].equals(paramMap.get(func).get(0)))){
				rej();
			}
		}
		expression();
		argListP(func, counter);
		return;
	}

	private static void argListP(String func, int counter) {
		if(currToken.equals(",")) {	
			currToken = sc.nextLine();
			if(!varMap.containsKey(currToken)) {
				//check prev scope for globals?
				rej();
			} else {
				if(!(varMap.get(currToken)[1].equals(paramMap.get(func).get(counter)))) {
					rej();
				}
			}
			expression();	
			argListP(func, counter+2);
		}	
		return;
	}

	private static void term() {
		factor();
		termP();
		return;
	}

	private static void returnStmt() {
		currToken = sc.nextLine();
		if (currToken.equals(";")) {
			if(!voidCheck.equals("K: void")) {
				System.out.println("Reject in returnStmt1");
				rej();
			}
			currToken = sc.nextLine();
		} else {
			expression();
			if(!voidCheck.equals("K: int")) {
				System.out.println("Reject in returnStmt2");
				rej();
			}
			if (currToken.equals(";")) {
				
				currToken = sc.nextLine();
			} else {
				rej();
			}
		}
		return;
	}

	private static void iterationStmt() {
		currToken = sc.nextLine();
		if(currToken.equals("(")) {
			
			currToken = sc.nextLine();
			expression();
			if (currToken.equals(")")) {
				
				currToken = sc.nextLine();
				statement();
			} else {
				rej();
			}
		} else {
			rej();
		}
		return;
	}

	private static void selectionStmt() {
		currToken = sc.nextLine();
		if (currToken.equals("(")) {
			
			currToken = sc.nextLine();
			expression();
			if (currToken.equals(")")) {
				
				currToken = sc.nextLine();
				statement();
				if (currToken.equals("K: else")) {
					
					currToken = sc.nextLine();
					statement();
				}
			} else {
				rej();
			}
		} else {
			rej();
		}
		return;
	}

	private static void localDec() {
		if (currToken.equals("K: int")) {
			varDec();
			localDec();
		}
		return;
	}

	private static void varDec() {
		String[] varArray = new String[4]; 
		typeSpecifier();
		String tempID = currToken;
		checkID();
		if(currToken.equals(";")) {
			if(!varMap.containsKey(tempID)){
				varArray[1] = "K: int";
				varArray[3] = (String.valueOf(currScope));
				varMap.put(tempID, varArray);				
			} else {
				rej();
			}
			currToken = sc.nextLine();
		} else if (currToken.equals("[")) {
			currToken = sc.nextLine();
			varArray[1] = "array";
			varArray[2] = currToken;
			varArray[3] = String.valueOf(currScope);
			checkNUM();
			if(!currToken.equals("]")) {
				rej();
			}
			if(!varMap.containsKey(tempID)){
				varMap.put(tempID, varArray);				
			} else {
				rej();
			}
			currToken = sc.nextLine();
			if(!currToken.equals(";")) {
				rej();
			}
			currToken = sc.nextLine();
		} else {
			rej();
		}
		
		if(ScopeArray.size() == 0) {
			ScopeArray.add(0,null);
			ScopeArray.add(currScope,varMap);					
		} else if(ScopeArray.size() == currScope){
			ScopeArray.add(currScope, varMap);
		}
		return;
	}

	private static void checkNUM() {
		if (currToken.contains("INT: ") ) {
			currToken = sc.nextLine();
		} else {
			rej();
		}
		return;
	}

	private static void params() {
		params = new ArrayList<String>();
		if(currToken.equals("K: void")) {
			if(funArray.get(funArray.size()-1).equals("ID: main")) {
				mainVoid = true;
			}
			params.add("void");
			paramMap.put(funString, params);
			currToken = sc.nextLine();
		} else {
			paramsList();
		}
		paramMap.put(funString, params);
		return;
	}

	private static void paramsList() {
		param();
		paramsListP();
		return;
	}

	private static void paramsListP() {
		if(currToken.equals(",")) {
			
			currToken = sc.nextLine();
			param();
			paramsListP();
		}
		return;
	}

	private static void param() {
		params.add(currToken);
		typeSpecifier();
		params.add(currToken);
		checkID();
		if(currToken.equals("[")) {
			params.set(params.size()-2, "array");
			currToken = sc.nextLine();
			if(currToken.equals("]")) {
				currToken = sc.nextLine();
			}
		}
		return;
	}

	private static void varDecP() {
		String[] varArray = new String[4];
		if(currToken.equals(";")) {
			if(!varMap.containsKey(funString)){
				varArray[1] = "K: int";
				varArray[3] = String.valueOf(currScope);
				varMap.put(funString, varArray);
			} else {
				rej();
			}
			currToken = sc.nextLine();
		} else if (currToken.equals("[")) {
			currToken = sc.nextLine();
			varArray[1] = "array";
			varArray[2] = currToken;
			varArray[3] = String.valueOf(currScope);
			checkNUM();
			if(!varMap.containsKey(funString)){
				varMap.put(funString, varArray);	
			} else {
				rej();
			}
			if(!currToken.equals("]")) {
				rej();
			}
			currToken = sc.nextLine();
			if(!currToken.equals(";")) {
				rej();
			}	
			currToken = sc.nextLine();
		} else {
			rej();
		}
		if(ScopeArray.size() == 0) {
			ScopeArray.add(currScope,varMap);					
		} else {
			ScopeArray.set(currScope, varMap);
		}
		//System.out.println(ScopeArray);
		return;
	}

	private static void typeSpecifier() {
		if(currToken.equals("K: int")){
			currToken = sc.nextLine();
		} else if(currToken.equals("K: void")){	
			
			currToken = sc.nextLine();
		} else {
			rej();
		}
		return;
	}	
	
	private static int scopeCheck(String func) {
		foundInParams = false;
		boolean found = false;
		int i=0;
		if(!(ScopeArray.size() == currScope)) {
			for(i = currScope+1; i > 0; i--) {
				if(ScopeArray.get(i-1) != null && ScopeArray.get(i-1).containsKey(func)) {
					found = true;
					break;
				}
			}			
		}
		if(!(ScopeArray.size() != currScope)) {
			for(i = currScope; i > 0; i--) {
				if(ScopeArray.get(i-1) != null && ScopeArray.get(i-1).containsKey(func)) {
					found = true;
					break;
				}
			}			
		}
		for(int j = 1; j<params.size();j=j+2) {
			if(params.get(j).equals(func)) {
				found = true;
				i = j;
				foundInParams = true;
				break;
			}
		}
		if(!found) {
			System.out.println("Reject in scope check");
			rej();
		}
		return i;
	}
	
	private static void arrayCheck(int i, String id) {
		if(foundInParams) {
			if(currToken.equals("=")) {
				if(!params.get(i-1).equals("K: int")) {
					System.out.println("Reject in arraycheck");
					rej();
				}	
			} else if(currToken.equals("[")) {
				if(!params.get(i-1).equals("array")) {
					System.out.println("Reject in arraycheck");
					rej();
				}	
			}
		} else {
			if(currToken.equals("=")) {
				if(!ScopeArray.get(i-1).get(id)[1].equals("K: int")) {
					System.out.println("Rejected in arraycheck");
					rej();
				}
			} else if(currToken.equals("[")) {
				if(!ScopeArray.get(i-1).get(id)[1].equals("array")) {
					System.out.println("Rejected in arraycheck");
					rej();
				}
			}
		} 
	}

	private static void rej() {
		System.out.println("REJECT");
		System.exit(0);
	}
}