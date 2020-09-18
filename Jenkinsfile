#!groovy

@Library('jenkinslib') _

//func from shareibrary
def build = new org.devops.build()
//def deploy = new org.devops.deploy()
def tools = new org.devops.tools()
def gitlab = new org.devops.gitlab()
def toemail = new org.devops.toemail()
def sonar = new org.devops.sonarqube()

//env
String buildType = "${env.buildType}"
String buildShell = "${env.buildShell}"
String deployHosts = "${env.deployHosts}"

String srcUrl = "${env.srcUrl}"
String branchName = "${env.branchName}"

if ("${runOpts}" == 'GitlabPush') {
    branchName = branch - 'refs/heads/'
    currentBuild.description = "Trigger by ${userName} ${branch}"
    
    gitlab.ChangeCommitStatus(projectId, commitSha, 'running')
                              
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
        stage('QA') {
          steps {
            script {
              tools.PrintMsg('代码扫描', 'green')
              sonar.SonarScan("${JOB_NAME}", "${JOB_NAME}", "src")
            }
          }
        }
    }
    
    post {
        always {
            script {
                println('always')
            }
        }

        success {
            script {
                println('success')
                if ("${runOpts}" == 'GitlabPush') {
                    gitlab.ChangeCommitStatus(projectId, commitSha, 'success')
                }
                toemail.Email('流水线成功', userEmail)
            }
        }
        failure {
            script {
                println('failure')
                if ("${runOpts}" == 'GitlabPush') {
                    gitlab.ChangeCommitStatus(projectId, commitSha, 'failed')
                }
                toemail.Email('流水线失败了！', userEmail)
            }
        }

        aborted {
            script {
                println('aborted')
                if ("${runOpts}" == 'GitlabPush') {
                    gitlab.ChangeCommitStatus(projectId, commitSha, 'canceled')
                }
                toemail.Email('流水线被取消了！', userEmail)
            }
        }
    }
}
