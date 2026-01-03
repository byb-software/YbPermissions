package com.library.model

/**
 * @project: YbPermission
 * @class: PermissionRequestMode.class
 * @content:权限申请模式，分为动态权限申请与跳转系统设置页两种
 * @date: 2025/12/26
 * @author: byb
 */
enum class PermissionRequestMode {
     RequestPermission, //发起动态权限申请
    SystemSettingPermission  //跳转系统设置页由用户手动开启
}