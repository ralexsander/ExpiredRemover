///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS info.picocli:picocli:4.7.1
//DEPS net.sf.jasperreports:jasperreports:6.3.1
//DEPS net.sf.barcode4j:barcode4j:2.1
//JAVA 11

import java.io.File;
import java.util.concurrent.Callable;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlWriter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 *  Check for expired files to be removed.
 * 	@author Ricardo Santana
 */
@Command(name = "jasperTools", mixinStandardHelpOptions = true, version = "jasperTools 0.1", description = "This command-line will compile jasper files")
class jasperTools implements Callable<Integer> {
	@Option(names = {"--path", "-p"}, description = "file path")
	String path;        // keep the last n yearly files
	@Option(names = {"--file", "-f"}, description = "file")
	String file;        // keep the last n yearly files

	public static void main(String[] args) {
		CommandLine commandLine = new CommandLine(new jasperTools());
        commandLine.execute(args);
	}	//	main
	
	public Integer call () throws Exception {
		if ((path == null || path.isBlank()) && (file == null || file.isBlank()))
			throw new Exception ("Invalid path or file, please input a valid path or file");

		//	List all files and sub-folders
		if (file != null && !file.isBlank()) {
			File toProcess = new File(file);
			if (!toProcess.exists() || !toProcess.isFile())
				throw new Exception ("File does not exists");

			processFile (toProcess);
		}
		else if (path != null && !path.isBlank()) {
			File toProcess = new File(path);
			if (!toProcess.exists() || !toProcess.isDirectory())
				throw new Exception ("Path does not exists or it's not a valid directory");

			processFolder (toProcess);
		}
		
		return 0;
	}	//	call

	private void processFile (File file) throws Exception {
		String fileName = file.getName();

		if (fileName.endsWith(".jrxml"))
			JasperCompileManager.compileReportToFile(fileName, fileName.replace(".jrxml", ".jasper"));
		else if (fileName.endsWith(".jasper")) {
			JasperReport report = (JasperReport) JRLoader.loadObject(file);
        	JRXmlWriter.writeReport(report, fileName.replace(".jasper", ".jrxml"), "UTF-8");
		}
		else
			throw new Exception ("File not supported: " + fileName);
	}	//	processFile

	private void processFolder (File folder) throws Exception {
		System.out.println("processing: " + folder);
		for (File file : folder.listFiles()) {
			String fileName = file.getName();
			if (file.isFile() && (fileName.endsWith(".jasper") || fileName.endsWith(".jrxml")))
				processFile(file);
			else if (file.isDirectory())
				processFolder(file);
		}
	}	//	processFolder
}	//	compressTool
