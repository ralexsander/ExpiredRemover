///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS info.picocli:picocli:4.7.1
//DEPS org.apache.commons:commons-compress:1.23.0
//JAVA 11

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Visibility;
import picocli.CommandLine.Option;

/**
 *  Check for expired files to be removed.
 * 	@author Ricardo Santana
 */
@Command(name = "compressTool", mixinStandardHelpOptions = true, version = "compressTool 0.1", description = "This command-line will delete old files based on a rule.")
class compressTool implements Callable<Integer> {
	@Option(names = {"--path", "-p"}, description = "file path", required = true)
	String path;        // keep the last n yearly files
	@Option(names = {"--out", "-o"}, description = "output file path", required = true)
	String out;        // keep the last n yearly files
	@Option(names = {"--preffix"}, description = "file preffix", defaultValue = "")
	String preffix;     // keep the last n yearly files
	@Option(names = {"--suffix"}, description = "file suffix", defaultValue = "")
	String suffix;      // keep the last n yearly files
	@Option(names = {"--pattern"}, description = "pattern of date", defaultValue = "yyyyMM", showDefaultValue = Visibility.ALWAYS)
	String pattern;     // keep the last n files
	@Option(names = {"--before"}, description = "before a given date <yyyy-MM-dd>", showDefaultValue = Visibility.ALWAYS)
	Date before;    		// keep the last n yearly files
	@Option(names = {"--delete"}, description = "delete after compress files", defaultValue = "false", showDefaultValue = Visibility.ALWAYS)
	boolean delete;    		// delete after compress files
	
    //	Classified files by date pattern
	Map<String,List<Path>> classified = new LinkedHashMap<String,List<Path>>();

	public static void main(String[] args) {
		CommandLine commandLine = new CommandLine(new compressTool());
        commandLine.execute(args);
	}	//	main
	
	public Integer call () throws Exception {
		if (path.isBlank())
			throw new Exception ("Invalid path, please input a valid path");
		File folder = new File(path);
		if (!folder.exists() || !folder.isDirectory())
			throw new Exception ("Path does not exists or it's not a valid directory");

		File outFile = new File (out);
		if (outFile.exists() && outFile.isFile())
			throw new Exception ("Out path should be a directory");

		//	List all files and sub-folders
		listFiles (folder);
		
		//	Save GZ files
		for (String key : classified.keySet()) {
			createTarGzipFiles (classified.get(key), new File(out + File.separator + key + ".tgz").toPath());
		}

		//	Delete files
		if (delete)
			for (List<Path> file : classified.values()) {
				file.stream().map(Path::toFile).forEach(File::delete);
			}

		return 0;
	}	//	call

	private void processFile (File file) {
		Timestamp modified = new Timestamp (file.lastModified());
		String fileName = file.getName();

		//	Skip files after certain date
		if (before != null && !modified.before(before))
			return;

		String key = fileName;
		key = getKey (modified);

		List<Path> list = classified.get(key);
		if (list == null) {
			list = new ArrayList<Path>();
			classified.put(key, list);
		}
		
		list.add(file.toPath());
	}	//	processFile

	private void listFiles (File folder) {
		System.out.println("processing: " + folder);
		for (File file : folder.listFiles()) {
			String fileName = file.getName();
			if (file.isFile() && fileName.startsWith(preffix) && fileName.endsWith(suffix)) {
				processFile(file);
			}
			else if (file.isDirectory())
				listFiles(file);
		}
	}	//	listFiles
	
	private String getKey (Timestamp ts) {
		SimpleDateFormat f = new SimpleDateFormat (pattern);
		return f.format(ts);
	}	//	getKey

	public static void createTarGzipFiles (List<Path> paths, Path output) throws IOException {
		System.out.println("compressing: " + output);
        try (OutputStream fOut = Files.newOutputStream(output);
             BufferedOutputStream buffOut = new BufferedOutputStream(fOut);
             GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(buffOut);
             TarArchiveOutputStream tOut = new TarArchiveOutputStream(gzOut)) {

            for (Path path : paths) {
                if (!Files.isRegularFile(path)) {
                    continue;
                }

                TarArchiveEntry tarEntry = new TarArchiveEntry(path.toFile(), path.getFileName().toString());
                tOut.putArchiveEntry(tarEntry);

                // copy file to TarArchiveOutputStream
                Files.copy(path, tOut);
                tOut.closeArchiveEntry();
            }

            tOut.finish();
        }
    }	//	createTarGzipFiles
}	//	compressTool