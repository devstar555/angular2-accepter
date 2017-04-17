import { RemoverReason } from "./remover-reason.model"

export interface RemoverOrder {
    id: number;
    action: string;
    reasons: RemoverReason[];
    matched?: boolean;
}