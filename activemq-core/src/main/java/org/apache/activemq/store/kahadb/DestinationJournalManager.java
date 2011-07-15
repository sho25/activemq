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
operator|.
name|kahadb
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
name|FilenameFilter
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|ConcurrentHashMap
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
name|atomic
operator|.
name|AtomicBoolean
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
name|atomic
operator|.
name|AtomicLong
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
name|command
operator|.
name|ActiveMQDestination
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
name|command
operator|.
name|ActiveMQQueue
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
name|command
operator|.
name|ActiveMQTopic
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
name|IOHelper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|journal
operator|.
name|DataFile
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kahadb
operator|.
name|journal
operator|.
name|Journal
import|;
end_import

begin_class
specifier|public
class|class
name|DestinationJournalManager
implements|implements
name|JournalManager
block|{
specifier|private
specifier|static
specifier|final
name|String
name|PREPEND
init|=
literal|"JournalDest-"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|QUEUE_PREPEND
init|=
name|PREPEND
operator|+
literal|"Queue-"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TOPIC_PREPEND
init|=
name|PREPEND
operator|+
literal|"Topic-"
decl_stmt|;
specifier|private
name|AtomicBoolean
name|started
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|ActiveMQDestination
argument_list|,
name|Journal
argument_list|>
name|journalMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|ActiveMQDestination
argument_list|,
name|Journal
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|File
name|directory
init|=
operator|new
name|File
argument_list|(
literal|"KahaDB"
argument_list|)
decl_stmt|;
specifier|private
name|File
name|directoryArchive
decl_stmt|;
specifier|private
name|int
name|maxFileLength
init|=
name|Journal
operator|.
name|DEFAULT_MAX_FILE_LENGTH
decl_stmt|;
specifier|private
name|boolean
name|checkForCorruptionOnStartup
decl_stmt|;
specifier|private
name|boolean
name|checksum
init|=
literal|false
decl_stmt|;
specifier|private
name|int
name|writeBatchSize
init|=
name|Journal
operator|.
name|DEFAULT_MAX_WRITE_BATCH_SIZE
decl_stmt|;
specifier|private
name|boolean
name|archiveDataLogs
decl_stmt|;
specifier|private
name|AtomicLong
name|storeSize
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|public
name|AtomicBoolean
name|getStarted
parameter_list|()
block|{
return|return
name|started
return|;
block|}
specifier|public
name|void
name|setStarted
parameter_list|(
name|AtomicBoolean
name|started
parameter_list|)
block|{
name|this
operator|.
name|started
operator|=
name|started
expr_stmt|;
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
specifier|public
name|File
name|getDirectoryArchive
parameter_list|()
block|{
return|return
name|directoryArchive
return|;
block|}
specifier|public
name|void
name|setDirectoryArchive
parameter_list|(
name|File
name|directoryArchive
parameter_list|)
block|{
name|this
operator|.
name|directoryArchive
operator|=
name|directoryArchive
expr_stmt|;
block|}
specifier|public
name|int
name|getMaxFileLength
parameter_list|()
block|{
return|return
name|maxFileLength
return|;
block|}
specifier|public
name|void
name|setMaxFileLength
parameter_list|(
name|int
name|maxFileLength
parameter_list|)
block|{
name|this
operator|.
name|maxFileLength
operator|=
name|maxFileLength
expr_stmt|;
block|}
specifier|public
name|boolean
name|isCheckForCorruptionOnStartup
parameter_list|()
block|{
return|return
name|checkForCorruptionOnStartup
return|;
block|}
specifier|public
name|void
name|setCheckForCorruptionOnStartup
parameter_list|(
name|boolean
name|checkForCorruptionOnStartup
parameter_list|)
block|{
name|this
operator|.
name|checkForCorruptionOnStartup
operator|=
name|checkForCorruptionOnStartup
expr_stmt|;
block|}
specifier|public
name|boolean
name|isChecksum
parameter_list|()
block|{
return|return
name|checksum
return|;
block|}
specifier|public
name|void
name|setChecksum
parameter_list|(
name|boolean
name|checksum
parameter_list|)
block|{
name|this
operator|.
name|checksum
operator|=
name|checksum
expr_stmt|;
block|}
specifier|public
name|int
name|getWriteBatchSize
parameter_list|()
block|{
return|return
name|writeBatchSize
return|;
block|}
specifier|public
name|void
name|setWriteBatchSize
parameter_list|(
name|int
name|writeBatchSize
parameter_list|)
block|{
name|this
operator|.
name|writeBatchSize
operator|=
name|writeBatchSize
expr_stmt|;
block|}
specifier|public
name|boolean
name|isArchiveDataLogs
parameter_list|()
block|{
return|return
name|archiveDataLogs
return|;
block|}
specifier|public
name|void
name|setArchiveDataLogs
parameter_list|(
name|boolean
name|archiveDataLogs
parameter_list|)
block|{
name|this
operator|.
name|archiveDataLogs
operator|=
name|archiveDataLogs
expr_stmt|;
block|}
specifier|public
name|AtomicLong
name|getStoreSize
parameter_list|()
block|{
return|return
name|storeSize
return|;
block|}
specifier|public
name|void
name|setStoreSize
parameter_list|(
name|AtomicLong
name|storeSize
parameter_list|)
block|{
name|this
operator|.
name|storeSize
operator|=
name|storeSize
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|started
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|File
index|[]
name|files
init|=
name|getDirectory
argument_list|()
operator|.
name|listFiles
argument_list|(
operator|new
name|FilenameFilter
argument_list|()
block|{
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|file
parameter_list|,
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|file
operator|.
name|isDirectory
argument_list|()
operator|&&
name|s
operator|!=
literal|null
operator|&&
name|s
operator|.
name|startsWith
argument_list|(
name|PREPEND
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|files
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|File
name|file
range|:
name|files
control|)
block|{
name|ActiveMQDestination
name|destination
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|TOPIC_PREPEND
argument_list|)
condition|)
block|{
name|String
name|destinationName
init|=
name|file
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
name|TOPIC_PREPEND
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|destination
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
name|destinationName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|destinationName
init|=
name|file
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
name|QUEUE_PREPEND
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|destination
operator|=
operator|new
name|ActiveMQQueue
argument_list|(
name|destinationName
argument_list|)
expr_stmt|;
block|}
name|Journal
name|journal
init|=
operator|new
name|Journal
argument_list|()
decl_stmt|;
name|journal
operator|.
name|setDirectory
argument_list|(
name|file
argument_list|)
expr_stmt|;
if|if
condition|(
name|getDirectoryArchive
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|IOHelper
operator|.
name|mkdirs
argument_list|(
name|getDirectoryArchive
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|archive
init|=
operator|new
name|File
argument_list|(
name|getDirectoryArchive
argument_list|()
argument_list|,
name|file
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|IOHelper
operator|.
name|mkdirs
argument_list|(
name|archive
argument_list|)
expr_stmt|;
name|journal
operator|.
name|setDirectoryArchive
argument_list|(
name|archive
argument_list|)
expr_stmt|;
block|}
name|configure
argument_list|(
name|journal
argument_list|)
expr_stmt|;
name|journalMap
operator|.
name|put
argument_list|(
name|destination
argument_list|,
name|journal
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Journal
name|journal
range|:
name|journalMap
operator|.
name|values
argument_list|()
control|)
block|{
name|journal
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|started
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
for|for
control|(
name|Journal
name|journal
range|:
name|journalMap
operator|.
name|values
argument_list|()
control|)
block|{
name|journal
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|journalMap
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|delete
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|Journal
name|journal
range|:
name|journalMap
operator|.
name|values
argument_list|()
control|)
block|{
name|journal
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
name|journalMap
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Journal
name|getJournal
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|IOException
block|{
name|Journal
name|journal
init|=
name|journalMap
operator|.
name|get
argument_list|(
name|destination
argument_list|)
decl_stmt|;
if|if
condition|(
name|journal
operator|==
literal|null
operator|&&
operator|!
name|destination
operator|.
name|isTemporary
argument_list|()
condition|)
block|{
name|journal
operator|=
operator|new
name|Journal
argument_list|()
expr_stmt|;
name|String
name|fileName
decl_stmt|;
if|if
condition|(
name|destination
operator|.
name|isTopic
argument_list|()
condition|)
block|{
name|fileName
operator|=
name|TOPIC_PREPEND
operator|+
name|destination
operator|.
name|getPhysicalName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|fileName
operator|=
name|QUEUE_PREPEND
operator|+
name|destination
operator|.
name|getPhysicalName
argument_list|()
expr_stmt|;
block|}
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|getDirectory
argument_list|()
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
name|IOHelper
operator|.
name|mkdirs
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|journal
operator|.
name|setDirectory
argument_list|(
name|file
argument_list|)
expr_stmt|;
if|if
condition|(
name|getDirectoryArchive
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|IOHelper
operator|.
name|mkdirs
argument_list|(
name|getDirectoryArchive
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|archive
init|=
operator|new
name|File
argument_list|(
name|getDirectoryArchive
argument_list|()
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
name|IOHelper
operator|.
name|mkdirs
argument_list|(
name|archive
argument_list|)
expr_stmt|;
name|journal
operator|.
name|setDirectoryArchive
argument_list|(
name|archive
argument_list|)
expr_stmt|;
block|}
name|configure
argument_list|(
name|journal
argument_list|)
expr_stmt|;
if|if
condition|(
name|started
operator|.
name|get
argument_list|()
condition|)
block|{
name|journal
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
return|return
name|journal
return|;
block|}
else|else
block|{
return|return
name|journal
return|;
block|}
block|}
specifier|public
name|Map
argument_list|<
name|Integer
argument_list|,
name|DataFile
argument_list|>
name|getFileMap
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Not supported"
argument_list|)
throw|;
block|}
specifier|public
name|Collection
argument_list|<
name|Journal
argument_list|>
name|getJournals
parameter_list|()
block|{
return|return
name|journalMap
operator|.
name|values
argument_list|()
return|;
block|}
specifier|public
name|Collection
argument_list|<
name|Journal
argument_list|>
name|getJournals
parameter_list|(
name|Set
argument_list|<
name|ActiveMQDestination
argument_list|>
name|set
parameter_list|)
block|{
name|List
argument_list|<
name|Journal
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Journal
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ActiveMQDestination
name|destination
range|:
name|set
control|)
block|{
name|Journal
name|j
init|=
name|journalMap
operator|.
name|get
argument_list|(
name|destination
argument_list|)
decl_stmt|;
if|if
condition|(
name|j
operator|!=
literal|null
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|j
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|list
return|;
block|}
specifier|protected
name|void
name|configure
parameter_list|(
name|Journal
name|journal
parameter_list|)
block|{
name|journal
operator|.
name|setMaxFileLength
argument_list|(
name|getMaxFileLength
argument_list|()
argument_list|)
expr_stmt|;
name|journal
operator|.
name|setCheckForCorruptionOnStartup
argument_list|(
name|isCheckForCorruptionOnStartup
argument_list|()
argument_list|)
expr_stmt|;
name|journal
operator|.
name|setChecksum
argument_list|(
name|isChecksum
argument_list|()
operator|||
name|isCheckForCorruptionOnStartup
argument_list|()
argument_list|)
expr_stmt|;
name|journal
operator|.
name|setWriteBatchSize
argument_list|(
name|getWriteBatchSize
argument_list|()
argument_list|)
expr_stmt|;
name|journal
operator|.
name|setArchiveDataLogs
argument_list|(
name|isArchiveDataLogs
argument_list|()
argument_list|)
expr_stmt|;
name|journal
operator|.
name|setSizeAccumulator
argument_list|(
name|getStoreSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

