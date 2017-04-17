import { RemoverCount } from "./remover-count.model"
import { RemoverOrder } from "./remover-order.model"

export interface RemoverReason {
    id: number;
    type: string;
    value: string;
    orders: RemoverOrder[];
    enabled: boolean;
    orderCount?: RemoverCount;
    directCount?: RemoverCount;
}