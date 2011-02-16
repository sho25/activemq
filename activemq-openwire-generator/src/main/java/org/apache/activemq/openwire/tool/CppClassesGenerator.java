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
name|File
import|;
end_import

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
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|CppClassesGenerator
extends|extends
name|MultiSourceGenerator
block|{
specifier|protected
name|String
name|targetDir
init|=
literal|"./src/main/cpp"
decl_stmt|;
specifier|public
name|Object
name|run
parameter_list|()
block|{
name|filePostFix
operator|=
name|getFilePostFix
argument_list|()
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
name|targetDir
operator|+
literal|"/activemq/command"
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
name|String
name|getFilePostFix
parameter_list|()
block|{
return|return
literal|".cpp"
return|;
block|}
comment|/**      * Converts the Java type to a C++ type name      */
specifier|public
name|String
name|toCppType
parameter_list|(
name|JClass
name|type
parameter_list|)
block|{
name|String
name|name
init|=
name|type
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"String"
argument_list|)
condition|)
block|{
return|return
literal|"p<string>"
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|isArrayType
argument_list|()
condition|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"byte[]"
argument_list|)
condition|)
block|{
name|name
operator|=
literal|"char[]"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"DataStructure[]"
argument_list|)
condition|)
block|{
name|name
operator|=
literal|"IDataStructure[]"
expr_stmt|;
block|}
return|return
literal|"array<"
operator|+
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|name
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
operator|+
literal|">"
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"Throwable"
argument_list|)
operator|||
name|name
operator|.
name|equals
argument_list|(
literal|"Exception"
argument_list|)
condition|)
block|{
return|return
literal|"p<BrokerError>"
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"ByteSequence"
argument_list|)
condition|)
block|{
return|return
literal|"array<char>"
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"boolean"
argument_list|)
condition|)
block|{
return|return
literal|"bool"
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"long"
argument_list|)
condition|)
block|{
return|return
literal|"long long"
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"byte"
argument_list|)
condition|)
block|{
return|return
literal|"char"
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"Command"
argument_list|)
operator|||
name|name
operator|.
name|equals
argument_list|(
literal|"DataStructure"
argument_list|)
condition|)
block|{
return|return
literal|"p<I"
operator|+
name|name
operator|+
literal|">"
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|type
operator|.
name|isPrimitiveType
argument_list|()
condition|)
block|{
return|return
literal|"p<"
operator|+
name|name
operator|+
literal|">"
return|;
block|}
else|else
block|{
return|return
name|name
return|;
block|}
block|}
comment|/**      * Converts the Java type to a C++ default value      */
specifier|public
name|String
name|toCppDefaultValue
parameter_list|(
name|JClass
name|type
parameter_list|)
block|{
name|String
name|name
init|=
name|type
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"boolean"
argument_list|)
condition|)
block|{
return|return
literal|"false"
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|type
operator|.
name|isPrimitiveType
argument_list|()
condition|)
block|{
return|return
literal|"NULL"
return|;
block|}
else|else
block|{
return|return
literal|"0"
return|;
block|}
block|}
comment|/**      * Converts the Java type to the name of the C++ marshal method to be used      */
specifier|public
name|String
name|toMarshalMethodName
parameter_list|(
name|JClass
name|type
parameter_list|)
block|{
name|String
name|name
init|=
name|type
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"String"
argument_list|)
condition|)
block|{
return|return
literal|"marshalString"
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|isArrayType
argument_list|()
condition|)
block|{
if|if
condition|(
name|type
operator|.
name|getArrayComponentType
argument_list|()
operator|.
name|isPrimitiveType
argument_list|()
operator|&&
name|name
operator|.
name|equals
argument_list|(
literal|"byte[]"
argument_list|)
condition|)
block|{
return|return
literal|"marshalByteArray"
return|;
block|}
else|else
block|{
return|return
literal|"marshalObjectArray"
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"ByteSequence"
argument_list|)
condition|)
block|{
return|return
literal|"marshalByteArray"
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"short"
argument_list|)
condition|)
block|{
return|return
literal|"marshalShort"
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"int"
argument_list|)
condition|)
block|{
return|return
literal|"marshalInt"
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"long"
argument_list|)
condition|)
block|{
return|return
literal|"marshalLong"
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"byte"
argument_list|)
condition|)
block|{
return|return
literal|"marshalByte"
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"double"
argument_list|)
condition|)
block|{
return|return
literal|"marshalDouble"
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"float"
argument_list|)
condition|)
block|{
return|return
literal|"marshalFloat"
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"boolean"
argument_list|)
condition|)
block|{
return|return
literal|"marshalBoolean"
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|type
operator|.
name|isPrimitiveType
argument_list|()
condition|)
block|{
return|return
literal|"marshalObject"
return|;
block|}
else|else
block|{
return|return
name|name
return|;
block|}
block|}
comment|/**      * Converts the Java type to the name of the C++ unmarshal method to be used      */
specifier|public
name|String
name|toUnmarshalMethodName
parameter_list|(
name|JClass
name|type
parameter_list|)
block|{
name|String
name|name
init|=
name|type
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"String"
argument_list|)
condition|)
block|{
return|return
literal|"unmarshalString"
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|isArrayType
argument_list|()
condition|)
block|{
if|if
condition|(
name|type
operator|.
name|getArrayComponentType
argument_list|()
operator|.
name|isPrimitiveType
argument_list|()
operator|&&
name|name
operator|.
name|equals
argument_list|(
literal|"byte[]"
argument_list|)
condition|)
block|{
return|return
literal|"unmarshalByteArray"
return|;
block|}
else|else
block|{
return|return
literal|"unmarshalObjectArray"
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"ByteSequence"
argument_list|)
condition|)
block|{
return|return
literal|"unmarshalByteArray"
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"short"
argument_list|)
condition|)
block|{
return|return
literal|"unmarshalShort"
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"int"
argument_list|)
condition|)
block|{
return|return
literal|"unmarshalInt"
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"long"
argument_list|)
condition|)
block|{
return|return
literal|"unmarshalLong"
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"byte"
argument_list|)
condition|)
block|{
return|return
literal|"unmarshalByte"
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"double"
argument_list|)
condition|)
block|{
return|return
literal|"unmarshalDouble"
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"float"
argument_list|)
condition|)
block|{
return|return
literal|"unmarshalFloat"
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"boolean"
argument_list|)
condition|)
block|{
return|return
literal|"unmarshalBoolean"
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|type
operator|.
name|isPrimitiveType
argument_list|()
condition|)
block|{
return|return
literal|"unmarshalObject"
return|;
block|}
else|else
block|{
return|return
name|name
return|;
block|}
block|}
comment|/**      * Converts the Java type to a C++ pointer cast      */
specifier|public
name|String
name|toUnmarshalCast
parameter_list|(
name|JClass
name|type
parameter_list|)
block|{
name|String
name|name
init|=
name|toCppType
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"p<"
argument_list|)
condition|)
block|{
return|return
literal|"p_cast<"
operator|+
name|name
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"array<"
argument_list|)
operator|&&
operator|(
name|type
operator|.
name|isArrayType
argument_list|()
operator|&&
operator|!
name|type
operator|.
name|getArrayComponentType
argument_list|()
operator|.
name|isPrimitiveType
argument_list|()
operator|)
operator|&&
operator|!
name|type
operator|.
name|getSimpleName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"ByteSequence"
argument_list|)
condition|)
block|{
return|return
literal|"array_cast<"
operator|+
name|name
operator|.
name|substring
argument_list|(
literal|6
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|""
return|;
block|}
block|}
specifier|protected
name|void
name|generateLicence
parameter_list|(
name|PrintWriter
name|out
parameter_list|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"/**"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" * Licensed to the Apache Software Foundation (ASF) under one or more"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" * contributor license agreements.  See the NOTICE file distributed with"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" * this work for additional information regarding copyright ownership."
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" * The ASF licenses this file to You under the Apache License, Version 2.0"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" * (the \"License\"); you may not use this file except in compliance with"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" * the License.  You may obtain a copy of the License at"
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
literal|" *      http://www.apache.org/licenses/LICENSE-2.0"
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
literal|" * Unless required by applicable law or agreed to in writing, software"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" * distributed under the License is distributed on an \"AS IS\" BASIS,"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied."
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" * See the License for the specific language governing permissions and"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" * limitations under the License."
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" */"
argument_list|)
expr_stmt|;
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
literal|"#include \"activemq/command/"
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
literal|"using namespace apache::activemq::command;"
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
literal|" *"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" *  Command and marshalling code for OpenWire format for "
operator|+
name|className
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
literal|" *"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" *  NOTE!: This file is autogenerated - do not modify!"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" *         if you need to make a change, please see the Groovy scripts in the"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" *         activemq-core module"
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
literal|" */"
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
name|String
name|value
init|=
name|toCppDefaultValue
argument_list|(
name|property
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|propertyName
init|=
name|property
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
name|String
name|parameterName
init|=
name|decapitalize
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    this->"
operator|+
name|parameterName
operator|+
literal|" = "
operator|+
name|value
operator|+
literal|" ;"
argument_list|)
expr_stmt|;
block|}
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
literal|"unsigned char "
operator|+
name|className
operator|+
literal|"::getDataStructureType()"
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
name|className
operator|+
literal|"::TYPE ; "
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
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
name|String
name|type
init|=
name|toCppType
argument_list|(
name|property
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|propertyName
init|=
name|property
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
name|String
name|parameterName
init|=
name|decapitalize
argument_list|(
name|propertyName
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
literal|"        "
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
operator|+
name|type
operator|+
literal|" "
operator|+
name|className
operator|+
literal|"::get"
operator|+
name|propertyName
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
literal|"    return "
operator|+
name|parameterName
operator|+
literal|" ;"
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
literal|"void "
operator|+
name|className
operator|+
literal|"::set"
operator|+
name|propertyName
operator|+
literal|"("
operator|+
name|type
operator|+
literal|" "
operator|+
name|parameterName
operator|+
literal|")"
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
literal|"    this->"
operator|+
name|parameterName
operator|+
literal|" = "
operator|+
name|parameterName
operator|+
literal|" ;"
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
literal|"int "
operator|+
name|className
operator|+
literal|"::marshal(p<IMarshaller> marshaller, int mode, p<IOutputStream> ostream) throw (IOException)"
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
literal|"    int size = 0 ;"
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
literal|"    size += "
operator|+
name|baseClass
operator|+
literal|"::marshal(marshaller, mode, ostream) ; "
argument_list|)
expr_stmt|;
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
name|String
name|marshalMethod
init|=
name|toMarshalMethodName
argument_list|(
name|property
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|propertyName
init|=
name|decapitalize
argument_list|(
name|property
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    size += marshaller->"
operator|+
name|marshalMethod
operator|+
literal|"("
operator|+
name|propertyName
operator|+
literal|", mode, ostream) ; "
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|"    return size ;"
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
literal|"void "
operator|+
name|className
operator|+
literal|"::unmarshal(p<IMarshaller> marshaller, int mode, p<IInputStream> istream) throw (IOException)"
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
literal|"    "
operator|+
name|baseClass
operator|+
literal|"::unmarshal(marshaller, mode, istream) ; "
argument_list|)
expr_stmt|;
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
name|String
name|cast
init|=
name|toUnmarshalCast
argument_list|(
name|property
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|unmarshalMethod
init|=
name|toUnmarshalMethodName
argument_list|(
name|property
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|propertyName
init|=
name|decapitalize
argument_list|(
name|property
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    "
operator|+
name|propertyName
operator|+
literal|" = "
operator|+
name|cast
operator|+
literal|"(marshaller->"
operator|+
name|unmarshalMethod
operator|+
literal|"(mode, istream)) ; "
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getTargetDir
parameter_list|()
block|{
return|return
name|targetDir
return|;
block|}
specifier|public
name|void
name|setTargetDir
parameter_list|(
name|String
name|targetDir
parameter_list|)
block|{
name|this
operator|.
name|targetDir
operator|=
name|targetDir
expr_stmt|;
block|}
block|}
end_class

end_unit

