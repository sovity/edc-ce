package de.sovity.edc.ext.wrapper.api.offering.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.eclipse.edc.policy.model.Identifiable;

import java.util.ArrayList;
import java.util.List;

/**
 * A permission, prohibition, or duty contained in a {@link Policy}.
 */
//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "edctype")
@Schema(description = "Test")
public abstract class Rule extends Identifiable {

    public interface Visitor<R> {
        R visitPermission(Permission policy);

        R visitProhibition(Prohibition policy);

        R visitDuty(Duty policy);
    }

    protected String target;
    protected Action action;

    protected String assignee;
    protected String assigner;

    protected List<AtomicConstraint> constraints = new ArrayList<>();

    public String getTarget() {
        return target;
    }

    public Action getAction() {
        return action;
    }

    public List<AtomicConstraint> getConstraints() {
        return constraints;
    }

    public String getAssigner() {
        return assigner;
    }

    public String getAssignee() {
        return assignee;
    }

    public abstract <R> R accept(Rule.Visitor<R> visitor);

    @SuppressWarnings("unchecked")
    protected abstract static class Builder<T extends Rule, B extends Rule.Builder<T, B>> {
        protected T rule;

        public B target(String target) {
            rule.target = target;
            return (B) this;
        }

        public B assigner(String assigner) {
            rule.assigner = assigner;
            return (B) this;
        }

        public B assignee(String assignee) {
            rule.assignee = assignee;
            return (B) this;
        }

        public B action(Action action) {
            rule.action = action;
            return (B) this;
        }

        public B constraint(AtomicConstraint constraint) {
            rule.constraints.add(constraint);
            return (B) this;
        }

        public B constraints(List<AtomicConstraint> constraints) {
            rule.constraints.addAll(constraints);
            return (B) this;
        }

    }

}
