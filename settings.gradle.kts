pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()

        //Google Maven 仓库的镜像，存放 Android官方依赖（如androidx、com.android.tools系列、Google服务库com.google.android.gms等）
        maven { url = uri("https://maven.aliyun.com/repository/google/") }
        //Gradle Plugin 仓库的镜像，存放 Android Gradle 插件（AGP）、Gradle等官方插件，需要精确版配置可以删除下面
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }

        // 极简配置方式：阿里云聚合仓库（覆盖 google、central、gradle-plugin 所有核心需求）
        maven { url = uri("https://maven.aliyun.com/repository/public/") }

        //jitpack仓库
        maven { url = uri("https://jitpack.io") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://maven.aliyun.com/repository/public/") }
        //maven { url = uri("https://maven.aliyun.com/repository/google/") }
        //maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "YbPermissions"
include(":sample")
include(":library")
