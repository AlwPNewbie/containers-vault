import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.DumperOptions

// Define file paths
def inputFile = new File('vault-cleanup.yaml')
def cronFile = new File('vault-cron-sleep.yaml')
def mockFile = new File('vault-mock-login.yaml')
def othersFile = new File('vault-encrpytion.yaml')

def yaml = new Yaml()
def data = yaml.load(inputFile.text)

def cronSleepData = [:]
def mockLoginData = [:]
def othersData = [:]

data.each { header, values ->
    if (values.keySet().any { it == 'AS_APP_CRON_SLEEP' }) {
        cronSleepData[header] = values
    } else if (values.keySet().any { it.startsWith('AS_APP_MOCK_LOGIN') }) {
        mockLoginData[header] = values
    } else {
        othersData[header] = values
    }
}

def saveYaml(file, yamlData) {
    if (yamlData) {
        file.withWriter('UTF-8') { writer ->
            def options = new DumperOptions()
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
            def yamlWriter = new Yaml(options)
            yamlWriter.dump(yamlData, writer)
        }
    }
}

saveYaml(cronFile, cronSleepData)
saveYaml(mockFile, mockLoginData)
saveYaml(othersFile, othersData)

println "YAML split completed:"
println "- CRON_SLEEP group: ${cronSleepData.size()} headers"
println "- MOCK_LOGIN group: ${mockLoginData.size()} headers"
println "- ENCRYPTION group: ${othersData.size()} headers"
