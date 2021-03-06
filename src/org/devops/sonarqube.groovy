package org.devops
/*
def SonarScan(projectName,projectDesc,projectPath){
    def scannerHome = "/usr/local/sonar-scanner-4.4.0.2170-linux/"
    def sonarServer = "http://10.0.0.10:9000"
    def sonarDate = sh  returnStdout: true, script: 'date  +%Y%m%d%H%M%S'
    sonarDate = sonarDate - "\n"  // 移除换行符
    
    
    sh """ 
        ${scannerHome}/bin/sonar-scanner -Dsonar.host.url=${sonarServer} \
        -Dsonar.projectKey=${projectName} \
        -Dsonar.projectName=${projectName} \
        -Dsonar.projectVersion=${sonarDate} \
        -Dsonar.ws.timeout=30 \
        -Dsonar.login=admin \
	    -Dsonar.password=admin \
        -Dsonar.projectDescription=${projectDesc} \
        -Dsonar.links.homepage=http://www.baidu.com \
        -Dsonar.sources=${projectPath} \
        -Dsonar.sourceEncoding=UTF-8 \
        -Dsonar.java.binaries=target/classes \
        -Dsonar.java.test.binaries=target/test-classes \
        -Dsonar.java.surefire.report=target/surefire-reports \
    """
}
*/
//def SonarScan(sonarServer,projectName,projectDesc,projectPath,branchName){
def SonarScan(sonarServer,projectName,projectDesc,projectPath){
    //定义服务器列表
    def servers = ["test":"sonarqube-test","prod":"sonarqube-prod"]
    
    withSonarQubeEnv("${servers[sonarServer]}"){
        def scannerHome = "/usr/local/sonar-scanner-4.4.0.2170-linux/"
        //def sonarServer = "http://10.0.0.10:9000"
        def sonarDate = sh  returnStdout: true, script: 'date  +%Y%m%d%H%M%S'
        sonarDate = sonarDate - "\n"  // 移除换行符
    
        
        sh """ 
            ${scannerHome}/bin/sonar-scanner \
            -Dsonar.projectKey=${projectName} \
            -Dsonar.projectName=${projectName} \
            -Dsonar.projectVersion=${sonarDate} \
            -Dsonar.ws.timeout=30 \
            -Dsonar.projectDescription=${projectDesc} \
            -Dsonar.links.homepage=http://www.baidu.com \
            -Dsonar.sources=${projectPath} \
            -Dsonar.sourceEncoding=UTF-8 \
            -Dsonar.java.binaries=target/classes \
            -Dsonar.java.test.binaries=target/test-classes \
            -Dsonar.java.surefire.report=target/surefire-reports \
            #-Dsonar.branch.name=${branchName} -X
        """
    }
    
    /* 该写法会卡住，暂时未找到解决办法
    // 判断扫描结果，是否达到阈值
    def qg = waitForQualityGate()
    if (qg.status != 'OK') {
        error "Pipeline aborted due to quality gate failure: ${qg.status}"
    }
    */
}
