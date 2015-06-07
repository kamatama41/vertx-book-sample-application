function MindMapEditor(mindMap, eventBus) {
    this.mindMap = mindMap;
    this.eventBus = eventBus;
    this.registerEventHandlers();
}

MindMapEditor.width = 1280;
MindMapEditor.height = 800;
MindMapEditor.levelWidth = 150;
MindMapEditor.treeLayout = d3.layout.tree().size([MindMapEditor.height, MindMapEditor.width]);
MindMapEditor.diagonalGenerator = d3.svg.diagonal().projection(function(d) {
    return [d.y, d.x];
});

MindMapEditor.prototype.initVisualization = function() {
    this.vis = d3.select(".editor").html('').append("svg:svg")
        .attr("width", MindMapEditor.width).attr("height", MindMapEditor.height)
        .append("svg:g").attr("transform", "translate(10,0)");
};

MindMapEditor.prototype.registerEventHandlers = function() {
    var self = this;
    this.eventBus.registerHandler('mindMaps.events.' + self.mindMap._id, function(event){
        switch (event.event) {
            case 'nodeAdded':   self.onNodeAdded(event);   break;
            case 'nodeRenamed': self.onNodeRenamed(event); break;
            case 'nodeDeleted': self.onNodeDeleted(event); break;
        }
    })
};

MindMapEditor.prototype.onNodeAdded = function(event) {
    var parent = findNodeByKey(this.mindMap, event.parentKey);
    if(parent) {
        if(!parent.children) {
            parent.children = [];
        }
        parent.children.push(event.node);
    }
};

MindMapEditor.prototype.onNodeRenamed = function(event) {
    var node = findNodeByKey(this.mindMap, event.key);
    if(node) {
        node.name = event.newName;
    }
};

MindMapEditor.prototype.onNodeDeleted = function(event) {
    var parent = findNodeByKey(this.mindMap, event.parentKey);
    if(parent) {
        for(var i = 0; i < parent.children.length; i++) {
            if(parent.children[i].key === event.key) {
                parent.children.splice(i, 1);
                return;
            }
        }
    }
};

MindMapEditor.prototype.addNode = function(parentNode) {
    this.eventBus.send('mindMaps.editor.addNode', {
        mindMapId: this.mindMap._id,
        parentKey: parentNode.key
    });
};

MindMapEditor.prototype.renameNode = function(node, newName) {
    this.eventBus.send('mindMaps.editor.renameNode', {
        mindMapId: this.mindMap._id,
        key: node.key,
        newName: newName
    });
};

MindMapEditor.prototype.deleteNode = function(parentNode, childNode) {
    this.eventBus.send('mindMaps.editor.deleteNode', {
        mindMapId: this.mindMap._id,
        parentKey: parentNode.key,
        key: childNode.key
    });
};