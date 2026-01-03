package com.library.core

import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.library.checker.PermissionChecker
import com.library.model.DialogShow
import com.library.model.PermissionRequestMode
import com.library.model.PermissionState
import com.library.model.PermissionStateData
import com.library.register.PermissionRegister
import com.library.ui.DialogPermissionShow
import com.library.ui.PermissionIntent

/**
 * @project: YbPermission
 * @class: PermissionManager.class
 * @content:管理类，负责流程协运转
 * @date: 2025/12/26
 * @author: byb
 */
class PermissionManager(private val register: PermissionRegister) {

    //启动器
    private var launcher: ActivityResultLauncher<Array<String>>? = null

    /**
     *  @describe: 整个流程的发起者，对目标权限进行检测，然后将未授予的权限按模式进行不同方式的开通
     *  @params:
     *     permissions:需要开通的权限集合
     *     checkerRequestAction:需要开通的权限进行检测后的数据传递者
     *     dialogShow:请求权限初次被拒绝后展示给用户的提示框样式
     *     dialogCallback:请求权限被初次拒绝后的数据传递者
     *     resultCallback:每次发起权限申请后的数据传递者
     *  @return:
     */
    fun request(
        permissions: List<String>,
        checkerRequestAction: ((deniedPermission: List<String>, proceed: (permissionRequestMode: PermissionRequestMode) -> Unit) -> Unit)?,
        dialogShow: DialogShow,
        dialogCallback: ((denied: List<String>, proceed: (permissionRequestMode: PermissionRequestMode) -> Unit) -> Unit)?,
        resultCallback: ((permissionStateData: PermissionStateData) -> Unit)?,
    ) {

        //首先进行注册并传递注册返回结果
        launcher = register.register { result ->
            onResult(result, resultCallback, dialogShow, dialogCallback)
        }
        //获取权限检测后的结果
        val stateData = PermissionChecker.checkPermission(register.context, permissions)
        when {
            stateData.allGrantedPermission -> { //全部被授予
                Log.i("PermissionManager", "request:allGrantedPermission ")
                resultCallback?.invoke(stateData) //直接返回全授予结果，供外部直接开启目标功能的使用
                return
            }

            stateData.deniedPermission.isNotEmpty() -> { //没有被授予
                Log.i(
                    "PermissionManager",
                    "request:stateData=${stateData.deniedPermission} "
                )
                //根据选择的开通模式进行权限开通
                setRequestPermissionMode(
                    checkerRequestAction,
                    stateData
                )
            }
        }
    }


    /**
     *  @describe: 在初次检测有权限没有授予时，根据用户选择的方案进行权限开通操作
     *  @params:
     *     permissionRequestAction：用来传递未开通权限，并提供用户手动发起申请的时机
     *     stateData:没有被授权的权限
     *  @return:
     */
    private fun setRequestPermissionMode(
        checkerRequestAction: ((deniedPermission: List<String>, proceed: (permissionRequestMode: PermissionRequestMode) -> Unit) -> Unit)?,
        stateData: PermissionStateData
    ) {

        checkerRequestAction?.invoke(stateData.deniedPermission) { permissionRequestMode ->

            when (permissionRequestMode) {
                PermissionRequestMode.RequestPermission -> { //发起权限申请
                    Log.i(
                        "PermissionManager",
                        "setRequestPermissionMode: 初次检测后被手动调用发起动态申请"
                    )
                    //额外提供使用端自身决定发起请求的时机
                    launcher?.launch(stateData.deniedPermission.toTypedArray())

                }

                PermissionRequestMode.SystemSettingPermission -> {  //跳转设置页进行手动开启
                    Log.i(
                        "PermissionManager",
                        "setRequestPermissionMode: 初次检测后跳转系统设置页被手动调用"
                    )
                    PermissionIntent.navigationToSetting(register.context)

                }
            }
            //没有设置检测后的回调时默认发起权限申请
        } ?: launcher?.launch(stateData.deniedPermission.toTypedArray())


    }

