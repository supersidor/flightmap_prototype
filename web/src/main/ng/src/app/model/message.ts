import {PlanePosition} from "@model/position";
import {MapBounds} from "@model/map-bounds";

export class Message{
  type: 'pos'|'bounds'
  body: PlanePosition | MapBounds
}
