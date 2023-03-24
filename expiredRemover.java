///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS info.picocli:picocli:4.7.1

import java.io.File;
import java.io.FileFilter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Visibility;
import picocli.CommandLine.Option;

/**
 *  Check for expired files to be removed.
 * 	@author Ricardo Santana
 */
@Command(name = "expiredRemover", mixinStandardHelpOptions = true, version = "expiredRemover 0.1", description = "This command-line will delete old files based on a rule.")
class expiredRemover implements Callable<Integer> {
	@Option(names = {"-p", "--path"}, description = "file path", required = true)
	String path;        // keep the last n yearly files
	@Option(names = {"--preffix"}, description = "file preffix", defaultValue = "")
	String preffix;     // keep the last n yearly files
	@Option(names = {"--suffix"}, description = "file suffix", defaultValue = "")
	String suffix;      // keep the last n yearly files
	@Option(names = {"--keep-last"}, description = "keep last N files", defaultValue = "1", showDefaultValue = Visibility.ALWAYS)
	int	last;           	// keep the last n files
	@Option(names = {"--keep-daily"}, description = "keep last N daily files", defaultValue = "7", showDefaultValue = Visibility.ALWAYS)
	int	daily;           	// keep the last n daily files
	@Option(names = {"--keep-weekly"}, description = "keep last N weekly files", defaultValue = "5", showDefaultValue = Visibility.ALWAYS)
	int	weekly;          	// keep the last n weekly files
	@Option(names = {"--keep-monthly"}, description = "keep last N monthly files", defaultValue = "12", showDefaultValue = Visibility.ALWAYS)
	int	mon;         	// keep the last n monthly files
	@Option(names = {"--keep-yearly"}, description = "keep last N yearly files", defaultValue = "5", showDefaultValue = Visibility.ALWAYS)
	int	yearly;          	// keep the last n yearly files
	@Option(names = {"--dry-run"}, description = "dry run", fallbackValue = "true", defaultValue = "true", showDefaultValue = Visibility.ALWAYS)
	boolean dryRun;    		// keep the last n yearly files
	
	public static void main(String[] args) {
		CommandLine commandLine = new CommandLine(new expiredRemover());
        commandLine.execute(args);
	}	//	main
	
	public Integer call () throws Exception {
		if (path.isBlank())
			throw new Exception ("Invalid path, please input a valid path");
		File folder = new File(path);
		if (!folder.exists() || !folder.isDirectory())
			throw new Exception ("Path does not exists or it's not a valid directory");
		
		File[] files = folder.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				String fileName = pathname.getName();
				return pathname.isFile() && fileName.startsWith(preffix) && fileName.endsWith(suffix);
			}
		});
		
        Arrays.sort (files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                return Long.compare(f2.lastModified(), f1.lastModified());
            }
        });
        
		MyHashMap<String,String> last = new MyHashMap<String,String>(this.last);
		MyHashMap<String,String> daily = new MyHashMap<String,String>(this.daily);
		MyHashMap<String,String> weekly = new MyHashMap<String,String>(this.weekly);
		MyHashMap<String,String> monthly = new MyHashMap<String,String>(this.mon);
		MyHashMap<String,String> yearly = new MyHashMap<String,String>(this.yearly);

		for (File arquivo : files) {
			if (arquivo.isFile()) {
				Timestamp modified = new Timestamp (arquivo.lastModified());
				String fileName = arquivo.getName();

				String key = fileName;
				last.put (key, fileName);

				key = convertDaily (modified);
				daily.put (key, fileName);
				
				key = convertWeekly (modified);
				weekly.put (key, fileName);
				
				key = convertMonthly (modified);
				monthly.put (key, fileName);
				
				key = convertYearly (modified);
				yearly.put (key, fileName);
			}
		}
		
		int count=0;
		
		for (File file : files) {
			System.out.print(file.getName());
			
			StringBuilder flags = new StringBuilder("");
			if (last.containsValue(file.getName()))
				flags.append(", last");
			if (daily.containsValue(file.getName()))
				flags.append(", daily");
			if (weekly.containsValue(file.getName()))
				flags.append(", weekly");
			if (monthly.containsValue(file.getName()))
				flags.append(", monthly");
			if (yearly.containsValue(file.getName()))
				flags.append(", yearly");
			
			//	No flags, delete
			if (!dryRun && flags.length() == 0) {
				if (file.delete())
					count++;
			}
			
			System.out.println(flags);
		}
		
		if (count>0)
			System.out.println("\n" + count + " file(s) deleted.\n");

		
		return 0;
	}
	
	private String convertDaily (Timestamp ts) {
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
		return f.format(ts);
	}	//	convertLast
	private String convertWeekly (Timestamp ts) {
		SimpleDateFormat f = new SimpleDateFormat("yyyyw");
		return f.format(ts);
	}	//	convertLast
	private String convertMonthly (Timestamp ts) {
		SimpleDateFormat f = new SimpleDateFormat("yyyyMM");
		return f.format(ts);
	}	//	convertLast
	private String convertYearly (Timestamp ts) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy");
		return f.format(ts);
	}	//	convertLast
}	//	ExpiredRemover

class MyHashMap<K,V> extends LinkedHashMap<String,String> {
	private static final long serialVersionUID = 2091240150002048943L;
	int initialCapacity = 0;
	public MyHashMap(int initialCapacity) {
        super(initialCapacity);
        this.initialCapacity = initialCapacity;
    }	//	MyHashMap
	@Override
	public String put(String key, String value) {
		String keep = get(key);
		if (keep != null || isFull())
			return keep;	//	Already inserted
		return super.put(key, value);
	}	//	put
	public int getMaxCapacity () {
		return initialCapacity;
	}	//	getMaxCapacity
	public boolean isFull () {
		return initialCapacity <= size();
	}	//	getMaxCapacity
}	//	MyHashMap