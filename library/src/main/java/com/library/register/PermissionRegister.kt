package com.library.register

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.library.core.PermissionManager

/**
 * @project: YbPermission
 * @class: PermissionHost.class
 * @content:用于对接收的activity/fragment进行处理与register的注册处理
 * @date: 2025/12/26
 * @author: byb
 */
class PermissionRegister(
    activity: ComponentActivity?,
    fragment: Fragment?
) {

    //可释放引用
    private var _owner: Any? =
        fragment ?: activity ?: throw IllegalStateException("请配置 Activity/Fragment 载体")

    //不持有避免内存泄漏
    val owner: Any
        get() = _owner ?: throw IllegalStateException("载体未配置/已释放")

    //context获取
    val context: Context
        get() = if (owner is Fragment) (owner as Fragment).requireContext() else (owner as Context)

    //launcher获取
    private lateinit var launcher: ActivityResultLauncher<Array<String>>

    /**
     *  @describe: 注册返回结果
     *  @params: 返回结果的数据类型
     *  @return: 返回的对象用来启动申请
     */
    fun register(registerCallback: (Map<String, Boolean>) -> Unit) {

        if ((owner as LifecycleOwner).lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
            throw IllegalStateException("请在 onStart()生命周期或之前使用with(this)，禁止在页面可见后注册")

        //注册返回结果
        launcher = when (owner) {
            is Fragment -> (owner as Fragment).registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions(), registerCallback
            )

            is AppCompatActivity -> (owner as ComponentActivity).registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions(), registerCallback
            )

            else -> throw IllegalStateException("请配置 Activity/Fragment 为载体")

        }
    }

    /**
     *  @describe: 用于对外提供权限的申请
     *  @params:
     *  @return:
     */
    fun launch(permission: Array<String>) {
        if (!::launcher.isInitialized) {
            throw IllegalStateException("未调用register进行注册操作")
        }
        launcher.launch(permission)
    }

    /**
     *  @describe: 供外部清理持有的引用
     *  @params:
     *  @return:
     */
    fun bind(permissionManager: PermissionManager) {
        (owner as LifecycleOwner).lifecycle.addObserver(object : DefaultLifecycleObserver{
            override fun onDestroy(owner: LifecycleOwner) {
                permissionManager.clear()
                _owner = null
            }
        })
    }

}