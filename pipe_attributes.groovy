#!groovy

properties ([
       buildDiscarder(logRotator(artifactDaysToKeepStr: "", artifactNumToKeepStr: "", daysToKeepStr: "15",
                              numToKeepStr: "10")
                  ),
       disableConcurrentBuilds(),
       [$class: 'EnvInjectJobProperty',
              info: [
                     loadFilesFromMaster: false,
                     propertiesContent:
'''
# Service variable to check whether this groovy pipeline has been configured
IS_PIPELINE_CONFIGURED=true
DRY_RUN=false
''',
                     secureGroovyScript: [classpath: [], sandbox: false, script: '']
                     ],
              keepBuildVariables: true,
              keepJenkinsSystemVariables: true,
              on: true
       ],
       parameters([
	       [$class: 'NodeParameterDefinition',
                     name: 'Slave_Node',
                     allowedSlaves: ['Ansible_1'],
                     defaultSlaves: ['Ansible_1'],
                     description: 'Jenkins node where the job will run.',
                     nodeEligibility: [$class: 'AllNodeEligibility'],
                     triggerIfResult: 'multiSelectionDisallowed'
              ],
              choice(
                     name: 'OVH_DATACENTER_ENDPOINT',
                     choices: "soyoustart-eu\nkimsufi-eu\nsoyoustart-ca",
                     defaultValue: 'soyoustart-eu',
                     description: 'OVH datacenter endpoint'),
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
              credentials(
                     name: "Target_Host_Creds",
                     required: false,
                     credentialType: "com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl",
                     defaultValue: "",
                     description: "Credentials for connecting to target host."),
              credentials(
                     name: "OVH_APP_KEY",
                     required: false,
                     credentialType: "com.cloudbees.plugins.plaincredentials.impl.StringCredentialsImpl",
                     defaultValue: "soyoustart_master_infra_1_app_key",
                     description: "OVH Application Key"),
              credentials(
                     name: "OVH_APP_SECRET",
                     required: false,
                     credentialType: "com.cloudbees.plugins.plaincredentials.impl.StringCredentialsImpl",
                     defaultValue: "soyoustart_master_infra_1_app_secret",
                     description: "OVH Application Secret"),
              credentials(
                     name: "OVH_CONSUMER_KEY",
                     required: false,
                     credentialType: "com.cloudbees.plugins.plaincredentials.impl.StringCredentialsImpl",
                     defaultValue: "soyoustart_master_infra_1_consumer_key",
                     description: "OVH Consumer Key."),
       ]),
])

node(Slave_Node){

        // setup PYTHONPATH
        //def pythonpath = sh (script: 'echo "$(pwd)/scripts"', returnStdout: true).trim()
        //env.PYTHONPATH = pythonpath

    def creds = [
        [$class: 'StringBinding', credentialsId: ${OVH_APP_KEY}, variable: 'app_key']
        [$class: 'StringBinding', credentialsId: ${OVH_APP_SECRET}, variable: 'app_secret']
        [$class: 'StringBinding', credentialsId: ${OVH_CONSUMER_KEY}, variable: 'consumer_key']
    ]


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
              withCredentials(creds) {
                     sh "ANSIBLE_HOST_KEY_CHECKING=false ansible-playbook infra-ovh-ansible.yaml --tags ovh-servers-list,ovh-templates-list -vv --extra-vars  'datacenter_endpoint=${OVH_DATACENTER_ENDPOINT} application_key=${app_key} application_secret=${app_secret} consumer_key=${consumer_key}'  "
              }
            //  ansiblePlaybook colorized: true, disableHostKeyChecking: true, installation: 'Ansible_1', playbook: 'infra-ovh-ansible.yaml', tags: 'ovh-servers-list'

              //ansiblePlaybook credentialsId: '${Target_Host_Creds}', colorized: true, disableHostKeyChecking: true, installation: 'Ansible_1', inventory: 'hosts', playbook: 'sample_playbook.yalm'
              //echo "Executing ansible playbook from sh"
              //sh "ANSIBLE_HOST_KEY_CHECKING=false ansible-playbook sample_playbook_local.yalm "


        }
        stage('cleanup'){
            deleteDir()
        }


}