<!DOCTYPE html>

<!--[if IE 8]> <html lang="en" class="ie8 no-js"> <![endif]-->
<!--[if IE 9]> <html lang="en" class="ie9 no-js"> <![endif]-->
<!--[if !IE]><!-->
<html lang="en" class="no-js">
<!--<![endif]-->
<!-- BEGIN HEAD -->
<head>
    <meta content="text/html; charset=utf-8" http-equiv="Content-Type">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0;">
    <meta name="apple-mobile-web-app-capable" content="no">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="format-detection" content="telephone=no">
    <title></title>
</head>
<!-- END HEAD -->
<body>
<script src="/js/jquery.min.js" type="text/javascript"></script>
<script type="text/javascript">
    var u = navigator.userAgent;
    var parameter = '{"flag":"${flag}"}';

    if (u.indexOf("Android") > -1 || u.indexOf("Linux") > -1) {//安卓手机
        JavaScriptInterface.onRequest("PaySuccess", parameter);
    } else if (u.indexOf("iPhone") > -1) {//苹果手机
        window.location.href = "objc::PaySuccess::"+parameter;
    }
</script>
</body>
</html>
