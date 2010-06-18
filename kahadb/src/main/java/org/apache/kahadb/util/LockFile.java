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
name|kahadb
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|RandomAccessFile
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|OverlappingFileLockException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_comment
comment|/**  * Used to lock a File.  *   * @author chirino  */
end_comment

begin_class
specifier|public
class|class
name|LockFile
block|{
specifier|private
specifier|static
specifier|final
name|boolean
name|DISABLE_FILE_LOCK
init|=
literal|"true"
operator|.
name|equals
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.nio.channels.FileLock.broken"
argument_list|,
literal|"false"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
specifier|private
name|File
name|file
decl_stmt|;
specifier|private
name|FileLock
name|lock
decl_stmt|;
specifier|private
name|RandomAccessFile
name|readFile
decl_stmt|;
specifier|private
name|int
name|lockCounter
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|deleteOnUnlock
decl_stmt|;
specifier|public
name|LockFile
parameter_list|(
name|File
name|file
parameter_list|,
name|boolean
name|deleteOnUnlock
parameter_list|)
block|{
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
name|this
operator|.
name|deleteOnUnlock
operator|=
name|deleteOnUnlock
expr_stmt|;
block|}
comment|/**      * @throws IOException      */
specifier|synchronized
specifier|public
name|void
name|lock
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|DISABLE_FILE_LOCK
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|lockCounter
operator|>
literal|0
condition|)
block|{
return|return;
block|}
name|IOHelper
operator|.
name|mkdirs
argument_list|(
name|file
operator|.
name|getParentFile
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
name|getVmLockKey
argument_list|()
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"File '"
operator|+
name|file
operator|+
literal|"' could not be locked as lock is already held for this jvm."
argument_list|)
throw|;
block|}
if|if
condition|(
name|lock
operator|==
literal|null
condition|)
block|{
name|readFile
operator|=
operator|new
name|RandomAccessFile
argument_list|(
name|file
argument_list|,
literal|"rw"
argument_list|)
expr_stmt|;
name|IOException
name|reason
init|=
literal|null
decl_stmt|;
try|try
block|{
name|lock
operator|=
name|readFile
operator|.
name|getChannel
argument_list|()
operator|.
name|tryLock
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OverlappingFileLockException
name|e
parameter_list|)
block|{
name|reason
operator|=
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"File '"
operator|+
name|file
operator|+
literal|"' could not be locked."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|lock
operator|!=
literal|null
condition|)
block|{
name|lockCounter
operator|++
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|getVmLockKey
argument_list|()
argument_list|,
operator|new
name|Date
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// new read file for next attempt
name|closeReadFile
argument_list|()
expr_stmt|;
if|if
condition|(
name|reason
operator|!=
literal|null
condition|)
block|{
throw|throw
name|reason
throw|;
block|}
throw|throw
operator|new
name|IOException
argument_list|(
literal|"File '"
operator|+
name|file
operator|+
literal|"' could not be locked."
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**      */
specifier|public
name|void
name|unlock
parameter_list|()
block|{
if|if
condition|(
name|DISABLE_FILE_LOCK
condition|)
block|{
return|return;
block|}
name|lockCounter
operator|--
expr_stmt|;
if|if
condition|(
name|lockCounter
operator|!=
literal|0
condition|)
block|{
return|return;
block|}
comment|// release the lock..
if|if
condition|(
name|lock
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|lock
operator|.
name|release
argument_list|()
expr_stmt|;
name|System
operator|.
name|getProperties
argument_list|()
operator|.
name|remove
argument_list|(
name|getVmLockKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ignore
parameter_list|)
block|{             }
name|lock
operator|=
literal|null
expr_stmt|;
block|}
name|closeReadFile
argument_list|()
expr_stmt|;
if|if
condition|(
name|deleteOnUnlock
condition|)
block|{
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|String
name|getVmLockKey
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".lock."
operator|+
name|file
operator|.
name|getCanonicalPath
argument_list|()
return|;
block|}
specifier|private
name|void
name|closeReadFile
parameter_list|()
block|{
comment|// close the file.
if|if
condition|(
name|readFile
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|readFile
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ignore
parameter_list|)
block|{             }
name|readFile
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

