

# YbPermissions

 - **可链式调用**
 - **没有任何反射实现**
 - **高度可定制**

高度定制化的权限申请框架，使用更加方便，采用kotlin语言进行开发，以onRequestPermissionsResult的api作为基础进行了扩展，可以在初次检测权限与发起申请后用户初次拒绝时进行高度自定义等模式的选择。

 **流程为：检查权限 → 授权?(a.) → 请求 → 用户是否允许(b.) → 是否可开启功能(c.)**

 功能点：    

 - **a点.**
--->未授权：
直接跳转系统权限页；
发起网络请求；
弹出自定义提示框后进行前两项的功能
--->授权：
直接通过函数返回全授权标志，进行目标功能展示
 - **b点.**
--->用户不允许但可提示：
默认用Alertdialog提示，可更改title与message内容；
不进行提示；
使用自定义弹窗进行提示，确定监听中可选择跳转系统页或再次发起申请功能，也可自定义接下来的流程处理
--->用户不允许且不可提示：
通过函数返回具体权限，供自由处理
--->允许：
用户允许后即通过函数返回全授权标记，进行目标功能展示
 - **c点.**
--->权限全授予则可开启对应功能：
函数返回结果中allGrantedPermission返回为true则表示全部权限已获取，可进行对应功能展示
--->权限没有全开启：
权限申请流程完毕后，会将所有被永久拒绝与被初次拒绝权限返回，可根据需要进行对应功能跳转

# 依赖

> 项目级Settins.gradle中需添加jitpack仓库：

```kotlin
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
```

> 模块级build.gradle中添加依赖:

```kotlin
 implementation 'com.github.byb-software:YbPermission:1.1.0'
```

# 使用
下面以摄像头与麦克风权限申请为例。（兼容了activity/fragment，两者页面的所有使用方式相同）
## 注册AndroidManifest.xml

```kotlin
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.CAMERA" />
```
## 最快可实现方案

```kotlin
YbPermission.with(this)
            //设置摄像头与麦克风权限
            .permissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
            //获取权限申请后的返回结果
            .resultCallback { stateData -> //发起权限申请后的权限状态
                Log.i(
                    "TestActivity",
                    "DefaultPermissionDemo:当前是否全部授予=${stateData.allGrantedPermission}," +
                            "初次被拒绝的权限是=${stateData.deniedPermission}，两次被拒绝的权限是=${stateData.permanentDeniedPermission} ，" +
                            "目前所有申请权限的已获取权限是=${stateData.state.filterValues { it == PermissionState.Granted }.keys}"
                )
                if (stateData.allGrantedPermission) { //全部授予了权限
                    secondTxt.text = "权限已全部授予"
                    Log.i("TestActivity", "DefaultPermissionDemo: 权限全部授予")
                    return@resultCallback
                }
                if (stateData.permanentDeniedPermission.isNotEmpty()) { //被永久拒绝的权限
                    //可手动跳转设置页，或添加弹窗提示后跳转等操作
                    PermissionIntent.navigationToSetting(this@SecondActivity)
                }
            }
            //发起
            .request() //必须结尾调用，发起整个流程
```

> 以下是可以定制化需求设置的两个api说明(checkerRequestAction 、dialogShow)，可以根据自身需求进行组合使用，

## 权限进行初次检测后进行定制化设置

### 权限检测后将未授权的进行发起权限申请操作

