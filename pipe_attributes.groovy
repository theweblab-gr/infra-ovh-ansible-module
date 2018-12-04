#!groovy

properties ([
    buildDiscarder(logRotator(artifactDaysToKeepStr: "", artifactNumToKeepStr: "", daysToKeepStr: "15",
                              numToKeepStr: "10")
                  ),
    disableConcurrentBuilds(),
    parameters([
	[$class: 'NodeParameterDefinition',
              name: 'Slave_Node',
              allowedSlaves: ['Ansible_1'],
              defaultSlaves: ['Ansible_1'],
              description: 'Jenkins node where the job will run.',
              nodeEligibility: [$class: 'AllNodeEligibility'],
              triggerIfResult: 'multiSelectionDisallowed'
       ],
       [$class: 'EnvInjectJobProperty',
              keepBuildVariables: true,
              keepJenkinsSystemVariables: true,
              on: true
       ],
       booleanParam(
              name: "BREAK_ON_LLD_ERRORS",
              defaultValue: false,
              description: "Break the build if errors are found in the Infrastructure LLD"),
       credentials(
              name: "Git_Creds",
              required: false,
              credentialType: "com.cloudbees.plugins.credentials.impl.CertificateCredentialsImpl",
              defaultValue: "github_theweblab_ovh",
              description: "Credentials for connecting to git."),
    ]),
])

node(Slave_Node){
       try{
        // setup PYTHONPATH
        //def pythonpath = sh (script: 'echo "$(pwd)/scripts"', returnStdout: true).trim()
        //env.PYTHONPATH = pythonpath

       stage('First Stage'){
              echo "Starting at slave '"
              echo "Slave Label: '${Slave_Node}' '"
              echo "Hello World!"
              sh "echo Hello from the shell"
              sh "hostname"
              sh "uptime"
              sh "ansible --version"
              sh "which ansible -a"
              sh "type ansible"
              sh "which ansible-playbook -a"
              sh "type ansible-playbook"
              sh "echo $PATH"
              sh "ls -al"
              checkout scm
              sh "ls -al"
              ansiblePlaybook colorized: true, disableHostKeyChecking: true, inventory: 'hosts', playbook: 'infra-ovh-ansible.yaml', tags: 'ovh-servers-list'


        }
        stage('cleanup'){
            deleteDir()
        }

    } catch (Exception err) {
        // Gracefully handle unexpected exceptions & report build as failure
        echo "Exception during pipeline build. Review the following error: ${err}"
        currentBuild.result = 'FAILURE'
    }
}