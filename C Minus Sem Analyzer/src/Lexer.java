import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Lexer {
	
	public Lexer(File input) {
		// declarations
		String line;
		Scanner sc = null;
		boolean comment = false;
		String[] keywords = { "while", "int", "void", "return", "if", "else", "float" };
		PrintWriter writer = null;
		
		

		// scan file
		try {
			File textFile = input;
			sc = new Scanner(textFile);
			writer = new PrintWriter("tokens.txt");	
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found");
		}

		// read file
		while (sc.hasNextLine()) {
			// read one line at a time
			line = sc.nextLine();
			// print input line
			if (!line.isEmpty()) {
//				System.out.println("Input: " + line);
			}

			for (int i = 0; i < line.length(); i++) {
				switch (line.charAt(i)) {
				case '/':
					if (i + 1 < line.length()) {
						if (line.charAt(i + 1) == '*') {
							comment = true;
							i++;
						} else if (line.charAt(i + 1) == '/') {
							i = line.length();
						} else {
							if (!comment) {
//							System.out.println("/");
							writer.println("/");
							}
						}
					} else {						
//						System.out.println("/");
						writer.println("/");
					}
					break;
				case '*':
					if (i + 1 < line.length()) {
						if (line.charAt(i + 1) == '/') {
							if (comment) {
								comment = false;
								i++;
							} else {
//								System.out.println("*");
								writer.println("*");
							}
						} else {
							if(!comment) {
//								System.out.println("*");	
								writer.println("*");
							}
						}
					} else {
						if (!comment) {
//							System.out.println("*");
							writer.println("*");
						}
					}
					break;
				case '+':
					if (!comment) {
//						System.out.println("+");
						writer.println("+");
					}
					break;
				case '-':
					if (!comment) {
//						System.out.println("-");
						writer.println("-");
					}
					break;
				case '=':
					if (!comment) {
						if (i + 1 < line.length()) {
							if (line.charAt(i + 1) == '=') {
//								System.out.println("==");
								writer.println("==");
								i++;
							} else {
//								System.out.println("=");
								writer.println("=");
							}
						} else {
//							System.out.println("=");
							writer.println("=");
						}
					}
					break;
				case ';':
					if (!comment) {
//						System.out.println(";");
						writer.println(";");
					}
					break;
				case ',':
					if (!comment) {
//						System.out.println(",");
						writer.println(",");
					}
					break;
				case '(':
					if (!comment) {
//						System.out.println("(");
						writer.println("(");
					}
					break;
				case ')':
					if (!comment) {
//						System.out.println(")");
						writer.println(")");
					}
					break;
				case '[':
					if (!comment) {
//						System.out.println("[");
						writer.println("[");
					}
					break;
				case ']':
					if (!comment) {
//						System.out.println("]");
						writer.println("]");
					}
					break;
				case '{':
					if (!comment) {
//						System.out.println("{");
						writer.println("{");
					}
					break;
				case '}':
					if (!comment) {
//						System.out.println("}");
						writer.println("}");
					}
					break;
				case '<':
					if (!comment) {
						if (i + 1 < line.length()) {
							if(line.charAt(i+1) == '=') {
//								System.out.println("<=");
								writer.println("<=");
								i++;
							} else {
//							System.out.println("<");
							writer.println("<");
							}
						}
					} break;
				case '>':
					if (!comment) {
						if (i + 1 < line.length()) {
							if(line.charAt(i+1) == '=') {
//								System.out.println(">=");
								writer.println(">=");
								i++;
							} else {
//							System.out.println(">");
							writer.println(">");
							}
						}
					}
					break;
				case '!':
					if (!comment) {
						if(i+1 < line.length()) {
							if(line.charAt(i+1) == '=') {
//								System.out.println("!=");
								writer.println("!=");
								i++;
							} else {
//								System.out.println("Error: !");
								writer.println("!");
								
							}							
						} else {
//							System.out.println("Error: !");
							writer.println("!");
						}					
					}
					break;
				default:
					if (!comment) {
						if(Character.isWhitespace(line.charAt(i))) {
							//DO NOTHING
						} else if(Character.isAlphabetic(line.charAt(i))) {
							String substring = buildString(i, line);
							i += substring.length()-1;
							boolean isKeyword = true;
							for(int j=0; j<keywords.length;j++) {
								if(substring.equals(keywords[j])) {
//									System.out.println("Keyword: " + substring);
									writer.println("K: " + substring);
									isKeyword = true;
									break;
								} else {
									isKeyword = false;
								}
							}
							if (!isKeyword) {
//								System.out.println("ID: " + substring);
								writer.println("ID: " + substring);
							}
							
						} else if(Character.isDigit(line.charAt(i))) {
							String substring = buildNum(i, line);
							i += substring.length()-1;
//							System.out.println("INT: " + substring);
							writer.println("INT: " + substring);
						} else {
//							System.out.println("Error: " + line.charAt(i));
							writer.println(line.charAt(i));
						}						
					}
				}
			}
		}	
		writer.println("$");
		writer.close();
	}
	
	public static String buildString(int i, String line) {
		int k = i;
		while(k < line.length()) {
			if(Character.isAlphabetic(line.charAt(k))) {
				k++;
			} else {
				return line.substring(i,k);
			}
		}
		return line.substring(i,k);
	}
	
	public static String buildNum(int i, String line) {
		int k = i;
		while(k < line.length()) {
			if(Character.isDigit(line.charAt(k))) {
				k++;
			} else {
				return line.substring(i,k);
			}
		}
		return line.substring(i,k);
	}
}
