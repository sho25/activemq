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
name|openwire
operator|.
name|tool
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|org
operator|.
name|codehaus
operator|.
name|jam
operator|.
name|JAnnotation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jam
operator|.
name|JAnnotationValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jam
operator|.
name|JClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jam
operator|.
name|JProperty
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 381410 $  */
end_comment

begin_class
specifier|public
class|class
name|CppMarshallingClassesGenerator
extends|extends
name|CppMarshallingHeadersGenerator
block|{
specifier|protected
name|String
name|getFilePostFix
parameter_list|()
block|{
return|return
literal|".cpp"
return|;
block|}
specifier|protected
name|void
name|generateUnmarshalBodyForProperty
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
name|JProperty
name|property
parameter_list|,
name|JAnnotationValue
name|size
parameter_list|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|"    "
argument_list|)
expr_stmt|;
name|String
name|setter
init|=
name|property
operator|.
name|getSetter
argument_list|()
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
name|String
name|type
init|=
name|property
operator|.
name|getType
argument_list|()
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"boolean"
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"info."
operator|+
name|setter
operator|+
literal|"( bs.readBoolean() );"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"byte"
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"info."
operator|+
name|setter
operator|+
literal|"( DataStreamMarshaller.readByte(dataIn) );"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"char"
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"info."
operator|+
name|setter
operator|+
literal|"( DataStreamMarshaller.readChar(dataIn) );"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"short"
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"info."
operator|+
name|setter
operator|+
literal|"( DataStreamMarshaller.readShort(dataIn) );"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"int"
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"info."
operator|+
name|setter
operator|+
literal|"( DataStreamMarshaller.readInt(dataIn) );"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"long"
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"info."
operator|+
name|setter
operator|+
literal|"( UnmarshalLong(wireFormat, dataIn, bs) );"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"String"
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"info."
operator|+
name|setter
operator|+
literal|"( readString(dataIn, bs) );"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"byte[]"
argument_list|)
operator|||
name|type
operator|.
name|equals
argument_list|(
literal|"ByteSequence"
argument_list|)
condition|)
block|{
if|if
condition|(
name|size
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"info."
operator|+
name|setter
operator|+
literal|"( readBytes(dataIn, "
operator|+
name|size
operator|.
name|asInt
argument_list|()
operator|+
literal|") );"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|"info."
operator|+
name|setter
operator|+
literal|"( readBytes(dataIn, bs.readBoolean()) );"
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|isThrowable
argument_list|(
name|property
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"info."
operator|+
name|setter
operator|+
literal|"( unmarshalBrokerError(wireFormat, dataIn, bs) );"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isCachedProperty
argument_list|(
name|property
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"info."
operator|+
name|setter
operator|+
literal|"( ("
operator|+
name|type
operator|+
literal|") unmarshalCachedObject(wireFormat, dataIn, bs) );"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|"info."
operator|+
name|setter
operator|+
literal|"( ("
operator|+
name|type
operator|+
literal|") unmarshalNestedObject(wireFormat, dataIn, bs) );"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|generateUnmarshalBodyForArrayProperty
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
name|JProperty
name|property
parameter_list|,
name|JAnnotationValue
name|size
parameter_list|)
block|{
name|JClass
name|propertyType
init|=
name|property
operator|.
name|getType
argument_list|()
decl_stmt|;
name|String
name|arrayType
init|=
name|propertyType
operator|.
name|getArrayComponentType
argument_list|()
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
name|String
name|setter
init|=
name|property
operator|.
name|getGetter
argument_list|()
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
if|if
condition|(
name|size
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"    {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"        "
operator|+
name|arrayType
operator|+
literal|"[] value = new "
operator|+
name|arrayType
operator|+
literal|"["
operator|+
name|size
operator|.
name|asInt
argument_list|()
operator|+
literal|"];"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"        "
operator|+
literal|"for( int i=0; i< "
operator|+
name|size
operator|.
name|asInt
argument_list|()
operator|+
literal|"; i++ ) {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"            value[i] = ("
operator|+
name|arrayType
operator|+
literal|") unmarshalNestedObject(wireFormat,dataIn, bs);"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"        }"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"        info."
operator|+
name|setter
operator|+
literal|"( value );"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    }"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|"    if (bs.readBoolean()) {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"        short size = DataStreamMarshaller.readShort(dataIn);"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"        "
operator|+
name|arrayType
operator|+
literal|"[] value = new "
operator|+
name|arrayType
operator|+
literal|"[size];"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"        for( int i=0; i< size; i++ ) {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"            value[i] = ("
operator|+
name|arrayType
operator|+
literal|") unmarshalNestedObject(wireFormat,dataIn, bs);"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"        }"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"        info."
operator|+
name|setter
operator|+
literal|"( value );"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    }"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    else {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"        info."
operator|+
name|setter
operator|+
literal|"( null );"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    }"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|int
name|generateMarshal1Body
parameter_list|(
name|PrintWriter
name|out
parameter_list|)
block|{
name|List
name|properties
init|=
name|getProperties
argument_list|()
decl_stmt|;
name|int
name|baseSize
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|properties
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
name|JProperty
name|property
init|=
operator|(
name|JProperty
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|JAnnotation
name|annotation
init|=
name|property
operator|.
name|getAnnotation
argument_list|(
literal|"openwire:property"
argument_list|)
decl_stmt|;
name|JAnnotationValue
name|size
init|=
name|annotation
operator|.
name|getValue
argument_list|(
literal|"size"
argument_list|)
decl_stmt|;
name|JClass
name|propertyType
init|=
name|property
operator|.
name|getType
argument_list|()
decl_stmt|;
name|String
name|type
init|=
name|propertyType
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
name|String
name|getter
init|=
literal|"info."
operator|+
name|property
operator|.
name|getGetter
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"()"
decl_stmt|;
name|out
operator|.
name|print
argument_list|(
name|indent
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"boolean"
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"bs.writeBoolean("
operator|+
name|getter
operator|+
literal|");"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"byte"
argument_list|)
condition|)
block|{
name|baseSize
operator|+=
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"char"
argument_list|)
condition|)
block|{
name|baseSize
operator|+=
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"short"
argument_list|)
condition|)
block|{
name|baseSize
operator|+=
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"int"
argument_list|)
condition|)
block|{
name|baseSize
operator|+=
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"long"
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"rc += marshal1Long(wireFormat, "
operator|+
name|getter
operator|+
literal|", bs);"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"String"
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"rc += writeString("
operator|+
name|getter
operator|+
literal|", bs);"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"byte[]"
argument_list|)
operator|||
name|type
operator|.
name|equals
argument_list|(
literal|"ByteSequence"
argument_list|)
condition|)
block|{
if|if
condition|(
name|size
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"bs.writeBoolean("
operator|+
name|getter
operator|+
literal|"!=null);"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    rc += "
operator|+
name|getter
operator|+
literal|"==null ? 0 : "
operator|+
name|getter
operator|+
literal|".Length+4;"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|baseSize
operator|+=
name|size
operator|.
name|asInt
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|propertyType
operator|.
name|isArrayType
argument_list|()
condition|)
block|{
if|if
condition|(
name|size
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"rc += marshalObjectArrayConstSize(wireFormat, "
operator|+
name|getter
operator|+
literal|", bs, "
operator|+
name|size
operator|.
name|asInt
argument_list|()
operator|+
literal|");"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|"rc += marshalObjectArray(wireFormat, "
operator|+
name|getter
operator|+
literal|", bs);"
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|isThrowable
argument_list|(
name|propertyType
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"rc += marshalBrokerError(wireFormat, "
operator|+
name|getter
operator|+
literal|", bs);"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|isCachedProperty
argument_list|(
name|property
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"rc += marshal1CachedObject(wireFormat, "
operator|+
name|getter
operator|+
literal|", bs);"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|"rc += marshal1NestedObject(wireFormat, "
operator|+
name|getter
operator|+
literal|", bs);"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|baseSize
return|;
block|}
specifier|protected
name|void
name|generateMarshal2Body
parameter_list|(
name|PrintWriter
name|out
parameter_list|)
block|{
name|List
name|properties
init|=
name|getProperties
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|properties
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
name|JProperty
name|property
init|=
operator|(
name|JProperty
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|JAnnotation
name|annotation
init|=
name|property
operator|.
name|getAnnotation
argument_list|(
literal|"openwire:property"
argument_list|)
decl_stmt|;
name|JAnnotationValue
name|size
init|=
name|annotation
operator|.
name|getValue
argument_list|(
literal|"size"
argument_list|)
decl_stmt|;
name|JClass
name|propertyType
init|=
name|property
operator|.
name|getType
argument_list|()
decl_stmt|;
name|String
name|type
init|=
name|propertyType
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
name|String
name|getter
init|=
literal|"info."
operator|+
name|property
operator|.
name|getGetter
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"()"
decl_stmt|;
name|out
operator|.
name|print
argument_list|(
name|indent
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"boolean"
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"bs.readBoolean();"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"byte"
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"DataStreamMarshaller.writeByte("
operator|+
name|getter
operator|+
literal|", dataOut);"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"char"
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"DataStreamMarshaller.writeChar("
operator|+
name|getter
operator|+
literal|", dataOut);"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"short"
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"DataStreamMarshaller.writeShort("
operator|+
name|getter
operator|+
literal|", dataOut);"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"int"
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"DataStreamMarshaller.writeInt("
operator|+
name|getter
operator|+
literal|", dataOut);"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"long"
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"marshal2Long(wireFormat, "
operator|+
name|getter
operator|+
literal|", dataOut, bs);"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"String"
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"writeString("
operator|+
name|getter
operator|+
literal|", dataOut, bs);"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"byte[]"
argument_list|)
operator|||
name|type
operator|.
name|equals
argument_list|(
literal|"ByteSequence"
argument_list|)
condition|)
block|{
if|if
condition|(
name|size
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"dataOut.write("
operator|+
name|getter
operator|+
literal|", 0, "
operator|+
name|size
operator|.
name|asInt
argument_list|()
operator|+
literal|");"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|"if(bs.readBoolean()) {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"       DataStreamMarshaller.writeInt("
operator|+
name|getter
operator|+
literal|".Length, dataOut);"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"       dataOut.write("
operator|+
name|getter
operator|+
literal|");"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    }"
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|propertyType
operator|.
name|isArrayType
argument_list|()
condition|)
block|{
if|if
condition|(
name|size
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"marshalObjectArrayConstSize(wireFormat, "
operator|+
name|getter
operator|+
literal|", dataOut, bs, "
operator|+
name|size
operator|.
name|asInt
argument_list|()
operator|+
literal|");"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|"marshalObjectArray(wireFormat, "
operator|+
name|getter
operator|+
literal|", dataOut, bs);"
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|isThrowable
argument_list|(
name|propertyType
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"marshalBrokerError(wireFormat, "
operator|+
name|getter
operator|+
literal|", dataOut, bs);"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|isCachedProperty
argument_list|(
name|property
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"marshal2CachedObject(wireFormat, "
operator|+
name|getter
operator|+
literal|", dataOut, bs);"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|"marshal2NestedObject(wireFormat, "
operator|+
name|getter
operator|+
literal|", dataOut, bs);"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|protected
name|void
name|generateFile
parameter_list|(
name|PrintWriter
name|out
parameter_list|)
throws|throws
name|Exception
block|{
name|generateLicence
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#include \"marshal/"
operator|+
name|className
operator|+
literal|".hpp\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"using namespace apache::activemq::client::marshal;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"/*"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" *  Marshalling code for Open Wire Format for "
operator|+
name|jclass
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" *"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" * NOTE!: This file is autogenerated - do not modify!"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" *        if you need to make a change, please see the Groovy scripts in the"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" *        activemq-core module"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" */"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
operator|+
name|className
operator|+
literal|"::"
operator|+
name|className
operator|+
literal|"()"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    // no-op"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
operator|+
name|className
operator|+
literal|"::~"
operator|+
name|className
operator|+
literal|"()"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    // no-op"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isAbstractClass
argument_list|()
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"IDataStructure* "
operator|+
name|className
operator|+
literal|"::createObject() "
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    return new "
operator|+
name|jclass
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"();"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"char "
operator|+
name|className
operator|+
literal|"::getDataStructureType() "
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    return "
operator|+
name|jclass
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|".ID_"
operator|+
name|jclass
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|";"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    /* "
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"     * Un-marshal an object instance from the data input stream"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"     */ "
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"void "
operator|+
name|className
operator|+
literal|"::unmarshal(ProtocolFormat& wireFormat, Object o, BinaryReader& dataIn, BooleanStream& bs) "
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    base.unmarshal(wireFormat, o, dataIn, bs);"
argument_list|)
expr_stmt|;
name|List
name|properties
init|=
name|getProperties
argument_list|()
decl_stmt|;
name|boolean
name|marshallerAware
init|=
name|isMarshallerAware
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|properties
operator|.
name|isEmpty
argument_list|()
operator|||
name|marshallerAware
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    "
operator|+
name|jclass
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"& info = ("
operator|+
name|jclass
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"&) o;"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|marshallerAware
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    info.beforeUnmarshall(wireFormat);"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"        "
argument_list|)
expr_stmt|;
block|}
name|generateTightUnmarshalBody
argument_list|(
name|out
argument_list|)
expr_stmt|;
if|if
condition|(
name|marshallerAware
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    info.afterUnmarshall(wireFormat);"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"/*"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" * Write the booleans that this object uses to a BooleanStream"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" */"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"int "
operator|+
name|className
operator|+
literal|"::marshal1(ProtocolFormat& wireFormat, Object& o, BooleanStream& bs) {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    "
operator|+
name|jclass
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"& info = ("
operator|+
name|jclass
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"&) o;"
argument_list|)
expr_stmt|;
if|if
condition|(
name|marshallerAware
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    info.beforeMarshall(wireFormat);"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    int rc = base.marshal1(wireFormat, info, bs);"
argument_list|)
expr_stmt|;
name|int
name|baseSize
init|=
name|generateMarshal1Body
argument_list|(
name|out
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    return rc + "
operator|+
name|baseSize
operator|+
literal|";"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"/* "
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" * Write a object instance to data output stream"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" */"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"void "
operator|+
name|className
operator|+
literal|"::marshal2(ProtocolFormat& wireFormat, Object& o, BinaryWriter& dataOut, BooleanStream& bs) {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    base.marshal2(wireFormat, o, dataOut, bs);"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|properties
operator|.
name|isEmpty
argument_list|()
operator|||
name|marshallerAware
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    "
operator|+
name|jclass
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"& info = ("
operator|+
name|jclass
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"&) o;"
argument_list|)
expr_stmt|;
block|}
name|generateMarshal2Body
argument_list|(
name|out
argument_list|)
expr_stmt|;
if|if
condition|(
name|marshallerAware
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    info.afterMarshall(wireFormat);"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|void
name|generateFactory
parameter_list|(
name|PrintWriter
name|out
parameter_list|)
block|{
name|generateLicence
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"// Marshalling code for Open Wire Format"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"//"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"//"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"// NOTE!: This file is autogenerated - do not modify!"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"//        if you need to make a change, please see the Groovy scripts in the"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"//        activemq-openwire module"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"//"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#include \"marshal/"
operator|+
name|className
operator|+
literal|".hpp\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|List
name|list
init|=
operator|new
name|ArrayList
argument_list|(
name|getConcreteClasses
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|list
argument_list|,
operator|new
name|Comparator
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
name|JClass
name|c1
init|=
operator|(
name|JClass
operator|)
name|o1
decl_stmt|;
name|JClass
name|c2
init|=
operator|(
name|JClass
operator|)
name|o2
decl_stmt|;
return|return
name|c1
operator|.
name|getSimpleName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|c2
operator|.
name|getSimpleName
argument_list|()
argument_list|)
return|;
block|}
block|}
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
name|JClass
name|jclass
init|=
operator|(
name|JClass
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#include \"marshal/"
operator|+
name|jclass
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"Marshaller.hpp\""
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"using namespace apache::activemq::client::marshal;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"void MarshallerFactory::configure(ProtocolFormat& format) "
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"{"
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
name|JClass
name|jclass
init|=
operator|(
name|JClass
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    format.addMarshaller(new "
operator|+
name|jclass
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"Marshaller());"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

