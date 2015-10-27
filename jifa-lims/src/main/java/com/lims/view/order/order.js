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

// @Bind #submitOrderBtn.onClick
!function(self,arg,saveOrdersUpdateAction) {
    saveOrdersUpdateAction.execute(function(){
        //清空现有数据
    });
};
