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
comment|/**  * An expression which performs an operation on two expression values  *   *   */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ArithmeticExpression
extends|extends
name|BinaryExpression
block|{
specifier|protected
specifier|static
specifier|final
name|int
name|INTEGER
init|=
literal|1
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|int
name|LONG
init|=
literal|2
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|int
name|DOUBLE
init|=
literal|3
decl_stmt|;
comment|/**      * @param left      * @param right      */
specifier|public
name|ArithmeticExpression
parameter_list|(
name|Expression
name|left
parameter_list|,
name|Expression
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
name|Expression
name|createPlus
parameter_list|(
name|Expression
name|left
parameter_list|,
name|Expression
name|right
parameter_list|)
block|{
return|return
operator|new
name|ArithmeticExpression
argument_list|(
name|left
argument_list|,
name|right
argument_list|)
block|{
specifier|protected
name|Object
name|evaluate
parameter_list|(
name|Object
name|lvalue
parameter_list|,
name|Object
name|rvalue
parameter_list|)
block|{
if|if
condition|(
name|lvalue
operator|instanceof
name|String
condition|)
block|{
name|String
name|text
init|=
operator|(
name|String
operator|)
name|lvalue
decl_stmt|;
name|String
name|answer
init|=
name|text
operator|+
name|rvalue
decl_stmt|;
return|return
name|answer
return|;
block|}
elseif|else
if|if
condition|(
name|lvalue
operator|instanceof
name|Number
condition|)
block|{
return|return
name|plus
argument_list|(
operator|(
name|Number
operator|)
name|lvalue
argument_list|,
name|asNumber
argument_list|(
name|rvalue
argument_list|)
argument_list|)
return|;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot call plus operation on: "
operator|+
name|lvalue
operator|+
literal|" and: "
operator|+
name|rvalue
argument_list|)
throw|;
block|}
specifier|public
name|String
name|getExpressionSymbol
parameter_list|()
block|{
return|return
literal|"+"
return|;
block|}
block|}
return|;
block|}
specifier|public
specifier|static
name|Expression
name|createMinus
parameter_list|(
name|Expression
name|left
parameter_list|,
name|Expression
name|right
parameter_list|)
block|{
return|return
operator|new
name|ArithmeticExpression
argument_list|(
name|left
argument_list|,
name|right
argument_list|)
block|{
specifier|protected
name|Object
name|evaluate
parameter_list|(
name|Object
name|lvalue
parameter_list|,
name|Object
name|rvalue
parameter_list|)
block|{
if|if
condition|(
name|lvalue
operator|instanceof
name|Number
condition|)
block|{
return|return
name|minus
argument_list|(
operator|(
name|Number
operator|)
name|lvalue
argument_list|,
name|asNumber
argument_list|(
name|rvalue
argument_list|)
argument_list|)
return|;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot call minus operation on: "
operator|+
name|lvalue
operator|+
literal|" and: "
operator|+
name|rvalue
argument_list|)
throw|;
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
name|Expression
name|createMultiply
parameter_list|(
name|Expression
name|left
parameter_list|,
name|Expression
name|right
parameter_list|)
block|{
return|return
operator|new
name|ArithmeticExpression
argument_list|(
name|left
argument_list|,
name|right
argument_list|)
block|{
specifier|protected
name|Object
name|evaluate
parameter_list|(
name|Object
name|lvalue
parameter_list|,
name|Object
name|rvalue
parameter_list|)
block|{
if|if
condition|(
name|lvalue
operator|instanceof
name|Number
condition|)
block|{
return|return
name|multiply
argument_list|(
operator|(
name|Number
operator|)
name|lvalue
argument_list|,
name|asNumber
argument_list|(
name|rvalue
argument_list|)
argument_list|)
return|;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot call multiply operation on: "
operator|+
name|lvalue
operator|+
literal|" and: "
operator|+
name|rvalue
argument_list|)
throw|;
block|}
specifier|public
name|String
name|getExpressionSymbol
parameter_list|()
block|{
return|return
literal|"*"
return|;
block|}
block|}
return|;
block|}
specifier|public
specifier|static
name|Expression
name|createDivide
parameter_list|(
name|Expression
name|left
parameter_list|,
name|Expression
name|right
parameter_list|)
block|{
return|return
operator|new
name|ArithmeticExpression
argument_list|(
name|left
argument_list|,
name|right
argument_list|)
block|{
specifier|protected
name|Object
name|evaluate
parameter_list|(
name|Object
name|lvalue
parameter_list|,
name|Object
name|rvalue
parameter_list|)
block|{
if|if
condition|(
name|lvalue
operator|instanceof
name|Number
condition|)
block|{
return|return
name|divide
argument_list|(
operator|(
name|Number
operator|)
name|lvalue
argument_list|,
name|asNumber
argument_list|(
name|rvalue
argument_list|)
argument_list|)
return|;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot call divide operation on: "
operator|+
name|lvalue
operator|+
literal|" and: "
operator|+
name|rvalue
argument_list|)
throw|;
block|}
specifier|public
name|String
name|getExpressionSymbol
parameter_list|()
block|{
return|return
literal|"/"
return|;
block|}
block|}
return|;
block|}
specifier|public
specifier|static
name|Expression
name|createMod
parameter_list|(
name|Expression
name|left
parameter_list|,
name|Expression
name|right
parameter_list|)
block|{
return|return
operator|new
name|ArithmeticExpression
argument_list|(
name|left
argument_list|,
name|right
argument_list|)
block|{
specifier|protected
name|Object
name|evaluate
parameter_list|(
name|Object
name|lvalue
parameter_list|,
name|Object
name|rvalue
parameter_list|)
block|{
if|if
condition|(
name|lvalue
operator|instanceof
name|Number
condition|)
block|{
return|return
name|mod
argument_list|(
operator|(
name|Number
operator|)
name|lvalue
argument_list|,
name|asNumber
argument_list|(
name|rvalue
argument_list|)
argument_list|)
return|;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot call mod operation on: "
operator|+
name|lvalue
operator|+
literal|" and: "
operator|+
name|rvalue
argument_list|)
throw|;
block|}
specifier|public
name|String
name|getExpressionSymbol
parameter_list|()
block|{
return|return
literal|"%"
return|;
block|}
block|}
return|;
block|}
specifier|protected
name|Number
name|plus
parameter_list|(
name|Number
name|left
parameter_list|,
name|Number
name|right
parameter_list|)
block|{
switch|switch
condition|(
name|numberType
argument_list|(
name|left
argument_list|,
name|right
argument_list|)
condition|)
block|{
case|case
name|INTEGER
case|:
return|return
operator|new
name|Integer
argument_list|(
name|left
operator|.
name|intValue
argument_list|()
operator|+
name|right
operator|.
name|intValue
argument_list|()
argument_list|)
return|;
case|case
name|LONG
case|:
return|return
operator|new
name|Long
argument_list|(
name|left
operator|.
name|longValue
argument_list|()
operator|+
name|right
operator|.
name|longValue
argument_list|()
argument_list|)
return|;
default|default:
return|return
operator|new
name|Double
argument_list|(
name|left
operator|.
name|doubleValue
argument_list|()
operator|+
name|right
operator|.
name|doubleValue
argument_list|()
argument_list|)
return|;
block|}
block|}
specifier|protected
name|Number
name|minus
parameter_list|(
name|Number
name|left
parameter_list|,
name|Number
name|right
parameter_list|)
block|{
switch|switch
condition|(
name|numberType
argument_list|(
name|left
argument_list|,
name|right
argument_list|)
condition|)
block|{
case|case
name|INTEGER
case|:
return|return
operator|new
name|Integer
argument_list|(
name|left
operator|.
name|intValue
argument_list|()
operator|-
name|right
operator|.
name|intValue
argument_list|()
argument_list|)
return|;
case|case
name|LONG
case|:
return|return
operator|new
name|Long
argument_list|(
name|left
operator|.
name|longValue
argument_list|()
operator|-
name|right
operator|.
name|longValue
argument_list|()
argument_list|)
return|;
default|default:
return|return
operator|new
name|Double
argument_list|(
name|left
operator|.
name|doubleValue
argument_list|()
operator|-
name|right
operator|.
name|doubleValue
argument_list|()
argument_list|)
return|;
block|}
block|}
specifier|protected
name|Number
name|multiply
parameter_list|(
name|Number
name|left
parameter_list|,
name|Number
name|right
parameter_list|)
block|{
switch|switch
condition|(
name|numberType
argument_list|(
name|left
argument_list|,
name|right
argument_list|)
condition|)
block|{
case|case
name|INTEGER
case|:
return|return
operator|new
name|Integer
argument_list|(
name|left
operator|.
name|intValue
argument_list|()
operator|*
name|right
operator|.
name|intValue
argument_list|()
argument_list|)
return|;
case|case
name|LONG
case|:
return|return
operator|new
name|Long
argument_list|(
name|left
operator|.
name|longValue
argument_list|()
operator|*
name|right
operator|.
name|longValue
argument_list|()
argument_list|)
return|;
default|default:
return|return
operator|new
name|Double
argument_list|(
name|left
operator|.
name|doubleValue
argument_list|()
operator|*
name|right
operator|.
name|doubleValue
argument_list|()
argument_list|)
return|;
block|}
block|}
specifier|protected
name|Number
name|divide
parameter_list|(
name|Number
name|left
parameter_list|,
name|Number
name|right
parameter_list|)
block|{
return|return
operator|new
name|Double
argument_list|(
name|left
operator|.
name|doubleValue
argument_list|()
operator|/
name|right
operator|.
name|doubleValue
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|Number
name|mod
parameter_list|(
name|Number
name|left
parameter_list|,
name|Number
name|right
parameter_list|)
block|{
return|return
operator|new
name|Double
argument_list|(
name|left
operator|.
name|doubleValue
argument_list|()
operator|%
name|right
operator|.
name|doubleValue
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|int
name|numberType
parameter_list|(
name|Number
name|left
parameter_list|,
name|Number
name|right
parameter_list|)
block|{
if|if
condition|(
name|isDouble
argument_list|(
name|left
argument_list|)
operator|||
name|isDouble
argument_list|(
name|right
argument_list|)
condition|)
block|{
return|return
name|DOUBLE
return|;
block|}
elseif|else
if|if
condition|(
name|left
operator|instanceof
name|Long
operator|||
name|right
operator|instanceof
name|Long
condition|)
block|{
return|return
name|LONG
return|;
block|}
else|else
block|{
return|return
name|INTEGER
return|;
block|}
block|}
specifier|private
name|boolean
name|isDouble
parameter_list|(
name|Number
name|n
parameter_list|)
block|{
return|return
name|n
operator|instanceof
name|Float
operator|||
name|n
operator|instanceof
name|Double
return|;
block|}
specifier|protected
name|Number
name|asNumber
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|instanceof
name|Number
condition|)
block|{
return|return
operator|(
name|Number
operator|)
name|value
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot convert value: "
operator|+
name|value
operator|+
literal|" into a number"
argument_list|)
throw|;
block|}
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
name|Object
name|lvalue
init|=
name|left
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
return|return
name|evaluate
argument_list|(
name|lvalue
argument_list|,
name|rvalue
argument_list|)
return|;
block|}
comment|/**      * @param lvalue      * @param rvalue      * @return      */
specifier|protected
specifier|abstract
name|Object
name|evaluate
parameter_list|(
name|Object
name|lvalue
parameter_list|,
name|Object
name|rvalue
parameter_list|)
function_decl|;
block|}
end_class

end_unit

