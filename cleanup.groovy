import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.DumperOptions

// Define file paths
def inputFile = new File('vault-credentials.yaml')
def outputFile = new File('vault-cleanup.yaml')

def yaml = new Yaml()
def data = yaml.load(inputFile.text)

def headersCount = data.size()
println "Total number of headers: ${headersCount}"

def cleanupData = [:]
def processedCount = 0
def notProcessedCount = 0

data.each { header, values ->
    def emptyEntries = values.findAll { it.value == '' }
    if (emptyEntries) {
        cleanupData[header] = emptyEntries
        processedCount++
    } else {
        notProcessedCount++
    }
}

println "Headers with missing strings processed: ${processedCount}"
println "Headers without missing strings, not processed: ${notProcessedCount}"

if (cleanupData) {
    outputFile.withWriter('UTF-8') { writer ->
        def options = new DumperOptions()
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
        def yamlWriter = new Yaml(options)
        yamlWriter.dump(cleanupData, writer)
    }
    println "vault-cleanup.yaml has been created with missing values."
} else {
    println "No missing values found."
}
