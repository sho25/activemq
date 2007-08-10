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
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_comment
comment|/**  * A filter performing a comparison of two objects  *   * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|LogicExpression
extends|extends
name|BinaryExpression
implements|implements
name|BooleanExpression
block|{
comment|/**      * @param left      * @param right      */
specifier|public
name|LogicExpression
parameter_list|(
name|BooleanExpression
name|left
parameter_list|,
name|BooleanExpression
name|right
parameter_list|)
block|{
name|super
argument_list|(
name|left
argument_list|,
name|right
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|BooleanExpression
name|createOR
parameter_list|(
name|BooleanExpression
name|lvalue
parameter_list|,
name|BooleanExpression
name|rvalue
parameter_list|)
block|{
return|return
operator|new
name|LogicExpression
argument_list|(
name|lvalue
argument_list|,
name|rvalue
argument_list|)
block|{
specifier|public
name|Object
name|evaluate
parameter_list|(
name|MessageEvaluationContext
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
name|Boolean
name|lv
init|=
operator|(
name|Boolean
operator|)
name|left
operator|.
name|evaluate
argument_list|(
name|message
argument_list|)
decl_stmt|;
comment|// Can we do an OR shortcut??
if|if
condition|(
name|lv
operator|!=
literal|null
operator|&&
name|lv
operator|.
name|booleanValue
argument_list|()
condition|)
block|{
return|return
name|Boolean
operator|.
name|TRUE
return|;
block|}
name|Boolean
name|rv
init|=
operator|(
name|Boolean
operator|)
name|right
operator|.
name|evaluate
argument_list|(
name|message
argument_list|)
decl_stmt|;
return|return
name|rv
operator|==
literal|null
condition|?
literal|null
else|:
name|rv
return|;
block|}
specifier|public
name|String
name|getExpressionSymbol
parameter_list|()
block|{
return|return
literal|"OR"
return|;
block|}
block|}
return|;
block|}
specifier|public
specifier|static
name|BooleanExpression
name|createAND
parameter_list|(
name|BooleanExpression
name|lvalue
parameter_list|,
name|BooleanExpression
name|rvalue
parameter_list|)
block|{
return|return
operator|new
name|LogicExpression
argument_list|(
name|lvalue
argument_list|,
name|rvalue
argument_list|)
block|{
specifier|public
name|Object
name|evaluate
parameter_list|(
name|MessageEvaluationContext
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
name|Boolean
name|lv
init|=
operator|(
name|Boolean
operator|)
name|left
operator|.
name|evaluate
argument_list|(
name|message
argument_list|)
decl_stmt|;
comment|// Can we do an AND shortcut??
if|if
condition|(
name|lv
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|lv
operator|.
name|booleanValue
argument_list|()
condition|)
block|{
return|return
name|Boolean
operator|.
name|FALSE
return|;
block|}
name|Boolean
name|rv
init|=
operator|(
name|Boolean
operator|)
name|right
operator|.
name|evaluate
argument_list|(
name|message
argument_list|)
decl_stmt|;
return|return
name|rv
operator|==
literal|null
condition|?
literal|null
else|:
name|rv
return|;
block|}
specifier|public
name|String
name|getExpressionSymbol
parameter_list|()
block|{
return|return
literal|"AND"
return|;
block|}
block|}
return|;
block|}
specifier|public
specifier|abstract
name|Object
name|evaluate
parameter_list|(
name|MessageEvaluationContext
name|message
parameter_list|)
throws|throws
name|JMSException
function_decl|;
specifier|public
name|boolean
name|matches
parameter_list|(
name|MessageEvaluationContext
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
name|Object
name|object
init|=
name|evaluate
argument_list|(
name|message
argument_list|)
decl_stmt|;
return|return
name|object
operator|!=
literal|null
operator|&&
name|object
operator|==
name|Boolean
operator|.
name|TRUE
return|;
block|}
block|}
end_class

end_unit

