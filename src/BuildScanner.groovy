import groovy.io.FileType

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

    String srcDir, dstDir
    long lastModified = 0L
    Set<File> laterFiles = new HashSet<>()

    BuildScanner(String srcDir, String destDir) {
        this.srcDir = srcDir
        scanDir(srcDir, FIRST_SCAN)
//        if (anyLaterFiles) {
//
//        }
    }

    def rescan() {
        scanDir(srcDir, SUBSEQUENT_SCAN)
        laterFiles.stream().forEach { File file -> { System.out.println(file.path) } }
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
            if (file.lastModified() > lastModified) {
                if (firstScan) {
                    lastModified = file.lastModified()
                } else {
                    laterFiles.add(file)
                    System.out.println("Found file: " + file.path)
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
