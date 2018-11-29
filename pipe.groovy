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
                ansiblePlaybook('infra-ovh-ansible.yaml') {
                    //inventoryPath('hosts.ini')
                    //ansibleName('1.9.4')
                    //limit('retry.limit')
                    tags('ovh-servers-list')
                    //skippedTags('three')
                    //startAtTask('task')
                   // credentialsId('credsid')
                   // become(true)
                   // becomeUser("user")
                   // forks(6)
                   // unbufferedOutput(false)
                    colorizedOutput(true)
                    disableHostKeyChecking(true)
                    //additionalParameters('params')
                    extraVars {
                        extraVar ("application_key","value",true)
                    }
                }
            }
        }
    }
}