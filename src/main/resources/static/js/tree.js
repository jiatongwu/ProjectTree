var text = "";

function f(data) {
    var name = data.name;
    var value = data.value;
    var children = data.children;
    if (text.length==0) {
        text += "<li><span class=\"open\"><i class=\"icon-folder-open\"></i>" + name + "</span><a href=\"\">"+value+"</a>";
    } else if (children.length<1) {
        text += "<li><span class=\"leaf\"><i class=\"icon-leaf\"></i>" + name + "</span><a href=\"\">"+value+"</a>";
    }else {
        text += "<li><span class=\"sign\"><i class=\"icon-minus-sign\"></i>" + name + "</span><a href=\"\">"+value+"</a>";
    }
    if (children.length>=1) {
        text += "<ul>";
        for (var i = 0; i < children.length; i++) {
            f(children[i]);
        }
        text += "</ul>";
    }
    text += "</li>"
}