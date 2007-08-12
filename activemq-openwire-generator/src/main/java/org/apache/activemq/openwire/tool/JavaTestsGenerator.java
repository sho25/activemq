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
name|JPackage
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
comment|/**  * @version $Revision: 384826 $  */
end_comment

begin_class
specifier|public
class|class
name|JavaTestsGenerator
extends|extends
name|MultiSourceGenerator
block|{
specifier|protected
name|String
name|targetDir
init|=
literal|"src/test/java"
decl_stmt|;
specifier|public
name|Object
name|run
parameter_list|()
block|{
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
literal|"/org/apache/activemq/openwire/v"
operator|+
name|getOpenwireVersion
argument_list|()
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
name|getClassName
parameter_list|(
name|JClass
name|jclass
parameter_list|)
block|{
if|if
condition|(
name|isAbstract
argument_list|(
name|jclass
argument_list|)
condition|)
block|{
return|return
name|super
operator|.
name|getClassName
argument_list|(
name|jclass
argument_list|)
operator|+
literal|"TestSupport"
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|getClassName
argument_list|(
name|jclass
argument_list|)
operator|+
literal|"Test"
return|;
block|}
block|}
specifier|protected
name|String
name|getBaseClassName
parameter_list|(
name|JClass
name|jclass
parameter_list|)
block|{
name|String
name|answer
init|=
literal|"DataFileGeneratorTestSupport"
decl_stmt|;
if|if
condition|(
name|superclass
operator|!=
literal|null
condition|)
block|{
name|String
name|name
init|=
name|superclass
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
operator|&&
operator|!
name|name
operator|.
name|equals
argument_list|(
literal|"JNDIBaseStorable"
argument_list|)
operator|&&
operator|!
name|name
operator|.
name|equals
argument_list|(
literal|"DataStructureSupport"
argument_list|)
operator|&&
operator|!
name|name
operator|.
name|equals
argument_list|(
literal|"Object"
argument_list|)
condition|)
block|{
name|answer
operator|=
name|name
operator|+
literal|"Test"
expr_stmt|;
if|if
condition|(
name|isAbstract
argument_list|(
name|getJclass
argument_list|()
operator|.
name|getSuperclass
argument_list|()
argument_list|)
condition|)
block|{
name|answer
operator|+=
literal|"Support"
expr_stmt|;
block|}
block|}
block|}
return|return
name|answer
return|;
block|}
specifier|private
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
literal|"package org.apache.activemq.openwire.v"
operator|+
name|openwireVersion
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
literal|"import java.io.DataInputStream;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"import java.io.DataOutputStream;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"import java.io.IOException;"
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
literal|"import org.apache.activemq.openwire.*;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"import org.apache.activemq.command.*;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
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
name|getJclass
argument_list|()
operator|.
name|getImportedPackages
argument_list|()
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|JPackage
name|pkg
init|=
name|getJclass
argument_list|()
operator|.
name|getImportedPackages
argument_list|()
index|[
name|i
index|]
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|pkg
operator|.
name|getClasses
argument_list|()
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|JClass
name|clazz
init|=
name|pkg
operator|.
name|getClasses
argument_list|()
index|[
name|j
index|]
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"import "
operator|+
name|clazz
operator|.
name|getQualifiedName
argument_list|()
operator|+
literal|";"
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
literal|"/**"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" * Test case for the OpenWire marshalling for "
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
literal|" *"
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
literal|" *"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" * @version $Revision: $"
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
literal|"public "
operator|+
name|getAbstractClassText
argument_list|()
operator|+
literal|"class "
operator|+
name|className
operator|+
literal|" extends "
operator|+
name|baseClass
operator|+
literal|" {"
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
literal|"    public static "
operator|+
name|jclass
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"Test SINGLETON = new "
operator|+
name|jclass
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"Test();"
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
literal|"    public Object createObject() throws Exception {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"        "
operator|+
name|jclass
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" info = new "
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
literal|"        populateObject(info);"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"        return info;"
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
literal|"    protected void populateObject(Object object) throws Exception {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"        super.populateObject(object);"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"        "
operator|+
name|getJclass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" info = ("
operator|+
name|getJclass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|") object;"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|TestDataGenerator
name|generator
init|=
operator|new
name|TestDataGenerator
argument_list|()
decl_stmt|;
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
name|String
name|size
init|=
name|stringValue
argument_list|(
name|annotation
argument_list|,
literal|"size"
argument_list|)
decl_stmt|;
name|String
name|testSize
init|=
name|stringValue
argument_list|(
name|annotation
argument_list|,
literal|"testSize"
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
name|getSimpleName
argument_list|()
decl_stmt|;
comment|//            boolean cached = isCachedProperty(property);
name|String
name|propertyName
init|=
name|property
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"-1"
operator|.
name|equals
argument_list|(
name|testSize
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|String
name|setterName
init|=
name|property
operator|.
name|getSetter
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
literal|"        info."
operator|+
name|setterName
operator|+
literal|"("
operator|+
name|generator
operator|.
name|createBool
argument_list|()
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
name|out
operator|.
name|println
argument_list|(
literal|"        info."
operator|+
name|setterName
operator|+
literal|"("
operator|+
name|generator
operator|.
name|createByte
argument_list|()
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
literal|"char"
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"        info."
operator|+
name|setterName
operator|+
literal|"("
operator|+
name|generator
operator|.
name|createChar
argument_list|()
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
literal|"short"
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"        info."
operator|+
name|setterName
operator|+
literal|"("
operator|+
name|generator
operator|.
name|createShort
argument_list|()
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
literal|"int"
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"        info."
operator|+
name|setterName
operator|+
literal|"("
operator|+
name|generator
operator|.
name|createInt
argument_list|()
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
literal|"long"
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"        info."
operator|+
name|setterName
operator|+
literal|"("
operator|+
name|generator
operator|.
name|createLong
argument_list|()
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
name|out
operator|.
name|println
argument_list|(
literal|"        info."
operator|+
name|setterName
operator|+
literal|"("
operator|+
name|generator
operator|.
name|createByteArray
argument_list|(
name|propertyName
argument_list|)
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
literal|"String"
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"        info."
operator|+
name|setterName
operator|+
literal|"(\""
operator|+
name|generator
operator|.
name|createString
argument_list|(
name|propertyName
argument_list|)
operator|+
literal|"\");"
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
literal|"ByteSequence"
argument_list|)
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
literal|"            byte data[] = "
operator|+
name|generator
operator|.
name|createByteArray
argument_list|(
name|propertyName
argument_list|)
operator|+
literal|";"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"            info."
operator|+
name|setterName
operator|+
literal|"(new org.apache.activemq.util.ByteSequence(data,0,data.length));"
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
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"Throwable"
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"        info."
operator|+
name|setterName
operator|+
literal|"(createThrowable(\""
operator|+
name|generator
operator|.
name|createString
argument_list|(
name|propertyName
argument_list|)
operator|+
literal|"\"));"
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
name|String
name|arrayType
init|=
name|property
operator|.
name|getType
argument_list|()
operator|.
name|getArrayComponentType
argument_list|()
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|==
literal|null
condition|)
block|{
name|size
operator|=
literal|"2"
expr_stmt|;
block|}
if|if
condition|(
name|arrayType
operator|==
name|jclass
operator|.
name|getSimpleName
argument_list|()
condition|)
block|{
name|size
operator|=
literal|"0"
expr_stmt|;
block|}
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
literal|" value[] = new "
operator|+
name|arrayType
operator|+
literal|"["
operator|+
name|size
operator|+
literal|"];"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"            for( int i=0; i< "
operator|+
name|size
operator|+
literal|"; i++ ) {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"                value[i] = create"
operator|+
name|arrayType
operator|+
literal|"(\""
operator|+
name|generator
operator|.
name|createString
argument_list|(
name|propertyName
argument_list|)
operator|+
literal|"\");"
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
name|setterName
operator|+
literal|"(value);"
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
literal|"        info."
operator|+
name|setterName
operator|+
literal|"(create"
operator|+
name|type
operator|+
literal|"(\""
operator|+
name|generator
operator|.
name|createString
argument_list|(
name|propertyName
argument_list|)
operator|+
literal|"\"));"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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

