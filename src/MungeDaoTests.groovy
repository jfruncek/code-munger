

class MungeDaoTests {
    
    static final String SRCTEST = 'C:/ti2m/trunk/Java/commonModule/srctest/'
    static final String OLD_PACKAGE = 'com.teramedica.domain.dao'
    static final String NEW_PACKAGE = 'com.teramedica.domain.dao.test'
    static final String PRIVATE_STATIC  = 'private static'
    static final String PRIVATE  = 'private'
    
	static main(args) {
        File srctest = new File(SRCTEST + makeDirectory(NEW_PACKAGE))
        srctest.mkdirs()
		List<File> classes = getDaoTests()
		classes.each {
			def clazz = it.getName()
            println clazz
            File file = new File(srctest, clazz)
            file.createNewFile()
           
            StringBuffer text = new StringBuffer()
			boolean found = false
            boolean remove = false
			it.eachLine {
                String line = it
				line = makeFieldsNonStatic(line)
                line = line.replace('BeforeClass', 'BeforeMethod')
                           .replace('AfterClass', 'AfterMethod')
/*                if ( line.contains('session.beginTransaction();') ) {
                  if (found) {
                     remove = true
                  } else {
                     found = true
                  }
                } else {
                  remove = false
                }
*/                  
                if ( ! removeLine(line) && ! remove ) {
                    text.append(line + "\n")
                }
			} 
            
/*            def methodContents = """
    public void closeSession() {
        if(session != null) {
            session.getTransaction().rollback();
            session.close();
        }
        TMContextUtil.setCurrentIdentifyingContext(null);
    }
"""
            
            String textString = text.toString().replaceAll(/public void closeSession\(\) \{\s+if\(session != null\) \{\s+session.close\(\);\s+\}\s+\}/, methodContents)
*/
            file.write(text.toString())
		}
	}
    
    private static boolean removeLine(String line) {
        return line.contains('commit')
    }
    
	private static String replaceFirst(String line) {
		if (line.contains('SESSION')) {
			line = line.replace('SESSION', 'session')
		}
		return line
	}

	private static String makeFieldsNonStatic(String line) {
		if ( line.stripIndent().startsWith(PRIVATE_STATIC) && ! line.contains('LOG') ) {
			//println "processing $clazz.name"
			line = line.replace(PRIVATE_STATIC, PRIVATE)
		}
		return line
	}
    
    static String makeDirectory(String packageName) {
        return packageName.replace('.', '/')
    }
	
	static List<File> getDaoTests() {
		File dir = new File(SRCTEST + makeDirectory(OLD_PACKAGE))
		return dir.listFiles(new FileFilter() { 
							public boolean accept(File file) {
								return file.name.endsWith('.java') && file.getText().indexOf('@Test') > 0
							}} )
	}
}
