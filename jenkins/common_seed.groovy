import groovy.json.JsonSlurper

def jsonFile = new File("${WORKSPACE}/repos.json")
def config = new JsonSlurper().parse(jsonFile)

def baseUrl = config.baseUrl

config.repositories.each { repo ->

    def repoName        = repo.name
    def branchName      = repo.branch
    def jenkinsfilePath = repo.scriptPath  

    def gitRepoUrl = "${baseUrl}${repoName}.git"
    def jobName    = repoName

    pipelineJob(jobName) {

        properties {
            pipelineTriggers {
                triggers {
                    pollSCM {
                        scmpoll_spec('*/1 * * * *')
                        ignorePostCommitHooks(true)
                    }
                }
            }
        }

        logRotator {
            numToKeep(5)
        }

        definition {
            cpsScm {
                scm {
                    git {
                        remote {
                            url(gitRepoUrl)
                            credentials('github_credentials')
                        }
                        branches(branchName)
                        extensions {
                            cleanBeforeCheckout()
                        }
                    }
                }
                scriptPath(jenkinsfilePath)   // ✅ now works
            }
        }
    }
}
