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
// @Bind @dtMethodStandard.onStateChange
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

//@Bind @dtProject.onRemove
!function(arg) {
	arg.entity.set("sortFlag", 0);
};


//@Bind #dataTreeProject.onDataRowClick
!function(dsProject,dataTreeProject,methodStandardDataPilot,methodStandardDataGrid) {
	var currEntity = dsProject.getData("!CURRENT_PROJECT");
	var id = currEntity.get("id");
	if(id){
		//点击树重新加载数据
		currEntity.reset("methodStandards");
		//dsProject.flushAsync();
		//currEntity.reset("child");
	}
	refreshActions();
};
// @Bind #dataTreeProject.onContextMenu
!function(self, arg) {
	view.id("menuProjects").show({
		position : {
			left : arg.event.pageX,
			top : arg.event.pageY
		}
	});
};
//@Bind #dataTreeProject.onDataNodeCreate
!function(self, arg) {
	var entity = arg.data;
	if (entity.state == dorado.Entity.STATE_NEW) {
		setTimeout(function() {
			self.set("currentNode", arg.node);
		}, 50);
	}
};
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

//@Bind #actionMove.onExecute
!function(dialogSelectProject) {
	dialogSelectProject.show();
};

// @Bind #actionCancel.onExecute
!function(dsProject) {
	var entity = dsProject.getData("!CURRENT_PROJECT");
	if (entity) {
		entity.cancel();
	}
};

// @Bind #actionSaveAll.onSuccess
!function() {
	dirtyEntityNum = 0;
	refreshActions();
};


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

// @Bind #buttonCancel.onClick
!function(dialogSelectProject) {
	dialogSelectProject.hide();
};


