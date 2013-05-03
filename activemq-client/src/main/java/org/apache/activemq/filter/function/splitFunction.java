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
comment|/**  * Function which splits a string into a list of strings given a regular expression for the separator.  */
end_comment

begin_class
specifier|public
class|class
name|splitFunction
implements|implements
name|FilterFunction
block|{
comment|/**      * Check whether the given expression is valid for this function.      *      * @param    expr - the expression consisting of a call to this function.      * @return true - if two or three arguments are passed to the function; false - otherwise.      */
specifier|public
name|boolean
name|isValid
parameter_list|(
name|FunctionCallExpression
name|expr
parameter_list|)
block|{
if|if
condition|(
operator|(
name|expr
operator|.
name|getNumArguments
argument_list|()
operator|>=
literal|2
operator|)
operator|&&
operator|(
name|expr
operator|.
name|getNumArguments
argument_list|()
operator|<=
literal|3
operator|)
condition|)
return|return
literal|true
return|;
return|return
literal|false
return|;
block|}
comment|/**      * Indicate that this function does not return a boolean value.      *      * @param    expr - the expression consisting of a call to this function.      * @return false - indicating this filter function never evaluates to a boolean result.      */
specifier|public
name|boolean
name|returnsBoolean
parameter_list|(
name|FunctionCallExpression
name|expr
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
comment|/**      * Evaluate the given expression for this function in the given context.  A list of zero or more strings      * results from the evaluation.  The result of the evaluation of the first argument is split with the regular      * expression which results from the evaluation of the second argument.  If a third argument is given, it      * is an integer which limits the split.  String#split() performs the split.      *<p/>      * The first two arguments must be Strings.  If a third is given, it must be an Integer.      *      * @param    expr - the expression consisting of a call to this function.      * @return List - a list of Strings resulting from the split.      */
specifier|public
name|Object
name|evaluate
parameter_list|(
name|FunctionCallExpression
name|expr
parameter_list|,
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
name|String
name|src
decl_stmt|;
name|String
name|split_pat
decl_stmt|;
name|String
index|[]
name|result
decl_stmt|;
name|src
operator|=
operator|(
name|String
operator|)
name|expr
operator|.
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|.
name|evaluate
argument_list|(
name|message_ctx
argument_list|)
expr_stmt|;
name|split_pat
operator|=
operator|(
name|String
operator|)
name|expr
operator|.
name|getArgument
argument_list|(
literal|1
argument_list|)
operator|.
name|evaluate
argument_list|(
name|message_ctx
argument_list|)
expr_stmt|;
if|if
condition|(
name|expr
operator|.
name|getNumArguments
argument_list|()
operator|>
literal|2
condition|)
block|{
name|Integer
name|limit
decl_stmt|;
name|limit
operator|=
operator|(
name|Integer
operator|)
name|expr
operator|.
name|getArgument
argument_list|(
literal|2
argument_list|)
operator|.
name|evaluate
argument_list|(
name|message_ctx
argument_list|)
expr_stmt|;
name|result
operator|=
name|src
operator|.
name|split
argument_list|(
name|split_pat
argument_list|,
name|limit
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|src
operator|.
name|split
argument_list|(
name|split_pat
argument_list|)
expr_stmt|;
block|}
return|return
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
argument_list|(
name|result
argument_list|)
return|;
block|}
block|}
end_class

end_unit

