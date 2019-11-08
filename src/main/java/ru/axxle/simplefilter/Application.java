package ru.axxle.simplefilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Application {

	private static final String LINE_DELIMITER = "\r\n";

	private static final Pattern[] ALL_PATTERNS = new Pattern[] {
		Pattern.compile("^.*with bind values\\s*:\\sselect.*$"),
		Pattern.compile("^.*Fetched result\\s*:\\s\\+-.*$"),
		Pattern.compile("^.*LoggerListener\\s*:\\s\\+-.*$"),
		Pattern.compile("^.*: \\|.*$"),
		Pattern.compile("^.*with bind values\\s*:\\sinsert.*$"),
		Pattern.compile("^.*with bind values\\s*:\\sdelete.*$")
	};

	public static void main(String[] args) {
		if (args.length > 0) {
			List<String> fileLines = parseFile(new File(args[0]));
			boolean[] filterArr = fillFilter(ALL_PATTERNS, new boolean[fileLines.size()], fileLines);
			if (args.length > 1) {
				String outputFilePath = args[1];
				printToFile(fileLines, filterArr, outputFilePath);
			} else {
				printWithFilter(fileLines, filterArr);
				System.out.println(LINE_DELIMITER + "The result is displayed on the console, because not specified <outputFilePath>. See README.md");
			}
		} else {
			System.out.println("Need path to file for find, <inputFilePath> not specified. See README.md");
		}
	}

	public static boolean[] fillFilter(Pattern[] patterns, boolean[] filterArr, List<String> fileLines) {
		for (Pattern pattern : patterns) {
			filterArr = checkLinesByPattern(fileLines, filterArr, pattern);
		}
		return filterArr;
	}

	public static void printWithFilter(List<String> fileLines, boolean[] filterArr) {
		for (int i = 0; i < fileLines.size(); i++) {
			if (filterArr[i]) {
				System.out.println(fileLines.get(i));
			}
		}
	}

	public static List<String> parseFile(File file){
		List<String> lines = new LinkedList<String>();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}

	public static boolean[] checkLinesByPattern (List<String> fileLines, boolean[] filterArr, Pattern pattern) {
		for (int i = 0; i < fileLines.size(); i++) {
			Matcher matcher = pattern.matcher(fileLines.get(i));
			if(matcher.find()) {
				filterArr[i] = true;
			}
		}
		return filterArr;
	}

	public static void printToFile(List<String> fileLines, boolean[] filterArr, String outputFilePath){
		try(FileWriter writer = new FileWriter(outputFilePath)) {
			for (int i = 0; i < fileLines.size(); i++) {
				if (filterArr[i]) {
					writer.write(fileLines.get(i));
					writer.write(LINE_DELIMITER);
				}
			}
		}
		catch(IOException ex){
			System.out.println(ex.getMessage());
		}
	}
}
