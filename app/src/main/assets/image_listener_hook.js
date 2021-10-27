$(document).ready(function(){
    var uris = []
    $("img").each(function(index){
        uris.push($(this)[0].src)
        $(this).click(function(){
            //
            Android.toast(this)
            var rect = this.getBoundingClientRect()
//            Android.transit(rect.left,rect.top,rect.right,rect.bottom)
        })
    })
    console.log(uris)
})

