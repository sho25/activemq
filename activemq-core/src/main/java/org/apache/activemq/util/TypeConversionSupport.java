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
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|command
operator|.
name|ActiveMQDestination
import|;
end_import

begin_class
specifier|public
class|class
name|TypeConversionSupport
block|{
specifier|static
class|class
name|ConversionKey
block|{
specifier|final
name|Class
name|from
decl_stmt|;
specifier|final
name|Class
name|to
decl_stmt|;
specifier|final
name|int
name|hashCode
decl_stmt|;
specifier|public
name|ConversionKey
parameter_list|(
name|Class
name|from
parameter_list|,
name|Class
name|to
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
name|this
operator|.
name|hashCode
operator|=
name|from
operator|.
name|hashCode
argument_list|()
operator|^
operator|(
name|to
operator|.
name|hashCode
argument_list|()
operator|<<
literal|1
operator|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|ConversionKey
name|x
init|=
operator|(
name|ConversionKey
operator|)
name|o
decl_stmt|;
return|return
name|x
operator|.
name|from
operator|==
name|from
operator|&&
name|x
operator|.
name|to
operator|==
name|to
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|hashCode
return|;
block|}
block|}
interface|interface
name|Converter
block|{
name|Object
name|convert
parameter_list|(
name|Object
name|value
parameter_list|)
function_decl|;
block|}
specifier|static
specifier|final
specifier|private
name|HashMap
name|CONVERSION_MAP
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
static|static
block|{
name|Converter
name|toStringConverter
init|=
operator|new
name|Converter
argument_list|()
block|{
specifier|public
name|Object
name|convert
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|value
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|CONVERSION_MAP
operator|.
name|put
argument_list|(
operator|new
name|ConversionKey
argument_list|(
name|Boolean
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|toStringConverter
argument_list|)
expr_stmt|;
name|CONVERSION_MAP
operator|.
name|put
argument_list|(
operator|new
name|ConversionKey
argument_list|(
name|Byte
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|toStringConverter
argument_list|)
expr_stmt|;
name|CONVERSION_MAP
operator|.
name|put
argument_list|(
operator|new
name|ConversionKey
argument_list|(
name|Short
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|toStringConverter
argument_list|)
expr_stmt|;
name|CONVERSION_MAP
operator|.
name|put
argument_list|(
operator|new
name|ConversionKey
argument_list|(
name|Integer
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|toStringConverter
argument_list|)
expr_stmt|;
name|CONVERSION_MAP
operator|.
name|put
argument_list|(
operator|new
name|ConversionKey
argument_list|(
name|Long
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|toStringConverter
argument_list|)
expr_stmt|;
name|CONVERSION_MAP
operator|.
name|put
argument_list|(
operator|new
name|ConversionKey
argument_list|(
name|Float
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|toStringConverter
argument_list|)
expr_stmt|;
name|CONVERSION_MAP
operator|.
name|put
argument_list|(
operator|new
name|ConversionKey
argument_list|(
name|Double
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|toStringConverter
argument_list|)
expr_stmt|;
name|CONVERSION_MAP
operator|.
name|put
argument_list|(
operator|new
name|ConversionKey
argument_list|(
name|String
operator|.
name|class
argument_list|,
name|Boolean
operator|.
name|class
argument_list|)
argument_list|,
operator|new
name|Converter
argument_list|()
block|{
specifier|public
name|Object
name|convert
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|Boolean
operator|.
name|valueOf
argument_list|(
operator|(
name|String
operator|)
name|value
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|CONVERSION_MAP
operator|.
name|put
argument_list|(
operator|new
name|ConversionKey
argument_list|(
name|String
operator|.
name|class
argument_list|,
name|Byte
operator|.
name|class
argument_list|)
argument_list|,
operator|new
name|Converter
argument_list|()
block|{
specifier|public
name|Object
name|convert
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|Byte
operator|.
name|valueOf
argument_list|(
operator|(
name|String
operator|)
name|value
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|CONVERSION_MAP
operator|.
name|put
argument_list|(
operator|new
name|ConversionKey
argument_list|(
name|String
operator|.
name|class
argument_list|,
name|Short
operator|.
name|class
argument_list|)
argument_list|,
operator|new
name|Converter
argument_list|()
block|{
specifier|public
name|Object
name|convert
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|Short
operator|.
name|valueOf
argument_list|(
operator|(
name|String
operator|)
name|value
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|CONVERSION_MAP
operator|.
name|put
argument_list|(
operator|new
name|ConversionKey
argument_list|(
name|String
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|class
argument_list|)
argument_list|,
operator|new
name|Converter
argument_list|()
block|{
specifier|public
name|Object
name|convert
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|valueOf
argument_list|(
operator|(
name|String
operator|)
name|value
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|CONVERSION_MAP
operator|.
name|put
argument_list|(
operator|new
name|ConversionKey
argument_list|(
name|String
operator|.
name|class
argument_list|,
name|Long
operator|.
name|class
argument_list|)
argument_list|,
operator|new
name|Converter
argument_list|()
block|{
specifier|public
name|Object
name|convert
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|Long
operator|.
name|valueOf
argument_list|(
operator|(
name|String
operator|)
name|value
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|CONVERSION_MAP
operator|.
name|put
argument_list|(
operator|new
name|ConversionKey
argument_list|(
name|String
operator|.
name|class
argument_list|,
name|Float
operator|.
name|class
argument_list|)
argument_list|,
operator|new
name|Converter
argument_list|()
block|{
specifier|public
name|Object
name|convert
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|Float
operator|.
name|valueOf
argument_list|(
operator|(
name|String
operator|)
name|value
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|CONVERSION_MAP
operator|.
name|put
argument_list|(
operator|new
name|ConversionKey
argument_list|(
name|String
operator|.
name|class
argument_list|,
name|Double
operator|.
name|class
argument_list|)
argument_list|,
operator|new
name|Converter
argument_list|()
block|{
specifier|public
name|Object
name|convert
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|Double
operator|.
name|valueOf
argument_list|(
operator|(
name|String
operator|)
name|value
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|Converter
name|longConverter
init|=
operator|new
name|Converter
argument_list|()
block|{
specifier|public
name|Object
name|convert
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|Long
operator|.
name|valueOf
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|value
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|CONVERSION_MAP
operator|.
name|put
argument_list|(
operator|new
name|ConversionKey
argument_list|(
name|Byte
operator|.
name|class
argument_list|,
name|Long
operator|.
name|class
argument_list|)
argument_list|,
name|longConverter
argument_list|)
expr_stmt|;
name|CONVERSION_MAP
operator|.
name|put
argument_list|(
operator|new
name|ConversionKey
argument_list|(
name|Short
operator|.
name|class
argument_list|,
name|Long
operator|.
name|class
argument_list|)
argument_list|,
name|longConverter
argument_list|)
expr_stmt|;
name|CONVERSION_MAP
operator|.
name|put
argument_list|(
operator|new
name|ConversionKey
argument_list|(
name|Integer
operator|.
name|class
argument_list|,
name|Long
operator|.
name|class
argument_list|)
argument_list|,
name|longConverter
argument_list|)
expr_stmt|;
name|CONVERSION_MAP
operator|.
name|put
argument_list|(
operator|new
name|ConversionKey
argument_list|(
name|Date
operator|.
name|class
argument_list|,
name|Long
operator|.
name|class
argument_list|)
argument_list|,
operator|new
name|Converter
argument_list|()
block|{
specifier|public
name|Object
name|convert
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|Long
operator|.
name|valueOf
argument_list|(
operator|(
operator|(
name|Date
operator|)
name|value
operator|)
operator|.
name|getTime
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|Converter
name|intConverter
init|=
operator|new
name|Converter
argument_list|()
block|{
specifier|public
name|Object
name|convert
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|valueOf
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|value
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|CONVERSION_MAP
operator|.
name|put
argument_list|(
operator|new
name|ConversionKey
argument_list|(
name|Byte
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|class
argument_list|)
argument_list|,
name|intConverter
argument_list|)
expr_stmt|;
name|CONVERSION_MAP
operator|.
name|put
argument_list|(
operator|new
name|ConversionKey
argument_list|(
name|Short
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|class
argument_list|)
argument_list|,
name|intConverter
argument_list|)
expr_stmt|;
name|CONVERSION_MAP
operator|.
name|put
argument_list|(
operator|new
name|ConversionKey
argument_list|(
name|Byte
operator|.
name|class
argument_list|,
name|Short
operator|.
name|class
argument_list|)
argument_list|,
operator|new
name|Converter
argument_list|()
block|{
specifier|public
name|Object
name|convert
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|Short
operator|.
name|valueOf
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|value
operator|)
operator|.
name|shortValue
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|CONVERSION_MAP
operator|.
name|put
argument_list|(
operator|new
name|ConversionKey
argument_list|(
name|Float
operator|.
name|class
argument_list|,
name|Double
operator|.
name|class
argument_list|)
argument_list|,
operator|new
name|Converter
argument_list|()
block|{
specifier|public
name|Object
name|convert
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
operator|new
name|Double
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|value
operator|)
operator|.
name|doubleValue
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|CONVERSION_MAP
operator|.
name|put
argument_list|(
operator|new
name|ConversionKey
argument_list|(
name|String
operator|.
name|class
argument_list|,
name|ActiveMQDestination
operator|.
name|class
argument_list|)
argument_list|,
operator|new
name|Converter
argument_list|()
block|{
specifier|public
name|Object
name|convert
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
operator|(
name|String
operator|)
name|value
argument_list|,
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|static
specifier|public
name|Object
name|convert
parameter_list|(
name|Object
name|value
parameter_list|,
name|Class
name|clazz
parameter_list|)
block|{
assert|assert
name|value
operator|!=
literal|null
operator|&&
name|clazz
operator|!=
literal|null
assert|;
if|if
condition|(
name|value
operator|.
name|getClass
argument_list|()
operator|==
name|clazz
condition|)
return|return
name|value
return|;
name|Converter
name|c
init|=
operator|(
name|Converter
operator|)
name|CONVERSION_MAP
operator|.
name|get
argument_list|(
operator|new
name|ConversionKey
argument_list|(
name|value
operator|.
name|getClass
argument_list|()
argument_list|,
name|clazz
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|c
operator|.
name|convert
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
end_class

end_unit

