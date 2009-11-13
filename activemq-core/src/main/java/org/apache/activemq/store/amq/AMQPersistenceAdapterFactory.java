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
name|amq
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|kaha
operator|.
name|impl
operator|.
name|async
operator|.
name|AsyncDataManager
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
name|kaha
operator|.
name|impl
operator|.
name|index
operator|.
name|hash
operator|.
name|HashIndex
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
name|store
operator|.
name|PersistenceAdapter
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
name|store
operator|.
name|PersistenceAdapterFactory
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
name|store
operator|.
name|ReferenceStoreAdapter
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
name|thread
operator|.
name|TaskRunnerFactory
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

begin_comment
comment|/**  * An implementation of {@link PersistenceAdapterFactory}  *   * @org.apache.xbean.XBean element="amqPersistenceAdapterFactory"  *   * @version $Revision: 1.17 $  */
end_comment

begin_class
specifier|public
class|class
name|AMQPersistenceAdapterFactory
implements|implements
name|PersistenceAdapterFactory
block|{
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_REFERNCE_FILE_LENGTH
init|=
literal|2
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
specifier|private
name|TaskRunnerFactory
name|taskRunnerFactory
decl_stmt|;
specifier|private
name|File
name|dataDirectory
decl_stmt|;
specifier|private
name|int
name|journalThreadPriority
init|=
name|Thread
operator|.
name|MAX_PRIORITY
decl_stmt|;
specifier|private
name|String
name|brokerName
init|=
literal|"localhost"
decl_stmt|;
specifier|private
name|ReferenceStoreAdapter
name|referenceStoreAdapter
decl_stmt|;
specifier|private
name|boolean
name|syncOnWrite
decl_stmt|;
specifier|private
name|boolean
name|syncOnTransaction
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|persistentIndex
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|useNio
init|=
literal|true
decl_stmt|;
specifier|private
name|int
name|maxFileLength
init|=
name|AsyncDataManager
operator|.
name|DEFAULT_MAX_FILE_LENGTH
decl_stmt|;
specifier|private
name|long
name|cleanupInterval
init|=
name|AsyncDataManager
operator|.
name|DEFAULT_CLEANUP_INTERVAL
decl_stmt|;
specifier|private
name|int
name|indexBinSize
init|=
name|HashIndex
operator|.
name|DEFAULT_BIN_SIZE
decl_stmt|;
specifier|private
name|int
name|indexKeySize
init|=
name|HashIndex
operator|.
name|DEFAULT_KEY_SIZE
decl_stmt|;
specifier|private
name|int
name|indexPageSize
init|=
name|HashIndex
operator|.
name|DEFAULT_PAGE_SIZE
decl_stmt|;
specifier|private
name|int
name|indexMaxBinSize
init|=
name|HashIndex
operator|.
name|MAXIMUM_CAPACITY
decl_stmt|;
specifier|private
name|int
name|indexLoadFactor
init|=
name|HashIndex
operator|.
name|DEFAULT_LOAD_FACTOR
decl_stmt|;
specifier|private
name|int
name|maxReferenceFileLength
init|=
name|DEFAULT_MAX_REFERNCE_FILE_LENGTH
decl_stmt|;
specifier|private
name|boolean
name|recoverReferenceStore
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|forceRecoverReferenceStore
init|=
literal|false
decl_stmt|;
specifier|private
name|long
name|checkpointInterval
init|=
literal|1000
operator|*
literal|20
decl_stmt|;
specifier|private
name|boolean
name|useDedicatedTaskRunner
decl_stmt|;
comment|/**      * @return a AMQPersistenceAdapter      * @see org.apache.activemq.store.PersistenceAdapterFactory#createPersistenceAdapter()      */
specifier|public
name|PersistenceAdapter
name|createPersistenceAdapter
parameter_list|()
block|{
name|AMQPersistenceAdapter
name|result
init|=
operator|new
name|AMQPersistenceAdapter
argument_list|()
decl_stmt|;
name|result
operator|.
name|setDirectory
argument_list|(
name|getDataDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setTaskRunnerFactory
argument_list|(
name|getTaskRunnerFactory
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setBrokerName
argument_list|(
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setSyncOnWrite
argument_list|(
name|isSyncOnWrite
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setPersistentIndex
argument_list|(
name|isPersistentIndex
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setReferenceStoreAdapter
argument_list|(
name|getReferenceStoreAdapter
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setUseNio
argument_list|(
name|isUseNio
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setMaxFileLength
argument_list|(
name|getMaxFileLength
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setCleanupInterval
argument_list|(
name|getCleanupInterval
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setCheckpointInterval
argument_list|(
name|getCheckpointInterval
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setIndexBinSize
argument_list|(
name|getIndexBinSize
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setIndexKeySize
argument_list|(
name|getIndexKeySize
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setIndexPageSize
argument_list|(
name|getIndexPageSize
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setIndexMaxBinSize
argument_list|(
name|getIndexMaxBinSize
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setIndexLoadFactor
argument_list|(
name|getIndexLoadFactor
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setMaxReferenceFileLength
argument_list|(
name|getMaxReferenceFileLength
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setForceRecoverReferenceStore
argument_list|(
name|isForceRecoverReferenceStore
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setRecoverReferenceStore
argument_list|(
name|isRecoverReferenceStore
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|public
name|long
name|getCleanupInterval
parameter_list|()
block|{
return|return
name|cleanupInterval
return|;
block|}
specifier|public
name|void
name|setCleanupInterval
parameter_list|(
name|long
name|val
parameter_list|)
block|{
name|cleanupInterval
operator|=
name|val
expr_stmt|;
block|}
comment|/**      * @return the dataDirectory      */
specifier|public
name|File
name|getDataDirectory
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|dataDirectory
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|dataDirectory
operator|=
operator|new
name|File
argument_list|(
name|IOHelper
operator|.
name|getDefaultDataDirectory
argument_list|()
argument_list|,
name|IOHelper
operator|.
name|toFileSystemSafeName
argument_list|(
name|brokerName
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|this
operator|.
name|dataDirectory
return|;
block|}
comment|/**      * @param dataDirectory the dataDirectory to set      */
specifier|public
name|void
name|setDataDirectory
parameter_list|(
name|File
name|dataDirectory
parameter_list|)
block|{
name|this
operator|.
name|dataDirectory
operator|=
name|dataDirectory
expr_stmt|;
block|}
specifier|public
name|boolean
name|isUseDedicatedTaskRunner
parameter_list|()
block|{
return|return
name|useDedicatedTaskRunner
return|;
block|}
specifier|public
name|void
name|setUseDedicatedTaskRunner
parameter_list|(
name|boolean
name|useDedicatedTaskRunner
parameter_list|)
block|{
name|this
operator|.
name|useDedicatedTaskRunner
operator|=
name|useDedicatedTaskRunner
expr_stmt|;
block|}
comment|/**      * @return the taskRunnerFactory      */
specifier|public
name|TaskRunnerFactory
name|getTaskRunnerFactory
parameter_list|()
block|{
if|if
condition|(
name|taskRunnerFactory
operator|==
literal|null
condition|)
block|{
name|taskRunnerFactory
operator|=
operator|new
name|TaskRunnerFactory
argument_list|(
literal|"AMQPersistenceAdaptor Task"
argument_list|,
name|journalThreadPriority
argument_list|,
literal|true
argument_list|,
literal|1000
argument_list|,
name|isUseDedicatedTaskRunner
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|taskRunnerFactory
return|;
block|}
comment|/**      * @param taskRunnerFactory the taskRunnerFactory to set      */
specifier|public
name|void
name|setTaskRunnerFactory
parameter_list|(
name|TaskRunnerFactory
name|taskRunnerFactory
parameter_list|)
block|{
name|this
operator|.
name|taskRunnerFactory
operator|=
name|taskRunnerFactory
expr_stmt|;
block|}
comment|/**      * @return the journalThreadPriority      */
specifier|public
name|int
name|getJournalThreadPriority
parameter_list|()
block|{
return|return
name|this
operator|.
name|journalThreadPriority
return|;
block|}
comment|/**      * @param journalThreadPriority the journalThreadPriority to set      */
specifier|public
name|void
name|setJournalThreadPriority
parameter_list|(
name|int
name|journalThreadPriority
parameter_list|)
block|{
name|this
operator|.
name|journalThreadPriority
operator|=
name|journalThreadPriority
expr_stmt|;
block|}
comment|/**      * @return the brokerName      */
specifier|public
name|String
name|getBrokerName
parameter_list|()
block|{
return|return
name|this
operator|.
name|brokerName
return|;
block|}
comment|/**      * @param brokerName the brokerName to set      */
specifier|public
name|void
name|setBrokerName
parameter_list|(
name|String
name|brokerName
parameter_list|)
block|{
name|this
operator|.
name|brokerName
operator|=
name|brokerName
expr_stmt|;
block|}
comment|/**      * @return the referenceStoreAdapter      */
specifier|public
name|ReferenceStoreAdapter
name|getReferenceStoreAdapter
parameter_list|()
block|{
return|return
name|this
operator|.
name|referenceStoreAdapter
return|;
block|}
comment|/**      * @param referenceStoreAdapter the referenceStoreAdapter to set      */
specifier|public
name|void
name|setReferenceStoreAdapter
parameter_list|(
name|ReferenceStoreAdapter
name|referenceStoreAdapter
parameter_list|)
block|{
name|this
operator|.
name|referenceStoreAdapter
operator|=
name|referenceStoreAdapter
expr_stmt|;
block|}
specifier|public
name|boolean
name|isPersistentIndex
parameter_list|()
block|{
return|return
name|persistentIndex
return|;
block|}
specifier|public
name|void
name|setPersistentIndex
parameter_list|(
name|boolean
name|persistentIndex
parameter_list|)
block|{
name|this
operator|.
name|persistentIndex
operator|=
name|persistentIndex
expr_stmt|;
block|}
specifier|public
name|boolean
name|isSyncOnWrite
parameter_list|()
block|{
return|return
name|syncOnWrite
return|;
block|}
specifier|public
name|void
name|setSyncOnWrite
parameter_list|(
name|boolean
name|syncOnWrite
parameter_list|)
block|{
name|this
operator|.
name|syncOnWrite
operator|=
name|syncOnWrite
expr_stmt|;
block|}
specifier|public
name|boolean
name|isSyncOnTransaction
parameter_list|()
block|{
return|return
name|syncOnTransaction
return|;
block|}
specifier|public
name|void
name|setSyncOnTransaction
parameter_list|(
name|boolean
name|syncOnTransaction
parameter_list|)
block|{
name|this
operator|.
name|syncOnTransaction
operator|=
name|syncOnTransaction
expr_stmt|;
block|}
specifier|public
name|boolean
name|isUseNio
parameter_list|()
block|{
return|return
name|useNio
return|;
block|}
specifier|public
name|void
name|setUseNio
parameter_list|(
name|boolean
name|useNio
parameter_list|)
block|{
name|this
operator|.
name|useNio
operator|=
name|useNio
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
comment|/**      * @return the indexBinSize      */
specifier|public
name|int
name|getIndexBinSize
parameter_list|()
block|{
return|return
name|indexBinSize
return|;
block|}
comment|/**      * @param indexBinSize the indexBinSize to set      */
specifier|public
name|void
name|setIndexBinSize
parameter_list|(
name|int
name|indexBinSize
parameter_list|)
block|{
name|this
operator|.
name|indexBinSize
operator|=
name|indexBinSize
expr_stmt|;
block|}
comment|/**      * @return the indexKeySize      */
specifier|public
name|int
name|getIndexKeySize
parameter_list|()
block|{
return|return
name|indexKeySize
return|;
block|}
comment|/**      * @param indexKeySize the indexKeySize to set      */
specifier|public
name|void
name|setIndexKeySize
parameter_list|(
name|int
name|indexKeySize
parameter_list|)
block|{
name|this
operator|.
name|indexKeySize
operator|=
name|indexKeySize
expr_stmt|;
block|}
comment|/**      * @return the indexPageSize      */
specifier|public
name|int
name|getIndexPageSize
parameter_list|()
block|{
return|return
name|indexPageSize
return|;
block|}
comment|/**      * @param indexPageSize the indexPageSize to set      */
specifier|public
name|void
name|setIndexPageSize
parameter_list|(
name|int
name|indexPageSize
parameter_list|)
block|{
name|this
operator|.
name|indexPageSize
operator|=
name|indexPageSize
expr_stmt|;
block|}
comment|/**      * @return the indexMaxBinSize      */
specifier|public
name|int
name|getIndexMaxBinSize
parameter_list|()
block|{
return|return
name|indexMaxBinSize
return|;
block|}
comment|/**      * @param indexMaxBinSize the indexMaxBinSize to set      */
specifier|public
name|void
name|setIndexMaxBinSize
parameter_list|(
name|int
name|indexMaxBinSize
parameter_list|)
block|{
name|this
operator|.
name|indexMaxBinSize
operator|=
name|indexMaxBinSize
expr_stmt|;
block|}
comment|/**      * @return the indexLoadFactor      */
specifier|public
name|int
name|getIndexLoadFactor
parameter_list|()
block|{
return|return
name|indexLoadFactor
return|;
block|}
comment|/**      * @param indexLoadFactor the indexLoadFactor to set      */
specifier|public
name|void
name|setIndexLoadFactor
parameter_list|(
name|int
name|indexLoadFactor
parameter_list|)
block|{
name|this
operator|.
name|indexLoadFactor
operator|=
name|indexLoadFactor
expr_stmt|;
block|}
comment|/**      * @return the maxReferenceFileLength      */
specifier|public
name|int
name|getMaxReferenceFileLength
parameter_list|()
block|{
return|return
name|maxReferenceFileLength
return|;
block|}
comment|/**      * @param maxReferenceFileLength the maxReferenceFileLength to set      */
specifier|public
name|void
name|setMaxReferenceFileLength
parameter_list|(
name|int
name|maxReferenceFileLength
parameter_list|)
block|{
name|this
operator|.
name|maxReferenceFileLength
operator|=
name|maxReferenceFileLength
expr_stmt|;
block|}
comment|/**      * @return the recoverReferenceStore      */
specifier|public
name|boolean
name|isRecoverReferenceStore
parameter_list|()
block|{
return|return
name|recoverReferenceStore
return|;
block|}
comment|/**      * @param recoverReferenceStore the recoverReferenceStore to set      */
specifier|public
name|void
name|setRecoverReferenceStore
parameter_list|(
name|boolean
name|recoverReferenceStore
parameter_list|)
block|{
name|this
operator|.
name|recoverReferenceStore
operator|=
name|recoverReferenceStore
expr_stmt|;
block|}
comment|/**      * @return the forceRecoverReferenceStore      */
specifier|public
name|boolean
name|isForceRecoverReferenceStore
parameter_list|()
block|{
return|return
name|forceRecoverReferenceStore
return|;
block|}
comment|/**      * @param forceRecoverReferenceStore the forceRecoverReferenceStore to set      */
specifier|public
name|void
name|setForceRecoverReferenceStore
parameter_list|(
name|boolean
name|forceRecoverReferenceStore
parameter_list|)
block|{
name|this
operator|.
name|forceRecoverReferenceStore
operator|=
name|forceRecoverReferenceStore
expr_stmt|;
block|}
comment|/**      * @return the checkpointInterval      */
specifier|public
name|long
name|getCheckpointInterval
parameter_list|()
block|{
return|return
name|checkpointInterval
return|;
block|}
comment|/**      * @param checkpointInterval the checkpointInterval to set      */
specifier|public
name|void
name|setCheckpointInterval
parameter_list|(
name|long
name|checkpointInterval
parameter_list|)
block|{
name|this
operator|.
name|checkpointInterval
operator|=
name|checkpointInterval
expr_stmt|;
block|}
block|}
end_class

end_unit

