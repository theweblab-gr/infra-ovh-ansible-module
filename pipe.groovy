pipeline {
    agent { label Slave_Node }
    stages {
        stage('build') {
            steps {
                echo "Hello World!"
                sh "echo Hello from the shell"
                sh "hostname"
                sh "uptime"
                sh "ansible --version"
                sh "which ansible -a"
                sh "type ansible"
                sh "echo $PATH"
            }
        }
        stage('ansible_build') {
            steps {


    ansibleAdHoc(String module, String command) {
        ansibleName(String name)
        inventoryPath(String path)
        inventoryContent(String content, boolean dynamic = false)
        credentialsId(String id)
        hostPattern(String pattern)
        become(boolean become = true)
        becomeUser(String user = 'root')
        sudo(boolean sudo = true)
        sudoUser(String user = 'root')
        forks(int forks = 5)
        unbufferedOutput(boolean unbufferedOutput = true)
        colorizedOutput(boolean colorizedOutput = false)
        hostKeyChecking(boolean hostKeyChecking = false)
        additionalParameters(String params)
        extraVars {
            extraVar(String key, String value, boolean hidden)
        }
    }

            }
        }
    }
}