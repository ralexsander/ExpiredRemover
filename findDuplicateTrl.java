//usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS info.picocli:picocli:4.5.0

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.*;

@Command(name = "findDuplicateTrl", mixinStandardHelpOptions = true, version = "findDuplicateTrl 0.1",
        description = "Analisa arquivos .xml em busca de UUIDs repetidos.")
class findDuplicateTrl implements Callable<Integer> {

    @Parameters(index = "0", description = "O caminho da pasta onde os arquivos .xml estão localizados.")
    private File directory;

    private static final Pattern UUID_PATTERN = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");

    public static void main(String... args) {
        int exitCode = new CommandLine(new findDuplicateTrl()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("O caminho fornecido não existe ou não é um diretório.");
            return 1;
        }

        Files.walk(directory.toPath())
            .filter(Files::isRegularFile)
            .filter(p -> p.toString().endsWith(".xml"))
            .forEach(this::processFile);

        return 0;
    }

    private void processFile(Path file) {
        Map<String, Integer> uuidCounts = new HashMap<>();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                Matcher matcher = UUID_PATTERN.matcher(line);
                while (matcher.find()) {
                    String uuid = matcher.group();
                    uuidCounts.put(uuid, uuidCounts.getOrDefault(uuid, 0) + 1);
                }
            }
        } catch (Exception e) {
            System.err.println("Arquivo não encontrado: " + file);
        }

        uuidCounts.forEach((uuid, count) -> {
            if (count > 1) {
                System.out.printf("UUID repetido encontrado no arquivo %s: %s (ocorrências: %d)%n", file, uuid, count);
            }
        });
    }
}

