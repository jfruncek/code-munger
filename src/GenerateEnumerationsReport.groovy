import com.github.javaparser.JavaParser
import com.github.javaparser.ParseException
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import groovy.io.FileType

/**
 * Created by jfruncek on 5/12/2017.
 */
class GenerateEnumerationsReport {

    static final String ROOT_DIR = "C:/dev/git/vna/Java/utilModule/src"

    static void main(String[] args) {
        GenerateEnumerationsReport report = new GenerateEnumerationsReport()
        report.scanClasses()
    }

    EnumVisitor enumVisitor = new EnumVisitor()

    def scanClasses() {
        new File(ROOT_DIR).eachDirRecurse { dir ->
            scanClass(dir, enumVisitor)
        }
    }

    static class EnumVisitor extends VoidVisitorAdapter {
        @Override
        public void visit(ClassOrInterfaceDeclaration n, Object arg) {
            if (n.extendedTypes != null) {

            }
        }

        private <V extends VoidVisitorAdapter> void scanClass(File it, V visitor) {
            it.eachFileMatch FileType.FILES, ~/.*\.java/, {

                FileInputStream i = new FileInputStream(it);
                String fullPath = i.path

                CompilationUnit cu
                try {
                    cu = JavaParser.parse(i);

                } catch (ParseException pe) {
                    println "Caught exception while parsing: " + i.path
                    println pe

                } finally {
                    i.close();
                }
            }
        }
    }
}