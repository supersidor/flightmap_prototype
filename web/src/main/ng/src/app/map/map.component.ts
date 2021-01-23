import {AfterViewInit, Component, OnInit} from '@angular/core';
import * as L from 'leaflet';
import 'leaflet-rotatedmarker'
import {webSocket} from "rxjs/webSocket";
import {UserService} from "../user.service";
import {AuthService} from "../auth.service";
@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements OnInit , AfterViewInit{
  private map;
  private marker;
  private planeIconBlack;
  private planeIconWhite;
  private planeIconGreen;
  private markerTeleport;

  constructor(private auth: AuthService) {
    let svgPlaneIconString = '<?xml version="1.0" encoding="UTF-8" standalone="no"?><svg xmlns="http://www.w3.org/2000/svg" height="249.84" width="248.25" version="1.0"><metadata id="metadata9"/><path id="path5724" d="M 247.51404,152.40266 139.05781,71.800946 c 0.80268,-12.451845 1.32473,-40.256266 0.85468,-45.417599 -3.94034,-43.266462 -31.23018,-24.6301193 -31.48335,-5.320367 -0.0693,5.281361 -1.01502,32.598388 -1.10471,50.836622 L 0.2842717,154.37562 0,180.19575 l 110.50058,-50.48239 3.99332,80.29163 -32.042567,22.93816 -0.203845,16.89693 42.271772,-11.59566 0.008,0.1395 42.71311,10.91879 -0.50929,-16.88213 -32.45374,-22.39903 2.61132,-80.35205 111.35995,48.50611 -0.73494,-25.77295 z" fill-rule="evenodd" fill="__COLOR__"/></svg>'

    this.planeIconBlack = L.icon({
      iconUrl: encodeURI("data:image/svg+xml," + svgPlaneIconString).replace("#","%23").replace("__COLOR__", "black"),
      iconSize: [64, 64],
    });
    this.planeIconWhite = L.icon({
      iconUrl: encodeURI("data:image/svg+xml," + svgPlaneIconString).replace("#","%23").replace("__COLOR__", "white"),
      iconSize: [64, 64],
    });
    this.planeIconGreen = L.icon({
      iconUrl: encodeURI("data:image/svg+xml," + svgPlaneIconString).replace("#","%23").replace("__COLOR__", "green"),
      iconSize: [64, 64],
    });
  }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {

    this.initMap();

    let subject = webSocket('ws://localhost:8080/ws?token='+this.auth.getToken());

    subject.subscribe(
      msg => console.log('message received: ',msg), // Called whenever there is a message from the server.
      err => console.log(err), // Called if at any point WebSocket API signals some kind of error.
      () => console.log('complete') // Called when connection is closed (for whatever reason).
    );

  }

  initMap() {
    var pos = L.latLng(52.4727,-1.7523);

    var osm = new L.TileLayer("http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      maxZoom: 18,
      minZoom: 2,
      //format: "image/png",
      subdomains: ["a", "b", "c"]
    });
    var openaip_cached_basemap = new L.TileLayer("http://{s}.tile.maps.openaip.net/geowebcache/service/tms/1.0.0/openaip_basemap@EPSG%3A900913@png/{z}/{x}/{y}.png", {
      maxZoom: 14,
      minZoom: 4,
      tms: true,
      //detectRetina: true,
      subdomains: "12",
      //format: "image/png",
      //transparent: true
    });
    var stamen_black_white = new L.TileLayer("http://{s}.tile.stamen.com/toner/{z}/{x}/{y}.png", {
      maxZoom: 18,
      minZoom: 2,
      //format: "image/png",
      subdomains: ["a", "b", "c"]
    });
    var stamen_terrain = new L.TileLayer("http://{s}.tile.stamen.com/terrain/{z}/{x}/{y}.png", {
      maxZoom: 18,
      minZoom: 2,
      //format: "image/png",
      subdomains: ["a", "b", "c"]
    });
    var stamen_water = new L.TileLayer("http://{s}.tile.stamen.com/watercolor/{z}/{x}/{y}.png", {
      maxZoom: 18,
      minZoom: 2,
      //format: "image/png",
      subdomains: ["a", "b", "c"]
    });
    var carto_dark = new L.TileLayer("https://cartodb-basemaps-{s}.global.ssl.fastly.net/dark_all/{z}/{x}/{y}.png", {
      maxZoom: 18,
      minZoom: 2,
      //format: "image/png",
      subdomains: ["a", "b", "c"]
    });

    this.map = new L.Map("map", {
      layers: [ osm ],
      center: pos,
      zoom: 10,
      attributionControl: false
    });

    var attrib = L.control.attribution({position: "bottomleft"});
    attrib.addAttribution("<a href=\"https://www.openstreetmap.org/copyright\" target=\"_blank\" style=\"\">OpenStreetMap</a>");
    attrib.addAttribution("<a href=\"https://www.openaip.net\" target=\"_blank\" style=\"\">openAIP</a>");
    attrib.addAttribution("<a href=\"http://maps.stamen.com\" target=\"_blank\" style=\"\">Stamen</a>");
    attrib.addAttribution("<a href=\"https://carto.com/\" target=\"_blank\" style=\"\">Carto</a>");

    attrib.addTo(this.map);

    var baseMaps = {
      "OpenStreetMap": osm,
      "Stamen Terrain": stamen_terrain,
      "Stamen Toner": stamen_black_white,
      "Stamen Water": stamen_water,
      "Carto Dark (Night Mode)": carto_dark,
    };

    var overlayMaps = {
      "Navigational Data": openaip_cached_basemap,
    };
    L.control.layers(baseMaps, overlayMaps).addTo(this.map);

    //const marker = L.marker(pos).addTo(this.map);


    this.marker = L.marker(pos, {
      icon: this.planeIconBlack,
      // rotationAngle: 0,
      // rotationOrigin: "center",
    });
    //debugger;
    this.marker.setRotationAngle(0)
    //this.marker.setRotationOrigin("center")
    //
     this.marker.setLatLng(pos);
     this.marker.addTo(this.map);
    // marker.bindPopup(L.popup({autoPan: false}).setLatLng(pos).setContent(plane_popup.main));
    //
    // var markerPos = L.latLng(0,0);
    // markerTeleport = L.marker(markerPos, {});
    // markerTeleport.addTo(map);
    // markerTeleport.bindPopup(L.popup({autoPan: false}).setContent(teleport_popup.main));
    // set_teleport_marker(markerPos);


    // map.on('dragstart', function(e) {
    //   follow_plane = true
    //   toggle_follow();
    // });
    //
    // map.on('click', function(e) {
    //   set_teleport_marker(e.latlng);
    // });

    // map.on('baselayerchange', function(e) {
    //   if (e.name == "Carto Dark (Night Mode)") {
    //     marker.setIcon(planeIconWhite);
    //   } else if (e.name == "Stamen Toner") {
    //     marker.setIcon(planeIconGreen);
    //   } else {
    //     marker.setIcon(planeIconBlack);
    //   }
    // });
    // let this_ = this;
    // let heading = 0;
    // const interval = setInterval(function() {
    //   heading = heading + 10;
    //   this_.marker.setRotationAngle(heading);
    //   pos.lat = pos.lat + 0.01;
    //   pos.lng = pos.lng + 0.01;
    //
    //   this_.marker.setLatLng(pos);
    //   console.log("newpos",pos)
    // }, 1000);





  }

}
