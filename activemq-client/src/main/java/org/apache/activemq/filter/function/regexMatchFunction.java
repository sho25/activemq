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
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|LRUCache
import|;
end_import

begin_comment
comment|/**  * Filter function that matches a value against a regular expression.  *<p/>  *<p style="margin-left: 4em">  * REGEX( 'A.B', 'A-B' )  *</p>  *<p/>  * Note that the regular expression is not anchored; use the anchor characters, ^ and $, as-needed.  For example,  * REGEX( 'AA', 'XAAX' ) evaluates to true while REGEX( '^AA$' , 'XAAX' ) evaluates to false.  */
end_comment

begin_class
specifier|public
class|class
name|regexMatchFunction
implements|implements
name|FilterFunction
block|{
specifier|protected
specifier|static
specifier|final
name|LRUCache
argument_list|<
name|String
argument_list|,
name|Pattern
argument_list|>
name|compiledExprCache
init|=
operator|new
name|LRUCache
argument_list|(
literal|100
argument_list|)
decl_stmt|;
comment|/**      * Check whether the given expression is a valid call of this function.  Two arguments are required.  When      * evaluated, the arguments are converted to strings if they are not already strings.      *      * @param    expr - the expression consisting of a call to this function.      * @return true - if the expression is valid; false - otherwise.      */
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
name|expr
operator|.
name|getNumArguments
argument_list|()
operator|==
literal|2
condition|)
return|return
literal|true
return|;
return|return
literal|false
return|;
block|}
comment|/**      * Indicate that this Filter Function evaluates to a Boolean result.      *      * @param    expr - the expression consisting of a call to this function.      * @return true - this function always evaluates to a Boolean result.      */
specifier|public
name|boolean
name|returnsBoolean
parameter_list|(
name|FunctionCallExpression
name|expr
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
comment|/**      * Evalutate the given expression, which consists of a call to this function, in the context given.  Returns      * an indication of whether the second argument matches the regular expression in the first argument.      *      * @param    expr - the expression consisting of a call to this function.      * @param    message_ctx - the context in which the call is being evaluated.      * @return true - if the value matches the regular expression; false - otherwise.      */
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
block|{
name|Object
name|reg
decl_stmt|;
name|Object
name|cand
decl_stmt|;
name|String
name|reg_str
decl_stmt|;
name|String
name|cand_str
decl_stmt|;
name|Pattern
name|pat
decl_stmt|;
name|Matcher
name|match_eng
decl_stmt|;
comment|//
comment|// Evaluate the first argument (the regular expression).
comment|//
name|reg
operator|=
name|expr
operator|.
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|.
name|evaluate
argument_list|(
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|reg
operator|!=
literal|null
condition|)
block|{
comment|// Convert to a string, if it's not already a string.
if|if
condition|(
name|reg
operator|instanceof
name|String
condition|)
name|reg_str
operator|=
operator|(
name|String
operator|)
name|reg
expr_stmt|;
else|else
name|reg_str
operator|=
name|reg
operator|.
name|toString
argument_list|()
expr_stmt|;
comment|//
comment|// Evaluate the second argument (the candidate to match against the regular
comment|//  expression).
comment|//
name|cand
operator|=
name|expr
operator|.
name|getArgument
argument_list|(
literal|1
argument_list|)
operator|.
name|evaluate
argument_list|(
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|cand
operator|!=
literal|null
condition|)
block|{
comment|// Convert to a string, if it's not already a string.
if|if
condition|(
name|cand
operator|instanceof
name|String
condition|)
name|cand_str
operator|=
operator|(
name|String
operator|)
name|cand
expr_stmt|;
else|else
name|cand_str
operator|=
name|cand
operator|.
name|toString
argument_list|()
expr_stmt|;
comment|//
comment|// Obtain the compiled regular expression and match it.
comment|//
name|pat
operator|=
name|getCompiledPattern
argument_list|(
name|reg_str
argument_list|)
expr_stmt|;
name|match_eng
operator|=
name|pat
operator|.
name|matcher
argument_list|(
name|cand_str
argument_list|)
expr_stmt|;
comment|//
comment|// Return an indication of whether the regular expression matches at any
comment|//  point in the candidate (see Matcher#find()).
comment|//
return|return
name|Boolean
operator|.
name|valueOf
argument_list|(
name|match_eng
operator|.
name|find
argument_list|()
argument_list|)
return|;
block|}
block|}
return|return
name|Boolean
operator|.
name|FALSE
return|;
block|}
comment|/**      * Retrieve a compiled pattern for the given pattern string.  A cache of recently used strings is maintained to      * improve performance.      *      * @param    reg_ex_str - the string specifying the regular expression.      * @return Pattern - compiled form of the regular expression.      */
specifier|protected
name|Pattern
name|getCompiledPattern
parameter_list|(
name|String
name|reg_ex_str
parameter_list|)
block|{
name|Pattern
name|result
decl_stmt|;
comment|//
comment|// Look for the compiled pattern in the cache.
comment|//
synchronized|synchronized
init|(
name|compiledExprCache
init|)
block|{
name|result
operator|=
name|compiledExprCache
operator|.
name|get
argument_list|(
name|reg_ex_str
argument_list|)
expr_stmt|;
block|}
comment|//
comment|// If it was not found, compile it and add it to the cache.
comment|//
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
name|result
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
name|reg_ex_str
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|compiledExprCache
init|)
block|{
name|compiledExprCache
operator|.
name|put
argument_list|(
name|reg_ex_str
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

