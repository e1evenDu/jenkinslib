#!groovy

// @Library('jenkinslib') _来加载共享库，注意后面符号_用于加载。 
@Library('jenkinslib') _

// tools.groovy 类的实例化
def mytools = new org.devops.tools()

pipeline {
    agent { node {  label "master" }}

    stages {
        //下载代码
        stage("GetCode"){ 
            steps{  
                timeout(time:5, unit:"MINUTES"){   
                    script{ 
                        // 调用类方法
                        mytools.PrintMes("获取代码")
                    }
                }
            }
        }
    }
}
