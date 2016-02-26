module.exports = {
    merge: function (obj1, obj2) {
        for (var attrname in obj2) { obj1[attrname] = obj2[attrname]; }
    },
    getRandomColor: function () {
        return Math.floor(Math.random()*16777215);
    },
    compareToDatesBySeconds: function (t1, t2) {
        return Math.abs(( t1 - t2 ) / 1000);
    }
};