```kotlin
    YbPermission.with(this)
            //设置申请的权限
            .permissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
            //设置权限检测后的拦截操作
            .checkerRequestAction { notGrantedPermissions, proceed -> //参数：未授予权限，根据参数设置调用后执行对应的发起模式
                Log.i(
                    "TestActivity",
                    "checkerPermissionDemo:检测到没有授权的权限是=$notGrantedPermissions "
                )

                proceed(PermissionRequestMode.RequestPermission) //执行发起权限申请逻辑

            }
            //获取权限申请后的结果
            .resultCallback { stateData -> //发起权限申请后的权限状态

                Log.i(
                    "TestActivity",
                    "checkerPermissionDemo:当前是否全部授予=${stateData.allGrantedPermission}," +
                            "初次被拒绝的权限是=${stateData.deniedPermission}，两次被拒绝的权限是=${stateData.permanentDeniedPermission} ，" +
                            "目前所有申请权限的已获取权限是=${stateData.state.filterValues { it == PermissionState.Granted }.keys}"
                )
                if (stateData.allGrantedPermission) { //全部授予了权限
                    secondTxt.text = "权限已全部授予"
                    Log.i("TestActivity", "DefaultPermissionDemo: 权限全部授予")
                    return@resultCallback
                }
                if (stateData.permanentDeniedPermission.isNotEmpty()) { //被永久拒绝的权限
                    //可手动跳转设置页，或添加弹窗提示后跳转等操作
                    PermissionIntent.navigationToSetting(this@SecondActivity)
                }
            }
            .request() //必须结尾调用，发起整个流程
```

### 权限检测后将未授权的权限直接跳转系统权限设置页

```kotlin
 .checkerRequestAction { notGrantedPermissions, proceed -> //参数：未授予权限，根据参数设置调用后执行对应的发起模式
                Log.i(
                    "TestActivity",
                    "checkerPermissionDemo:检测到没有授权的权限是=$notGrantedPermissions "
                )

                //执行跳转系统权限设置页
                proceed(PermissionRequestMode.SystemSettingPermission) 

            }
```

### 检测到有权限没有授权后，中断框架逻辑，自定义后续设置

```kotlin
.checkerRequestAction { notGrantedPermissions, proceed -> //参数：未授予权限，根据参数设置调用后执行对应的发起模式
                Log.i(
                    "TestActivity",
                    "checkerPermissionDemo:检测到没有授权的权限是=$notGrantedPermissions "
                )
                //此处跳转到定位开启页，不调用proceed(),也可以在这里设置弹窗进行提示
                this.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }
```

## 权限申请被用户初次拒绝后定制化设置
### 权限申请被用户初次绝后使用默认dialog提示，同意后进行再次申请

```kotlin
 YbPermission.with(this)
            //需要开通的权限
            .permissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
            //用户初次拒绝权限后的提示框拦截
            //.dialogShow( DialogShow.DialogDefault("标题","权限的正文")) //可以自定义弹窗标题与正文
            .dialogShow
            { denied, proceed -> //被初次拒绝的权限，执行对应的权限开通模式
                Log.i("TestActivity", "dialogPermissionDemo:被用户初次拒绝的权限是:$denied ")
                //此处为默认dialog的确定按钮点击后的逻辑
                proceed(PermissionRequestMode.RequestPermission)// 再次发起申请
            }
            //每次权限申请后的状态回调    
            .resultCallback { stateData -> //发起权限申请后的权限状态

                Log.i(
                    "TestActivity",
                    "dialogPermissionDemo:当前是否全部授予=${stateData.allGrantedPermission}," +
                            "初次被拒绝的权限是=${stateData.deniedPermission}，两次被拒绝的权限是=${stateData.permanentDeniedPermission} ，" +
                            "目前所有申请权限的已获取权限是=${stateData.state.filterValues { it == PermissionState.Granted }.keys}"
                )
                if (stateData.allGrantedPermission) { //全部授予了权限
                    secondTxt.text = "权限已全部授予"
                    Log.i("TestActivity", "DefaultPermissionDemo: 权限全部授予")
                    return@resultCallback
                }
                if (stateData.permanentDeniedPermission.isNotEmpty()) { //被永久拒绝的权限
                    //可手动跳转设置页，或添加弹窗提示后跳转等操作
                    PermissionIntent.navigationToSetting(this@SecondActivity)
                }
            }
            .request() //必须结尾调用，发起整个流程
```
### 权限申请被用户初次绝后使用默认dialog提示，同意后直接跳转系统权限设置页

