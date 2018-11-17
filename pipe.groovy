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
                sh "ansible -i /app/jenkins/workspace/02.OBO_Application_Deployment/Axiros/ACS_Pipeline/oboaxirosautomation/scripts/deploymentScripts/ansible/inventories/ecxlab/5A/inventory -m ping -u oboadm -k acsmysqlnodes,acsnorthboundnodes,acssouthboundnodes --extra-vars 'ansible_user=oboadm ansible_password=oboadm1n'"
                sh "sshpass -p 'oboadm1n' ssh oboadm@ee-l-p-obo00005 'ls -al'"
                sh "sshpass -p 'oboadm1n' which ansible"
                sh "sshpass -p oboadm1n ~/default_py_27/bin/ansible -i /app/jenkins/workspace/02.OBO_Application_Deployment/Axiros/ACS_Pipeline/oboaxirosautomation/scripts/deploymentScripts/ansible/inventories/ecxlab/5A/inventory -m ping -u oboadm -k acsmysqlnodes,acsnorthboundnodes,acssouthboundnodes"
				withEnv(["PATH=${env.var_path}", "PYTHONPATH=${env.var_pythonpath}"]) {
					sh "echo Path= $PATH"
					sh "echo var_pythonpath= $var_pythonpath"
					sh "echo var_path= $var_path"
					sh "sshpass -p oboadm1n $var_ansible_path/ansible -i /app/jenkins/workspace/02.OBO_Application_Deployment/Axiros/ACS_Pipeline/oboaxirosautomation/scripts/deploymentScripts/ansible/inventories/ecxlab/5A/inventory -m ping -u oboadm -k acsmysqlnodes,acsnorthboundnodes,acssouthboundnodes"
				}
            }
        }
    }
}