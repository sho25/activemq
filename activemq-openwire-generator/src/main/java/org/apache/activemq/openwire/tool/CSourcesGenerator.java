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
name|ArrayList
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
comment|/**  *   * @version $Revision: 383749 $  */
end_comment

begin_class
specifier|public
class|class
name|CSourcesGenerator
extends|extends
name|CHeadersGenerator
block|{
specifier|public
name|Object
name|run
parameter_list|()
block|{
name|filePostFix
operator|=
literal|".c"
expr_stmt|;
if|if
condition|(
name|destFile
operator|==
literal|null
condition|)
block|{
name|destFile
operator|=
operator|new
name|File
argument_list|(
name|targetDir
operator|+
literal|"/ow_commands_v"
operator|+
name|getOpenwireVersion
argument_list|()
operator|+
literal|".c"
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
name|List
name|sort
parameter_list|(
name|List
name|source
parameter_list|)
block|{
return|return
name|source
return|;
block|}
specifier|protected
name|void
name|generateSetup
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
literal|"/*****************************************************************************************"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" *  "
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" * NOTE!: This file is auto generated - do not modify!"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" *        if you need to make a change, please see the modify the groovy scripts in the"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" *        under src/gram/script and then use maven openwire:generate to regenerate "
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" *        this file."
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" *  "
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" *****************************************************************************************/"
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
literal|"#include \"ow_commands_v"
operator|+
name|openwireVersion
operator|+
literal|".h\""
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
literal|"#define SUCCESS_CHECK( f ) { apr_status_t rc=f; if(rc!=APR_SUCCESS) return rc; }"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
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
name|ArrayList
name|properties
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|jclass
operator|.
name|getDeclaredProperties
argument_list|()
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
name|jclass
operator|.
name|getDeclaredProperties
argument_list|()
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|JProperty
name|p
init|=
name|jclass
operator|.
name|getDeclaredProperties
argument_list|()
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|isValidProperty
argument_list|(
name|p
argument_list|)
condition|)
block|{
name|properties
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|name
init|=
name|jclass
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
name|String
name|type
init|=
operator|(
literal|"ow_"
operator|+
name|name
operator|)
operator|.
name|toUpperCase
argument_list|()
operator|+
literal|"_TYPE"
decl_stmt|;
name|String
name|baseName
init|=
literal|"DataStructure"
decl_stmt|;
name|JClass
name|superclass
init|=
name|jclass
operator|.
name|getSuperclass
argument_list|()
decl_stmt|;
while|while
condition|(
name|superclass
operator|.
name|getSuperclass
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|sortedClasses
operator|.
name|contains
argument_list|(
name|superclass
argument_list|)
condition|)
block|{
name|baseName
operator|=
name|superclass
operator|.
name|getSimpleName
argument_list|()
expr_stmt|;
break|break;
block|}
else|else
block|{
name|superclass
operator|=
name|superclass
operator|.
name|getSuperclass
argument_list|()
expr_stmt|;
block|}
block|}
name|out
operator|.
name|println
argument_list|(
literal|"ow_boolean ow_is_a_"
operator|+
name|name
operator|+
literal|"(ow_DataStructure *object) {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"   if( object == 0 )"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"      return 0;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"      "
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"   switch(object->structType) {"
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|iterator
init|=
name|sortedClasses
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|JClass
name|sub
init|=
operator|(
name|JClass
operator|)
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|subtype
init|=
literal|"OW_"
operator|+
name|sub
operator|.
name|getSimpleName
argument_list|()
operator|.
name|toUpperCase
argument_list|()
operator|+
literal|"_TYPE"
decl_stmt|;
if|if
condition|(
name|jclass
operator|.
name|isAssignableFrom
argument_list|(
name|sub
argument_list|)
operator|&&
operator|!
name|isAbstract
argument_list|(
name|sub
argument_list|)
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
literal|"   case "
operator|+
name|subtype
operator|+
literal|":"
argument_list|)
expr_stmt|;
block|}
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
literal|"      return 1;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"   }"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"   return 0;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isAbstract
argument_list|(
name|jclass
argument_list|)
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
literal|"ow_"
operator|+
name|name
operator|+
literal|" *ow_"
operator|+
name|name
operator|+
literal|"_create(apr_pool_t *pool) "
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
literal|"   ow_"
operator|+
name|name
operator|+
literal|" *value = apr_pcalloc(pool,sizeof(ow_"
operator|+
name|name
operator|+
literal|"));"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"   if( value!=0 ) {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"      ((ow_DataStructure*)value)->structType = "
operator|+
name|type
operator|+
literal|";"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"   }"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"   return value;"
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
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"apr_status_t ow_marshal1_"
operator|+
name|name
operator|+
literal|"(ow_bit_buffer *buffer, ow_"
operator|+
name|name
operator|+
literal|" *object)"
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
literal|"   ow_marshal1_"
operator|+
name|baseName
operator|+
literal|"(buffer, (ow_"
operator|+
name|baseName
operator|+
literal|"*)object);"
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
name|propname
init|=
name|toPropertyCase
argument_list|(
name|property
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|cached
init|=
name|isCachedProperty
argument_list|(
name|property
argument_list|)
decl_stmt|;
name|JAnnotation
name|annotation
init|=
name|property
operator|.
name|getGetter
argument_list|()
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
name|type
operator|=
name|property
operator|.
name|getType
argument_list|()
operator|.
name|getQualifiedName
argument_list|()
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
literal|"   ow_bit_buffer_append(buffer, object->"
operator|+
name|propname
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
block|{                    }
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
block|{                    }
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
block|{                    }
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
block|{                    }
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
literal|"   ow_marshal1_long(buffer, object->"
operator|+
name|propname
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
literal|"byte[]"
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
literal|"   ow_bit_buffer_append(buffer,  object->"
operator|+
name|propname
operator|+
literal|"!=0 );"
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"org.apache.activeio.packet.ByteSequence"
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
literal|"   ow_bit_buffer_append(buffer,  object->"
operator|+
name|propname
operator|+
literal|"!=0 );"
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"java.lang.String"
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"   ow_marshal1_string(buffer, object->"
operator|+
name|propname
operator|+
literal|");"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|property
operator|.
name|getType
argument_list|()
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
literal|"   SUCCESS_CHECK(ow_marshal1_DataStructure_array_const_size(buffer, object->"
operator|+
name|propname
operator|+
literal|", "
operator|+
name|size
operator|.
name|asInt
argument_list|()
operator|+
literal|"));"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|"   SUCCESS_CHECK(ow_marshal1_DataStructure_array(buffer, object->"
operator|+
name|propname
operator|+
literal|"));"
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
literal|"   SUCCESS_CHECK(ow_marshal1_throwable(buffer, object->"
operator|+
name|propname
operator|+
literal|"));"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|cached
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"   SUCCESS_CHECK(ow_marshal1_cached_object(buffer, (ow_DataStructure*)object->"
operator|+
name|propname
operator|+
literal|"));"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|"   SUCCESS_CHECK(ow_marshal1_nested_object(buffer, (ow_DataStructure*)object->"
operator|+
name|propname
operator|+
literal|"));"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|"   "
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"	return APR_SUCCESS;"
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
literal|"apr_status_t ow_marshal2_"
operator|+
name|name
operator|+
literal|"(ow_byte_buffer *buffer, ow_bit_buffer *bitbuffer, ow_"
operator|+
name|name
operator|+
literal|" *object)"
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
literal|"   ow_marshal2_"
operator|+
name|baseName
operator|+
literal|"(buffer, bitbuffer, (ow_"
operator|+
name|baseName
operator|+
literal|"*)object);   "
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
name|JAnnotation
name|annotation
init|=
name|property
operator|.
name|getGetter
argument_list|()
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
name|Object
name|propname
init|=
name|toPropertyCase
argument_list|(
name|property
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|cached
init|=
name|isCachedProperty
argument_list|(
name|property
argument_list|)
decl_stmt|;
name|type
operator|=
name|property
operator|.
name|getType
argument_list|()
operator|.
name|getQualifiedName
argument_list|()
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
literal|"   ow_bit_buffer_read(bitbuffer);"
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
literal|"   SUCCESS_CHECK(ow_byte_buffer_append_"
operator|+
name|type
operator|+
literal|"(buffer, object->"
operator|+
name|propname
operator|+
literal|"));"
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
literal|"   SUCCESS_CHECK(ow_byte_buffer_append_"
operator|+
name|type
operator|+
literal|"(buffer, object->"
operator|+
name|propname
operator|+
literal|"));"
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
literal|"   SUCCESS_CHECK(ow_byte_buffer_append_"
operator|+
name|type
operator|+
literal|"(buffer, object->"
operator|+
name|propname
operator|+
literal|"));"
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
literal|"   SUCCESS_CHECK(ow_byte_buffer_append_"
operator|+
name|type
operator|+
literal|"(buffer, object->"
operator|+
name|propname
operator|+
literal|"));"
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
literal|"   SUCCESS_CHECK(ow_marshal2_long(buffer, bitbuffer, object->"
operator|+
name|propname
operator|+
literal|"));"
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
literal|"   SUCCESS_CHECK(ow_marshal2_byte_array_const_size(buffer, object->"
operator|+
name|propname
operator|+
literal|", "
operator|+
name|size
operator|.
name|asInt
argument_list|()
operator|+
literal|"));"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|"   SUCCESS_CHECK(ow_marshal2_byte_array(buffer, bitbuffer, object->"
operator|+
name|propname
operator|+
literal|"));"
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"org.apache.activeio.packet.ByteSequence"
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
literal|"   SUCCESS_CHECK(ow_marshal2_byte_array_const_size(buffer, object->"
operator|+
name|propname
operator|+
literal|", "
operator|+
name|size
operator|.
name|asInt
argument_list|()
operator|+
literal|"));"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|"   SUCCESS_CHECK(ow_marshal2_byte_array(buffer, bitbuffer, object->"
operator|+
name|propname
operator|+
literal|"));"
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"java.lang.String"
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"   SUCCESS_CHECK(ow_marshal2_string(buffer, bitbuffer, object->"
operator|+
name|propname
operator|+
literal|"));"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|property
operator|.
name|getType
argument_list|()
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
literal|"   SUCCESS_CHECK(ow_marshal2_DataStructure_array_const_size(buffer, bitbuffer, object->"
operator|+
name|propname
operator|+
literal|", "
operator|+
name|size
operator|.
name|asInt
argument_list|()
operator|+
literal|"));"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|"   SUCCESS_CHECK(ow_marshal2_DataStructure_array(buffer, bitbuffer, object->"
operator|+
name|propname
operator|+
literal|"));"
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
literal|"   SUCCESS_CHECK(ow_marshal2_throwable(buffer, bitbuffer, object->"
operator|+
name|propname
operator|+
literal|"));"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|cached
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"   SUCCESS_CHECK(ow_marshal2_cached_object(buffer, bitbuffer, (ow_DataStructure*)object->"
operator|+
name|propname
operator|+
literal|"));"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|"   SUCCESS_CHECK(ow_marshal2_nested_object(buffer, bitbuffer, (ow_DataStructure*)object->"
operator|+
name|propname
operator|+
literal|"));"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|"   "
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"	return APR_SUCCESS;"
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
literal|"apr_status_t ow_unmarshal_"
operator|+
name|name
operator|+
literal|"(ow_byte_array *buffer, ow_bit_buffer *bitbuffer, ow_"
operator|+
name|name
operator|+
literal|" *object, apr_pool_t *pool)"
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
literal|"   ow_unmarshal_"
operator|+
name|baseName
operator|+
literal|"(buffer, bitbuffer, (ow_"
operator|+
name|baseName
operator|+
literal|"*)object, pool);   "
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
name|JAnnotation
name|annotation
init|=
name|property
operator|.
name|getGetter
argument_list|()
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
name|String
name|propname
init|=
name|toPropertyCase
argument_list|(
name|property
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|cached
init|=
name|isCachedProperty
argument_list|(
name|property
argument_list|)
decl_stmt|;
name|type
operator|=
name|property
operator|.
name|getType
argument_list|()
operator|.
name|getQualifiedName
argument_list|()
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
literal|"   object->"
operator|+
name|propname
operator|+
literal|" = ow_bit_buffer_read(bitbuffer);"
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
literal|"   SUCCESS_CHECK(ow_byte_array_read_"
operator|+
name|type
operator|+
literal|"(buffer,&object->"
operator|+
name|propname
operator|+
literal|"));"
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
literal|"   SUCCESS_CHECK(ow_byte_array_read_"
operator|+
name|type
operator|+
literal|"(buffer,&object->"
operator|+
name|propname
operator|+
literal|"));"
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
literal|"   SUCCESS_CHECK(ow_byte_array_read_"
operator|+
name|type
operator|+
literal|"(buffer,&object->"
operator|+
name|propname
operator|+
literal|"));"
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
literal|"   SUCCESS_CHECK(ow_byte_array_read_"
operator|+
name|type
operator|+
literal|"(buffer,&object->"
operator|+
name|propname
operator|+
literal|"));"
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
literal|"   SUCCESS_CHECK(ow_unmarshal_long(buffer, bitbuffer,&object->"
operator|+
name|propname
operator|+
literal|", pool));"
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
literal|"   SUCCESS_CHECK(ow_unmarshal_byte_array_const_size(buffer,&object->"
operator|+
name|propname
operator|+
literal|", "
operator|+
name|size
operator|.
name|asInt
argument_list|()
operator|+
literal|", pool));"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|"   SUCCESS_CHECK(ow_unmarshal_byte_array(buffer, bitbuffer,&object->"
operator|+
name|propname
operator|+
literal|", pool));"
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"org.apache.activeio.packet.ByteSequence"
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
literal|"   SUCCESS_CHECK(ow_unmarshal_byte_array_const_size(buffer,&object->"
operator|+
name|propname
operator|+
literal|", "
operator|+
name|size
operator|.
name|asInt
argument_list|()
operator|+
literal|", pool));"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|"   SUCCESS_CHECK(ow_unmarshal_byte_array(buffer, bitbuffer,&object->"
operator|+
name|propname
operator|+
literal|", pool));"
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"java.lang.String"
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"   SUCCESS_CHECK(ow_unmarshal_string(buffer, bitbuffer,&object->"
operator|+
name|propname
operator|+
literal|", pool));"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|property
operator|.
name|getType
argument_list|()
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
literal|"   SUCCESS_CHECK(ow_unmarshal_DataStructure_array_const_size(buffer, bitbuffer,&object->"
operator|+
name|propname
operator|+
literal|", "
operator|+
name|size
operator|.
name|asInt
argument_list|()
operator|+
literal|", pool));"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|"   SUCCESS_CHECK(ow_unmarshal_DataStructure_array(buffer, bitbuffer,&object->"
operator|+
name|propname
operator|+
literal|", pool));"
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
literal|"   SUCCESS_CHECK(ow_unmarshal_throwable(buffer, bitbuffer,&object->"
operator|+
name|propname
operator|+
literal|", pool));"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|cached
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"   SUCCESS_CHECK(ow_unmarshal_cached_object(buffer, bitbuffer, (ow_DataStructure**)&object->"
operator|+
name|propname
operator|+
literal|", pool));"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|"   SUCCESS_CHECK(ow_unmarshal_nested_object(buffer, bitbuffer, (ow_DataStructure**)&object->"
operator|+
name|propname
operator|+
literal|", pool));"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|"   "
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"	return APR_SUCCESS;"
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
specifier|protected
name|void
name|generateTearDown
parameter_list|(
name|PrintWriter
name|out
parameter_list|)
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
literal|"ow_DataStructure *ow_create_object(ow_byte type, apr_pool_t *pool)"
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
literal|"   switch( type ) {"
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|iterator
init|=
name|sortedClasses
operator|.
name|iterator
argument_list|()
init|;
name|iterator
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
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|jclass
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
name|String
name|type
init|=
operator|(
literal|"ow_"
operator|+
name|name
operator|)
operator|.
name|toUpperCase
argument_list|()
operator|+
literal|"_TYPE"
decl_stmt|;
if|if
condition|(
operator|!
name|isAbstract
argument_list|(
name|jclass
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"      case "
operator|+
name|type
operator|+
literal|": return (ow_DataStructure *)ow_"
operator|+
name|name
operator|+
literal|"_create(pool);"
argument_list|)
expr_stmt|;
block|}
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
literal|"   }"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"   return 0;"
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
literal|"apr_status_t ow_marshal1_object(ow_bit_buffer *buffer, ow_DataStructure *object)"
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
literal|"   switch( object->structType ) {"
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|iterator
init|=
name|sortedClasses
operator|.
name|iterator
argument_list|()
init|;
name|iterator
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
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|jclass
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
name|String
name|type
init|=
operator|(
literal|"ow_"
operator|+
name|name
operator|)
operator|.
name|toUpperCase
argument_list|()
operator|+
literal|"_TYPE"
decl_stmt|;
if|if
condition|(
operator|!
name|isAbstract
argument_list|(
name|jclass
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"      case "
operator|+
name|type
operator|+
literal|": return ow_marshal1_"
operator|+
name|name
operator|+
literal|"(buffer, (ow_"
operator|+
name|name
operator|+
literal|"*)object);"
argument_list|)
expr_stmt|;
block|}
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
literal|"   }"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"   return APR_EGENERAL;"
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
literal|"apr_status_t ow_marshal2_object(ow_byte_buffer *buffer, ow_bit_buffer *bitbuffer, ow_DataStructure *object)"
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
literal|"   switch( object->structType ) {"
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|iterator
init|=
name|sortedClasses
operator|.
name|iterator
argument_list|()
init|;
name|iterator
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
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|jclass
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
name|String
name|type
init|=
operator|(
literal|"ow_"
operator|+
name|name
operator|)
operator|.
name|toUpperCase
argument_list|()
operator|+
literal|"_TYPE"
decl_stmt|;
if|if
condition|(
operator|!
name|isAbstract
argument_list|(
name|jclass
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"      case "
operator|+
name|type
operator|+
literal|": return ow_marshal2_"
operator|+
name|name
operator|+
literal|"(buffer, bitbuffer, (ow_"
operator|+
name|name
operator|+
literal|"*)object);"
argument_list|)
expr_stmt|;
block|}
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
literal|"   }"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"   return APR_EGENERAL;"
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
literal|"apr_status_t ow_unmarshal_object(ow_byte_array *buffer, ow_bit_buffer *bitbuffer, ow_DataStructure *object, apr_pool_t *pool)"
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
literal|"   switch( object->structType ) {"
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|iterator
init|=
name|sortedClasses
operator|.
name|iterator
argument_list|()
init|;
name|iterator
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
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|jclass
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
name|String
name|type
init|=
operator|(
literal|"ow_"
operator|+
name|name
operator|)
operator|.
name|toUpperCase
argument_list|()
operator|+
literal|"_TYPE"
decl_stmt|;
if|if
condition|(
operator|!
name|isAbstract
argument_list|(
name|jclass
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"      case "
operator|+
name|type
operator|+
literal|": return ow_unmarshal_"
operator|+
name|name
operator|+
literal|"(buffer, bitbuffer, (ow_"
operator|+
name|name
operator|+
literal|"*)object, pool);"
argument_list|)
expr_stmt|;
block|}
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
literal|"   }"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"   return APR_EGENERAL;"
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

