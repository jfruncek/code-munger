import groovy.text.SimpleTemplateEngine

class GenerateJMeterTestPlan {

    static SimpleTemplateEngine engine = new SimpleTemplateEngine()

    def qido_steps = '', wado_steps = ''

    static void main(String[] args) {
        def generator = new GenerateJMeterTestPlan()
        generator.makeTemplate()
    }

    void makeTemplate() {
        def study = ['1.2.840.114302.3122019.171956232.8716.1.386500', 'MG', 4, 573064,
                     '1.2.840.114302.3122019.17209733.5976.1.154636', 'US', 10, 2689920,
                     '1.2.840.114302.3132019.81021495.4000.1.326196', 'OT', 2, 284676,
                     '1.2.840.114302.3132019.81029296.3112.1.171638', 'OT', 4, 569800,
                     '1.2.840.114302.3132019.81038411.440.1.436677', 'OT', 4, 569320,
                     '1.2.840.114302.3132019.81046621.5844.1.269324', 'OT', 2, 284672,
                     '1.2.840.114302.3132019.81054293.3176.1.741157', 'CT', 10, 1509664,
                     '1.2.840.114302.3132019.8108900.8724.1.844813', 'OT', 2, 284652,
                     '1.2.840.114302.3132019.81114402.2688.1.716741', 'NM', 10, 126669840,
                     '1.2.840.114302.3132019.8112558.3356.1.172516', 'MR', 40, 12937244,
                     '1.2.840.114302.3132019.81128480.8020.1.450513', 'PT', 10, 121800,
                     '1.2.840.114302.3132019.81136949.4260.1.828479', 'MG', 4, 9175864,
                     '1.2.840.114302.3132019.885978.1408.1.921961', 'MR', 40, 12937520,
                     '1.2.840.114302.3132019.8914997.6448.1.971791', 'DX', 10, 1432560,
                     '1.2.840.114302.3132019.8924956.8504.1.700247', 'NM', 20, 253339600,
                     '1.2.840.114302.3132019.8941359.8924.1.619991', 'OT', 2, 284668,
                     '1.2.840.114302.3132019.9022941.192.1.927642', 'OT', 8, 1471584]
        for (int i = 0; i < study.size(); i++) {
            def url = '/studies/' + study[i++] + '/instances'
            def binding = ['server': 'vm190', 'port': '8080', 'protocol': 'http', 'listener': 'dicomlistener1', 'url': url,
                           'modality': study[i++], 'images': study[i++], 'size': (int)(study[i] / 1024) ]
            def template = QIDO_STEP
            def step = engine.createTemplate(template).make(binding)
            qido_steps += step
        }
        def binding = ['qido_steps': qido_steps, 'wado_steps': wado_steps]
        def template = BASE_TESTPLAN
        def plan = engine.createTemplate(template).make(binding)
        File output  = new File('generated_test_plan.jmx')
        output.write(plan.toString())
    }

