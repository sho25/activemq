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
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_comment
comment|/**  * Represents a constant expression  *   *   */
end_comment

begin_class
specifier|public
class|class
name|ConstantExpression
implements|implements
name|Expression
block|{
specifier|static
class|class
name|BooleanConstantExpression
extends|extends
name|ConstantExpression
implements|implements
name|BooleanExpression
block|{
specifier|public
name|BooleanConstantExpression
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|value
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
specifier|public
specifier|static
specifier|final
name|BooleanConstantExpression
name|NULL
init|=
operator|new
name|BooleanConstantExpression
argument_list|(
literal|null
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|BooleanConstantExpression
name|TRUE
init|=
operator|new
name|BooleanConstantExpression
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|BooleanConstantExpression
name|FALSE
init|=
operator|new
name|BooleanConstantExpression
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|)
decl_stmt|;
specifier|private
name|Object
name|value
decl_stmt|;
specifier|public
name|ConstantExpression
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
specifier|public
specifier|static
name|ConstantExpression
name|createFromDecimal
parameter_list|(
name|String
name|text
parameter_list|)
block|{
comment|// Strip off the 'l' or 'L' if needed.
if|if
condition|(
name|text
operator|.
name|endsWith
argument_list|(
literal|"l"
argument_list|)
operator|||
name|text
operator|.
name|endsWith
argument_list|(
literal|"L"
argument_list|)
condition|)
block|{
name|text
operator|=
name|text
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|text
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|Number
name|value
decl_stmt|;
try|try
block|{
name|value
operator|=
operator|new
name|Long
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
comment|// The number may be too big to fit in a long.
name|value
operator|=
operator|new
name|BigDecimal
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
name|long
name|l
init|=
name|value
operator|.
name|longValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|Integer
operator|.
name|MIN_VALUE
operator|<=
name|l
operator|&&
name|l
operator|<=
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|value
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|value
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ConstantExpression
argument_list|(
name|value
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|ConstantExpression
name|createFromHex
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|Number
name|value
init|=
name|Long
operator|.
name|valueOf
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|text
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|16
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|l
init|=
name|value
operator|.
name|longValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|Integer
operator|.
name|MIN_VALUE
operator|<=
name|l
operator|&&
name|l
operator|<=
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|value
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|value
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ConstantExpression
argument_list|(
name|value
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|ConstantExpression
name|createFromOctal
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|Number
name|value
init|=
name|Long
operator|.
name|valueOf
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|text
argument_list|,
literal|8
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|l
init|=
name|value
operator|.
name|longValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|Integer
operator|.
name|MIN_VALUE
operator|<=
name|l
operator|&&
name|l
operator|<=
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|value
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|value
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ConstantExpression
argument_list|(
name|value
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|ConstantExpression
name|createFloat
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|Number
name|value
init|=
operator|new
name|Double
argument_list|(
name|text
argument_list|)
decl_stmt|;
return|return
operator|new
name|ConstantExpression
argument_list|(
name|value
argument_list|)
return|;
block|}
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
return|return
name|value
return|;
block|}
specifier|public
name|Object
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
comment|/**      * @see java.lang.Object#toString()      */
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
literal|"NULL"
return|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|Boolean
condition|)
block|{
return|return
operator|(
operator|(
name|Boolean
operator|)
name|value
operator|)
operator|.
name|booleanValue
argument_list|()
condition|?
literal|"TRUE"
else|:
literal|"FALSE"
return|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|String
condition|)
block|{
return|return
name|encodeString
argument_list|(
operator|(
name|String
operator|)
name|value
argument_list|)
return|;
block|}
return|return
name|value
operator|.
name|toString
argument_list|()
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
comment|/**      * Encodes the value of string so that it looks like it would look like when      * it was provided in a selector.      *       * @param string      * @return      */
specifier|public
specifier|static
name|String
name|encodeString
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|StringBuffer
name|b
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|s
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'\''
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit
