import groovy.io.FileType
import org.apache.commons.configuration2.XMLConfiguration
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder
import org.apache.commons.configuration2.builder.fluent.Parameters
import org.apache.commons.configuration2.ex.ConfigurationException
import org.apache.commons.configuration2.tree.xpath.XPathExpressionEngine

/**
 * Created by jfruncek on 4/8/2016.
 */
class TryCommonsConfiguration {

    static Map<String, WebXmlConfiguration> files = [:]

    static void main(String[] args) {
        def vnaHome = System.getenv().get("VNA_HOME")

        def rootDir = new File(vnaHome + '')
        println rootDir
        rootDir.eachFileRecurse(FileType.FILES) { file ->

            if (file.path.contains('ui.war') && file.path.contains(WebXmlConfiguration.FILENAME)) {
                println file.path
                files.put(file.name, new WebXmlConfiguration(file.path))
            } else if (file.path.contains(ServerMBeanXmlConfiguration.FILENAME)) {
                println file.path
                files.put(file.name, new ServerMBeanXmlConfiguration(file.path))
            } else if (file.path.contains(ServerXml.FILENAME)) {
                println file.path
                files.put(file.name, new ServerXml(file.path))
            }
        }
        ServerXml server = files[ServerXml.FILENAME]
        server.addHttpConnector()
        println server.getProp('Connector')
        /*WebXmlConfiguration web = files[WebXmlConfiguration.FILENAME]
        println web.SESSION_TIMEOUT + ': ' + web.getProp(web.SESSION_TIMEOUT)
        //cfg.setProperty(cfg.SESSION_TIMEOUT, '30')
        ServerMBeanXmlConfiguration cfg = files[ServerMBeanXmlConfiguration.FILENAME]
        println cfg.HL7_SERVER_ULN + ': ' + cfg.getProp(cfg.HL7_SERVER_ULN)*/
    }
}

class ServerXml extends BaseXMLConfiguration {
    public static String FILENAME = 'server.xml'

    def addHttpConnector() {
        addProp('Connector', '')
        addProp('Connector/@executor', 'tomcatThreadPool')
        addProp('Connector/@port', '8080')
        addProp('Connector/@protocol', 'HTTP/1.1')
        addProp('Connector/@connectionTimeout','20000')
    }

    def ServerXml(String path) {
        super(path)
    }
}

class ServerMBeanXmlConfiguration extends BaseXMLConfiguration {
    public static String FILENAME = 'jboss-service.xml'
    public final String HL7_SERVER_ULN = "mbean/attribute[@name='HL7ServerULN']"

    def ServerMBeanXmlConfiguration(String path) {
        super(path)
    }
}

class WebXmlConfiguration extends BaseXMLConfiguration {
    public static String FILENAME = 'web.xml'
    public final String SESSION_TIMEOUT = 'session-config/session-timeout'

    WebXmlConfiguration(String path) {
        super(path)
    }
}

class BaseXMLConfiguration {
    def builder

    BaseXMLConfiguration(String path) {
        parseWebXmlFile(path)
    }

    def parseWebXmlFile(String path) {
        Parameters params = new Parameters()
        builder = new FileBasedConfigurationBuilder<XMLConfiguration>(XMLConfiguration.class)
                    .configure(params.xml().setFileName(path).setExpressionEngine(new XPathExpressionEngine()))

        try {
            XMLConfiguration config = builder.getConfiguration()
            /*def iter = config.getKeys().iterator()
            while (iter.hasNext())  {
                print iter.next()
                if (iter.hasNext()) { println '' }
            }*/
        }
        catch (ConfigurationException cex) {
            println cex
        }
    }

    def setProp(String propertyName, String value) {
        println propertyName + '->' + value
        builder.getConfiguration().setString(propertyName, value)
        builder.save()
    }

    String getProp(String name) {
        builder.getConfiguration().getString(name)
    }

    def addProp(String propertyName, String value) {
        println '+' + propertyName + '=' + value
        builder.getConfiguration().addProperty(propertyName, value)
        builder.save()
    }
}

//        builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
//                        .configure(params.properties()
//                        .setFileName(vnaHome + "/config/contentserver-log.properties"))

//static final String ATLAS_DATABASE_XML = '/config/atlas-database_DEPLOY.xml'
/*    try
    {
        XMLConfiguration config = builder.getConfiguration();
        String bean = config.getString('bean(0).property(2).bean(0)[@class]')
        if (!bean.contains('EclipseLinkJpaVendorAdapter')) { println 'Bad config'; System.exit(1) }
        if (config.getString('bean(0).property(2).bean(0).property(1)[@name]') != null) {
            println 'Already added property'
        } else {
            ImmutableNode.Builder nodeBuilder = new ImmutableNode.Builder()
            nodeBuilder.name('property')
            nodeBuilder.addAttribute('name', 'databasePlatform')
            nodeBuilder.addAttribute('value', 'org.eclipse.persistence.platform.database.SQLServerPlatform')
            List<ImmutableNode> properties = []
            properties.add(nodeBuilder.create())
            config.addNodes('bean(0).property(2).bean(0)', properties)
        }

        config.setProperty('bean(1).property(0).props.prop(0)[@key]',  'applicationName')
        config.setProperty('bean(1).property(3)[@value]', 'sa56')
        config.setProperty('bean(1).property(4)[@value]', 'dev')

        builder.save()
    }
    catch(ConfigurationException cex)
    {
        println 'loading of the xml configuration file failed'
    }
*/

