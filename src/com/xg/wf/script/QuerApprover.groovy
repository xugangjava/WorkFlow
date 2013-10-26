package com.xg.wf.script

sql = db.SQL();
def showapprovers=""
try {
    approvers = ""
    switch (callerId) {
        case "admin":
            showapprovers = "securitysir"
            break
        case "securitysir":
            showapprovers = "auditor"
            break
        case "auditor":
            showapprovers = "admin"
            break
        default:
            caller = sql.firstRow("SELECT  ID_ ,FIRST_ , UID_ ,LV_ ,UNO_ ,PUNO_  " +
                    "   FROM    dbo.ACT_ID_USER WHERE ID_='" + callerId+"'")
            IsDisableApprovalSelfEmail = db.Config(sql, 26).equals("True")
            sqlStr="SELECT USER_ID_ FROM dbo.ACT_ID_USER_GROUP WHERE  " +
                    "((GROUP_ID_='approver2' OR GROUP_ID_='leader') AND UNO_ LIKE  '"+caller.PUNO_+"%' )  " +
                    "OR (GROUP_ID_='approver3' AND UNO_='"+caller.UNO_+"') AND USTATUS_=1 AND LV_<"+caller.LV_+" "
            if(IsDisableApprovalSelfEmail){
                sqlStr+=" AND USER_ID_!="+caller.ID_
            }
            sql.eachRow(sqlStr){
                if(!showapprovers.isEmpty())approvers+=","
                showapprovers +="${it.USER_ID_}"
            };
    }
} finally {
    sql.close()
}
execution.setVariable("showapprovers", showapprovers)