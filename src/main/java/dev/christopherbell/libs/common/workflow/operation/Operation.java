package dev.christopherbell.libs.common.workflow.operation;

import dev.christopherbell.libs.common.workflow.model.WorkflowContext;

/**
 * Represents an operation that can be executed within a workflow.
 */
public interface Operation {

  /**
   * Executes the operation with the provided workflow context.
   *
   * @param ctx the context to be used for execution
   * @return the result of the operation execution
   */
  OperationResult execute(WorkflowContext ctx);

  /**
   * Returns the name of the operation.
   *
   * @return the operation name
   */
  default String getOperationName() {
    return this.getClass().getSimpleName();
  }
}
