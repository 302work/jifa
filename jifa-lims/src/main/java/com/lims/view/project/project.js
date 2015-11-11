var dirtyEntityNum = 0;

function refreshActions() {
	var node = view.get("#dataTreeProject.currentNode");
	view.set({
		"^onCurrentNode.disabled" : node == null
	});
	var disabled = true;
	if (node != null) {
		var entity = node.get("data");
		disabled = !entity.isDirty();
	}
	view.set({
		"^onDirtyNode.disabled" : disabled,
		"^onDirty.disabled" : dirtyEntityNum == 0
	});
}

// @Bind @dtProject.onStateChange
!function(arg) {
	if (arg.oldState == dorado.Entity.STATE_NONE) {
		dirtyEntityNum++;
	} else if (arg.newState == dorado.Entity.STATE_NONE) {
		dirtyEntityNum--;
	} else if (arg.oldState == dorado.Entity.STATE_NEW
			&& arg.newState == dorado.Entity.STATE_DELETED) {
		dirtyEntityNum--;
	}
	refreshActions();
};

//插入项目时
//@Bind @dtProject.onInsert
!function(arg) {
	var entityList = arg.entityList;
	var maxSortFlag = 0;
	entityList.each(function(entity) {
		var sortFlag = entity.get("sortFlag");
		if (sortFlag <= maxSortFlag) {
			maxSortFlag++;
			entity.set("sortFlag", maxSortFlag);
		} else {
			maxSortFlag = sortFlag;
		}
	});
};

//删除项目时
//@Bind @dtProject.onRemove
!function(arg) {
	arg.entity.set("sortFlag", 0);
};

//单击项目
//@Bind #dataTreeProject.onDataRowClick
!function(dsProject,dsMethodStandard,dsResultColumn,dsTestCondition,dsProjectRole) {
	var currEntity = dsProject.getData("!CURRENT_PROJECT");
	var id = currEntity.get("id");
	if(id){
		//点击树重新加载数据
		dsMethodStandard.flushAsync();
		//dsProject.flushAsync();
		//currEntity.reset("child");
		dsResultColumn.flushAsync();
		dsTestCondition.flushAsync();
		dsProjectRole.flushAsync();
	}
	refreshActions();
};

//右键菜单
// @Bind #dataTreeProject.onContextMenu
!function(self, arg) {
	view.id("menuProjects").show({
		position : {
			left : arg.event.pageX,
			top : arg.event.pageY
		}
	});
};

//新建项目时
//@Bind #dataTreeProject.onDataNodeCreate
!function(self, arg) {
	var entity = arg.data;
	if (entity.state == dorado.Entity.STATE_NEW) {
		setTimeout(function() {
			self.set("currentNode", arg.node);
		}, 50);
	}
};

//拖拽项目
//@Bind #dataTreeProject.onDraggingSourceMove
!function(arg) {
	var draggingInfo = arg.draggingInfo;
	var sourceNode = draggingInfo.get("object");
	var sourceType = sourceNode.get("bindingConfig.name");

	var targetNode = draggingInfo.get("targetObject");
	if (targetNode) {
		var accept = false;
		if (sourceType == "Project") {
			accept = (targetNode.get("bindingConfig.name") == "Project");
		}
		draggingInfo.set("accept", accept);
	}
};

//添加项目
//@Bind #actionAdd.onExecute
!function(dsProject, dataTreeProject) {
	var currentEntity = dsProject.getData("!CURRENT_PROJECT");	
	if (currentEntity) {
		var data = {
				name : "<新项目>",
				parentId : currentEntity.get("id"),
				sortFlag:0
			};
//		currentEntity.createChild("child", data);
		//插入
		currentEntity.get("child").insert(data,"before");
	} else {
		//dsProject.getData().createChild({name:"<新项目>"});
		dsProject.getData().insert({name:"<新项目>",sortFlag:0},"before");
	}
	var currentNode = dataTreeProject.get("currentNode");
	if (currentNode) {
		currentNode.expandAsync();
	}
	
};

//删除项目
//@Bind #actionRemove.onExecute
!function(dsProject) {
	var currentEntity = dsProject.getData("!CURRENT_PROJECT");
	if (currentEntity) {
		if (currentEntity.get("hasChild")) {
		      dorado.MessageBox.alert("请先删除子项目");
		      return false;
	    } else {
	      currentEntity.remove();        
	    }
	}
};

//移动项目
//@Bind #actionMove.onExecute
!function(dialogSelectProject) {
	dialogSelectProject.show();
};

//取消
// @Bind #actionCancel.onExecute
!function(dsProject) {
	var entity = dsProject.getData("!CURRENT_PROJECT");
	if (entity) {
		entity.cancel();
	}
};

//保存项目成功后
// @Bind #actionSaveAll.onSuccess
!function() {
	dirtyEntityNum = 0;
	refreshActions();
};

//确定移动项目
//@Bind #buttonOk.onClick
!function(selectDataTreeProject, dataTreeProject, dialogSelectProject) {
	//用户选择的项目
	var project = selectDataTreeProject.get("currentNode.data");
	if (!project) {
		dorado.MessageBox.alert("请选择一个有效的项目。");
		return;
	}
	//要移动的项目
	var currentProjectNode = dataTreeProject.get("currentNode");
	if(project == currentProjectNode.get("data")){
		dorado.MessageBox.alert("不能选择自己。");
		return;
	}
	//不能parent
	var parentCurrentProjectNode = currentProjectNode.get("parent");
	if(project == parentCurrentProjectNode.get("data")){
		dorado.MessageBox.alert("不能选择自己的父项目。");
		return;
	}
	var newProject = dataTreeProject.get("currentNode.data");
	newProject = newProject.clone();
	newProject.set("sortFlag", 0);
	project.get("child").insert(newProject,"before");
	currentProjectNode.remove();
	dialogSelectProject.hide();
};

