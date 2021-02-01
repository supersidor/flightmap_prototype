import {LatLng} from "@model/lat-lng";

export class MapBounds{
  constructor(public northWest: LatLng,public southEast: LatLng) {
  }
}
