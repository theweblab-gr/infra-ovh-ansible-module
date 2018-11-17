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
    }
}