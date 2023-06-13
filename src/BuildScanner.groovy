import groovy.io.FileType
import org.apache.commons.io.FileUtils

import java.text.SimpleDateFormat

// Scan files of given type, recording latest date. Upon re-scan, copy any files with later date and reset date.

class BuildScanner {
    static final String MENU = "1. Auto diff/compile/test/war/deploy \n" +
            "2. Auto diff only \n"
    static final String SRC_APP_DIR = "/home/fishribs/IdeaProjects/fm/src/main/webapp"
    static final String DST_APP_DIR = "/opt/tomcat/webapps/ROOT"
    static boolean FIRST_SCAN = true
    static boolean SUBSEQUENT_SCAN = false
    static FORMATTER = new SimpleDateFormat("MM/dd/yyy HH:mm:ss.SSS Z")

    static void main(String[] args) {
        BuildScanner scanner = new BuildScanner(SRC_APP_DIR, DST_APP_DIR)
        println("latest date: " + scanner.getLatestDate())
        scanner.menu()
    }

    String srcDir, destDir
    long lastModified = 0L
    Set<File> laterFiles = new HashSet<>()

    BuildScanner(String srcDir, String destDir) {
        this.srcDir = srcDir
        this.destDir = destDir
        scanDir(srcDir, FIRST_SCAN)
//        if (anyLaterFiles) {
//
//        }
    }

    def rescan() {
        scanDir(srcDir, SUBSEQUENT_SCAN)
        laterFiles.stream().forEach {
            File file -> {
                String stem = file.path.replace(SRC_APP_DIR, "").replace(file.name, "")
                System.out.println("Copying file: " + file.name + " to " + destDir + stem + ".")
                String[] cmd = ["/bin/bash", "-c","echo | sudo -S cp " + file.path + " " + destDir + stem + "."]
                Process p = Runtime.getRuntime().exec(cmd)
                BufferedReader is = new BufferedReader(new InputStreamReader(p.getErrorStream(  )));
                def line
                while ((line = is.readLine(  )) != null)
                    System.out.println(line);
                is = new BufferedReader(new InputStreamReader(p.getInputStream(  )));
                while ((line = is.readLine(  )) != null)
                    System.out.println(line);
                //FileUtils.copyFileToDirectory(file, new File(destDir + stem))
            }
        }
    }

    def scanDir(String dir, boolean firstScan) {
        File rootDir = new File(dir)

        rootDir.eachFileRecurse (FileType.DIRECTORIES) {file ->
            scanFile(file, firstScan)
        }
    }

    def scanFile(File dir, boolean firstScan) {
        //System.out.println("Scanning directory: " + dir.path)
        dir.eachFileMatch(FileType.FILES, ~/^.+.jsp$/, { File file ->
            //System.out.println("Examining: " + file.path)
            if (file.lastModified() > lastModified) {
                if (firstScan) {
                    lastModified = file.lastModified()
                } else {
                    laterFiles.add(file)
                    //System.out.println("Found file: " + file.path)
                }
            }
        })
    }

    def getLatestDate() {
        return FORMATTER.format(new Date(lastModified))
    }

    def menu() {
        System.out.println(MENU)
        System.out.println("Your choice?")
        Scanner scanner = new Scanner(System.in)
        int choice = -1
        choice = scanner.nextInt()
        while (choice != 0) {
            switch (choice) {
                case 1: rescan(); break;
            }
            System.out.println("Your choice?")
            choice = scanner.nextInt()
        }
        return
    }
}
