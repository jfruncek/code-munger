import java.text.DateFormat


class ExploreDateFormats {

    static main(args) {
        def lang = System.getProperty('user.language') 
        println "System.property('user.language'):" + lang
        println "Locale.getDefault():" + Locale.getDefault()
        Locale loc = Locale.US
        println loc
        println DateFormat.getDateInstance(DateFormat.SHORT, loc).format(new Date())
        println DateFormat.getDateInstance(DateFormat.MEDIUM, loc).format(new Date())
        println DateFormat.getDateInstance(DateFormat.LONG, loc).format(new Date())
        loc = Locale.UK
        println loc
        println DateFormat.getDateInstance(DateFormat.SHORT, loc).format(new Date())
        println DateFormat.getDateInstance(DateFormat.MEDIUM, loc).format(new Date())
        println DateFormat.getDateInstance(DateFormat.LONG, loc).format(new Date())
        loc = Locale.CANADA
        println loc
        println DateFormat.getDateInstance(DateFormat.SHORT, loc).format(new Date())
        println DateFormat.getDateInstance(DateFormat.MEDIUM, loc).format(new Date())
        println DateFormat.getDateInstance(DateFormat.LONG, loc).format(new Date())

    }

}
