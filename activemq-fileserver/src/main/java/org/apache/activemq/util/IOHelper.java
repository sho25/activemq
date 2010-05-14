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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 661435 $  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|IOHelper
block|{
specifier|protected
specifier|static
specifier|final
name|int
name|MAX_DIR_NAME_LENGTH
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|int
name|MAX_FILE_NAME_LENGTH
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_BUFFER_SIZE
init|=
literal|4096
decl_stmt|;
specifier|private
name|IOHelper
parameter_list|()
block|{     }
specifier|public
specifier|static
name|String
name|getDefaultDataDirectory
parameter_list|()
block|{
return|return
name|getDefaultDirectoryPrefix
argument_list|()
operator|+
literal|"activemq-data"
return|;
block|}
specifier|public
specifier|static
name|String
name|getDefaultStoreDirectory
parameter_list|()
block|{
return|return
name|getDefaultDirectoryPrefix
argument_list|()
operator|+
literal|"amqstore"
return|;
block|}
comment|/**      * Allows a system property to be used to overload the default data      * directory which can be useful for forcing the test cases to use a target/      * prefix      */
specifier|public
specifier|static
name|String
name|getDefaultDirectoryPrefix
parameter_list|()
block|{
try|try
block|{
return|return
name|System
operator|.
name|getProperty
argument_list|(
literal|"org.apache.activemq.default.directory.prefix"
argument_list|,
literal|""
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|""
return|;
block|}
block|}
specifier|public
specifier|static
name|boolean
name|deleteFile
parameter_list|(
name|File
name|fileToDelete
parameter_list|)
block|{
if|if
condition|(
name|fileToDelete
operator|==
literal|null
operator|||
operator|!
name|fileToDelete
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
name|boolean
name|result
init|=
name|deleteChildren
argument_list|(
name|fileToDelete
argument_list|)
decl_stmt|;
name|result
operator|&=
name|fileToDelete
operator|.
name|delete
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|public
specifier|static
name|boolean
name|deleteChildren
parameter_list|(
name|File
name|parent
parameter_list|)
block|{
if|if
condition|(
name|parent
operator|==
literal|null
operator|||
operator|!
name|parent
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|boolean
name|result
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|parent
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|File
index|[]
name|files
init|=
name|parent
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|files
operator|==
literal|null
condition|)
block|{
name|result
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
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
name|equals
argument_list|(
literal|"."
argument_list|)
operator|||
name|file
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|".."
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|result
operator|&=
name|deleteFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|&=
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|result
return|;
block|}
specifier|public
specifier|static
name|void
name|moveFile
parameter_list|(
name|File
name|src
parameter_list|,
name|File
name|targetDirectory
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|src
operator|.
name|renameTo
argument_list|(
operator|new
name|File
argument_list|(
name|targetDirectory
argument_list|,
name|src
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to move "
operator|+
name|src
operator|+
literal|" to "
operator|+
name|targetDirectory
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|static
name|void
name|copyFile
parameter_list|(
name|File
name|src
parameter_list|,
name|File
name|dest
parameter_list|)
throws|throws
name|IOException
block|{
name|FileInputStream
name|fileSrc
init|=
operator|new
name|FileInputStream
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|FileOutputStream
name|fileDest
init|=
operator|new
name|FileOutputStream
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|copyInputStream
argument_list|(
name|fileSrc
argument_list|,
name|fileDest
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|copyInputStream
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|DEFAULT_BUFFER_SIZE
index|]
decl_stmt|;
name|int
name|len
init|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
while|while
condition|(
name|len
operator|>=
literal|0
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|len
operator|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
static|static
block|{
name|MAX_DIR_NAME_LENGTH
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"MaximumDirNameLength"
argument_list|,
literal|"200"
argument_list|)
argument_list|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
name|MAX_FILE_NAME_LENGTH
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"MaximumFileNameLength"
argument_list|,
literal|"64"
argument_list|)
argument_list|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|mkdirs
parameter_list|(
name|File
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|dir
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|dir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to create directory '"
operator|+
name|dir
operator|+
literal|"', regular file already existed with that name"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
if|if
condition|(
operator|!
name|dir
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to create directory '"
operator|+
name|dir
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

