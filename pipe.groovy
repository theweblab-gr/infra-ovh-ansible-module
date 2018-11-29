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
    ansiblePlaybook('path/playbook.yml') {
        inventoryPath('hosts.ini')
        ansibleName('1.9.4')
        tags('one,two')
        credentialsId('credsid')
        become(true)
        becomeUser("user")
        extraVars {
            extraVar("key1", "value1", false)
            extraVar("key2", "value2", true)
        }
    }
}
        }
    }
}