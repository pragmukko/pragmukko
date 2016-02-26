require('./general.styles.scss');
var dronesList;

var $dronesList = document.querySelector('.drones-menu-list');
// var $btnDronesMenuToggle = document.querySelector('.drones-menu-toggle');
var $activeDron = document.querySelector('.active-dron');

var activeDron;

$dronesList.addEventListener('click', (e) => {
    e.preventDefault();
    var dronId = e.target.getAttribute('data-id');
    activateDron(dronId);
});

function activateDron (id) {
    activeDron = dronesList.find(drone => drone.id == id);

    if(!activeDron.id) {
        dronesList.some(drone => {
            activeDron = drone;
            return drone.id;
        })
    }

    if (dronesList.length && activeDron.id) {
        $activeDron.innerHTML = activeDron.address;
        getData();
    }
}

// $btnDronesMenuToggle.addEventListener('click', (e) => {
//     e.preventDefault();
//     $dronesList.classList.toggle('collapsed');
// });

fetch('http://localhost:9000/cmd/list', {mode: 'cors'})
    .then(res => res.json())
    .then(data => {
        dronesList = data;
        activateDron(dronesList[0].id);

        data.forEach(dronData => {
            let dronNode = document.createElement("a");
            let droneTitle = document.createTextNode(`Dron ${dronData.id}`);

            dronNode.appendChild(droneTitle);
            dronNode.title = dronData.address;
            dronNode.href = '';
            dronNode.classList.add('btn', 'btn-default');
            dronNode.setAttribute('data-id', dronData.id);

            $dronesList.appendChild(dronNode);
        })
    })
    .catch( err => console.error(err));

var PIXI = require('../bin/pixi.js'),
    renderer = new PIXI.WebGLRenderer(800, 600, {
        "antialias": true,
        "transparent": false,
        "preserveDrawingBuffer": false,
        "clearBeforeRender": true
    }),
    mainLayout = new PIXI.Container(),

    gridLayer = new PIXI.Container(),
    grid = new PIXI.Graphics(),

    pathLine = new PIXI.Graphics(),
    helper = require('./helper.js'),

    centerX = renderer.width/2,
    centerY = renderer.height/2,

    Dron = require('./classes/Dron.js'),
    dron = new Dron({
        color: helper.getRandomColor(),
        x: centerX,
        y: centerY
    }),
    counter = 0,
    path = [],
    droneMovementData = [];


renderer.backgroundColor = 0xFFFFFF;
document.getElementById('swrnCanvas').appendChild(renderer.view);

drawGrid();

gridLayer.addChild(grid);
mainLayout.addChild(gridLayer);
mainLayout.addChild(pathLine);
mainLayout.addChild(dron);


function drawGrid() {
    var gridStep = 40;
    var startX = renderer.width/2;
    var startY = renderer.height/2;

    var gridPaintIterations = 10;

    var gridHeight = renderer.height*gridPaintIterations;
    var gridWidth = renderer.width*gridPaintIterations;

    grid.lineStyle(1, 0xCCCCCC, 1);

    // X coord line
    grid.moveTo(0,  startY);
    grid.lineTo(gridWidth, centerY);
    for (var x = centerX; x <= gridWidth; x += gridStep) {
        grid.moveTo(x,  0);
        grid.lineTo(x, gridHeight);
    }
    for (var x = centerX; x >= -gridWidth; x -= gridStep) {
        grid.moveTo(x,  0);
        grid.lineTo(x, gridHeight);
    }

    // Y coord line
    grid.moveTo(centerX,  0);
    grid.lineTo(centerX, gridHeight);
    for (var y = centerY; y <= gridHeight; y += gridStep) {
        grid.moveTo(0,  y);
        grid.lineTo(gridWidth, y);
    }
    for (var y = centerY; y >= -gridHeight; y -= gridStep) {
        grid.moveTo(0,  y);
        grid.lineTo(gridWidth, y);
    }

}

function drawPath () {
    pathLine.lineStyle(4, 0x000000);
    pathLine.moveTo(centerX, centerY);

    for (var i=0; i<path.length; i++)
        pathLine.lineTo(path[i].x, path[i].y);
}

var lastRecordTime = 0;
var firstLanch = true;

function getData () {
    fetch(`http://localhost:9000/cmd/mlhistory/${activeDron.id}`, {mode: 'cors'})
        .then(res => res.json())
        .then(data => {
            var startTime = data[0].time_boot_ms;
            var filteredData = data.filter(el => {
                if(
                    helper.compareToDatesBySeconds(el.time_boot_ms, startTime) > 0.025
                    && el.MAVType == 'msg_local_position_ned'
                    && el.time_boot_ms != undefined
                    && el.time_boot_ms > lastRecordTime
                    && el.x != undefined
                    && el.y != undefined
                ){
                    startTime = el.time_boot_ms;
                    el.x *= 10;
                    el.y *= 10;
                    return true;
                }
            })

            if (path.length > 300) { path.splice(0, path.length); }
            droneMovementData = droneMovementData.concat(filteredData);
            lastRecordTime = data[data.length-1].time_boot_ms;

            if(firstLanch){
                animate();
                firstLanch = false;
            }

        })
        .catch( err => console.error(err));

    setTimeout(getData, 1000);
}

function animate() {
    // start the timer for the next animation loop
    requestAnimationFrame(animate);

    // each frame we spin the bunny around a bit
    if (counter < droneMovementData.length) {

        let coordX = droneMovementData[counter].x + centerX;
        let coordY = droneMovementData[counter].y + centerY;

        path.push({x:coordX, y:coordY});

        dron.position.x = coordX;
        dron.position.y = coordY;

        drawPath();
        counter++;
    }

    mainLayout.scale.x = mainLayout.scale.y = 1;

    if (dron.position.x > renderer.width) {
        mainLayout.scale.x = mainLayout.scale.y = renderer.width/dron.position.x;
    }

    if (dron.position.y > renderer.height) {
        mainLayout.scale.x = mainLayout.scale.y = renderer.height/dron.position.y;
    }

    // this is the main render call that makes pixi draw your container and its children.
    renderer.render(mainLayout);
}

