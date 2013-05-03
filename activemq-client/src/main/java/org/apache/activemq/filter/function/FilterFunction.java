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
operator|.
name|function
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|filter
operator|.
name|FunctionCallExpression
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|filter
operator|.
name|MessageEvaluationContext
import|;
end_import

begin_comment
comment|/**  * Interface required for objects that will be registered as functions for use in selectors.  Handles parse-  * time and evaluation-time operations.  */
end_comment

begin_interface
specifier|public
interface|interface
name|FilterFunction
block|{
comment|/**      * Check whether the function, as it is used, is valid.  Checking arguments here will return errors      * to clients at the time invalid selectors are initially specified, rather than waiting until the selector is      * applied to a message.      *      * @param    FunctionCallExpression expr - the full expression of the function call, as used.      * @return true - if the function call is valid; false - otherwise.      */
specifier|public
name|boolean
name|isValid
parameter_list|(
name|FunctionCallExpression
name|expr
parameter_list|)
function_decl|;
comment|/**      * Determine whether the function, as it will be called, returns a boolean value.  Called during      * expression parsing after the full expression for the function call, including its arguments, has      * been parsed.  This allows functions with variable return types to function as boolean expressions in      * selectors without sacrificing parse-time checking.      *      * @param    FunctionCallExpression expr - the full expression of the function call, as used.      * @return true - if the function returns a boolean value for its use in the given expression;      * false - otherwise.      */
specifier|public
name|boolean
name|returnsBoolean
parameter_list|(
name|FunctionCallExpression
name|expr
parameter_list|)
function_decl|;
comment|/**      * Evaluate the function call in the given context.  The arguments must be evaluated, as-needed, by the      * function.  Note that boolean expressions must return Boolean objects.      *      * @param    FunctionCallExpression expr - the full expression of the function call, as used.      * @param    MessageEvaluationContext message - the context within which to evaluate the call.      */
specifier|public
name|Object
name|evaluate
parameter_list|(
name|FunctionCallExpression
name|expr
parameter_list|,
name|MessageEvaluationContext
name|message
parameter_list|)
throws|throws
name|javax
operator|.
name|jms
operator|.
name|JMSException
function_decl|;
block|}
end_interface

end_unit

