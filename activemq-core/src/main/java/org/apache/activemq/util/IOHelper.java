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
name|*
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Stack
import|;
end_import

begin_comment
comment|/**  *   */
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
comment|/**      * Converts any string into a string that is safe to use as a file name.      * The result will only include ascii characters and numbers, and the "-","_", and "." characters.      *      * @param name      * @return      */
specifier|public
specifier|static
name|String
name|toFileSystemDirectorySafeName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|toFileSystemSafeName
argument_list|(
name|name
argument_list|,
literal|true
argument_list|,
name|MAX_DIR_NAME_LENGTH
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|toFileSystemSafeName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|toFileSystemSafeName
argument_list|(
name|name
argument_list|,
literal|false
argument_list|,
name|MAX_FILE_NAME_LENGTH
argument_list|)
return|;
block|}
comment|/**      * Converts any string into a string that is safe to use as a file name.      * The result will only include ascii characters and numbers, and the "-","_", and "." characters.      *      * @param name      * @param dirSeparators       * @param maxFileLength       * @return      */
specifier|public
specifier|static
name|String
name|toFileSystemSafeName
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|dirSeparators
parameter_list|,
name|int
name|maxFileLength
parameter_list|)
block|{
name|int
name|size
init|=
name|name
operator|.
name|length
argument_list|()
decl_stmt|;
name|StringBuffer
name|rc
init|=
operator|new
name|StringBuffer
argument_list|(
name|size
operator|*
literal|2
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
name|char
name|c
init|=
name|name
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|boolean
name|valid
init|=
name|c
operator|>=
literal|'a'
operator|&&
name|c
operator|<=
literal|'z'
decl_stmt|;
name|valid
operator|=
name|valid
operator|||
operator|(
name|c
operator|>=
literal|'A'
operator|&&
name|c
operator|<=
literal|'Z'
operator|)
expr_stmt|;
name|valid
operator|=
name|valid
operator|||
operator|(
name|c
operator|>=
literal|'0'
operator|&&
name|c
operator|<=
literal|'9'
operator|)
expr_stmt|;
name|valid
operator|=
name|valid
operator|||
operator|(
name|c
operator|==
literal|'_'
operator|)
operator|||
operator|(
name|c
operator|==
literal|'-'
operator|)
operator|||
operator|(
name|c
operator|==
literal|'.'
operator|)
operator|||
operator|(
name|c
operator|==
literal|'#'
operator|)
operator|||
operator|(
name|dirSeparators
operator|&&
operator|(
operator|(
name|c
operator|==
literal|'/'
operator|)
operator|||
operator|(
name|c
operator|==
literal|'\\'
operator|)
operator|)
operator|)
expr_stmt|;
if|if
condition|(
name|valid
condition|)
block|{
name|rc
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Encode the character using hex notation
name|rc
operator|.
name|append
argument_list|(
literal|'#'
argument_list|)
expr_stmt|;
name|rc
operator|.
name|append
argument_list|(
name|HexSupport
operator|.
name|toHexFromInt
argument_list|(
name|c
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|result
init|=
name|rc
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|length
argument_list|()
operator|>
name|maxFileLength
condition|)
block|{
name|result
operator|=
name|result
operator|.
name|substring
argument_list|(
name|result
operator|.
name|length
argument_list|()
operator|-
name|maxFileLength
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
specifier|static
name|boolean
name|delete
parameter_list|(
name|File
name|top
parameter_list|)
block|{
name|boolean
name|result
init|=
literal|true
decl_stmt|;
name|Stack
argument_list|<
name|File
argument_list|>
name|files
init|=
operator|new
name|Stack
argument_list|<
name|File
argument_list|>
argument_list|()
decl_stmt|;
comment|// Add file to the stack to be processed...
name|files
operator|.
name|push
argument_list|(
name|top
argument_list|)
expr_stmt|;
comment|// Process all files until none remain...
while|while
condition|(
operator|!
name|files
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|File
name|file
init|=
name|files
operator|.
name|pop
argument_list|()
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|File
name|list
index|[]
init|=
name|file
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
operator|||
name|list
operator|.
name|length
operator|==
literal|0
condition|)
block|{
comment|// The current directory contains no entries...
comment|// delete directory and continue...
name|result
operator|&=
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// Add back the directory since it is not empty....
comment|// and when we process it again it will be empty and can be
comment|// deleted safely...
name|files
operator|.
name|push
argument_list|(
name|file
argument_list|)
expr_stmt|;
for|for
control|(
name|File
name|dirFile
range|:
name|list
control|)
block|{
if|if
condition|(
name|dirFile
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
comment|// Place the directory on the stack...
name|files
operator|.
name|push
argument_list|(
name|dirFile
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// This is a simple file, delete it...
name|result
operator|&=
name|dirFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
comment|// This is a simple file, delete it...
name|result
operator|&=
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
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
name|copyFile
argument_list|(
name|src
argument_list|,
name|dest
argument_list|,
literal|null
argument_list|)
expr_stmt|;
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
parameter_list|,
name|FilenameFilter
name|filter
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|src
operator|.
name|getCanonicalPath
argument_list|()
operator|.
name|equals
argument_list|(
name|dest
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
operator|==
literal|false
condition|)
block|{
if|if
condition|(
name|src
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|mkdirs
argument_list|(
name|dest
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|File
argument_list|>
name|list
init|=
name|getFiles
argument_list|(
name|src
argument_list|,
name|filter
argument_list|)
decl_stmt|;
for|for
control|(
name|File
name|f
range|:
name|list
control|)
block|{
if|if
condition|(
name|f
operator|.
name|isFile
argument_list|()
condition|)
block|{
name|File
name|target
init|=
operator|new
name|File
argument_list|(
name|getCopyParent
argument_list|(
name|src
argument_list|,
name|dest
argument_list|,
name|f
argument_list|)
argument_list|,
name|f
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|copySingleFile
argument_list|(
name|f
argument_list|,
name|target
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|dest
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|mkdirs
argument_list|(
name|dest
argument_list|)
expr_stmt|;
name|File
name|target
init|=
operator|new
name|File
argument_list|(
name|dest
argument_list|,
name|src
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|copySingleFile
argument_list|(
name|src
argument_list|,
name|target
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|copySingleFile
argument_list|(
name|src
argument_list|,
name|dest
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|static
name|File
name|getCopyParent
parameter_list|(
name|File
name|from
parameter_list|,
name|File
name|to
parameter_list|,
name|File
name|src
parameter_list|)
block|{
name|File
name|result
init|=
literal|null
decl_stmt|;
name|File
name|parent
init|=
name|src
operator|.
name|getParentFile
argument_list|()
decl_stmt|;
name|String
name|fromPath
init|=
name|from
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|.
name|getAbsolutePath
argument_list|()
operator|.
name|equals
argument_list|(
name|fromPath
argument_list|)
condition|)
block|{
comment|//one level down
name|result
operator|=
name|to
expr_stmt|;
block|}
else|else
block|{
name|String
name|parentPath
init|=
name|parent
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|parentPath
operator|.
name|substring
argument_list|(
name|fromPath
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|File
argument_list|(
name|to
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|static
name|List
argument_list|<
name|File
argument_list|>
name|getFiles
parameter_list|(
name|File
name|dir
parameter_list|,
name|FilenameFilter
name|filter
parameter_list|)
block|{
name|List
argument_list|<
name|File
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|File
argument_list|>
argument_list|()
decl_stmt|;
name|getFiles
argument_list|(
name|dir
argument_list|,
name|result
argument_list|,
name|filter
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|static
name|void
name|getFiles
parameter_list|(
name|File
name|dir
parameter_list|,
name|List
argument_list|<
name|File
argument_list|>
name|list
parameter_list|,
name|FilenameFilter
name|filter
parameter_list|)
block|{
if|if
condition|(
operator|!
name|list
operator|.
name|contains
argument_list|(
name|dir
argument_list|)
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|String
index|[]
name|fileNames
init|=
name|dir
operator|.
name|list
argument_list|(
name|filter
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
name|fileNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
name|fileNames
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|isFile
argument_list|()
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|getFiles
argument_list|(
name|dir
argument_list|,
name|list
argument_list|,
name|filter
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|public
specifier|static
name|void
name|copySingleFile
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
name|getInteger
argument_list|(
literal|"MaximumDirNameLength"
argument_list|,
literal|200
argument_list|)
expr_stmt|;
name|MAX_FILE_NAME_LENGTH
operator|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"MaximumFileNameLength"
argument_list|,
literal|64
argument_list|)
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

