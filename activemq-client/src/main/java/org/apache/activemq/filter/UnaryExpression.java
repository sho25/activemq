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
name|math
operator|.
name|BigDecimal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

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
comment|/**  * An expression which performs an operation on two expression values  *   *   */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|UnaryExpression
implements|implements
name|Expression
block|{
specifier|private
specifier|static
specifier|final
name|BigDecimal
name|BD_LONG_MIN_VALUE
init|=
name|BigDecimal
operator|.
name|valueOf
argument_list|(
name|Long
operator|.
name|MIN_VALUE
argument_list|)
decl_stmt|;
specifier|protected
name|Expression
name|right
decl_stmt|;
specifier|public
name|UnaryExpression
parameter_list|(
name|Expression
name|left
parameter_list|)
block|{
name|this
operator|.
name|right
operator|=
name|left
expr_stmt|;
block|}
specifier|public
specifier|static
name|Expression
name|createNegate
parameter_list|(
name|Expression
name|left
parameter_list|)
block|{
return|return
operator|new
name|UnaryExpression
argument_list|(
name|left
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
name|Object
name|rvalue
init|=
name|right
operator|.
name|evaluate
argument_list|(
name|message
argument_list|)
decl_stmt|;
if|if
condition|(
name|rvalue
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
name|rvalue
operator|instanceof
name|Number
condition|)
block|{
return|return
name|negate
argument_list|(
operator|(
name|Number
operator|)
name|rvalue
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|String
name|getExpressionSymbol
parameter_list|()
block|{
return|return
literal|"-"
return|;
block|}
block|}
return|;
block|}
specifier|public
specifier|static
name|BooleanExpression
name|createInExpression
parameter_list|(
name|PropertyExpression
name|right
parameter_list|,
name|List
argument_list|<
name|Object
argument_list|>
name|elements
parameter_list|,
specifier|final
name|boolean
name|not
parameter_list|)
block|{
comment|// Use a HashSet if there are many elements.
name|Collection
argument_list|<
name|Object
argument_list|>
name|t
decl_stmt|;
if|if
condition|(
name|elements
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|t
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|elements
operator|.
name|size
argument_list|()
operator|<
literal|5
condition|)
block|{
name|t
operator|=
name|elements
expr_stmt|;
block|}
else|else
block|{
name|t
operator|=
operator|new
name|HashSet
argument_list|<
name|Object
argument_list|>
argument_list|(
name|elements
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Collection
name|inList
init|=
name|t
decl_stmt|;
return|return
operator|new
name|BooleanUnaryExpression
argument_list|(
name|right
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
name|Object
name|rvalue
init|=
name|right
operator|.
name|evaluate
argument_list|(
name|message
argument_list|)
decl_stmt|;
if|if
condition|(
name|rvalue
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
name|rvalue
operator|.
name|getClass
argument_list|()
operator|!=
name|String
operator|.
name|class
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|(
name|inList
operator|!=
literal|null
operator|&&
name|inList
operator|.
name|contains
argument_list|(
name|rvalue
argument_list|)
operator|)
operator|^
name|not
condition|)
block|{
return|return
name|Boolean
operator|.
name|TRUE
return|;
block|}
else|else
block|{
return|return
name|Boolean
operator|.
name|FALSE
return|;
block|}
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|answer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|answer
operator|.
name|append
argument_list|(
name|right
argument_list|)
expr_stmt|;
name|answer
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|answer
operator|.
name|append
argument_list|(
name|getExpressionSymbol
argument_list|()
argument_list|)
expr_stmt|;
name|answer
operator|.
name|append
argument_list|(
literal|" ( "
argument_list|)
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|inList
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Object
name|o
init|=
operator|(
name|Object
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|!=
literal|0
condition|)
block|{
name|answer
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|answer
operator|.
name|append
argument_list|(
name|o
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|answer
operator|.
name|append
argument_list|(
literal|" )"
argument_list|)
expr_stmt|;
return|return
name|answer
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|String
name|getExpressionSymbol
parameter_list|()
block|{
if|if
condition|(
name|not
condition|)
block|{
return|return
literal|"NOT IN"
return|;
block|}
else|else
block|{
return|return
literal|"IN"
return|;
block|}
block|}
block|}
return|;
block|}
specifier|abstract
specifier|static
class|class
name|BooleanUnaryExpression
extends|extends
name|UnaryExpression
implements|implements
name|BooleanExpression
block|{
specifier|public
name|BooleanUnaryExpression
parameter_list|(
name|Expression
name|left
parameter_list|)
block|{
name|super
argument_list|(
name|left
argument_list|)
expr_stmt|;
block|}
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
empty_stmt|;
specifier|public
specifier|static
name|BooleanExpression
name|createNOT
parameter_list|(
name|BooleanExpression
name|left
parameter_list|)
block|{
return|return
operator|new
name|BooleanUnaryExpression
argument_list|(
name|left
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
name|lvalue
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
if|if
condition|(
name|lvalue
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|lvalue
operator|.
name|booleanValue
argument_list|()
condition|?
name|Boolean
operator|.
name|FALSE
else|:
name|Boolean
operator|.
name|TRUE
return|;
block|}
specifier|public
name|String
name|getExpressionSymbol
parameter_list|()
block|{
return|return
literal|"NOT"
return|;
block|}
block|}
return|;
block|}
specifier|public
specifier|static
name|BooleanExpression
name|createXPath
parameter_list|(
specifier|final
name|String
name|xpath
parameter_list|)
block|{
return|return
operator|new
name|XPathExpression
argument_list|(
name|xpath
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|BooleanExpression
name|createXQuery
parameter_list|(
specifier|final
name|String
name|xpath
parameter_list|)
block|{
return|return
operator|new
name|XQueryExpression
argument_list|(
name|xpath
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|BooleanExpression
name|createBooleanCast
parameter_list|(
name|Expression
name|left
parameter_list|)
block|{
return|return
operator|new
name|BooleanUnaryExpression
argument_list|(
name|left
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
name|Object
name|rvalue
init|=
name|right
operator|.
name|evaluate
argument_list|(
name|message
argument_list|)
decl_stmt|;
if|if
condition|(
name|rvalue
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
name|rvalue
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|Boolean
operator|.
name|class
argument_list|)
condition|)
block|{
return|return
name|Boolean
operator|.
name|FALSE
return|;
block|}
return|return
operator|(
operator|(
name|Boolean
operator|)
name|rvalue
operator|)
operator|.
name|booleanValue
argument_list|()
condition|?
name|Boolean
operator|.
name|TRUE
else|:
name|Boolean
operator|.
name|FALSE
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|right
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|String
name|getExpressionSymbol
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
block|}
return|;
block|}
specifier|private
specifier|static
name|Number
name|negate
parameter_list|(
name|Number
name|left
parameter_list|)
block|{
name|Class
name|clazz
init|=
name|left
operator|.
name|getClass
argument_list|()
decl_stmt|;
if|if
condition|(
name|clazz
operator|==
name|Integer
operator|.
name|class
condition|)
block|{
return|return
operator|new
name|Integer
argument_list|(
operator|-
name|left
operator|.
name|intValue
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|clazz
operator|==
name|Long
operator|.
name|class
condition|)
block|{
return|return
operator|new
name|Long
argument_list|(
operator|-
name|left
operator|.
name|longValue
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|clazz
operator|==
name|Float
operator|.
name|class
condition|)
block|{
return|return
operator|new
name|Float
argument_list|(
operator|-
name|left
operator|.
name|floatValue
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|clazz
operator|==
name|Double
operator|.
name|class
condition|)
block|{
return|return
operator|new
name|Double
argument_list|(
operator|-
name|left
operator|.
name|doubleValue
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|clazz
operator|==
name|BigDecimal
operator|.
name|class
condition|)
block|{
comment|// We ussually get a big deciamal when we have Long.MIN_VALUE
comment|// constant in the
comment|// Selector. Long.MIN_VALUE is too big to store in a Long as a
comment|// positive so we store it
comment|// as a Big decimal. But it gets Negated right away.. to here we try
comment|// to covert it back
comment|// to a Long.
name|BigDecimal
name|bd
init|=
operator|(
name|BigDecimal
operator|)
name|left
decl_stmt|;
name|bd
operator|=
name|bd
operator|.
name|negate
argument_list|()
expr_stmt|;
if|if
condition|(
name|BD_LONG_MIN_VALUE
operator|.
name|compareTo
argument_list|(
name|bd
argument_list|)
operator|==
literal|0
condition|)
block|{
return|return
name|Long
operator|.
name|valueOf
argument_list|(
name|Long
operator|.
name|MIN_VALUE
argument_list|)
return|;
block|}
return|return
name|bd
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Don't know how to negate: "
operator|+
name|left
argument_list|)
throw|;
block|}
block|}
specifier|public
name|Expression
name|getRight
parameter_list|()
block|{
return|return
name|right
return|;
block|}
specifier|public
name|void
name|setRight
parameter_list|(
name|Expression
name|expression
parameter_list|)
block|{
name|right
operator|=
name|expression
expr_stmt|;
block|}
comment|/**      * @see java.lang.Object#toString()      */
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"("
operator|+
name|getExpressionSymbol
argument_list|()
operator|+
literal|" "
operator|+
name|right
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
comment|/**      * TODO: more efficient hashCode()      *       * @see java.lang.Object#hashCode()      */
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|toString
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/**      * TODO: more efficient hashCode()      *       * @see java.lang.Object#equals(java.lang.Object)      */
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
operator|||
operator|!
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|o
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|o
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Returns the symbol that represents this binary expression. For example,      * addition is represented by "+"      *       * @return      */
specifier|public
specifier|abstract
name|String
name|getExpressionSymbol
parameter_list|()
function_decl|;
block|}
end_class

end_unit

