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
comment|/**  *  * @version $Revision: 379734 $  */
end_comment

begin_class
specifier|public
class|class
name|CppHeadersGenerator
extends|extends
name|CppClassesGenerator
block|{
specifier|protected
name|String
name|getFilePostFix
parameter_list|()
block|{
return|return
literal|".hpp"
return|;
block|}
specifier|protected
name|void
name|generateFile
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
literal|"#ifndef ActiveMQ_"
operator|+
name|className
operator|+
literal|"_hpp_"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#define ActiveMQ_"
operator|+
name|className
operator|+
literal|"_hpp_"
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
literal|"// Turn off warning message for ignored exception specification"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#ifdef _MSC_VER"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#pragma warning( disable : 4290 )"
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
literal|"#include<string>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#include \"activemq/command/"
operator|+
name|baseClass
operator|+
literal|".hpp\""
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
if|if
condition|(
operator|!
name|property
operator|.
name|getType
argument_list|()
operator|.
name|isPrimitiveType
argument_list|()
operator|&&
operator|!
name|property
operator|.
name|getType
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"String"
argument_list|)
operator|&&
operator|!
name|property
operator|.
name|getType
argument_list|()
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
name|String
name|includeName
init|=
name|toCppType
argument_list|(
name|property
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
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
name|JClass
name|arrayType
init|=
name|property
operator|.
name|getType
argument_list|()
operator|.
name|getArrayComponentType
argument_list|()
decl_stmt|;
if|if
condition|(
name|arrayType
operator|.
name|isPrimitiveType
argument_list|()
condition|)
continue|continue ;
block|}
if|if
condition|(
name|includeName
operator|.
name|startsWith
argument_list|(
literal|"array<"
argument_list|)
condition|)
name|includeName
operator|=
name|includeName
operator|.
name|substring
argument_list|(
literal|6
argument_list|,
name|includeName
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|includeName
operator|.
name|startsWith
argument_list|(
literal|"p<"
argument_list|)
condition|)
name|includeName
operator|=
name|includeName
operator|.
name|substring
argument_list|(
literal|2
argument_list|,
name|includeName
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|includeName
operator|.
name|equals
argument_list|(
literal|"IDataStructure"
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"#include \"activemq/"
operator|+
name|includeName
operator|+
literal|".hpp\""
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|"#include \"activemq/command/"
operator|+
name|includeName
operator|+
literal|".hpp\""
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
name|out
operator|.
name|println
argument_list|(
literal|"#include \"activemq/protocol/IMarshaller.hpp\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#include \"ppr/io/IOutputStream.hpp\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#include \"ppr/io/IInputStream.hpp\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#include \"ppr/io/IOException.hpp\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#include \"ppr/util/ifr/array\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"#include \"ppr/util/ifr/p\""
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
literal|"namespace apache"
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
literal|"  namespace activemq"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"  {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    namespace command"
argument_list|)
expr_stmt|;
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
literal|"      using namespace ifr;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"      using namespace std;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"      using namespace apache::activemq;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"      using namespace apache::activemq::protocol;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"      using namespace apache::ppr::io;"
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
literal|"class "
operator|+
name|className
operator|+
literal|" : public "
operator|+
name|baseClass
operator|+
literal|""
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
literal|"protected:"
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
name|name
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
name|type
operator|+
literal|" "
operator|+
name|name
operator|+
literal|" ;"
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
literal|"public:"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    const static unsigned char TYPE = "
operator|+
name|getOpenWireOpCode
argument_list|(
name|jclass
argument_list|)
operator|+
literal|";"
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
literal|"public:"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    "
operator|+
name|className
operator|+
literal|"() ;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    virtual ~"
operator|+
name|className
operator|+
literal|"() ;"
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
literal|"    virtual unsigned char getDataStructureType() ;"
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
literal|"    virtual "
operator|+
name|type
operator|+
literal|" get"
operator|+
name|propertyName
operator|+
literal|"() ;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    virtual void set"
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
literal|") ;"
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
literal|"    virtual int marshal(p<IMarshaller> marshaller, int mode, p<IOutputStream> ostream) throw (IOException) ;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    virtual void unmarshal(p<IMarshaller> marshaller, int mode, p<IInputStream> istream) throw (IOException) ;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"} ;"
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
literal|"/* namespace */"
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
literal|"  }"
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
literal|"#endif /*ActiveMQ_"
operator|+
name|className
operator|+
literal|"_hpp_*/"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

