package meelesh;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@NoArgsConstructor
public class ParseArgs {

    private String[] args;
    private boolean h = false;
    private boolean c = false;
    private boolean si = false;
    private Set<String> allPaths = new HashSet<>();
    private List<String> files = new ArrayList<>();

    public ParseArgs(String[] args) {
        this.args = args;
    }

    public List<String> parse() {
        if (!args[0].equals("du")) {
            System.out.println("Command '" + args[0] + "' not found, did you mean: command 'du' \nExample: du -h -c --si file1 file2 file3");
            System.exit(1);
        }

        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
                case ("-h") -> h = true;
                case ("-c") -> c = true;
                case ("--si") -> si = true;
                default -> sortFilesAndCatalogs(args[i]);
            }
        }
        findAllFiles();
        return files;
    }

    private void sortFilesAndCatalogs(String arg) {
        List<String> insideCatalog = searchInside(arg);
        allPaths.addAll(insideCatalog);
        insideCatalog.forEach(this::sortFilesAndCatalogs);
    }

    private List<String> searchInside(String catalog) {
        List<String> paths = new ArrayList<>();
        File[] fileList = getFileList(catalog);
        for(File file : fileList) {
            paths.add(catalog + "/" + file.getName());
        }
        return paths;
    }

    private File[] getFileList(String dirPath) {
        File dir = new File(dirPath);
        File[] fileList = dir.listFiles();
        return fileList == null? new File[] {}: fileList;
    }

    public boolean isFile(String fileString) {
        String pattern = "\\w\\.\\w";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(fileString);
        return m.find() && new File(fileString).isFile();
    }


    private void findAllFiles() {
        allPaths.stream().filter(this::isFile).forEach(filePath -> files.add(filePath));
    }
}