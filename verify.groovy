import org.yaml.snakeyaml.Yaml

// Define file paths
def credentialsFile = new File('vault-credentials.yaml')
def cleanupFile = new File('vault-cleanup.yaml')

def yaml = new Yaml()
def credentialsData = yaml.load(credentialsFile.text)
def cleanupData = yaml.load(cleanupFile.text)

def missingEntries = [:]

// Verify all keys in cleanup exist in credentials
topLevelCheck: 
cleanupData.each { header, values ->
    if (!credentialsData.containsKey(header)) {
        missingEntries[header] = "Header missing in vault-credentials.yaml"
        return
    }
    
    values.each { key, value ->
        if (!credentialsData[header].containsKey(key)) {
            missingEntries[header] = missingEntries.get(header, []) + key
        }
    }
}

if (missingEntries) {
    println "Verification failed! Some entries in vault-cleanup.yaml are not found in vault-credentials.yaml:"
    missingEntries.each { header, keys ->
        println "- $header: ${keys instanceof List ? keys.join(', ') : keys}"
    }
} else {
    println "Verification successful! All entries in vault-cleanup.yaml exist in vault-credentials.yaml."
}