    /**
     *  @describe: 用于对权限申请结果的返回值进行处理
     *  @params:
     *     result:权限申请结果的返回值
     *     resultCallback:权限申请结果的传递者
     *     dialogShow:权限申请初次被用户拒绝后的提示框样式
     *     dialogCallback:权限申请初次被用户拒绝后的数据传递者
     *  @return:
     */
    private fun onResult(
        result: Map<String, Boolean>,
        resultCallback: ((permissionStateData: PermissionStateData) -> Unit)?,
        dialogShow: DialogShow,
        dialogCallback: ((denied: List<String>, proceed: (permissionRequestMode: PermissionRequestMode) -> Unit) -> Unit)?
    ) {
        Log.i("PermissionManager", "dialogShow:$dialogShow ")
        val permissionState = mutableMapOf<String, PermissionState>()
        result.forEach { (permission, isGranted) ->
            Log.i(
                "PermissionManager",
                "onResult: 权限申请回调结果:$permission = $isGranted"
            )

            permissionState[permission] = when {
                isGranted -> {
                    Log.i("PermissionManager", "onResult: 权限申请回调结果为:true")
                    PermissionState.Granted
                }

                else -> {
                    Log.i("PermissionManager", "onResult: 权限申请回调结果为:false")

                    val isShow =
                        PermissionChecker.classifyDeniedActivity(register.owner, permission)
                            ?: return@forEach
                    Log.i("PermissionManager", "onResult: isShow=$isShow")
                    if (isShow) PermissionState.Denied
                    else PermissionState.PermanentDenied
                }
            }

        }
        val permissionStateData = PermissionStateData(permissionState)

        if (permissionStateData.deniedPermission.isNotEmpty()) { //此时可以通过弹窗提示暂时拒绝导致的后果
            Log.i("PermissionManager", "onResult: deniedPermission isNotEmpty")
            setShowDialog(dialogShow, permissionStateData, dialogCallback)
        }
        resultCallback?.invoke(permissionStateData)
    }


    /**
     *  @describe: 设置权限申请初次被用户拒绝后的不同样式逻辑
     *  @params:
     *     dialogShow:权限申请初次被用户拒绝后的提示框样式
     *     permissionStateData:权限被初次拒绝后的状态保存对象
     *     dialogCallback:权限申请初次被用户拒绝后的数据传递者
     *  @return:
     */
    private fun setShowDialog(
        dialogShow: DialogShow,
        permissionStateData: PermissionStateData,
        dialogCallback: ((denied: List<String>, proceed: (permissionRequestMode: PermissionRequestMode) -> Unit) -> Unit)?
    ) {
        Log.i("PermissionManager", "setShowDialog: dialogShow=$dialogShow")

        when (dialogShow) {
            is DialogShow.DialogDefault -> {
                //先使用回调保证dialog是否被点击都能将初次被拒绝权限传递出去
                dialogCallback?.invoke(permissionStateData.deniedPermission) { permissionRequestMode -> //使用时通过proceed()执行发起权限申请的逻辑
                    Log.i(
                        "PermissionManager",
                        "setShowDialog: 设置dialog回调时,permissionRequestMode=$permissionRequestMode"
                    )
                    DialogPermissionShow.setDialogDefault(
                        register.context,
                        dialogShow
                    ) { //默认dialog被点击确定后
                        if (permissionRequestMode == PermissionRequestMode.SystemSettingPermission) { //跳转系统设置页面
                            PermissionIntent.navigationToSetting(register.context)
                        } else { //进行动态申请
                            launcher?.launch(permissionStateData.deniedPermission.toTypedArray())
                        }
                    }
                } ?: apply { //默认最简模式不设置dialog回调时发起权限申请
                    Log.i("PermissionManager", "setShowDialog: 未设置dialog回调时")
                    DialogPermissionShow.setDialogDefault(
                        register.context,
                        dialogShow
                    ) {
                        launcher?.launch(permissionStateData.deniedPermission.toTypedArray())
                    }
                }
            }

            is DialogShow.DialogCustom -> {
                //使用自定义提示并提供权限申请发起
                Log.i("PermissionManager", "setShowDialog: 点击了自定义dialog确定")
                dialogCallback?.invoke(permissionStateData.deniedPermission) { permissionRequestMode ->  //使用时通过proceed()执行发起权限申请的逻辑
                    if (permissionRequestMode == PermissionRequestMode.SystemSettingPermission) { //跳转系统设置页面
                        PermissionIntent.navigationToSetting(register.context)
                    } else { //进行动态申请,默认的设置
                        launcher?.launch(permissionStateData.deniedPermission.toTypedArray())
                    }
                }
            }
        }
    }


}