import {Action} from "./action";
import {Constraint} from "./constraint";

export interface Rule{
  uid?: string;
  target?: string;
  action?: Action;
  assignee?: string;
  assigner?: string;
  constraints?: Array<Constraint>;
}
