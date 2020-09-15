#!groovy

@Library('jenkinslib') _

//func from shareibrary
def build = new org.devops.build()
//def deploy = new org.devops.deploy()
def tools = new org.devops.tools()

//env
String buildType = "${env.buildType}"
String buildShell = "${env.buildShell}"
String deployHosts = "${env.deployHosts}"

String srcUrl = "${env.srcUrl}"
String branchName = "${env.branchName}"

if ("${runOpts}" == 'GitlabPush') {
    branchName = branch - 'refs/heads/'
    currentBuild.description = "Trigger by ${userName} ${branch}"
    
    println("${branchName}")
}

//pipeline
pipeline {
    agent { node { label 'master' } }

    stages {
        stage('CheckOut') {
          steps {
            script {
              tools.PrintMsg('获取代码', 'green')
              checkout([$class: 'GitSCM', branches: [[name: "${branchName}"]],
                                          doGenerateSubmoduleConfigurations: false,
                                          extensions: [],
                                          submoduleCfg: [],
                                          userRemoteConfigs: [[credentialsId: 'gitlab-admin-user', url: "${srcUrl}"]]])
            }
          }
        }
        stage('Build') {
          steps {
            script {
              tools.PrintMsg('执行打包', 'green')

              build.Build(buildType, buildShell)
              //deploy.SaltDeploy("${deployHosts}","test.ping")
              //deploy.AnsibleDeploy("${deployHosts}","-m ping ")
            }
          }
        }
    }
}