```kotlin
.dialogShow( DialogShow.DialogDefault("标题","权限的正文"))
            { denied, proceed -> //被初次拒绝的权限，执行对应的权限开通模式
                Log.i("TestActivity", "dialogPermissionDemo:被用户初次拒绝的权限是:$denied ")
                proceed(PermissionRequestMode.SystemSettingPermission) //跳转系统设置页面
            }
```
### 使用自定义模式不进行弹窗提示，直接再次发起权限申请

```kotlin
.dialogShow( DialogShow.DialogCustom) //改为自定义模式
            { denied, proceed -> //被初次拒绝的权限，执行对应的权限开通模式
                Log.i("TestActivity", "dialogPermissionDemo:被用户初次拒绝的权限是:$denied ")
                proceed(PermissionRequestMode.RequestPermission) //再次发起申请
            }
```
### 使用自定义模式不进行弹窗提示，直接跳转系统权限设置页

```kotlin
.dialogShow( DialogShow.DialogCustom) //改为自定义模式
            { denied, proceed -> //被初次拒绝的权限，执行对应的权限开通模式
                Log.i("TestActivity", "dialogPermissionDemo:被用户初次拒绝的权限是:$denied ")
                proceed(PermissionRequestMode.SystemSettingPermission) //跳转系统设置页
            }
```
### 使用自定义模式不进行弹窗提示，中断框架逻辑，自定义跳转目标

```kotlin
.dialogShow( DialogShow.DialogCustom) //改为自定义模式
            { denied, proceed -> //被初次拒绝的权限，执行对应的权限开通模式
                Log.i("TestActivity", "dialogPermissionDemo:被用户初次拒绝的权限是:$denied ")
                //不调用proceed() 此处跳转到定位界面  同样可以设置自定义弹窗等
                this.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }
```

### 使用自定义弹窗模式，设置弹窗内容，点击确定后再次发起权限申请

```kotlin
  .dialogShow(DialogShow.DialogCustom) //改为自定义模式
            { denied, proceed -> //被初次拒绝的权限，执行对应的权限开通模式
                Log.i("TestActivity", "dialogPermissionDemo:被用户初次拒绝的权限是:$denied ")
                AlertDialog.Builder(this@SecondActivity)
                    .setMessage("自定义提示文本")
                    .setPositiveButton("开启") { dialog, _ ->
                        //执行权限申请
                        proceed(PermissionRequestMode.RequestPermission)

                        dialog.dismiss()
                    }
                    .setNegativeButton("取消") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
```
### 使用自定义弹窗模式，设置弹窗内容，点击确定后直接跳转系统设置页

```kotlin
.dialogShow(DialogShow.DialogCustom) //改为自定义模式
            { denied, proceed -> //被初次拒绝的权限，执行对应的权限开通模式
                Log.i("TestActivity", "dialogPermissionDemo:被用户初次拒绝的权限是:$denied ")
                AlertDialog.Builder(this@SecondActivity)
                    .setMessage("自定义提示文本")
                    .setPositiveButton("开启") { dialog, _ ->
                        //跳转系统权限设置页,同样可以不调用proceed()而是自定义跳转的目标
                        proceed(PermissionRequestMode.SystemSettingPermission)

                        dialog.dismiss()
                    }
                    .setNegativeButton("取消") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
```

# 优点

 - 没有使用反射，可以提高性能
 - 使用kotlin开发可以快速接入
 - 同步了activity/fragment使用，两者用法完全一致

# 现状
目前该框架还在完善中，后续有更好的想法会加入其中，或者发现的bug也会进行修复，有遇到其他问题的，欢迎及时留言，只要看到了就会尽力解决的。
# 许可证
MIT License

Copyright (c) 2026 byb

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
