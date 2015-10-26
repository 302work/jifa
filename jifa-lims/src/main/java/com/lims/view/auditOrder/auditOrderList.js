//查看详情
// @Bind #orderDetailBtn.onClick
!function(self,arg,orderDetailDialog,orderDetailAutoForm,saveOrderBtn) {
    orderDetailAutoForm.set("readOnly",true);
    saveOrderBtn.set("visible",false);
    orderDetailDialog.show();
};

//关闭
// @Bind #closeOrderDetailBtn.onClick
!function(self,arg,orderDetailDialog) {
    orderDetailDialog.hide();
};

//首次加载订单
// @Bind #dsOrder.onReady
!function(self,arg,dsOrder,dsOrderProject,dsOrderRecord) {
    var currOrder = dsOrder.getData("#");
    if(currOrder){
        dsOrderProject.set("parameter",currOrder.get("id")).flushAsync();
        dsOrderRecord.set("parameter",currOrder.get("id")).flushAsync();
    }
};

//行点击
// @Bind #orderDataGrid.onDataRowClick
!function(self,arg,dsOrder,dsOrderProject,dsOrderRecord) {
    var currOrder = dsOrder.getData("#");
    if(currOrder){
        dsOrderProject.set("parameter",currOrder.get("id")).flushAsync();
        dsOrderRecord.set("parameter",currOrder.get("id")).flushAsync();
    }
};

//记录的检测结果
// @Bind #recordResultBtn.onClick
!function(self,arg,resultDialog,dsOrderRecord,resultIFrame,picDataGrid) {
    var currRecord = dsOrderRecord.getData("#");
    if(currRecord){
        //
        var recordId = currRecord.get("id");
        var path = resultIFrame.get("path");
        var index = path.indexOf("?");
        if(index!=-1){
            path = path.substring(0,index);
        }
        path += "?recordId="+recordId;
        resultIFrame.set("path",path);

        //显示图片
        //原样
        var samplePic = currRecord.get("samplePic");
        //测试样
        var testSamplePic = currRecord.get("testSamplePic");
        var data = [{
            samplePic:samplePic,
            testSamplePic:testSamplePic
        }];
        picDataGrid.set("items",data);
        resultDialog.show();
    }
};

//加载检测结果之前，设置recordId
// @Bind #dsResult.beforeLoadData
!function(self,arg,dsOrderRecord) {
    var currRecord = dsOrderRecord.getData("#");
    if(currRecord){
        self.set("parameter",currRecord.get("id"));
    }
};

//关闭检测结果弹窗
// @Bind #closeResultDialogBtn.onClick
!function(self,arg,resultDialog) {
    resultDialog.hide();
};

// @Bind #picDataGrid.#samplePic.onRenderCell
!function(arg) {
    var samplePic = arg.data.samplePic;
    $(arg.dom).empty();
    if(samplePic){
        $(arg.dom).empty().xCreate({
            tagName: "IMG",
            src: samplePic,
            width:350
        });
    }
}

// @Bind #picDataGrid.#testSamplePic.onRenderCell
!function(arg) {
    var testSamplePic = arg.data.testSamplePic;
    $(arg.dom).empty();
    if(testSamplePic){
        $(arg.dom).empty().xCreate({
            tagName: "IMG",
            src: testSamplePic,
            width:350
        });
    }
}


//编辑
// @Bind #editOrderBtn.onClick
!function(self,arg,orderDetailDialog,orderDetailAutoForm,saveOrderBtn) {
    orderDetailAutoForm.set("readOnly",false);
    //设置某些字段不可编辑
    view.get("^readOnly").set("readOnly",true);
    saveOrderBtn.set("visible",true);
    orderDetailDialog.show();
};