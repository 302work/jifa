//增加一打开自动加载下面方法标准的内容
// @Bind #dsStandards.onReady
!function(self,arg) {
    var  dsStandards=view.id("dsStandards");
    var  sdata=dsStandards.getData("#");
    var dsMethodStandards=view.id("dsMethodStandards");
    var dsStandardIndexs=view.id("dsStandardIndexs");
    if(sdata){
        dsMethodStandards.set("parameter",sdata.get("id")).flushAsync();
        dsStandardIndexs.set("parameter",sdata.get("id")).flushAsync();
    }
};

//方法标准DataPilot新增，删除事件
// @Bind #methodStandardsDataPilot.onSubControlAction
!function(self,arg) {
    var action = view.get("#msUpdateAction");
    var dsStandards = view.id("dsStandards");
    var sData = dsStandards.getData("#");
    if(sData){
        var standardId=sData.get("id");
    }

    var datas=view.id("dsMethodStandards").get("data");

    switch(arg.code){
        case "+":{
            var entity=datas.insert();
            entity.set("standardId",standardId);
            view.get("#methodStandardDialog").show();
            arg.processDefault=false;
            break;
        }
        case "-":{
            dorado.MessageBox.confirm("您真的想删除当前数据吗?",function(){
                datas.remove();
                action.execute();
            });
            arg.processDefault=false;
            break;
        }
    }
};

//编辑方法标准
// @Bind #editMsBtn.onClick
!function(self,arg) {
    var dsMethodStandards =view.id("dsMethodStandards");
    var currData=dsMethodStandards.getData("#");
    if(currData){
        view.id("methodStandardDialog").show();
    }
};

//保存方法标准
// @Bind #saveMsBtn.onClick
!function(self,arg) {
    var msUpdateAction=view.id("msUpdateAction");
    var methodStandardDialog=view.id("methodStandardDialog");
    var dsStandards=view.id("dsStandards");
    var standardId=dsStandards.getData("#").get("id");
    var dsMethodStandards=view.id("dsMethodStandards");
    msUpdateAction.execute(function(){
        methodStandardDialog.hide();
        dsMethodStandards.set("parameter",standardId).flushAsync();
    });
};

//取消新增方法标准
// @Bind #cancelMsBtn.onClick
!function(self,arg) {
    var methodStandardDialog=view.id("methodStandardDialog");
    var dsMethodStandards=view.id("dsMethodStandards");
    var data=dsMethodStandards.get("data");
    data.cancel();
    methodStandardDialog.hide();
};

//standardIndex的DataPilot
// @Bind #siDataPilot.onSubControlAction
!function(self,arg) {
    var action = view.get("#siUpdateAction");
    var dsStandards = view.id("dsStandards");
    var sData = dsStandards.getData("#");
    if(sData){
        var standardId=sData.get("id");
    }

    var datas=view.id("dsStandardIndexs").get("data");

    switch(arg.code){
        case "+":{
            var entity=datas.insert();
            entity.set("standardId",standardId);
            view.get("#standardIndexDialog").show();
            arg.processDefault=false;
            break;
        }
        case "-":{
            dorado.MessageBox.confirm("您真的想删除当前数据吗?",function(){
                datas.remove();
                action.execute();
            });
            arg.processDefault=false;
            break;
        }
    }
};


//编辑检测标准的指标值
// @Bind #editStandardIndexBtn.onClick
!function(self,arg) {
    var dsStandardIndexs =view.id("dsStandardIndexs");
    var currData=dsStandardIndexs.getData("#");
    if(currData){
        view.id("standardIndexDialog").show();
    }
};


//保存检测标准的指标值
// @Bind #saveSiBtn.onClick
!function(self,arg) {
    var siUpdateAction=view.id("siUpdateAction");
    var standardIndexDialog=view.id("standardIndexDialog");
    var dsStandards=view.id("dsStandards");
    var standardId=dsStandards.getData("#").get("id");
    var dsStandardIndexs=view.id("dsStandardIndexs");
    siUpdateAction.execute(function(){
        standardIndexDialog.hide();
        dsStandardIndexs.set("parameter",standardId).flushAsync();
    });
};


//取消保存检测标准的指标值
// @Bind #cancelSiBtn.onClick
!function(self,arg) {
    var standardIndexDialog=view.id("standardIndexDialog");
    var dsStandardIndexs=view.id("dsStandardIndexs");
    var data=dsStandardIndexs.get("data");
    data.cancel();
    standardIndexDialog.hide();
};


//产品标准DataPilot
// @Bind #standardDataPilot.onSubControlAction
!function(self,arg) {
    var datas = view.get("#dsStandards.data");
    var action = view.get("#standardUpdateAction");
    switch(arg.code){
        case "+":{
            datas.insert();
            view.get("#standardDialog").show();
            arg.processDefault=false;
            break;
        }
        case "-":{
            dorado.MessageBox.confirm("您真的想删除当前数据吗?",function(){
                datas.remove();
                action.execute();
            });
            arg.processDefault=false;
            break;
        }
    }
};


//编辑产品标准
// @Bind #btEdit.onClick
!function(self,arg) {
    var dsStandards = view.id("dsStandards");
    var currData = dsStandards.getData("#");
    if(currData){
        view.id("standardDialog").show();
    }
};


//根据选择的行加载方法标准和项目指标
// @Bind #standardDaraGrid.onDataRowClick
!function(self,arg) {
    var  dsStandards=view.id("dsStandards");
    var  sdata=dsStandards.getData("#");
    var dsMethodStandards=view.id("dsMethodStandards");
    var dsStandardIndexs=view.id("dsStandardIndexs");
    if(sdata){
        dsMethodStandards.set("parameter",sdata.get("id")).flushAsync();
        dsStandardIndexs.set("parameter",sdata.get("id")).flushAsync();
    }
};

//保存产品标准
// @Bind #saveStandardBtn.onClick
!function(self,arg) {
    var standardUpdateAction=view.id("standardUpdateAction");
    var standardDialog=view.id("standardDialog");
    var dsStandards=view.id("dsStandards");
    standardUpdateAction.execute(function(){
        standardDialog.hide();
        dsStandards.flushAsync();
    });
};


//取消保存产品标准
// @Bind #closeStanardardBtn.onClick
!function(self,arg) {
    var standardDialog=view.id("standardDialog");
    var datas = view.get("#dsStandards.data");
    datas.cancel();
    standardDialog.hide();
};


//取消保存产品标准
// @Bind #standardDialog.onClose
!function(self,arg) {
    var datas = view.get("#dsStandards.data");
    datas.cancel();
};

//取消保存方法标准
// @Bind #methodStandardDialog.onClose
!function(self,arg) {
    var dsMethodStandards=view.id("dsMethodStandards");
    var data=dsMethodStandards.get("data");
    data.cancel();
};

//取消保存方法标准
// @Bind #standardIndexDialog.onClose
!function(self,arg) {
    var dsStandardIndexs=view.id("dsStandardIndexs");
    var data=dsStandardIndexs.get("data");
    data.cancel();
};

