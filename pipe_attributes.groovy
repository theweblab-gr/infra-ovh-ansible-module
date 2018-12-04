properties ([
    buildDiscarder(logRotator(artifactDaysToKeepStr: "", artifactNumToKeepStr: "", daysToKeepStr: "15",
                              numToKeepStr: "10")
                  ),
    disableConcurrentBuilds(),
    parameters([
        choice(name: "NODE_LABEL", choices: "Ansible_1\nAnsible_2",
               description: "Jenkins node where the job will run."),
        booleanParam(name: "BREAK_ON_LLD_ERRORS", defaultValue: false,
            description: "Break the build if errors are found in the Infrastructure LLD"),
        credentials(name: "Git_Creds", required: false,
                    credentialType: "com.cloudbees.plugins.credentials.impl.CertificateCredentialsImpl",
                    defaultValue: "github_theweblab_ovh",
                    description: "Credentials for connecting to git."),
    ]),
])



node(node_label){
       try{
        // setup PYTHONPATH
        //def pythonpath = sh (script: 'echo "$(pwd)/scripts"', returnStdout: true).trim()
        //env.PYTHONPATH = pythonpath

              stage('First Stage'){
                     echo "Starting at slave '"
                     echo "Slave Label: '${NODE_LABEL}' '"

                     ansiblePlaybook('infra-ovh-ansible.yaml') {
                            inventoryPath('hosts')
                            // ansibleName('1.9.4')
                            // limit('retry.limit')
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
                                   extraVar ("application_secret","value",true)
                                   extraVar ("consumer_key","value",true)
                            }
                     }




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