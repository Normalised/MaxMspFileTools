var newWidth = 200; 
var newHeight = 400;

function resize()
{
    var patch = this.patcher.wind.assoc;

    if(patch.box){
		var w = patch.box.rect[2] - patch.box.rect[0];
		var h = patch.box.rect[3] - patch.box.rect[1];

        if((patch.box.rect[3] - patch.box.rect[1]) < 50){
            // we are in patcher context...
            this.patcher.wind.size = [newWidth, newHeight];
            // remove the scroll bars
            this.patcher.wind.hasgrow = 0;                        
        }
        else{    // we are in bpatcher context...
            var left = patch.box.rect[0];
            var top = patch.box.rect[1];
            var right = left + newWidth;
            var bottom = top + newHeight;
            patch.box.rect = [left, top, right, bottom];
        }
    } else {
		post("Couldnt get patch box");
		post();
	}
}

// Use a task to resize after 50ms because
// at loadbang time the patcher box isnt available.
t = new Task(resize, this);
t.schedule(50);
