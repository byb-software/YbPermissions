package com.ybpermissions

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.byb.ybpermissions.R
import com.library.core.YbPermission
import com.library.model.DialogShow
import com.library.model.PermissionRequestMode
import com.library.model.PermissionState
import com.library.ui.PermissionIntent

class TestActivity : AppCompatActivity() {

    private lateinit var activityText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        initView()
        initData()
    }

    private fun initData() {
        DefaultPermissionDemo() //全部使用默认的最简使用方式
        //checkerPermissionDemo() //对目标权限初次检测后进行各种定制化
        //dialogPermissionDemo()
    }

    private fun initView() {
        activityText = findViewById(R.id.activity_text)
    }

    /**
     *  @describe: YbPermission默认设置的最简使用
     *  @params:
     *  @return:
     */
    private fun DefaultPermissionDemo() {
        Log.i(
            "TestActivity",
            "DefaultPermissionDemo"
        )
        YbPermission.with(this)
            //申请摄像头与麦克风权限
            .permissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
            .resultCallback { stateData -> //发起权限申请后的权限状态

                Log.i(
                    "TestActivity",
                    "DefaultPermissionDemo:当前是否全部授予=${stateData.allGrantedPermission}," +
                            "初次被拒绝的权限是=${stateData.deniedPermission}，两次被拒绝的权限是=${stateData.permanentDeniedPermission} ，" +
                            "目前所有申请权限的已获取权限是=${stateData.state.filterValues { it == PermissionState.Granted }.keys}"
                )
                if (stateData.allGrantedPermission) { //全部授予了权限
                    activityText.text = "权限已全部授予"
                    Log.i("TestActivity", "DefaultPermissionDemo: 权限全部授予")
                    return@resultCallback
                }
                if (stateData.permanentDeniedPermission.isNotEmpty()) { //被永久拒绝的权限
                    //可手动跳转设置页，或添加弹窗提示后跳转等操作
                    PermissionIntent.navigationToSetting(this@TestActivity)
                }
            }
            .request() //必须结尾调用，发起整个流程
    }


    /**
     *  @describe: 对权限进行初步检测授予情况后，不同定制化需求的案例
     *  @params:
     *  @return:
     */
    private fun checkerPermissionDemo() {
        Log.i(
            "TestActivity",
            "checkerPermissionDemo"
        )
        YbPermission.with(this)
            .permissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)

            .checkerRequestAction { notGrantedPermissions, proceed -> //参数：未授予权限，根据参数设置调用后执行对应的发起模式
                Log.i(
                    "TestActivity",
                    "checkerPermissionDemo:检测到没有授权的权限是=$notGrantedPermissions "
                )
                // 模式1：检测后有权限未授予则发起权限申请
                proceed(PermissionRequestMode.RequestPermission) //执行发起权限申请逻辑
                //模式2：检测到有权限未授予则直接跳转系统设置页
                //proceed(PermissionRequestMode.SystemSettingPermission) //执行跳转系统权限设置页
                //模式3：检测到有权限未授予后中断框架流程，自定义跳转目标，也可额外添加自定义弹窗
                //关键点：不执行proceed()，自己手动设置跳转需要的系统设置页面
                /*this.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }) *///此处举例跳转到定位设置页
            }

            .resultCallback { stateData -> //发起权限申请后的权限状态

                Log.i(
                    "TestActivity",
                    "checkerPermissionDemo:当前是否全部授予=${stateData.allGrantedPermission}," +
                            "初次被拒绝的权限是=${stateData.deniedPermission}，两次被拒绝的权限是=${stateData.permanentDeniedPermission} ，" +
                            "目前所有申请权限的已获取权限是=${stateData.state.filterValues { it == PermissionState.Granted }.keys}"
                )
                if (stateData.allGrantedPermission) { //全部授予了权限
                    activityText.text = "权限已全部授予"
                    Log.i("TestActivity", "DefaultPermissionDemo: 权限全部授予")
                    return@resultCallback
                }
                if (stateData.permanentDeniedPermission.isNotEmpty()) { //被永久拒绝的权限
                    //可手动跳转设置页，或添加弹窗提示后跳转等操作
                    PermissionIntent.navigationToSetting(this@TestActivity)
                }
            }
            .request() //必须结尾调用，发起整个流程

    }

    /**
     *  @describe: 对权限进行权限申请被用户初次拒绝后，不同定制化需求的案例
     *  @params:
     *  @return:
     */
    private fun dialogPermissionDemo() {
        Log.i(
            "TestActivity",
            "dialogPermissionDemo"
        )
        YbPermission.with(this)
            .permissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
            //模式1：权限申请被用户初次拒绝后，使用默认的dialogt弹窗进行提示
            //.dialogShow{ denied,proceed -> //默认模式为DialogDefault
            .dialogShow( //自定义弹窗标题跟正文
                DialogShow.DialogDefault(
                    "标题",
                    "权限的正文"
                )
            ) { denied, proceed -> //被初次拒绝的权限，执行对应的权限开通模式
                Log.i("TestActivity", "dialogPermissionDemo:被用户初次拒绝的权限是:$denied ")
                //模式1-1：默认弹窗被用户点击确定后，发起权限申请
                //proceed(PermissionRequestMode.RequestPermission)

                //模式1-2：默认弹窗被用户点击确定后，跳转到系统权限设置页
                proceed(PermissionRequestMode.SystemSettingPermission)
            }

            //模式2：权限申请被用户初次拒绝后，使用自定义设置进行提示
            .dialogShow(DialogShow.DialogCustom) { denied, proceed -> //被初次拒绝的权限，执行对应的权限开通模式
                Log.i("TestActivity", "dialogPermissionDemo:被用户初次拒绝的权限是:$denied ")

                //模式2-1：不使用弹窗直接尝试获取权限(以下案例任选其一)
                //案例1.发起权限申请
                //proceed(PermissionRequestMode.RequestPermission)
                //案例2.直接跳转系统设置页
                //proceed(PermissionRequestMode.SystemSettingPermission)
                //案例3.不使用预设跳转而是自定义跳转目标页面
                /*this.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })*/

                //模式2-2：使用自定义弹窗尝试获取权限(以下案例任选其一)
                /*AlertDialog.Builder(this@TestActivity)
                    .setMessage("自定义提示文本")
                    .setPositiveButton("开启") { dialog, _ ->
                          //案例1.执行权限申请
                        proceed(PermissionRequestMode.RequestPermission)
                          //案例2.直接跳转系统设置页
                        //proceed(PermissionRequestMode.SystemSettingPermission)
                          //案例3.不使用预设跳转而是自定义跳转目标页面
                        *//*this.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })*//*
                        dialog.dismiss()
                    }
                    .setNegativeButton("取消") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()*/
            }
            .resultCallback { stateData -> //发起权限申请后的权限状态

                Log.i(
                    "TestActivity",
                    "dialogPermissionDemo:当前是否全部授予=${stateData.allGrantedPermission}," +
                            "初次被拒绝的权限是=${stateData.deniedPermission}，两次被拒绝的权限是=${stateData.permanentDeniedPermission} ，" +
                            "目前所有申请权限的已获取权限是=${stateData.state.filterValues { it == PermissionState.Granted }.keys}"
                )
                if (stateData.allGrantedPermission) { //全部授予了权限
                    activityText.text = "权限已全部授予"
                    Log.i("TestActivity", "DefaultPermissionDemo: 权限全部授予")
                    return@resultCallback
                }
                if (stateData.permanentDeniedPermission.isNotEmpty()) { //被永久拒绝的权限
                    //可手动跳转设置页，或添加弹窗提示后跳转等操作
                    PermissionIntent.navigationToSetting(this@TestActivity)
                }
            }
            .request() //必须结尾调用，发起整个流程
    }

}