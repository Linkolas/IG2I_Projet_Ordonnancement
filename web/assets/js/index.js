
$(function() {
    $("#calculate").click(function() {
        $("#calculate").html("Calcul en cours...");
        $.get("api/calculate", function(data) {
            $("#calculate").html("CALCULER");
            location.reload();
        });
    });
    
    $("#loadData").click(function() {
        $("#loadData").html("Chargement en cours...");
        $.get("upload", function(data) {
            $("#loadData").html("Charger les données");
            location.reload();
        });
    });
    
    $("#generate").click(function() {
        $("#generate").html("Génération...");
        $.get("api/generate", function(data) {
            $("#generate").html("GENERER");
            window.location.href = 'assets/csv/Solution.csv';
        });
    });
    
    
    $.get("api/lieux", function(data) {
        
        var markers = [];
        for(var i = 0; i < data.lieux.length; i++) {
            var lieu = data.lieux[i];
            var marker = new google.maps.Marker({
                position: {
                    lat: lieu.coordY,
                    lng: lieu.coordX
                },
                title: lieu.numeroLieu
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
