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
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UTFDataFormatException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * The fixed version of the UTF8 encoding function. Some older JVM's UTF8  * encoding function breaks when handling large strings.  *   *   */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|MarshallingSupport
block|{
specifier|public
specifier|static
specifier|final
name|byte
name|NULL
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|BOOLEAN_TYPE
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|BYTE_TYPE
init|=
literal|2
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|CHAR_TYPE
init|=
literal|3
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|SHORT_TYPE
init|=
literal|4
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|INTEGER_TYPE
init|=
literal|5
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|LONG_TYPE
init|=
literal|6
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|DOUBLE_TYPE
init|=
literal|7
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|FLOAT_TYPE
init|=
literal|8
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|STRING_TYPE
init|=
literal|9
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|BYTE_ARRAY_TYPE
init|=
literal|10
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|MAP_TYPE
init|=
literal|11
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|LIST_TYPE
init|=
literal|12
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|BIG_STRING_TYPE
init|=
literal|13
decl_stmt|;
specifier|private
name|MarshallingSupport
parameter_list|()
block|{     }
specifier|public
specifier|static
name|void
name|marshalPrimitiveMap
parameter_list|(
name|Map
name|map
parameter_list|,
name|DataOutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|map
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeInt
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|map
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|name
init|=
operator|(
name|String
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|Object
name|value
init|=
name|map
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|marshalPrimitive
argument_list|(
name|out
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|unmarshalPrimitiveMap
parameter_list|(
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|unmarshalPrimitiveMap
argument_list|(
name|in
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
return|;
block|}
comment|/**      * @param in      * @return      * @throws IOException      * @throws IOException      */
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|unmarshalPrimitiveMap
parameter_list|(
name|DataInputStream
name|in
parameter_list|,
name|int
name|maxPropertySize
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|size
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|>
name|maxPropertySize
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Primitive map is larger than the allowed size: "
operator|+
name|size
argument_list|)
throw|;
block|}
if|if
condition|(
name|size
operator|<
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|rc
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|(
name|size
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|in
operator|.
name|readUTF
argument_list|()
decl_stmt|;
name|rc
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|unmarshalPrimitive
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
block|}
specifier|public
specifier|static
name|void
name|marshalPrimitiveList
parameter_list|(
name|List
name|list
parameter_list|,
name|DataOutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|list
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Object
name|element
init|=
operator|(
name|Object
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|marshalPrimitive
argument_list|(
name|out
argument_list|,
name|element
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|List
argument_list|<
name|Object
argument_list|>
name|unmarshalPrimitiveList
parameter_list|(
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|size
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|answer
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|(
name|size
argument_list|)
decl_stmt|;
while|while
condition|(
name|size
operator|--
operator|>
literal|0
condition|)
block|{
name|answer
operator|.
name|add
argument_list|(
name|unmarshalPrimitive
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|answer
return|;
block|}
specifier|public
specifier|static
name|void
name|marshalPrimitive
parameter_list|(
name|DataOutputStream
name|out
parameter_list|,
name|Object
name|value
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|marshalNull
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|getClass
argument_list|()
operator|==
name|Boolean
operator|.
name|class
condition|)
block|{
name|marshalBoolean
argument_list|(
name|out
argument_list|,
operator|(
operator|(
name|Boolean
operator|)
name|value
operator|)
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|getClass
argument_list|()
operator|==
name|Byte
operator|.
name|class
condition|)
block|{
name|marshalByte
argument_list|(
name|out
argument_list|,
operator|(
operator|(
name|Byte
operator|)
name|value
operator|)
operator|.
name|byteValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|getClass
argument_list|()
operator|==
name|Character
operator|.
name|class
condition|)
block|{
name|marshalChar
argument_list|(
name|out
argument_list|,
operator|(
operator|(
name|Character
operator|)
name|value
operator|)
operator|.
name|charValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|getClass
argument_list|()
operator|==
name|Short
operator|.
name|class
condition|)
block|{
name|marshalShort
argument_list|(
name|out
argument_list|,
operator|(
operator|(
name|Short
operator|)
name|value
operator|)
operator|.
name|shortValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|getClass
argument_list|()
operator|==
name|Integer
operator|.
name|class
condition|)
block|{
name|marshalInt
argument_list|(
name|out
argument_list|,
operator|(
operator|(
name|Integer
operator|)
name|value
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|getClass
argument_list|()
operator|==
name|Long
operator|.
name|class
condition|)
block|{
name|marshalLong
argument_list|(
name|out
argument_list|,
operator|(
operator|(
name|Long
operator|)
name|value
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|getClass
argument_list|()
operator|==
name|Float
operator|.
name|class
condition|)
block|{
name|marshalFloat
argument_list|(
name|out
argument_list|,
operator|(
operator|(
name|Float
operator|)
name|value
operator|)
operator|.
name|floatValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|getClass
argument_list|()
operator|==
name|Double
operator|.
name|class
condition|)
block|{
name|marshalDouble
argument_list|(
name|out
argument_list|,
operator|(
operator|(
name|Double
operator|)
name|value
operator|)
operator|.
name|doubleValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|getClass
argument_list|()
operator|==
name|byte
index|[]
operator|.
name|class
condition|)
block|{
name|marshalByteArray
argument_list|(
name|out
argument_list|,
operator|(
name|byte
index|[]
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|getClass
argument_list|()
operator|==
name|String
operator|.
name|class
condition|)
block|{
name|marshalString
argument_list|(
name|out
argument_list|,
operator|(
name|String
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Map
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|MAP_TYPE
argument_list|)
expr_stmt|;
name|marshalPrimitiveMap
argument_list|(
operator|(
name|Map
operator|)
name|value
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|List
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|LIST_TYPE
argument_list|)
expr_stmt|;
name|marshalPrimitiveList
argument_list|(
operator|(
name|List
operator|)
name|value
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Object is not a primitive: "
operator|+
name|value
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|static
name|Object
name|unmarshalPrimitive
parameter_list|(
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|Object
name|value
init|=
literal|null
decl_stmt|;
name|byte
name|type
init|=
name|in
operator|.
name|readByte
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|BYTE_TYPE
case|:
name|value
operator|=
name|Byte
operator|.
name|valueOf
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|BOOLEAN_TYPE
case|:
name|value
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
condition|?
name|Boolean
operator|.
name|TRUE
else|:
name|Boolean
operator|.
name|FALSE
expr_stmt|;
break|break;
case|case
name|CHAR_TYPE
case|:
name|value
operator|=
name|Character
operator|.
name|valueOf
argument_list|(
name|in
operator|.
name|readChar
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|SHORT_TYPE
case|:
name|value
operator|=
name|Short
operator|.
name|valueOf
argument_list|(
name|in
operator|.
name|readShort
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|INTEGER_TYPE
case|:
name|value
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|in
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|LONG_TYPE
case|:
name|value
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|in
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_TYPE
case|:
name|value
operator|=
operator|new
name|Float
argument_list|(
name|in
operator|.
name|readFloat
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|DOUBLE_TYPE
case|:
name|value
operator|=
operator|new
name|Double
argument_list|(
name|in
operator|.
name|readDouble
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTE_ARRAY_TYPE
case|:
name|value
operator|=
operator|new
name|byte
index|[
name|in
operator|.
name|readInt
argument_list|()
index|]
expr_stmt|;
name|in
operator|.
name|readFully
argument_list|(
operator|(
name|byte
index|[]
operator|)
name|value
argument_list|)
expr_stmt|;
break|break;
case|case
name|STRING_TYPE
case|:
name|value
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
break|break;
case|case
name|BIG_STRING_TYPE
case|:
name|value
operator|=
name|readUTF8
argument_list|(
name|in
argument_list|)
expr_stmt|;
break|break;
case|case
name|MAP_TYPE
case|:
name|value
operator|=
name|unmarshalPrimitiveMap
argument_list|(
name|in
argument_list|)
expr_stmt|;
break|break;
case|case
name|LIST_TYPE
case|:
name|value
operator|=
name|unmarshalPrimitiveList
argument_list|(
name|in
argument_list|)
expr_stmt|;
break|break;
case|case
name|NULL
case|:
name|value
operator|=
literal|null
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown primitive type: "
operator|+
name|type
argument_list|)
throw|;
block|}
return|return
name|value
return|;
block|}
specifier|public
specifier|static
name|void
name|marshalNull
parameter_list|(
name|DataOutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|NULL
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|marshalBoolean
parameter_list|(
name|DataOutputStream
name|out
parameter_list|,
name|boolean
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|BOOLEAN_TYPE
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|marshalByte
parameter_list|(
name|DataOutputStream
name|out
parameter_list|,
name|byte
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|BYTE_TYPE
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|marshalChar
parameter_list|(
name|DataOutputStream
name|out
parameter_list|,
name|char
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|CHAR_TYPE
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeChar
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|marshalShort
parameter_list|(
name|DataOutputStream
name|out
parameter_list|,
name|short
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|SHORT_TYPE
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeShort
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|marshalInt
parameter_list|(
name|DataOutputStream
name|out
parameter_list|,
name|int
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|INTEGER_TYPE
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|marshalLong
parameter_list|(
name|DataOutputStream
name|out
parameter_list|,
name|long
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|LONG_TYPE
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|marshalFloat
parameter_list|(
name|DataOutputStream
name|out
parameter_list|,
name|float
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|FLOAT_TYPE
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeFloat
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|marshalDouble
parameter_list|(
name|DataOutputStream
name|out
parameter_list|,
name|double
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|DOUBLE_TYPE
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|marshalByteArray
parameter_list|(
name|DataOutputStream
name|out
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|marshalByteArray
argument_list|(
name|out
argument_list|,
name|value
argument_list|,
literal|0
argument_list|,
name|value
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|marshalByteArray
parameter_list|(
name|DataOutputStream
name|out
parameter_list|,
name|byte
index|[]
name|value
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|BYTE_ARRAY_TYPE
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|value
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|marshalString
parameter_list|(
name|DataOutputStream
name|out
parameter_list|,
name|String
name|s
parameter_list|)
throws|throws
name|IOException
block|{
comment|// If it's too big, out.writeUTF may not able able to write it out.
if|if
condition|(
name|s
operator|.
name|length
argument_list|()
operator|<
name|Short
operator|.
name|MAX_VALUE
operator|/
literal|4
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|STRING_TYPE
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|BIG_STRING_TYPE
argument_list|)
expr_stmt|;
name|writeUTF8
argument_list|(
name|out
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|void
name|writeUTF8
parameter_list|(
name|DataOutput
name|dataOut
parameter_list|,
name|String
name|text
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|text
operator|!=
literal|null
condition|)
block|{
name|int
name|strlen
init|=
name|text
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|utflen
init|=
literal|0
decl_stmt|;
name|char
index|[]
name|charr
init|=
operator|new
name|char
index|[
name|strlen
index|]
decl_stmt|;
name|int
name|c
init|=
literal|0
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|text
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|strlen
argument_list|,
name|charr
argument_list|,
literal|0
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
name|strlen
condition|;
name|i
operator|++
control|)
block|{
name|c
operator|=
name|charr
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
operator|(
name|c
operator|>=
literal|0x0001
operator|)
operator|&&
operator|(
name|c
operator|<=
literal|0x007F
operator|)
condition|)
block|{
name|utflen
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|>
literal|0x07FF
condition|)
block|{
name|utflen
operator|+=
literal|3
expr_stmt|;
block|}
else|else
block|{
name|utflen
operator|+=
literal|2
expr_stmt|;
block|}
block|}
comment|// TODO diff: Sun code - removed
name|byte
index|[]
name|bytearr
init|=
operator|new
name|byte
index|[
name|utflen
operator|+
literal|4
index|]
decl_stmt|;
comment|// TODO diff: Sun code
name|bytearr
index|[
name|count
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|utflen
operator|>>>
literal|24
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
comment|// TODO diff:
comment|// Sun code
name|bytearr
index|[
name|count
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|utflen
operator|>>>
literal|16
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
comment|// TODO diff:
comment|// Sun code
name|bytearr
index|[
name|count
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|utflen
operator|>>>
literal|8
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|bytearr
index|[
name|count
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|utflen
operator|>>>
literal|0
operator|)
operator|&
literal|0xFF
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
name|strlen
condition|;
name|i
operator|++
control|)
block|{
name|c
operator|=
name|charr
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
operator|(
name|c
operator|>=
literal|0x0001
operator|)
operator|&&
operator|(
name|c
operator|<=
literal|0x007F
operator|)
condition|)
block|{
name|bytearr
index|[
name|count
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
name|c
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|>
literal|0x07FF
condition|)
block|{
name|bytearr
index|[
name|count
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0xE0
operator||
operator|(
operator|(
name|c
operator|>>
literal|12
operator|)
operator|&
literal|0x0F
operator|)
argument_list|)
expr_stmt|;
name|bytearr
index|[
name|count
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|c
operator|>>
literal|6
operator|)
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
name|bytearr
index|[
name|count
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|c
operator|>>
literal|0
operator|)
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|bytearr
index|[
name|count
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0xC0
operator||
operator|(
operator|(
name|c
operator|>>
literal|6
operator|)
operator|&
literal|0x1F
operator|)
argument_list|)
expr_stmt|;
name|bytearr
index|[
name|count
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|c
operator|>>
literal|0
operator|)
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
name|dataOut
operator|.
name|write
argument_list|(
name|bytearr
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dataOut
operator|.
name|writeInt
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|String
name|readUTF8
parameter_list|(
name|DataInput
name|dataIn
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|utflen
init|=
name|dataIn
operator|.
name|readInt
argument_list|()
decl_stmt|;
comment|// TODO diff: Sun code
if|if
condition|(
name|utflen
operator|>
operator|-
literal|1
condition|)
block|{
name|StringBuffer
name|str
init|=
operator|new
name|StringBuffer
argument_list|(
name|utflen
argument_list|)
decl_stmt|;
name|byte
name|bytearr
index|[]
init|=
operator|new
name|byte
index|[
name|utflen
index|]
decl_stmt|;
name|int
name|c
decl_stmt|;
name|int
name|char2
decl_stmt|;
name|int
name|char3
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|dataIn
operator|.
name|readFully
argument_list|(
name|bytearr
argument_list|,
literal|0
argument_list|,
name|utflen
argument_list|)
expr_stmt|;
while|while
condition|(
name|count
operator|<
name|utflen
condition|)
block|{
name|c
operator|=
name|bytearr
index|[
name|count
index|]
operator|&
literal|0xff
expr_stmt|;
switch|switch
condition|(
name|c
operator|>>
literal|4
condition|)
block|{
case|case
literal|0
case|:
case|case
literal|1
case|:
case|case
literal|2
case|:
case|case
literal|3
case|:
case|case
literal|4
case|:
case|case
literal|5
case|:
case|case
literal|6
case|:
case|case
literal|7
case|:
comment|/* 0xxxxxxx */
name|count
operator|++
expr_stmt|;
name|str
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|c
argument_list|)
expr_stmt|;
break|break;
case|case
literal|12
case|:
case|case
literal|13
case|:
comment|/* 110x xxxx 10xx xxxx */
name|count
operator|+=
literal|2
expr_stmt|;
if|if
condition|(
name|count
operator|>
name|utflen
condition|)
block|{
throw|throw
operator|new
name|UTFDataFormatException
argument_list|()
throw|;
block|}
name|char2
operator|=
name|bytearr
index|[
name|count
operator|-
literal|1
index|]
expr_stmt|;
if|if
condition|(
operator|(
name|char2
operator|&
literal|0xC0
operator|)
operator|!=
literal|0x80
condition|)
block|{
throw|throw
operator|new
name|UTFDataFormatException
argument_list|()
throw|;
block|}
name|str
operator|.
name|append
argument_list|(
call|(
name|char
call|)
argument_list|(
operator|(
operator|(
name|c
operator|&
literal|0x1F
operator|)
operator|<<
literal|6
operator|)
operator||
operator|(
name|char2
operator|&
literal|0x3F
operator|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|14
case|:
comment|/* 1110 xxxx 10xx xxxx 10xx xxxx */
name|count
operator|+=
literal|3
expr_stmt|;
if|if
condition|(
name|count
operator|>
name|utflen
condition|)
block|{
throw|throw
operator|new
name|UTFDataFormatException
argument_list|()
throw|;
block|}
name|char2
operator|=
name|bytearr
index|[
name|count
operator|-
literal|2
index|]
expr_stmt|;
comment|// TODO diff: Sun code
name|char3
operator|=
name|bytearr
index|[
name|count
operator|-
literal|1
index|]
expr_stmt|;
comment|// TODO diff: Sun code
if|if
condition|(
operator|(
operator|(
name|char2
operator|&
literal|0xC0
operator|)
operator|!=
literal|0x80
operator|)
operator|||
operator|(
operator|(
name|char3
operator|&
literal|0xC0
operator|)
operator|!=
literal|0x80
operator|)
condition|)
block|{
throw|throw
operator|new
name|UTFDataFormatException
argument_list|()
throw|;
block|}
name|str
operator|.
name|append
argument_list|(
call|(
name|char
call|)
argument_list|(
operator|(
operator|(
name|c
operator|&
literal|0x0F
operator|)
operator|<<
literal|12
operator|)
operator||
operator|(
operator|(
name|char2
operator|&
literal|0x3F
operator|)
operator|<<
literal|6
operator|)
operator||
operator|(
operator|(
name|char3
operator|&
literal|0x3F
operator|)
operator|<<
literal|0
operator|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
comment|/* 10xx xxxx, 1111 xxxx */
throw|throw
operator|new
name|UTFDataFormatException
argument_list|()
throw|;
block|}
block|}
comment|// The number of chars produced may be less than utflen
return|return
operator|new
name|String
argument_list|(
name|str
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|public
specifier|static
name|String
name|propertiesToString
parameter_list|(
name|Properties
name|props
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|result
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|props
operator|!=
literal|null
condition|)
block|{
name|DataByteArrayOutputStream
name|dataOut
init|=
operator|new
name|DataByteArrayOutputStream
argument_list|()
decl_stmt|;
name|props
operator|.
name|store
argument_list|(
name|dataOut
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|String
argument_list|(
name|dataOut
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|dataOut
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
specifier|static
name|Properties
name|stringToProperties
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|IOException
block|{
name|Properties
name|result
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
if|if
condition|(
name|str
operator|!=
literal|null
operator|&&
name|str
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|DataByteArrayInputStream
name|dataIn
init|=
operator|new
name|DataByteArrayInputStream
argument_list|(
name|str
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|result
operator|.
name|load
argument_list|(
name|dataIn
argument_list|)
expr_stmt|;
name|dataIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
specifier|static
name|String
name|truncate64
parameter_list|(
name|String
name|text
parameter_list|)
block|{
if|if
condition|(
name|text
operator|.
name|length
argument_list|()
operator|>
literal|63
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
literal|45
argument_list|)
operator|+
literal|"..."
operator|+
name|text
operator|.
name|substring
argument_list|(
name|text
operator|.
name|length
argument_list|()
operator|-
literal|12
argument_list|)
expr_stmt|;
block|}
return|return
name|text
return|;
block|}
block|}
end_class

end_unit
