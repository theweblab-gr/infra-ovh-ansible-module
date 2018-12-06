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
        choice(
            name: 'ROLES_TAGS',
            choices: "ovh-servers-list\novh-templates-list\novh-servers-list,ovh-templates-list",
            defaultValue: 'ovh-servers-list,ovh-templates-list',
            description: 'Getting ovh account information such us dedicated servers , installation templates for the dedicated servers'),
        choice(
            name: 'OVH_DATACENTER_ENDPOINT',
            choices: "soyoustart-eu\nkimsufi-eu\nsoyoustart-ca",
            defaultValue: 'soyoustart-eu',
            description: 'OVH datacenter endpoint'),
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
        [$class: 'NodeParameterDefinition',
            name: 'Slave_Node',
            allowedSlaves: ['Ansible_1'],
            defaultSlaves: ['Ansible_1'],
            description: 'Jenkins node where the job will run.',
            nodeEligibility: [$class: 'AllNodeEligibility'],
            triggerIfResult: 'multiSelectionDisallowed'
        ],
    ]),
])

node(Slave_Node){
    try{

        def ovh_creds = [
                     string(credentialsId: "${OVH_APP_KEY}", variable: 'app_key'),
                     string(credentialsId: "${OVH_APP_SECRET}", variable: 'app_secret'),
                     string(credentialsId: "${OVH_CONSUMER_KEY}", variable: 'consumer_key')
		]



        stage('First Stage'){
            echo "################ HOST INFO ################"
            echo "Starting at slave '"
            echo "Slave Label: '${Slave_Node}' '"
            echo "################ HOST INFO ################"
            sh "hostname"
            sh "uptime"
            sh "ansible --version"
            sh "which ansible -a"
            sh "type ansible"
            sh "which ansible-playbook -a"
            sh "type ansible-playbook"
            sh "echo $PATH"
            echo "################ END OF HOST INFO ################"
            checkout scm
            withCredentials(ovh_creds) {
                sh "ANSIBLE_HOST_KEY_CHECKING=false ansible-playbook infra-ovh-ansible.yaml --tags ${ROLES_TAGS} -vv --extra-vars  'datacenter_endpoint=${OVH_DATACENTER_ENDPOINT} application_key=${app_key} application_secret=${app_secret} consumer_key=${consumer_key}'  "
            }
        }

        stage('cleanup'){
            deleteDir()
        }

    } catch (Exception err) {
        // Gracefully handle unexpected exceptions & report build as failure
        echo "Exception during pipeline build. Review the following error: ${err}"
        currentBuild.result = 'FAILURE'
        error(err)
    }
}