package eu.cqse.config

import eu.cqse.GitRepositoryHelper
import eu.cqse.teamscale.client.CommitDescriptor
import org.gradle.api.Project
import java.io.IOException
import java.io.Serializable

/** The commit object which holds the end commit for which we do Test Impact Analysis. */
class Commit : Serializable {

    /** The branch to which the artifacts belong to. */
    var branchName: String? = null
        set(value) {
            field = value?.trim()
        }

    /** The timestamp of the commit that has been used to generate the artifacts. */
    var timestamp: String? = null
        set(value) {
            field = value?.trim()
        }

    fun getCommitDescriptor(): CommitDescriptor {
        return CommitDescriptor(branchName, timestamp)
    }

    fun copyWithDefault(toCopy: Commit, default: Commit) {
        branchName = toCopy.branchName ?: default.branchName
        timestamp = toCopy.timestamp ?: default.timestamp
    }

    fun validate(project: Project, testTaskName: String): Boolean {
        return try {
            if (branchName == null || timestamp == null) {
                val commit = GitRepositoryHelper.getHeadCommitDescriptor(project.rootDir)
                branchName = branchName ?: commit.branchName
                timestamp = timestamp ?: commit.timestamp
            }
            true
        } catch (e: IOException) {
            project.logger.error("Could not determine Teamscale upload commit for ${project.name} $testTaskName", e)
            false
        }
    }
}
