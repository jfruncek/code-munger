import java.util.zip.*

ZIP = '/home/fishribs/Downloads/love-care-serve-site.zip'

File destDir = new File("/home/fishribs/unzipTest")
destDir.mkdir()

byte[] buffer = new byte[1024]
ZipInputStream zis = new ZipInputStream(new FileInputStream(ZIP))
ZipEntry zipEntry = zis.getNextEntry()
while (zipEntry != null) {
    def name = zipEntry.getName()
    //println "Considering: " + name
    if (name.endsWith('jpg') || name.endsWith('png') || name.endsWith('pdf')) {
        println "Found: " + name
        FileOutputStream fos = new FileOutputStream(new File(destDir, name.substring(name.lastIndexOf('/')++)))
        int len
        while ((len = zis.read(buffer)) > 0) {
            fos.write(buffer, 0, len)
        }
        fos.close()
    }
    zipEntry = zis.getNextEntry()
}
zis.closeEntry()
zis.close()