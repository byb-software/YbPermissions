package com.ybpermissions

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.byb.ybpermissions.R
import com.library.core.YbPermission
import com.library.model.DialogShow
import com.library.model.PermissionRequestMode
import com.library.model.PermissionState
import com.library.ui.PermissionIntent


class TestFragment : Fragment() {

    private lateinit var fragmentText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        initData()
    }

    /**
     * 该方法中展示的三个方法除了DefaultPermissionDemo是最快使用方式外，
     * checkerPermissionDemo跟dialogPermissionDemo两种代表的是可以自定义的两个点位，为了避免混淆每个方法只展示了单独
     * 点位设置的方式，但如果有需要，可以参考这两个方法进行同时设置，这样就可以满足更多的需求
     *
     */
    private fun initData() {
        //DefaultPermissionDemo()
        //checkerPermissionDemo()
        dialogPermissionDemo()

    }

    private fun initView(view: View) {
        fragmentText = view.findViewById(R.id.fragment_text)
    }


    /**
     *  @describe: YbPermission默认设置的最简使用
     *  @params:
     *  @return:
     */
    private fun DefaultPermissionDemo() {
        Log.i(
            "TestFragment",
            "DefaultPermissionDemo"
        )
        YbPermission.with(this)
            //申请摄像头与麦克风权限
            .permissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
            .resultCallback { stateData -> //发起权限申请后的权限状态

                Log.i(
                    "TestFragment",
                    "DefaultPermissionDemo:当前是否全部授予=${stateData.allGrantedPermission}," +
                            "初次被拒绝的权限是=${stateData.deniedPermission}，两次被拒绝的权限是=${stateData.permanentDeniedPermission} ，" +
                            "目前所有申请权限的已获取权限是=${stateData.state.filterValues { it == PermissionState.Granted }.keys}"
                )
                if (stateData.allGrantedPermission) { //全部授予了权限
                    fragmentText.text = "权限已全部授予"
                    Log.i("TestFragment", "DefaultPermissionDemo: 权限全部授予")
                    return@resultCallback
                }
                if (stateData.permanentDeniedPermission.isNotEmpty()) { //被永久拒绝的权限
                    //可手动跳转设置页，或添加弹窗提示后跳转等操作
                    PermissionIntent.navigationToSetting(requireContext())
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
            "TestFragment",
            "checkerPermissionDemo"
        )
        YbPermission.with(this)
            .permissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)

            .checkerRequestAction { notGrantedPermissions, proceed -> //参数：未授予权限，根据参数设置调用后执行对应的发起模式
                Log.i(
                    "TestFragment",
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
                    "TestFragment",
                    "checkerPermissionDemo:当前是否全部授予=${stateData.allGrantedPermission}," +
                            "初次被拒绝的权限是=${stateData.deniedPermission}，两次被拒绝的权限是=${stateData.permanentDeniedPermission} ，" +
                            "目前所有申请权限的已获取权限是=${stateData.state.filterValues { it == PermissionState.Granted }.keys}"
                )
                if (stateData.allGrantedPermission) { //全部授予了权限
                    fragmentText.text = "权限已全部授予"
                    Log.i("TestFragment", "DefaultPermissionDemo: 权限全部授予")
                    return@resultCallback
                }
                if (stateData.permanentDeniedPermission.isNotEmpty()) { //被永久拒绝的权限
                    //可手动跳转设置页，或添加弹窗提示后跳转等操作
                    PermissionIntent.navigationToSetting(requireContext())
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
            "TestFragment",
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
                Log.i("TestFragment", "dialogPermissionDemo:被用户初次拒绝的权限是:$denied ")
                //模式1-1：默认弹窗被用户点击确定后，发起权限申请
                //proceed(PermissionRequestMode.RequestPermission)

                //模式1-2：默认弹窗被用户点击确定后，跳转到系统权限设置页
                proceed(PermissionRequestMode.SystemSettingPermission)
            }

            //模式2：权限申请被用户初次拒绝后，使用自定义设置进行提示
            /*.dialogShow(DialogShow.DialogCustom) { denied, proceed -> //被初次拒绝的权限，执行对应的权限开通模式
                Log.i("TestFragment", "dialogPermissionDemo:被用户初次拒绝的权限是:$denied ")

                //模式2-1：不使用弹窗直接尝试获取权限(以下案例任选其一)
                  //案例1.发起权限申请
                //proceed(PermissionRequestMode.RequestPermission)
                  //案例2.直接跳转系统设置页
                //proceed(PermissionRequestMode.SystemSettingPermission)
                  //案例3.不使用预设跳转而是自定义跳转目标页面
                *//*this.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })*//*

                //模式2-2：使用自定义弹窗尝试获取权限(以下案例任选其一)
                *//*AlertDialog.Builder(requireContext())
                    .setMessage("自定义提示文本")
                    .setPositiveButton("开启") { dialog, _ ->
                          //案例1.执行权限申请
                        proceed(PermissionRequestMode.RequestPermission)
                          //案例2.直接跳转系统设置页
                        //proceed(PermissionRequestMode.SystemSettingPermission)
                          //案例3.不使用预设跳转而是自定义跳转目标页面
                        *//**//*this.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })*//**//*
                        dialog.dismiss()
                    }
                    .setNegativeButton("取消") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()*//*
            }*/
            .resultCallback { stateData -> //发起权限申请后的权限状态

                Log.i(
                    "TestFragment",
                    "dialogPermissionDemo:当前是否全部授予=${stateData.allGrantedPermission}," +
                            "初次被拒绝的权限是=${stateData.deniedPermission}，两次被拒绝的权限是=${stateData.permanentDeniedPermission} ，" +
                            "目前所有申请权限的已获取权限是=${stateData.state.filterValues { it == PermissionState.Granted }.keys}"
                )
                if (stateData.allGrantedPermission) { //全部授予了权限
                    fragmentText.text = "权限已全部授予"
                    Log.i("TestFragment", "DefaultPermissionDemo: 权限全部授予")
                    return@resultCallback
                }
                if (stateData.permanentDeniedPermission.isNotEmpty()) { //被永久拒绝的权限
                    //可手动跳转设置页，或添加弹窗提示后跳转等操作
                    PermissionIntent.navigationToSetting(requireContext())
                }
            }
            .request() //必须结尾调用，发起整个流程
    }

}