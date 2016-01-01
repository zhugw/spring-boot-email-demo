<html>
<body>
        <h3>今日订餐人数统计</h3>
        <div>
           共<font color="red" size="10">${users?size}</font> 人加一<br>
           订餐人员清单：
           <#list users as user>
               <p>${user.name}
           </#list>
           <br>
           <br>
           每天下午16:30 发送统计订餐人数邮件到dev_server@helijia.com
        </div>
    </body>
</html>