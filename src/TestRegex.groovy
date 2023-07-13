/**
 * Created by jfruncek on 12/11/2015.
 */
class TestRegex {

    static void main(String[] args) {
        testFor();

//        def text = (new File(args[0])).text
//        //text = text.replaceAll(/(?s)(:Java:[a-zA-Z0-9]+:test\s)(.+)test FAILED/, "found one!!!!!!!!")
//        text = text.replaceAll(/(?s)(?:Failed while reading the response from.+?GradleWrapperMain\.java:56\))+?/, "found one!!!!!!!!")
//        print text
    }

    static void testFor() {
        int bind_port = 8000
        int port_range=10
        String socket = null
        for(int i=bind_port; i <= bind_port+port_range; i++) {
            try {
                socket = null;
                break
            } catch (Throwable t) {
                    if (i > bind_port + port_range)
                        throw new RuntimeException()
            }
        }
        println  socket
    }
}
