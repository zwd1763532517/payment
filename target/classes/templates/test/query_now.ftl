<!DOCTYPE html>
<head>
    <meta content="text/html; charset=utf-8" http-equiv="Content-Type">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <meta name="apple-mobile-web-app-capable" content="no">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="format-detection" content="telephone=no">
    <title>支付测试</title>
    <style>
        input{
            width: 400px;
            height: 35px;
        }
        table th {
            text-align: right;
        }
        input {
            background-color: #eeeeee;
            border: 0px;
        }
    </style>
</head>
<body>
<div>
    <h3>查询订单</h3>
    <div>
        <form name="subForm" action="/test/query_order_status_now" method="post">
            <table>
                <tbody>
                <tr>
                    <th>支付渠道【payChannel】:</th>
                    <td><input type=text name="payChannel" value="" /> <span>*</span></td>
                </tr>
                <tr>
                    <th>支付产品【payProduct】：</th>
                    <td><input type=text name="payProduct" value="" /> <span>*</span></td>
                </tr>
                <tr>
                    <th>订单号【orderNumber】：</th>
                    <td ><input type="text" name="orderNumber" value="" /> <span>*</span></td>
                </tr>
                <tr>
                    <th></th>
                    <td><input type="submit" value="提交" style="background-color: #e30045;" /></td>
                </tr>
                </tbody>
            </table>
        </form>
    </div>
</div>
</body>
</html>