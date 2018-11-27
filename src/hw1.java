
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;


class Fasta {
	String id;
	String str;
	Fasta(String id1, String str1){
		id = id1;
		str = str1;
	}
}

class HelperClass implements Comparable<HelperClass> {
	String id1;
	String id2;
	int score;
	String a2;
	String b2;
	HelperClass(String i1, String i2, String str1, String str2, int sco){
		id1 = i1;
		id2 = i2;
		a2 = str1;
		b2 = str2;
		score = sco;
	}
	
	@Override
	public int compareTo(HelperClass hc) {  //覆写compareTo方法实现排序规则的应用
		if(this.score > hc.score){
			return -1;
			}
		else if(this.score < hc.score){
			return 1;
		}
		else return 0;
	}
}

public class hw1 {
	static int ini_i = 1;
	static int ini_j = 1;
	public static void main(String[] args) throws IOException{

		/*
		int method = Integer.parseInt(args[0]);
		if(method != 1 && method != 2 && method != 3){
			System.out.println("Function not finished yet");
			return;
		}
		String queryPath = args[1];
		String dataPath = args[2];
		String alphabetFile = args[3];
		String scoreMatrixFile = args[4];
		int TopRank = Integer.parseInt(args[5]);
		int gap_penalty = Integer.parseInt(args[6]);
		*/
		
		//for test!!!!!	
		
		int method = 3;
		String queryPath = "/Users/huchenyang/Desktop/query.txt";
		String dataPath = "/Users/huchenyang/Desktop/database.txt";
		int TopRank = 1;
		int gap_penalty = -3;
		String alphabetFile = "/Users/huchenyang/Desktop/alphabet.txt";
		String scoreMatrixFile = "/Users/huchenyang/Desktop/scoringmatrix.txt";
		
		
		StringBuffer sb_Query= new StringBuffer("");
		StringBuffer sb_Database= new StringBuffer("");
		FileReader read_Query = new FileReader(queryPath);
		FileReader read_Database = new FileReader(dataPath);		
		BufferedReader br = new BufferedReader(read_Query);
		BufferedReader br2 = new BufferedReader(read_Database);
		StringBuffer a2 = new StringBuffer("");
		StringBuffer b2 = new StringBuffer("");
		
		String str_Query = null;
		String str_Database = null;
		String id = "";	
		
		HashMap<Character, Integer> alphabet = alphabetReader(alphabetFile);
		int[][] scorematrix = scorematrixParser(scoreMatrixFile);

		//load all queries in list
		List<Fasta> queries = new ArrayList<Fasta>();
		while((str_Query = br.readLine()) != null) {
			if(!str_Query.startsWith(">")) 
				sb_Query.append(str_Query);
			else {
				if(sb_Query.length() != 0) {
					String a = sb_Query.toString();
					Fasta ft = new Fasta(id, a);
					queries.add(ft);
				}
				id = "";
				int i = 5;
				while(str_Query.charAt(i) != ' ') {
					id = id + str_Query.charAt(i);	
					i++;
				}
				sb_Query.setLength(0);
			}
		}
		
		//add the last query statement
		String temp = sb_Query.toString();
		Fasta t = new Fasta(id, temp);
		queries.add(t);
		
		//load all from Database into list
		List<Fasta> database = new ArrayList<Fasta>();
		while((str_Database = br2.readLine()) != null) {
			if(!str_Database.startsWith(">")) 
				sb_Database.append(str_Database);
			else {
				if(sb_Database.length() != 0) {
					String a = sb_Database.toString();
					Fasta ft = new Fasta(id, a);
					database.add(ft);
				}
				id = "";
				int i = 5;
				while(str_Database.charAt(i) != ' ') {
					id = id + str_Database.charAt(i);	
					i++;
				}
				sb_Database.setLength(0);
			}
		}
		
		// load the last one record from Database
		temp = sb_Database.toString();
		t = new Fasta(id,temp);
		database.add(t);
		//Write file
		List<HelperClass> list = new ArrayList<HelperClass>();
		File f = new File(method + ".txt");
        FileOutputStream fop = new FileOutputStream(f);
        OutputStreamWriter writer = new OutputStreamWriter(fop, "UTF-8");
        
        switch(method) {
        		//Global alignment
        		case 1:{
        			for(int i = 0; i < queries.size(); i++) {
        				//long startTime = System.currentTimeMillis();
        				for(int j = 0; j < database.size(); j++) {
        					String a1 = queries.get(i).str;
        					String b1 = database.get(j).str;
        					int penalty = globalAlignment(a1, b1, alphabet, gap_penalty, scorematrix, a2, b2);
        					HelperClass hc = new HelperClass(queries.get(i).id, database.get(j).id, a2.toString(), b2.toString(), penalty);
        					list.add(hc);
        				}
        				Collections.sort(list);
        				//long endTime = System.currentTimeMillis();
        				for(int index = 0; index < TopRank; index++) {
        					//test performance
        					//writer.append(queries.get(i).str.length() + " " + (endTime - startTime) + "\r\n");
        					
        					writer.append("Score: " + list.get(index).score + "\r\n");
        					writer.append("id " + list.get(index).id1 + " " + ini_i + " " + list.get(index).a2 + "\r\n");
        					writer.append("id " + list.get(index).id2 + " " + ini_j + " " + list.get(index).b2 + "\r\n" + "\n");
        				}
        				list.clear();
        			}
        		}
        		//local alignment
        		case 2:{
        			for(int i = 0; i < queries.size(); i++) {
        				//long startTime=System.currentTimeMillis();
        				for(int j = 0; j < database.size(); j++) {
        					String a1 = queries.get(i).str;
        					String b1 = database.get(j).str;
        					int penalty = localAlignment(a1, b1, alphabet, gap_penalty, scorematrix, a2, b2);
        					HelperClass hc = new HelperClass(queries.get(i).id, database.get(j).id, a2.toString(), b2.toString(), penalty);
        					list.add(hc);
        				}
        				Collections.sort(list);
        				//long endTime=System.currentTimeMillis();
        				for(int index = 0; index < TopRank; index++) {
        					//test performance:
        					//writer.append(queries.get(i).str.length() + " " + (endTime - startTime) + "\r\n");
        					writer.append("Score: " + list.get(index).score + "\r\n");
        					writer.append("id " + list.get(index).id1 + " " + ini_i + " " + list.get(index).a2 + "\r\n");
        					writer.append("id " + list.get(index).id2 + " " + ini_j + " " + list.get(index).b2 + "\r\n" + "\n");
        				}
        				list.clear();
        			}
        		}
        		//dovetail alignment
        		case 3:{
        			for(int i = 0; i < queries.size(); i++) {
        				//long startTime=System.currentTimeMillis();
        				for(int j = 0; j < database.size(); j++) {
        					String a1 = queries.get(i).str;
        					String b1 = database.get(j).str;
        					int penalty = dovetailAlignment(a1, b1, alphabet, gap_penalty, scorematrix, a2, b2);
        					HelperClass hc = new HelperClass(queries.get(i).id, database.get(j).id, a2.toString(), b2.toString(), penalty);
        					list.add(hc);
        				}
        				Collections.sort(list);
        				//long endTime=System.currentTimeMillis();
        				for(int index = 0; index < TopRank; index++) {
        					//Test of performance:
        					//writer.append(queries.get(i).str.length() + " " + (endTime - startTime) + "\r\n"); 					
        					writer.append("Score: " + list.get(index).score + "\r\n");
        					writer.append("id " + list.get(index).id1 + " " + ini_i + " " + list.get(index).a2 + "\r\n");
        					writer.append("id " + list.get(index).id2 + " " + ini_j + " " + list.get(index).b2 + "\r\n" + "\n");
        				}
        				list.clear();
        			}
        		}
        		default: 
        }
        writer.close();
        fop.close();
		br.close();
		br2.close();
		read_Query.close();
		read_Database.close();
	}
	
