//作废数据显示红色
//@Bind #resultDataGrid.onRenderRow
!function(arg) {
    //状态
    var status = arg.data.get("status");
    //作废数据
    if(status==2){
        arg.dom.style.color = "red";
    }
    arg.processDefault = true;
};