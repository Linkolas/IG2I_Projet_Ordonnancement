
$(function() {
    $.get("api/lieux", function(data) {
        
        var markers = [];
        for(var i = 0; i < data.lieux.length; i++) {
            var lieu = data.lieux[i];
            var marker = new google.maps.Marker({
                position: {
                    lat: lieu.coordY,
                    lng: lieu.coordX
                },
                title: lieu.libelle
            });
            markers.push(marker);
        }
        
        initMap(markers);
    });
});
 

function initMap(markers) {
    var map = new google.maps.Map(document.getElementById('map'), {});
    
    var bounds = new google.maps.LatLngBounds();
    for(var i = 0; i < markers.length; i++) {
        markers[i].setMap(map);
        bounds.extend(markers[i].position);
    }
    
    map.fitBounds(bounds);
}
