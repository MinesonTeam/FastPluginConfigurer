plugins {
    id("java")
    id("maven-publish")
    id("io.github.goooler.shadow") version "8.1.7"
    id("org.ajoberstar.grgit.service") version "5.2.0"
}

val projectVersion = project.property("projectVersion") as String
tasks {
    shadowJar.get().archiveFileName.set("FastPluginConfigurer-${projectVersion}.jar")
    build.get().dependsOn(shadowJar)
}

repositories {
    maven("https://papermc.io/repo/repository/maven-public/") // Paper
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.3-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

publishing {
    val publishData = PublishData(project)
    publications {
        create<MavenPublication>("maven") {
            groupId = rootProject.group.toString()
            artifactId = rootProject.name
            version = publishData.getVersion()

            from(components["java"])
            //artifact(tasks.shadowJar.get().apply { archiveClassifier.set("") })
        }
    }

    repositories {
        maven {
            authentication {
                credentials(PasswordCredentials::class) {
                    username = System.getenv("MAVEN_USERNAME") ?: project.findProperty("username") as? String ?: ""
                    password = System.getenv("MAVEN_PASSWORD") ?: project.findProperty("password") as? String ?: ""
                }
                authentication {
                    create<BasicAuthentication>("basic")
                }
            }

            url = uri(publishData.getRepository())
            name = "FastPluginConfigurer"
        }
    }
}


class PublishData(private val project: Project) {
    private var type: Type = getReleaseType()
    private var hashLength: Int = 7

    private fun getReleaseType(): Type {
        val branch = getCheckedOutBranch()
        println("Branch: $branch")
        return when {
            branch.contentEquals("master") -> Type.RELEASE
            branch.contentEquals("development") -> Type.SNAPSHOT
            else -> Type.DEV
        }
    }

    private fun getCheckedOutGitCommitHash(): String =
        System.getenv("GITHUB_SHA")?.substring(0, hashLength) ?: "local"

    private fun getCheckedOutBranch(): String =
        System.getenv("GITHUB_REF")?.replace("refs/heads/", "") ?: grgitService.service.get().grgit.branch.current().name

    fun getVersion(): String = getVersion(false)

    fun getVersion(appendCommit: Boolean): String =
        type.append(getVersionString(), appendCommit, getCheckedOutGitCommitHash())

    fun getVersionString(): String =
        (rootProject.version as String).removeSuffix("-SNAPSHOT").removeSuffix("-DEV")

    fun getRepository(): String = type.repo

    enum class Type(private val append: String, val repo: String, private val addCommit: Boolean) {
        RELEASE("", "", false),
        DEV("-DEV", "", true),
        SNAPSHOT("-SNAPSHOT", "", true);

        fun append(name: String, appendCommit: Boolean, commitHash: String): String =
            name.plus(append).plus(if (appendCommit && addCommit) "-".plus(commitHash) else "")
    }
}