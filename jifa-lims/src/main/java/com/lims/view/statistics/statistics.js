// @Bind #queryBtn.onClick
!function(self,arg,dsStatisticInfo,testUserName,testDateStart,testDateEnd,client){
    var testUserNameValue=testUserName.get("value");
    var testDateStartValue=testDateStart.get("value");
    var testDateEndValue=testDateEnd.get("value");
    var clientValue=client.get("value");
    var params=new Object();
    if(testUserNameValue){
        params["testUserName"]=testUserNameValue;
    }
    if(testDateStartValue){
        params["testDateStart"]=testDateStartValue;
    }
    if(testDateEndValue){
        params["testDateEnd"]=testDateEndValue;
    }
    if(clientValue){
        params["client"]=clientValue;
    }
    dsStatisticInfo.set("parameter",params);
    dsStatisticInfo.flushAsync();
}
