node {
    ansiblePlaybook(
        playbook: 'infra-ovh-ansible.yaml',
        inventory: 'hosts',
        credentialsId: 'sample-ssh-key')
}