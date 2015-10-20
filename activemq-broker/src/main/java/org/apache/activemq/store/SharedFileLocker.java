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
name|store
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
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|AbstractLocker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|LockFile
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|ServiceStopper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Represents an exclusive lock on a database to avoid multiple brokers running  * against the same logical database.  *  * @org.apache.xbean.XBean element="shared-file-locker"  *  */
end_comment

begin_class
specifier|public
class|class
name|SharedFileLocker
extends|extends
name|AbstractLocker
block|{
specifier|public
specifier|static
specifier|final
name|File
name|DEFAULT_DIRECTORY
init|=
operator|new
name|File
argument_list|(
literal|"KahaDB"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SharedFileLocker
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|LockFile
name|lockFile
decl_stmt|;
specifier|protected
name|File
name|directory
init|=
name|DEFAULT_DIRECTORY
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|doStart
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|lockFile
operator|==
literal|null
condition|)
block|{
name|File
name|lockFileName
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"lock"
argument_list|)
decl_stmt|;
name|lockFile
operator|=
operator|new
name|LockFile
argument_list|(
name|lockFileName
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|failIfLocked
condition|)
block|{
name|lockFile
operator|.
name|lock
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// Print a warning only once
name|boolean
name|warned
init|=
literal|false
decl_stmt|;
name|boolean
name|locked
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|(
operator|!
name|isStopped
argument_list|()
operator|)
operator|&&
operator|(
operator|!
name|isStopping
argument_list|()
operator|)
condition|)
block|{
try|try
block|{
name|lockFile
operator|.
name|lock
argument_list|()
expr_stmt|;
if|if
condition|(
name|warned
condition|)
block|{
comment|// ensure lockHolder has released; wait for one keepAlive iteration
try|try
block|{
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
name|lockable
operator|!=
literal|null
condition|?
name|lockable
operator|.
name|getLockKeepAlivePeriod
argument_list|()
else|:
literal|0l
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e1
parameter_list|)
block|{                             }
block|}
name|locked
operator|=
name|keepAlive
argument_list|()
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|warned
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Database "
operator|+
name|lockFileName
operator|+
literal|" is locked by another server. This broker is now in slave mode waiting a lock to be acquired"
argument_list|)
expr_stmt|;
name|warned
operator|=
literal|true
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Database "
operator|+
name|lockFileName
operator|+
literal|" is locked... waiting "
operator|+
operator|(
name|lockAcquireSleepInterval
operator|/
literal|1000
operator|)
operator|+
literal|" seconds for the database to be unlocked. Reason: "
operator|+
name|e
argument_list|)
expr_stmt|;
try|try
block|{
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
name|lockAcquireSleepInterval
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e1
parameter_list|)
block|{                         }
block|}
block|}
if|if
condition|(
operator|!
name|locked
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"attempt to obtain lock aborted due to shutdown"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|keepAlive
parameter_list|()
block|{
return|return
name|lockFile
operator|!=
literal|null
operator|&&
name|lockFile
operator|.
name|keepAlive
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|doStop
parameter_list|(
name|ServiceStopper
name|stopper
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|lockFile
operator|!=
literal|null
condition|)
block|{
name|lockFile
operator|.
name|unlock
argument_list|()
expr_stmt|;
name|lockFile
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|public
name|File
name|getDirectory
parameter_list|()
block|{
return|return
name|directory
return|;
block|}
specifier|public
name|void
name|setDirectory
parameter_list|(
name|File
name|directory
parameter_list|)
block|{
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|(
name|PersistenceAdapter
name|persistenceAdapter
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|setDirectory
argument_list|(
name|persistenceAdapter
operator|.
name|getDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

