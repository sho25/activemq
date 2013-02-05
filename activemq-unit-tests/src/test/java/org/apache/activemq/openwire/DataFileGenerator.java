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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|InputStream
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
name|junit
operator|.
name|framework
operator|.
name|Assert
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|DataFileGenerator
extends|extends
name|Assert
block|{
specifier|static
specifier|final
name|File
name|MODULE_BASE_DIR
decl_stmt|;
specifier|static
specifier|final
name|File
name|CONTROL_DIR
decl_stmt|;
specifier|static
specifier|final
name|File
name|CLASS_FILE_DIR
decl_stmt|;
static|static
block|{
name|MODULE_BASE_DIR
operator|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"basedir"
argument_list|,
literal|"."
argument_list|)
argument_list|)
expr_stmt|;
name|CONTROL_DIR
operator|=
operator|new
name|File
argument_list|(
name|MODULE_BASE_DIR
argument_list|,
literal|"src/test/resources/openwire-control"
argument_list|)
expr_stmt|;
name|CLASS_FILE_DIR
operator|=
operator|new
name|File
argument_list|(
name|MODULE_BASE_DIR
argument_list|,
literal|"src/test/java/org/apache/activemq/openwire"
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|generateControlFiles
argument_list|()
expr_stmt|;
block|}
comment|/**      * @param srcdir      * @return      * @throws ClassNotFoundException      * @throws InstantiationException      * @throws IllegalAccessException      */
specifier|public
specifier|static
name|ArrayList
name|getAllDataFileGenerators
parameter_list|()
throws|throws
name|Exception
block|{
comment|// System.out.println("Looking for generators in : "+classFileDir);
name|ArrayList
name|l
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|File
index|[]
name|files
init|=
name|CLASS_FILE_DIR
operator|.
name|listFiles
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|files
operator|!=
literal|null
operator|&&
name|i
operator|<
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|File
name|file
init|=
name|files
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"Data.java"
argument_list|)
condition|)
block|{
name|String
name|cn
init|=
name|file
operator|.
name|getName
argument_list|()
decl_stmt|;
name|cn
operator|=
name|cn
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|cn
operator|.
name|length
argument_list|()
operator|-
literal|".java"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|Class
name|clazz
init|=
name|DataFileGenerator
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
operator|.
name|loadClass
argument_list|(
literal|"org.apache.activemq.openwire."
operator|+
name|cn
argument_list|)
decl_stmt|;
name|l
operator|.
name|add
argument_list|(
operator|(
name|DataFileGenerator
operator|)
name|clazz
operator|.
name|newInstance
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|l
return|;
block|}
specifier|private
specifier|static
name|void
name|generateControlFiles
parameter_list|()
throws|throws
name|Exception
block|{
name|ArrayList
name|generators
init|=
name|getAllDataFileGenerators
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|generators
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
name|DataFileGenerator
name|object
init|=
operator|(
name|DataFileGenerator
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
comment|// System.out.println("Processing: "+object.getClass());
name|object
operator|.
name|generateControlFile
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// System.err.println("Error while processing:
comment|// "+object.getClass() + ". Reason: " + e);
block|}
block|}
block|}
specifier|public
name|void
name|generateControlFile
parameter_list|()
throws|throws
name|Exception
block|{
name|CONTROL_DIR
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|File
name|dataFile
init|=
operator|new
name|File
argument_list|(
name|CONTROL_DIR
argument_list|,
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".bin"
argument_list|)
decl_stmt|;
name|OpenWireFormat
name|wf
init|=
operator|new
name|OpenWireFormat
argument_list|()
decl_stmt|;
name|wf
operator|.
name|setCacheEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|wf
operator|.
name|setStackTraceEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|wf
operator|.
name|setVersion
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|FileOutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|dataFile
argument_list|)
decl_stmt|;
name|DataOutputStream
name|ds
init|=
operator|new
name|DataOutputStream
argument_list|(
name|os
argument_list|)
decl_stmt|;
name|wf
operator|.
name|marshal
argument_list|(
name|createObject
argument_list|()
argument_list|,
name|ds
argument_list|)
expr_stmt|;
name|ds
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|InputStream
name|generateInputStream
parameter_list|()
throws|throws
name|Exception
block|{
name|OpenWireFormat
name|wf
init|=
operator|new
name|OpenWireFormat
argument_list|()
decl_stmt|;
name|wf
operator|.
name|setCacheEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|wf
operator|.
name|setStackTraceEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|wf
operator|.
name|setVersion
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|os
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|DataOutputStream
name|ds
init|=
operator|new
name|DataOutputStream
argument_list|(
name|os
argument_list|)
decl_stmt|;
name|wf
operator|.
name|marshal
argument_list|(
name|createObject
argument_list|()
argument_list|,
name|ds
argument_list|)
expr_stmt|;
name|ds
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|os
operator|.
name|toByteArray
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|void
name|assertAllControlFileAreEqual
parameter_list|()
throws|throws
name|Exception
block|{
name|ArrayList
name|generators
init|=
name|getAllDataFileGenerators
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|generators
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
name|DataFileGenerator
name|object
init|=
operator|(
name|DataFileGenerator
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
comment|// System.out.println("Processing: "+object.getClass());
name|object
operator|.
name|assertControlFileIsEqual
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|assertControlFileIsEqual
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|dataFile
init|=
operator|new
name|File
argument_list|(
name|CONTROL_DIR
argument_list|,
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".bin"
argument_list|)
decl_stmt|;
name|FileInputStream
name|is1
init|=
operator|new
name|FileInputStream
argument_list|(
name|dataFile
argument_list|)
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
try|try
block|{
name|InputStream
name|is2
init|=
name|generateInputStream
argument_list|()
decl_stmt|;
name|int
name|a
init|=
name|is1
operator|.
name|read
argument_list|()
decl_stmt|;
name|int
name|b
init|=
name|is2
operator|.
name|read
argument_list|()
decl_stmt|;
name|pos
operator|++
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Data does not match control file: "
operator|+
name|dataFile
operator|+
literal|" at byte position "
operator|+
name|pos
argument_list|,
name|a
argument_list|,
name|b
argument_list|)
expr_stmt|;
while|while
condition|(
name|a
operator|>=
literal|0
operator|&&
name|b
operator|>=
literal|0
condition|)
block|{
name|a
operator|=
name|is1
operator|.
name|read
argument_list|()
expr_stmt|;
name|b
operator|=
name|is2
operator|.
name|read
argument_list|()
expr_stmt|;
name|pos
operator|++
expr_stmt|;
name|assertEquals
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
name|is2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|is1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
specifier|abstract
name|Object
name|createObject
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit
