import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.body.AnnotationDeclaration
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.MemberValuePair
import com.github.javaparser.ast.expr.NormalAnnotationExpr
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr
import groovy.io.FileType
import com.github.javaparser.JavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.stmt.BlockStmt
import com.github.javaparser.ast.stmt.CatchClause
import com.github.javaparser.ast.stmt.Statement
import com.github.javaparser.ast.stmt.TryStmt
import com.github.javaparser.ast.visitor.VoidVisitorAdapter

/**
 * Used to search for various constructs within the codebase. Latest: RESTful service processors
 * 
 * @author jfruncek
 */
class ScanCodebase {

    static final String DIR = "C:/dev/git/vna2/Java"
    static Set<String> sourcePaths = new HashSet<String>()
    static Set<Clazz> classes = new TreeSet<Clazz>()

    public static void main(String[] args) {
        
        File rootDir = new File(DIR)

        rootDir.eachFileRecurse (FileType.DIRECTORIES) { file ->
            buildSourceDirs(file)
        }
        println 'SynapseVNA Rest APIs\n--------------------'
        sourcePaths.each {
            println '\nModule: '+ getModuleName(it)
            scanClass(new File(it))
        }
        System.exit(0)
        
        println 'Found ' + classes.size() + ' such classes'
        for (Clazz clazz : classes) {
            // visit and findStats the methods names
            println "Class: " + clazz.name 
            for (String stmt : clazz.blocks) {
                println stmt
            }
            //debug break;
        }
        
        
        System.exit(0)
    }

    static String getModuleName(String path) {
        int src = path.indexOf(File.separator + 'src')
        int mod = path.substring(0, src).lastIndexOf(File.separator)
        return path.substring(mod+1, src)
    }

    private static scanClass(File it) {
        it.eachFileRecurse(FileType.FILES) {
            if(it.name.endsWith('.java')) {
                //println 'Checking ' + it.getPath()

                FileInputStream i = new FileInputStream(it);
                CompilationUnit cu
                try {
                    // parse the file
                    cu = JavaParser.parse(i);
                } finally {
                    i.close();
                }

                ControllerVisitor visitor = new ControllerVisitor()
                visitor.visit(cu, null)
                if (visitor.isRestController) {
                    MethodVisitor mv = new MethodVisitor()
                    mv.visit(cu, null)
                }
            }
        }
    }

    static class AnnotationPropertiesVisitor extends VoidVisitorAdapter<String> {
        @Override
        public void visit(NormalAnnotationExpr n, String arg) {
            print "Method: " + arg + ' type: ' + n.getName() + ' '
            n.getPairs().each {
                print it.getName().toString() + ': ' + it.getValue().toString() + ' '
            }
            println()
            super.visit(n.getPairs(), arg);
        }
    }
    
    static class MethodVisitor extends VoidVisitorAdapter {
        @Override
        void visit(MethodDeclaration n, Object arg) {
            if (n.getAnnotationByName('RequestMapping').isPresent()) {
                n.accept(new AnnotationPropertiesVisitor(), n.getName().toString())
                super.visit(n, arg)
            }
        }
    }

    static class ControllerVisitor extends VoidVisitorAdapter {
        boolean isRestController = false

        @Override
        void visit(ClassOrInterfaceDeclaration n, Object arg) {
            //n.getAnnotations().each { println it.name }

            if (n.getAnnotationByName('Controller').isPresent() &&
                    n.getAnnotationByName('RequestMapping').isPresent()) {
                print '\nClass: ' + n.name + ' '
                Object anno = n.getAnnotationByName('RequestMapping').get()
                if (anno instanceof SingleMemberAnnotationExpr) {
                    println 'mapping: ' + ((SingleMemberAnnotationExpr) anno).getMemberValue().toString()
                } else if (anno instanceof NormalAnnotationExpr) {
                    def pairs = ((NormalAnnotationExpr) anno).getPairs()
                    String msg = ""
                    for (MemberValuePair pair : pairs) msg += pair.getValue().toString() + ' '
                    println 'mappings: ' + msg
                }

                isRestController = true
            }
        }
    }
    
    static class TryVisitor extends VoidVisitorAdapter {
        Boolean hasCatchOfInterest;
        def blocks = [];

        @Override
        public void visit(TryStmt clause, Object arg) {
            for (CatchClause cats : clause.catchs) {
                for (Statement s : cats.catchBlock.getStmts()) {
                    if (s.toString().contains('isDebugEnabled')) {
                        hasCatchOfInterest = true
                        blocks.add(clause.toString())
                    }
                }
            }
        }
    }
    
    static class TryCatchVisitor extends VoidVisitorAdapter {
        Boolean hasCatchOfInterest;
        def catc = [];
        
        @Override
        public void visit(CatchClause clause, Object arg) {
            
            for (Statement s : clause.catchBlock.getStmts()) {
                if (s.toString().contains('isDebugEnabled')) {
                    hasCatchOfInterest = true
                    catc.add(clause.catchBlock)
                }
            }            
        }
    }
    
    static class Clazz implements Comparable {
        String name
        def cu
        List<String> blocks;

        Clazz(String name, CompilationUnit cu, List<String> blocks) {
            this.name = name
            this.cu = cu
            this.blocks = blocks
        }

        @Override
        public int compareTo(Object o) {
            Clazz clazz = (Clazz) o
            return this.name.compareTo(clazz.name);
        }
    }


    static buildSourceDirs(File dir) {
        String path = dir.getPath()
        if (!path.contains("test") && !path.contains("build") && (dir.getName().equals('src') || dir.getName().equals('java'))) {
            if (path.endsWith(File.separator+'src')) {
                if (sourcePaths.contains(path+File.separator+'main'+File.separator+'java'))
                    println 'Rejected ' + dir.getPath()
                else {
                    sourcePaths.add(path)
                }
            } else if (path.endsWith(File.separator+'main'+File.separator+'java')) {
                sourcePaths.add(path)
                sourcePaths.remove(path.substring(0, path.length()-10))
            }
        }
    }
}
