pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.name == "rdgen") {
                useModule("com.jetbrains.rd:rd-gen:${requested.version}")
            }
        }
    }
}

rootProject.name = "utbot"

include("utbot-core")
include("utbot-framework")
include("utbot-framework-api")
include("utbot-intellij")
include("utbot-rider")
include("utbot-sample")
include("utbot-fuzzers")
include("utbot-junit-contest")
include("utbot-analytics")
include("utbot-analytics-torch")
include("utbot-cli")
include("utbot-api")
include("utbot-instrumentation")
include("utbot-instrumentation-tests")

include("utbot-summary")
include("utbot-gradle")
include("utbot-maven")
include("utbot-summary-tests")
include("utbot-framework-test")
include("utbot-rd")
