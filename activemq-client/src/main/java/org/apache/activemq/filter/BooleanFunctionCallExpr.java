begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|filter
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Function call expression that evaluates to a boolean value.  Selector parsing requires BooleanExpression objects for  * Boolean expressions, such as operands to AND, and as the final result of a selector.  This provides that interface  * for function call expressions that resolve to Boolean values.  *<p/>  * If a function can return different types at evaluation-time, the function implementation needs to decide whether it  * supports casting to Boolean at parse-time.  *  * @see    FunctionCallExpression#createFunctionCall  */
end_comment

begin_class
specifier|public
class|class
name|BooleanFunctionCallExpr
extends|extends
name|FunctionCallExpression
implements|implements
name|BooleanExpression
block|{
comment|/**      * Constructs a function call expression with the named filter function and arguments, which returns a boolean      * result.      *      * @param    func_name - Name of the filter function to be called when evaluated.      * @param    args - List of argument expressions passed to the function.      */
specifier|public
name|BooleanFunctionCallExpr
parameter_list|(
name|String
name|func_name
parameter_list|,
name|List
argument_list|<
name|Expression
argument_list|>
name|args
parameter_list|)
throws|throws
name|invalidFunctionExpressionException
block|{
name|super
argument_list|(
name|func_name
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
comment|/**      * Evaluate the function call expression, in the given context, and return an indication of whether the      * expression "matches" (i.e.&nbsp;evaluates to true).      *      * @param    message_ctx - message context against which the expression will be evaluated.      * @return the boolean evaluation of the function call expression.      */
specifier|public
name|boolean
name|matches
parameter_list|(
name|MessageEvaluationContext
name|message_ctx
parameter_list|)
throws|throws
name|javax
operator|.
name|jms
operator|.
name|JMSException
block|{
name|Boolean
name|result
decl_stmt|;
name|result
operator|=
operator|(
name|Boolean
operator|)
name|evaluate
argument_list|(
name|message_ctx
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
return|return
name|result
operator|.
name|booleanValue
argument_list|()
return|;
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

