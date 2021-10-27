$(document).ready(function(){
    var uris = []
    $("img").each(function(index){
        uris.push($(this)[0].src)
    })
    console.log(uris)
})

