import { RemoteDataReason } from "./remote-data-reason.model"
import { RemoteDataOrder } from "./remote-data-order.model"

export interface RemoteData {
    reasons: RemoteDataReason[];
    orders: RemoteDataOrder[];
}