//关闭移动项目弹窗
// @Bind #buttonCancel.onClick
!function(dialogSelectProject) {
	dialogSelectProject.hide();
};

//加载记录项之前，设置projectId
// @Bind #dsResultColumn.beforeLoadData
!function(self,arg,dsProject) {
	var projectId = dsProject.getData("!CURRENT_PROJECT").get("id");
    self.set("parameter",projectId);
};

//保存记录项之前，设置projectId
// @Bind #saveResultColumnAction.beforeExecute
!function(self,arg,dsProject) {
	var projectId = dsProject.getData("!CURRENT_PROJECT").get("id");
    self.set("parameter",projectId);
};

//加载方法标准之前，设置projectId
// @Bind #dsMethodStandard.beforeLoadData
!function(self,arg,dsProject) {
	//http://wiki.bsdn.org/pages/viewpage.action?pageId=10912502
	dsProject.getDataAsync("!CURRENT_PROJECT", function(entity) {
		if(entity){
			self.set("parameter",entity.get("id"));
		}
	});
};

//添加方法标准
// @Bind #addMethodStandardBtn.onClick
!function(self,arg,dsProject,methodStandardDialog) {
	methodStandardDialog.show();
};

//删除方法标准
// @Bind #delMethodStandardBtn.onClick
!function(self,arg,dsProject,dsMethodStandard,deleteMethodStandardAction) {
	var projectId = dsProject.getData("!CURRENT_PROJECT").get("id");
	var currMethodStandard = dsMethodStandard.getData("#");
	if(currMethodStandard){
		var methodStandardId = currMethodStandard.get("id");
		deleteMethodStandardAction.set("parameter",{projectId:projectId,methodStandardId:methodStandardId}).execute(function(data){
			dsMethodStandard.flushAsync();
		});
	}else{
		dorado.MessageBox.alert("请选择一个方法标准");
		return false;
	}
};

//添加方法标准
// @Bind #saveMethodStandardBtn.onClick
!function(self,arg,dsMethodStandard,dsProject,standardView,saveMethodStandardAction,methodStandardDialog) {
	var projectId = dsProject.getData("!CURRENT_PROJECT").get("id");
	var methodStandardId = null;
	//获取当前选中的方法标准
	standardView = standardView.get("subView");
	var dsMethodStandards = standardView.id("dsMethodStandards");
	var currMethodStandard = dsMethodStandards.getData("#");
	if(currMethodStandard){
		methodStandardId = currMethodStandard.get("id");
	}else{
		dorado.MessageBox.alert("请选择一个方法标准");
		return false;
	}
	saveMethodStandardAction.set("parameter",{projectId:projectId,methodStandardId:methodStandardId}).execute(function(data){
		dsMethodStandard.flushAsync();
		methodStandardDialog.hide();
	});
};

//关闭选择方法标准弹窗
// @Bind #closeMethodStandardDialogBtn.onClick
!function(self,arg,methodStandardDialog) {
	methodStandardDialog.hide();
};


//加载检测条件之前，设置projectId
// @Bind #dsTestCondition.beforeLoadData
!function(self,arg,dsProject) {
	var projectId = dsProject.getData("!CURRENT_PROJECT").get("id");
	self.set("parameter",projectId);
};

//保存检测条件之前，设置projectId
// @Bind #saveTestConditionUpdateAction.beforeExecute
!function(self,arg,dsProject) {
	var projectId = dsProject.getData("!CURRENT_PROJECT").get("id");
	self.set("parameter",projectId);
};

//保存项目成功
// @Bind #actionSaveAll.onSuccess
!function(self,arg,dsProject) {
	dsProject.flushAsync();
}

//加载检测小组之前，设置projectId
// @Bind #dsProjectRole.beforeLoadData
!function(self,arg,dsProject) {
	var projectId = dsProject.getData("!CURRENT_PROJECT").get("id");
	self.set("parameter",projectId);
};

//删除角色
// @Bind #delProjectRoleBtn.onClick
!function(self,arg,dsProject,dsProjectRole,delProjectRoleAjaxAction) {
	var currProjectRole = dsProjectRole.getData("#");
	if(currProjectRole){
		delProjectRoleAjaxAction.
		set("parameter",{projectRoleId:currProjectRole.get("projectRoleId")}).execute(function(){
			dsProjectRole.flushAsync();
		});
	}

};

//关闭选择角色弹窗
// @Bind #closeProjectRoleDialogBtn.onClick
!function(self,arg,addProjectRoleDialog) {
	addProjectRoleDialog.hide();
};

//打开选择角色弹窗
// @Bind #addProjectRoleBtn.onClick
!function(self,arg,addProjectRoleDialog) {
	addProjectRoleDialog.show();
};

//保存角色
// @Bind #saveProjectRoleBtn.onClick
!function(self,arg,addProjectRoleDialog,saveProjectRoleAjaxAction,dsProject,dsProjectRole,roleSubView) {
	var projectId = dsProject.getData("!CURRENT_PROJECT").get("id");
	//从subView中获取选中的角色id
	var roleView = roleSubView.get("subView");
	var currRole = roleView.id("dataSetRole").getData("#");
	if(currRole){
		var roleId = currRole.get("id");
		saveProjectRoleAjaxAction.
		set("parameter",{roleId:roleId,projectId:projectId}).execute(function(){
			dsProjectRole.flushAsync();
			addProjectRoleDialog.hide();
		});
	}

};