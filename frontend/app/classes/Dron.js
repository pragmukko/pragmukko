var helper = require("../helper.js");

function Dron(params){
    var defaults = {
            color: 0xCCCCCC,
            x: 100,
            y: 100,
            height: 50,
            width: 50
        },
        dron = new PIXI.Graphics();

    helper.merge(defaults, params);

    dron.beginFill(defaults.color);
    dron.drawRect(0, 0, defaults.width, defaults.height);
    dron.endFill();

    dron.pivot.x = defaults.width/2;
    dron.pivot.y = defaults.height/2;

    dron.position.x = defaults.x;
    dron.position.y = defaults.y;

    // element.hitArea = new PIXI.Circle(0, 0, 50);
    // element.interactive = true;
    // element.mousedown = function() {
    //     console.log('onclick');
    // }

    return dron
}

module.exports = Dron;
