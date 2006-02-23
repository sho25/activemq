begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *  * @version $Revision$  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|OpenWireCSharpMarshallingScript
extends|extends
name|OpenWireJavaMarshallingScript
block|{
specifier|public
name|Object
name|run
parameter_list|()
block|{
name|filePostFix
operator|=
literal|".cs"
expr_stmt|;
if|if
condition|(
name|destDir
operator|==
literal|null
condition|)
block|{
name|destDir
operator|=
operator|new
name|File
argument_list|(
literal|"../openwire-dotnet/src/OpenWire.Client/IO"
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|run
argument_list|()
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
literal|"        "
argument_list|)
expr_stmt|;
name|String
name|propertyName
init|=
name|property
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
name|propertyName
operator|+
literal|" = bs.ReadBoolean();"
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
name|propertyName
operator|+
literal|" = DataStreamMarshaller.ReadByte(dataIn);"
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
name|propertyName
operator|+
literal|" = DataStreamMarshaller.ReadChar(dataIn);"
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
name|propertyName
operator|+
literal|" = DataStreamMarshaller.ReadShort(dataIn);"
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
name|propertyName
operator|+
literal|" = DataStreamMarshaller.ReadInt(dataIn);"
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
name|propertyName
operator|+
literal|" = UnmarshalLong(wireFormat, dataIn, bs);"
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
name|propertyName
operator|+
literal|" = ReadString(dataIn, bs);"
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
name|propertyName
operator|+
literal|" = ReadBytes(dataIn, "
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
literal|"info."
operator|+
name|propertyName
operator|+
literal|" = ReadBytes(dataIn, bs.ReadBoolean());"
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
name|propertyName
operator|+
literal|" = UnmarshalBrokerError(wireFormat, dataIn, bs);"
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
name|propertyName
operator|+
literal|" = ("
operator|+
name|type
operator|+
literal|") UnmarshalCachedObject(wireFormat, dataIn, bs);"
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
name|propertyName
operator|+
literal|" = ("
operator|+
name|type
operator|+
literal|") UnmarshalNestedObject(wireFormat, dataIn, bs);"
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
name|propertyName
init|=
name|property
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
literal|"        {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"            "
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
literal|"            "
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
literal|"                value[i] = ("
operator|+
name|arrayType
operator|+
literal|") UnmarshalNestedObject(wireFormat,dataIn, bs);"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"            }"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"            info."
operator|+
name|propertyName
operator|+
literal|" = value;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"        }"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|"        if (bs.ReadBoolean()) {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"            short size = DataStreamMarshaller.ReadShort(dataIn);"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"            "
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
literal|"            for( int i=0; i< size; i++ ) {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"                value[i] = ("
operator|+
name|arrayType
operator|+
literal|") UnmarshalNestedObject(wireFormat,dataIn, bs);"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"            }"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"            info."
operator|+
name|propertyName
operator|+
literal|" = value;"
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
literal|"        else {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"            info."
operator|+
name|propertyName
operator|+
literal|" = null;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"        }"
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
name|getSimpleName
argument_list|()
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
literal|"bs.WriteBoolean("
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
literal|"rc += Marshal1Long(wireFormat, "
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
literal|"rc += WriteString("
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
literal|"bs.WriteBoolean("
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
literal|"        rc += "
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
literal|"rc += MarshalObjectArrayConstSize(wireFormat, "
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
literal|"rc += MarshalObjectArray(wireFormat, "
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
literal|"rc += MarshalBrokerError(wireFormat, "
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
literal|"rc += Marshal1CachedObject(wireFormat, "
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
literal|"rc += Marshal1NestedObject(wireFormat, "
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
name|getSimpleName
argument_list|()
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
literal|"bs.ReadBoolean();"
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
literal|"DataStreamMarshaller.WriteByte("
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
literal|"DataStreamMarshaller.WriteChar("
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
literal|"DataStreamMarshaller.WriteShort("
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
literal|"DataStreamMarshaller.WriteInt("
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
literal|"Marshal2Long(wireFormat, "
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
literal|"WriteString("
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
literal|"dataOut.Write("
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
literal|"if(bs.ReadBoolean()) {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"           DataStreamMarshaller.WriteInt("
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
literal|"           dataOut.Write("
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
literal|"        }"
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
literal|"MarshalObjectArrayConstSize(wireFormat, "
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
literal|"MarshalObjectArray(wireFormat, "
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
literal|"MarshalBrokerError(wireFormat, "
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
literal|"Marshal2CachedObject(wireFormat, "
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
literal|"Marshal2NestedObject(wireFormat, "
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
block|}
end_class

end_unit