    static final String BASE_TESTPLAN = '''
<?xml version="1.0" encoding="UTF-8"?>
<jmeterTestPlan version="1.2" properties="4.0" jmeter="4.0 r1823414">
  <hashTree>
    <TestPlan guiclass="TestPlanGui" testclass="TestPlan" testname="QIDO/WADO Full API Test Plan" enabled="true">
      <stringProp name="TestPlan.comments">This test plan is meant to exercise the full breadth of the QIDO/WADO API</stringProp>
      <boolProp name="TestPlan.functional_mode">false</boolProp>
      <boolProp name="TestPlan.tearDown_on_shutdown">true</boolProp>
      <boolProp name="TestPlan.serialize_threadgroups">false</boolProp>
      <elementProp name="TestPlan.user_defined_variables" elementType="Arguments" guiclass="ArgumentsPanel" testclass="Arguments" testname="User Defined Variables" enabled="true">
        <collectionProp name="Arguments.arguments"/>
      </elementProp>
      <stringProp name="TestPlan.user_define_classpath"></stringProp>
    </TestPlan>
    <hashTree>
      <ResultCollector guiclass="GraphVisualizer" testclass="ResultCollector" testname="Graph Results" enabled="true">
        <boolProp name="ResultCollector.error_logging">false</boolProp>
        <objProp>
          <name>saveConfig</name>
          <value class="SampleSaveConfiguration">
            <time>true</time>
            <latency>true</latency>
            <timestamp>true</timestamp>
            <success>true</success>
            <label>true</label>
            <code>true</code>
            <message>true</message>
            <threadName>true</threadName>
            <dataType>true</dataType>
            <encoding>false</encoding>
            <assertions>true</assertions>
            <subresults>true</subresults>
            <responseData>false</responseData>
            <samplerData>false</samplerData>
            <xml>false</xml>
            <fieldNames>true</fieldNames>
            <responseHeaders>false</responseHeaders>
            <requestHeaders>false</requestHeaders>
            <responseDataOnError>false</responseDataOnError>
            <saveAssertionResultsFailureMessage>true</saveAssertionResultsFailureMessage>
            <assertionsResultsToSave>0</assertionsResultsToSave>
            <bytes>true</bytes>
            <sentBytes>true</sentBytes>
            <threadCounts>true</threadCounts>
            <idleTime>true</idleTime>
            <connectTime>true</connectTime>
          </value>
        </objProp>
        <stringProp name="filename"></stringProp>
      </ResultCollector>
      <hashTree/>
      <ResultCollector guiclass="SummaryReport" testclass="ResultCollector" testname="Summary Report" enabled="true">
        <boolProp name="ResultCollector.error_logging">false</boolProp>
        <objProp>
          <name>saveConfig</name>
          <value class="SampleSaveConfiguration">
            <time>true</time>
            <latency>true</latency>
            <timestamp>true</timestamp>
            <success>true</success>
            <label>true</label>
            <code>true</code>
            <message>true</message>
            <threadName>true</threadName>
            <dataType>true</dataType>
            <encoding>false</encoding>
            <assertions>true</assertions>
            <subresults>true</subresults>
            <responseData>false</responseData>
            <samplerData>false</samplerData>
            <xml>false</xml>
            <fieldNames>true</fieldNames>
            <responseHeaders>false</responseHeaders>
            <requestHeaders>false</requestHeaders>
            <responseDataOnError>false</responseDataOnError>
            <saveAssertionResultsFailureMessage>true</saveAssertionResultsFailureMessage>
            <assertionsResultsToSave>0</assertionsResultsToSave>
            <bytes>true</bytes>
            <sentBytes>true</sentBytes>
            <threadCounts>true</threadCounts>
            <idleTime>true</idleTime>
            <connectTime>true</connectTime>
          </value>
        </objProp>
        <stringProp name="filename"></stringProp>
      </ResultCollector>
      <hashTree/>
      <kg.apc.jmeter.threads.SteppingThreadGroup guiclass="kg.apc.jmeter.threads.SteppingThreadGroupGui" testclass="kg.apc.jmeter.threads.SteppingThreadGroup" testname="jp@gc - Stepping Thread Group (deprecated)" enabled="true">
        <stringProp name="ThreadGroup.on_sample_error">continue</stringProp>
        <stringProp name="ThreadGroup.num_threads">10</stringProp>
        <stringProp name="Threads initial delay">1</stringProp>
        <stringProp name="Start users count">1</stringProp>
        <stringProp name="Start users count burst">1</stringProp>
        <stringProp name="Start users period">10</stringProp>
        <stringProp name="Stop users count">5</stringProp>
        <stringProp name="Stop users period">1</stringProp>
        <stringProp name="flighttime">60</stringProp>
        <stringProp name="rampUp">5</stringProp>
        <elementProp name="ThreadGroup.main_controller" elementType="LoopController" guiclass="LoopControlPanel" testclass="LoopController" testname="Loop Controller" enabled="true">
          <boolProp name="LoopController.continue_forever">false</boolProp>
          <intProp name="LoopController.loops">-1</intProp>
        </elementProp>
      </kg.apc.jmeter.threads.SteppingThreadGroup>
      <hashTree>
        ${qido_steps}
        ${wado_steps}
      </hashTree>
      <ResultSaver guiclass="ResultSaverGui" testclass="ResultSaver" testname="Save Responses to a file" enabled="true">
        <stringProp name="FileSaver.filename">dcm</stringProp>
        <boolProp name="FileSaver.errorsonly">false</boolProp>
        <boolProp name="FileSaver.skipautonumber">false</boolProp>
        <boolProp name="FileSaver.skipsuffix">true</boolProp>
        <boolProp name="FileSaver.successonly">true</boolProp>
      </ResultSaver>
      <hashTree/>
    </hashTree>
  </hashTree>
</jmeterTestPlan>
'''
    static final String QIDO_STEP = '''
        <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="QIDO ${images} ${modality} ${size} KB" enabled="true">
          <elementProp name="HTTPsampler.Arguments" elementType="Arguments" guiclass="HTTPArgumentsPanel" testclass="Arguments" testname="User Defined Variables" enabled="true">
            <collectionProp name="Arguments.arguments">
              <elementProp name="" elementType="HTTPArgument">
                <boolProp name="HTTPArgument.always_encode">false</boolProp>
                <stringProp name="Argument.value"></stringProp>
                <stringProp name="Argument.metadata">=</stringProp>
                <boolProp name="HTTPArgument.use_equals">true</boolProp>
              </elementProp>
            </collectionProp>
          </elementProp>
          <stringProp name="HTTPSampler.domain">${server}</stringProp>
          <stringProp name="HTTPSampler.port">${port}</stringProp>
          <stringProp name="HTTPSampler.protocol">${protocol}</stringProp>
          <stringProp name="HTTPSampler.contentEncoding"></stringProp>
          <stringProp name="HTTPSampler.path">/qido-rs/${listener}${url}</stringProp>
          <stringProp name="HTTPSampler.method">GET</stringProp>
          <boolProp name="HTTPSampler.follow_redirects">true</boolProp>
          <boolProp name="HTTPSampler.auto_redirects">false</boolProp>
          <boolProp name="HTTPSampler.use_keepalive">true</boolProp>
          <boolProp name="HTTPSampler.DO_MULTIPART_POST">false</boolProp>
          <stringProp name="HTTPSampler.embedded_url_re"></stringProp>
          <stringProp name="HTTPSampler.connect_timeout"></stringProp>
          <stringProp name="HTTPSampler.response_timeout"></stringProp>
        </HTTPSamplerProxy>
        <hashTree>
          <HeaderManager guiclass="HeaderPanel" testclass="HeaderManager" testname="HTTP Header Manager" enabled="true">
            <collectionProp name="HeaderManager.headers">
              <elementProp name="" elementType="Header">
                <stringProp name="Header.name">Accept</stringProp>
                <stringProp name="Header.value">application/dicom+json</stringProp>
              </elementProp>
            </collectionProp>
          </HeaderManager>
          <hashTree/>
        </hashTree>
'''
    static final String WADO_STEP = '''
        <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="WADO ${images} ${modality} ${size}" enabled="true">
          <elementProp name="HTTPsampler.Arguments" elementType="Arguments" guiclass="HTTPArgumentsPanel" testclass="Arguments" testname="User Defined Variables" enabled="true">
            <collectionProp name="Arguments.arguments"/>
          </elementProp>
          <stringProp name="HTTPSampler.domain">${server}</stringProp>
          <stringProp name="HTTPSampler.port">${port}</stringProp>
          <stringProp name="HTTPSampler.protocol">${protocol}</stringProp>
          <stringProp name="HTTPSampler.contentEncoding"></stringProp>
          <stringProp name="HTTPSampler.path">/wado-rs/${listener}${url}</stringProp>
          <stringProp name="HTTPSampler.method">GET</stringProp>
          <boolProp name="HTTPSampler.follow_redirects">true</boolProp>
          <boolProp name="HTTPSampler.auto_redirects">false</boolProp>
          <boolProp name="HTTPSampler.use_keepalive">true</boolProp>
          <boolProp name="HTTPSampler.DO_MULTIPART_POST">false</boolProp>
          <stringProp name="HTTPSampler.embedded_url_re"></stringProp>
          <stringProp name="HTTPSampler.connect_timeout"></stringProp>
          <stringProp name="HTTPSampler.response_timeout"></stringProp>
        </HTTPSamplerProxy>
        <hashTree>
          <HeaderManager guiclass="HeaderPanel" testclass="HeaderManager" testname="HTTP Header Manager" enabled="true">
            <collectionProp name="HeaderManager.headers">
              <elementProp name="" elementType="Header">
                <stringProp name="Header.name">Accept</stringProp>
                <stringProp name="Header.value">*/*</stringProp>
              </elementProp>
            </collectionProp>
          </HeaderManager>
          <hashTree/>
        </hashTree>
'''
}