	public static HashMap<Character, Integer> alphabetReader(String arg)throws IOException {
		HashMap<Character, Integer> hm = new HashMap<Character, Integer>();
		BufferedReader br = new BufferedReader(new FileReader(arg));
		String s = br.readLine();
		char[] c = s.toCharArray();
		for (int i = 0; i < c.length; i++)
			hm.put(Character.toLowerCase(c[i]), i);
		return hm;
	}
	
	private static int[][] scorematrixParser(String scoreMatrixFile) {
		File file = new File(scoreMatrixFile);
		Scanner in = null;
		int[][] arr = new int[4][4];
		int i = 0, j = 0;
		try {
			in = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while(in.hasNextLine()) {
			arr[i][j] = in.nextInt();
			j++;
			if(j == 4) {
				i++;
				j=0;
			}
		}
		return arr;
	}
	
	static private int globalAlignment(String a, String b, HashMap<Character, Integer> alphabet,int alpha_gap, int scorematrix[][], StringBuffer a_aligned, StringBuffer b_aligned){
		int n = a.length();
		int m = b.length();
		int[][] A = new int[n+1][m+1];
		
		a_aligned.setLength(0);
		b_aligned.setLength(0);
		
		for(int i = 0; i <= m; ++i)
			A[0][i] = alpha_gap * i;
		for(int i = 0; i <= n; ++i)
			A[i][0] = alpha_gap * i;
		
		for(int i = 1; i <= n; ++i)
			for(int j = 1; j <= m; ++j) {
				A[i][j] = Math.max(Math.max(A[i - 1][j - 1] + scorematrix[alphabet.get(a.charAt(i - 1))][alphabet.get(b.charAt(j - 1))], 
									A[i - 1][j] + alpha_gap), A[i][j-1] + alpha_gap);
			}
		int j = m;
		int i = n;
		for(; i >= 1 && j >= 1; --i) {
			char x_i = a.charAt(i - 1);
			char y_j = b.charAt(j - 1);
			if(A[i][j] == A[i - 1][j - 1] + scorematrix[alphabet.get(a.charAt(i - 1))][alphabet.get(b.charAt(j - 1))]){
				a_aligned.insert(0, x_i);
				b_aligned.insert(0, y_j);
				--j;
			}
			else if(A[i][j] == A[i - 1][j] + alpha_gap) {
				a_aligned.insert(0, x_i);
				b_aligned.insert(0, '.');
			}
			else {
				a_aligned.insert(0, '.');
				b_aligned.insert(0, y_j);
				--j;
			}
		}
		while (i >= 1 && j < 1){
			a_aligned.insert(0, a.charAt(i - 1));
			b_aligned.insert(0, '.');
			--i;
		    }    
		while (j >= 1 && i < 1){
			a_aligned.insert(0, '.');
			b_aligned.insert(0, b.charAt(j - 1));
			--j;
		}
		return A[n][m];		
	}
	
	static private int localAlignment(String a, String b, HashMap<Character, Integer> alphabet,int alpha_gap, int scorematrix[][], StringBuffer a_aligned, StringBuffer b_aligned){
		int n = a.length();
		int m = b.length();
		int[][] A = new int[n+1][m+1];
		
		a_aligned.setLength(0);
		b_aligned.setLength(0);
		
		for(int i = 0; i <= m; ++i)
			A[0][i] = 0;
		for(int i = 0; i <= n; ++i)
			A[i][0] = 0;
		
		int max = 0, max_i = 1, max_j = 1;//, ini_i = 0, ini_j = 0;
		boolean isFirst = true;
		
		for(int i = 1; i <= n; ++i)
			for(int j = 1; j <= m; ++j) {
				A[i][j] = Math.max(Math.max(A[i - 1][j - 1] + scorematrix[alphabet.get(a.charAt(i - 1))][alphabet.get(b.charAt(j - 1))], 
									A[i - 1][j] + alpha_gap), A[i][j-1] + alpha_gap);
				if(A[i][j] < 0)
					A[i][j] = 0;
				else
					if(isFirst)
					{
						ini_i = i;
						ini_j = j;
						isFirst = false;
					}
				
				if(A[i][j] > max) {
					max = A[i][j];
					max_i = i;
					max_j = j;
				}		
			}
		
		int i = max_i;
		int j = max_j;
		
		for(; i >= ini_i && j >= ini_j; --i) {
			char x_i = a.charAt(i - 1);
			char y_j = b.charAt(j - 1);
			if(A[i][j] == A[i - 1][j - 1] + scorematrix[alphabet.get(a.charAt(i - 1))][alphabet.get(b.charAt(j - 1))]){
				a_aligned.insert(0, x_i);
				b_aligned.insert(0, y_j);
				--j;
			}
			else if(A[i][j] == A[i - 1][j] + alpha_gap) {
				a_aligned.insert(0, x_i);
				b_aligned.insert(0, '.');
			}
			else {
				a_aligned.insert(0, '.');
				b_aligned.insert(0, y_j);
				--j;
			}
		}
		while (i >= 1 && j < 1){
			a_aligned.insert(0, a.charAt(i - 1));
			b_aligned.insert(0, '.');
			--i;
		    }    
		while (j >= 1 && i < 1){
			a_aligned.insert(0, '.');
			b_aligned.insert(0, b.charAt(j - 1));
			--j;
		}
		return A[max_i][max_j];		
	}

	static private int dovetailAlignment(String a, String b, HashMap<Character, Integer> alphabet,int alpha_gap, int scorematrix[][], StringBuffer a_aligned, StringBuffer b_aligned){
		int n = a.length();
		int m = b.length();
		int[][] A = new int[n+1][m+1];
		
		a_aligned.setLength(0);
		b_aligned.setLength(0);
		
		for(int i = 0; i <= m; ++i)
			A[0][i] = 0;
		for(int i = 0; i <= n; ++i)
			A[i][0] = 0;
		
		for(int i = 1; i <= n; ++i)
			for(int j = 1; j <= m; ++j) {
				A[i][j] = Math.max(Math.max(A[i - 1][j - 1] + scorematrix[alphabet.get(a.charAt(i - 1))][alphabet.get(b.charAt(j - 1))], 
									A[i - 1][j] + alpha_gap), A[i][j-1] + alpha_gap);
			}
		
		int max = Integer.MIN_VALUE;
		
		int max_i = n, max_j = m;
		
		for(int i = 0; i < n; i++) {
			if(A[i][m] > max) {
				max = A[i][m];
				max_i = i;
				max_j = m;
			}
		}
		for(int j = 0; j < m; j++) {
			if(A[n][j] > max) {
				max = A[n][j];
				max_i = n;
				max_j = j;
			}
		}
		
		int i = max_i;
		int j = max_j;
		
		
		for(; i >= 1 && j >= 1; --i) {
			char x_i = a.charAt(i - 1);
			char y_j = b.charAt(j - 1);
			if(A[i][j] == A[i - 1][j - 1] + scorematrix[alphabet.get(a.charAt(i - 1))][alphabet.get(b.charAt(j - 1))]){
				a_aligned.insert(0, x_i);
				b_aligned.insert(0, y_j);
				--j;
			}
			else if(A[i][j] == A[i - 1][j] + alpha_gap) {
				a_aligned.insert(0, x_i);
				b_aligned.insert(0, '.');
			}
			else {
				a_aligned.insert(0, '.');
				b_aligned.insert(0, y_j);
				--j;
			}
		}
		ini_i = i;
		ini_j = j;
		return A[max_i][max_j];		
	}
}

