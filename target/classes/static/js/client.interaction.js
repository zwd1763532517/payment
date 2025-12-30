/**
 * Created by cln on 2015/8/3.
 */

/**
 * 客户端（android，ios）调用的公共js方法
 * @param cmd 方法名
 * @param para 参数json的字符串
 */
function commonMethod(cmd, para) {
    if(cmd=="operatingSystem"){
        operatingSystem(para);
    }
}
/**
 *  设置操作系统，并且返回title
 * @param operatingSystem
 */
function operatingSystem(operatingSystem){
    var vOperatingSystem=eval("(" +operatingSystem+ ")");
    v_operatingSystem=vOperatingSystem;
}

var v_operatingSystem;
