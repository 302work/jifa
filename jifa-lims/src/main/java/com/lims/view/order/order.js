//给dsProjects绑定查询项目、方法标准的projectMethodStandardIds
// @Bind #dsOrders.onLoadData
!function(self,arg,dsOrders,dsProjects) {
    var datas=dsOrders.get("data");
    if(datas){
        var entity=datas.insert();
        //entity.set("projectMethodStandardIds","18");
    }
};

//点击弹出选择项目和方法标准的界面
// @Bind #selectProjectBtn.onClick
!function(self,arg,projectMethodStandardDialog) {
    projectMethodStandardDialog.show();
};

//选择项目和方法标准后，点击确定按钮事件
// @Bind #confirmBtn.onClick
!function(self,arg,projectMethodStandardIFrame,dsOrders,dsProjects,projectMethodStandardDialog) {
    var window=projectMethodStandardIFrame.getIFrameWindow();
    var dsMethodStandard=window.$id("dsMethodStandard").objects[0];
    var methodStandard=dsMethodStandard.getData("#");
    if(methodStandard){
        var selectedProjectMethodStandardId=methodStandard.get("projectMethodStandardId");
        var standardId=methodStandard.get("productStandardId");
    }else{
        dorado.MessageBox.alert("请选择项目以及其对应的方法标准！");
    }
    var data=dsOrders.getData("#");
    if(data){
        var projectMethodStandardIds=data.get("projectMethodStandardIds");
        if(!projectMethodStandardIds || projectMethodStandardIds==""){
            projectMethodStandardIds=selectedProjectMethodStandardId;
        }else{
            var index=projectMethodStandardIds.indexOf(selectedProjectMethodStandardId);
            if(index==-1){
                projectMethodStandardIds=projectMethodStandardIds+","+selectedProjectMethodStandardId;
            }
        }
        data.set("standardId",standardId);
        data.set("projectMethodStandardIds",projectMethodStandardIds);
        dsProjects.set("parameter",projectMethodStandardIds).flushAsync();
    }
    projectMethodStandardDialog.hide();
};

//选择项目和方法标准后，点击确定按钮事件
// @Bind #cancelBtn.onClick
!function(self,arg,projectMethodStandardDialog) {
    projectMethodStandardDialog.hide();
};

// @Bind #submitOrderBtn.onClick
!function(self,arg,saveOrdersUpdateAction,orderAutoForm,dsOrders,dsProjects) {
    var entity = orderAutoForm.get("entity");
    saveOrdersUpdateAction.execute(function(){
        //清空现有数据
        dsOrders.flush();
        dsProjects.set("parameter",null).flush();
        // debugger
        // orderView.refresh();
    });
};


//上传原样图片
// @Bind #uploadOldSamplePicAction.onFileUploaded
!function(arg,orderAutoForm) {
    var returnValue = arg.returnValue;//获取FileResolver方法返回的信息
    var entity = orderAutoForm.get("entity");
    entity.set("oldSamplePic", returnValue);
};

//删除项目
// @Bind #deleteProjectBtn.onClick
!function(self,arg,dsProjects,dsOrders) {
    var orderData = dsOrders.getData("#");
    var currData= dsProjects.getData("#");
    var projectMethodStandardId;
    if(currData){
        var projectMethodStandardId = currData.get("projectMethodStandardId");
        var allData = dsProjects.getData();
        var allPsId = [];
        allData.each(function(data){
            var psId = data.get("projectMethodStandardId");
            if(psId != projectMethodStandardId){
                allPsId.push(psId);
            }
        });
        var projectMethodStandardIds = allPsId.join(",");
        orderData.set("projectMethodStandardIds",projectMethodStandardIds);
        dsProjects.set("parameter",projectMethodStandardIds).flushAsync();
    }

};