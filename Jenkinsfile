#!groovy

@Library('jenkinslib') _

//func from shareibrary
def build = new org.devops.build()
//def deploy = new org.devops.deploy()
def tools = new org.devops.tools()
def gitlab = new org.devops.gitlab()
def toemail = new org.devops.toemail()
def sonar = new org.devops.sonarqube()
def sonarapi = new org.devops.sonarapi()

def runOpts // 用于手动跑项目，不通过 gitlab 触发
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
} else {
    userEmail = 'jeffduuu@gmail.com' // 用于手动跑项目，不通过 gitlab 触发
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
                
              // 上传制品
              def jarName = sh returnStdout: true, script: "cd target;ls *.jar"
              jarName = jarName - "\n"

              def pom = readMavenPom file: 'pom.xml'
              pomVersion = "${pom.version}"
              pomArtifact = "${pom.artifactId}"
              pomPackaging = "${pom.packaging}"
              pomGroupId = "${pom.groupId}"

              println("${pomGroupId}-${pomArtifact}-${pomVersion}-${pomPackaging}")

              def mvnHome = tool "M2"
              sh  """ 
                  cd target/
                  ${mvnHome}/bin/mvn deploy:deploy-file -Dmaven.test.skip=true  \
                  -Dfile=${jarName} -DgroupId=${pomGroupId} \
                  -DartifactId=${pomArtifact} -Dversion=${pomVersion}  \
                  -Dpackaging=${pomPackaging} -DrepositoryId=maven-hostd \
                  -Durl=http://10.0.0.10:8081/repository/maven-hostd 
                  """
                
              //deploy.SaltDeploy("${deployHosts}","test.ping")
              //deploy.AnsibleDeploy("${deployHosts}","-m ping ")
            }
          }
        }
        /*
        stage('QA') {
          steps {
            script {
              tools.PrintMsg('搜索项目', 'green')
              result = sonarapi.SerarchProject("${JOB_NAME}")
              println(result)

              if (result == 'false') {
                 println("${JOB_NAME}---项目不存在,准备创建项目---> ${JOB_NAME}！")
                 sonarapi.CreateProject("${JOB_NAME}")
              } else {
                 println("${JOB_NAME}---项目已存在！")
              }
              
              tools.PrintMsg('配置项目质量规则', 'green')
              qpName = "${JOB_NAME}".split('-')[0]   //demo-maven-service_PUSH 分割后就是 demo(规则名）
              sonarapi.ConfigQualityProfiles("${JOB_NAME}", 'java', qpName)

              tools.PrintMsg('配置质量阈', 'green')
              sonarapi.ConfigQualityGates("${JOB_NAME}", qpName)
              
              tools.PrintMsg('代码扫描', 'green')
              sonar.SonarScan("test", "${JOB_NAME}", "${JOB_NAME}", "src")
              
              tools.PrintMsg('获取扫描结果', 'green')
              result = sonarapi.GetProjectStatus("${JOB_NAME}")
              
              println(result)

              if (result.toString() == 'ERROR') {
                  toemail.Email('代码质量阈错误！请及时修复！', userEmail)
                  error '代码质量阈错误！请及时修复！'
              } else {
                  println(result)
              }
                
            }
          }
        }
        */
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
