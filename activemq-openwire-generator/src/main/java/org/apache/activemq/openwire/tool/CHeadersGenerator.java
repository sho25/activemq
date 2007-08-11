begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|LinkedHashMap
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
name|CHeadersGenerator
extends|extends
name|SingleSourceGenerator
block|{
specifier|protected
name|String
name|targetDir
init|=
literal|"./src/lib/openwire"
decl_stmt|;
specifier|public
name|Object
name|run
parameter_list|()
block|{
name|filePostFix
operator|=
literal|".h"
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
literal|".h"
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
literal|" *"
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
literal|" * http://www.apache.org/licenses/LICENSE-2.0"
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
name|String
name|changeCase
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|StringBuffer
name|b
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|char
index|[]
name|cs
init|=
name|value
operator|.
name|toCharArray
argument_list|()
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
name|cs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|cs
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|Character
operator|.
name|isUpperCase
argument_list|(
operator|(
name|char
operator|)
name|c
argument_list|)
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|'_'
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|Character
operator|.
name|toLowerCase
argument_list|(
operator|(
name|char
operator|)
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|b
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
name|String
name|toPropertyCase
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|value
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|toLowerCase
argument_list|()
operator|+
name|value
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
return|;
block|}
comment|/**      * Sort the class list so that base classes come up first.      */
specifier|protected
name|List
argument_list|<
name|JClass
argument_list|>
name|sort
parameter_list|(
name|List
name|source
parameter_list|)
block|{
name|LinkedHashMap
argument_list|<
name|JClass
argument_list|,
name|JClass
argument_list|>
name|rc
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|JClass
argument_list|,
name|JClass
argument_list|>
argument_list|()
decl_stmt|;
name|ArrayList
name|classes
init|=
operator|new
name|ArrayList
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|classes
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
comment|// lets make a map of all the class names
name|HashMap
argument_list|<
name|JClass
argument_list|,
name|JClass
argument_list|>
name|classNames
init|=
operator|new
name|HashMap
argument_list|<
name|JClass
argument_list|,
name|JClass
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|classes
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
name|c
init|=
operator|(
name|JClass
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|classNames
operator|.
name|put
argument_list|(
name|c
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
comment|// Add all classes that have no parent first
for|for
control|(
name|Iterator
name|iter
init|=
name|classes
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
name|c
init|=
operator|(
name|JClass
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|classNames
operator|.
name|containsKey
argument_list|(
name|c
operator|.
name|getSuperclass
argument_list|()
argument_list|)
condition|)
name|rc
operator|.
name|put
argument_list|(
name|c
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
comment|// now lets add the rest
for|for
control|(
name|Iterator
name|iter
init|=
name|classes
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
name|c
init|=
operator|(
name|JClass
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|rc
operator|.
name|containsKey
argument_list|(
name|c
argument_list|)
condition|)
name|rc
operator|.
name|put
argument_list|(
name|c
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ArrayList
argument_list|<
name|JClass
argument_list|>
argument_list|(
name|rc
operator|.
name|keySet
argument_list|()
argument_list|)
return|;
block|}
name|void
name|generateFields
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
name|JClass
name|jclass
parameter_list|)
block|{
if|if
condition|(
name|jclass
operator|.
name|getSuperclass
argument_list|()
operator|==
literal|null
operator|||
name|jclass
operator|.
name|getSuperclass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Object"
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
literal|"   ow_byte structType;"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|generateFields
argument_list|(
name|out
argument_list|,
name|jclass
operator|.
name|getSuperclass
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ArrayList
argument_list|<
name|JProperty
argument_list|>
name|properties
init|=
operator|new
name|ArrayList
argument_list|<
name|JProperty
argument_list|>
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
for|for
control|(
name|Iterator
argument_list|<
name|JProperty
argument_list|>
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
name|name
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
name|String
name|type
init|=
name|property
operator|.
name|getType
argument_list|()
operator|.
name|getQualifiedName
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
literal|"   ow_"
operator|+
name|type
operator|+
literal|" "
operator|+
name|name
operator|+
literal|";"
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
literal|"   ow_"
operator|+
name|type
operator|+
literal|" "
operator|+
name|name
operator|+
literal|";"
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
literal|"   ow_"
operator|+
name|type
operator|+
literal|" "
operator|+
name|name
operator|+
literal|";"
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
literal|"   ow_"
operator|+
name|type
operator|+
literal|" "
operator|+
name|name
operator|+
literal|";"
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
literal|"   ow_"
operator|+
name|type
operator|+
literal|" "
operator|+
name|name
operator|+
literal|";"
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
literal|"   ow_"
operator|+
name|type
operator|+
literal|" "
operator|+
name|name
operator|+
literal|";"
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
name|out
operator|.
name|println
argument_list|(
literal|"   ow_byte_array *"
operator|+
name|name
operator|+
literal|";"
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
literal|"org.apache.activeio.packet.ByteSequence"
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"   ow_byte_array *"
operator|+
name|name
operator|+
literal|";"
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
literal|"org.apache.activeio.packet.ByteSequence"
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"   ow_byte_array *"
operator|+
name|name
operator|+
literal|";"
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
literal|"java.lang.String"
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"   ow_string *"
operator|+
name|name
operator|+
literal|";"
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
name|out
operator|.
name|println
argument_list|(
literal|"   ow_DataStructure_array *"
operator|+
name|name
operator|+
literal|";"
argument_list|)
expr_stmt|;
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
literal|"   ow_throwable *"
operator|+
name|name
operator|+
literal|";"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|"   struct ow_"
operator|+
name|property
operator|.
name|getType
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" *"
operator|+
name|name
operator|+
literal|";"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
literal|" "
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#ifndef OW_COMMANDS_V"
operator|+
name|openwireVersion
operator|+
literal|"_H"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#define OW_COMMANDS_V"
operator|+
name|openwireVersion
operator|+
literal|"_H"
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
literal|"#include \"ow.h\""
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
literal|"#ifdef __cplusplus"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"extern \"C\" {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#endif /* __cplusplus */"
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
literal|"#define OW_WIREFORMAT_VERSION "
operator|+
name|openwireVersion
operator|+
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#define OW_WIREFORMAT_STACK_TRACE_MASK     0x00000001;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#define OW_WIREFORMAT_TCP_NO_DELAY_MASK    0x00000002;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#define OW_WIREFORMAT_CACHE_MASK           0x00000004;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#define OW_WIREFORMAT_COMPRESSION_MASK     0x00000008;"
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
literal|"#define "
operator|+
name|type
operator|+
literal|" "
operator|+
name|getOpenWireOpCode
argument_list|(
name|jclass
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
literal|"apr_status_t ow_bitmarshall(ow_bit_buffer *buffer, ow_DataStructure *object);"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"apr_status_t ow_marshall(ow_byte_buffer *buffer, ow_DataStructure *object);"
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
name|String
name|structName
init|=
name|jclass
operator|.
name|getSimpleName
argument_list|()
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
literal|"typedef struct ow_"
operator|+
name|structName
operator|+
literal|" {"
argument_list|)
expr_stmt|;
comment|// This recusivly generates the field definitions of the class and it's supper classes.
name|generateFields
argument_list|(
name|out
argument_list|,
name|jclass
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
literal|"} ow_"
operator|+
name|structName
operator|+
literal|";"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"ow_"
operator|+
name|structName
operator|+
literal|" *ow_"
operator|+
name|structName
operator|+
literal|"_create(apr_pool_t *pool);"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"ow_boolean ow_is_a_"
operator|+
name|structName
operator|+
literal|"(ow_DataStructure *object);"
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
literal|"#ifdef __cplusplus"
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
literal|"#endif"
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
literal|"#endif  /* ! OW_COMMANDS_V"
operator|+
name|openwireVersion
operator|+
literal|"_H */"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

